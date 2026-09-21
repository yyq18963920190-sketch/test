package fish.ai;

import fish.Fish;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Native Java inference over the same 1600 fitted samples as the Python model. */
public final class AiAdvisor {
    public enum Action { LEFT, RIGHT, UP, DOWN, STAY }
    public static final class Body {
        public final double x, y, width, height, speed, ySpeed;
        public final int grade, state;
        public Body(double x, double y, double w, double h, double speed, double ys, int grade, int state) {
            this.x=x; this.y=y; width=w; height=h; this.speed=speed; ySpeed=ys;
            this.grade=grade; this.state=state;
        }
        public static Body of(Fish f) {
            return new Body(f.getX(),f.getY(),f.getWidth(),f.getHeight(),f.getSpeed(),f.getY_speed(),f.getGrade(),f.getState());
        }
    }
    public static final class Decision {
        public final Action action, raw;
        public final double confidence;
        public final String reason;
        Decision(Action action, Action raw, double confidence, String reason) {
            this.action=action; this.raw=raw; this.confidence=confidence; this.reason=reason;
        }
        public String protocol() {
            return action+"\t"+(raw==null ? "null" : raw)+"\t"+confidence+"\t"+reason;
        }
    }
    private final List<double[]> samples = new ArrayList<double[]>();
    private final List<Action> labels = new ArrayList<Action>();

    public AiAdvisor() throws IOException { this(AiAdvisor.class.getResourceAsStream("/ai/policy.tsv")); }
    public AiAdvisor(InputStream input) throws IOException {
        if (input==null) throw new IOException("AI model /ai/policy.tsv missing");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            if (!"deep-sea-knn-v1\tk=3".equals(in.readLine())) throw new IOException("Invalid model header");
            String line;
            while ((line=in.readLine())!=null) {
                String[] v=line.split("\t",-1);
                if (v.length!=4 || samples.size()>=100000) throw new IOException("Invalid model row count/shape");
                double[] row={Double.parseDouble(v[0]),Double.parseDouble(v[1]),Double.parseDouble(v[2])};
                for (double n:row) if (!Double.isFinite(n)) throw new IOException("Nonfinite model sample");
                samples.add(row); labels.add(Action.valueOf(v[3]));
            }
        } catch (IllegalArgumentException ex) { throw new IOException("Invalid model data",ex); }
        if (samples.size()<3) throw new IOException("Insufficient model samples");
    }
    public int sampleCount() { return samples.size(); }
    public static Decision reject(String reason) { return new Decision(Action.STAY,null,0,reason); }
    private static boolean range(double n,double lo,double hi) { return Double.isFinite(n)&&n>=lo&&n<=hi; }
    private static boolean valid(Body b,boolean player) {
        return b!=null && range(b.x,player?0:-300,player?1900:2200)
            && range(b.y,player?0:-150,player?980:1130) && range(b.width,1,220)
            && range(b.height,1,150) && b.grade>=1&&b.grade<=4 && b.state>=0&&b.state<=(player?4:1)
            && (!player || (range(b.speed,0,100)&&range(b.ySpeed,0,100)&&b.x+b.width<=1900&&b.y+b.height<=980));
    }
    public Decision advise(Body p,List<Body> fish,boolean guarded) {
        if (!valid(p,true)||fish==null||fish.size()>200) return reject("invalid_input");
        List<Body> live=new ArrayList<Body>(), threats=new ArrayList<Body>();
        for (Body f:fish) {
            if (!valid(f,false)) return reject("invalid_input");
            if (f.state==1) { live.add(f); if (f.grade<p.grade) threats.add(f); }
        }
        if (p.state!=1) return reject("inactive");
        List<Body> targets=threats.isEmpty()?live:threats;
        if (targets.isEmpty()) return reject("empty_scene");
        Body target=Collections.min(targets,(a,b)->compareTarget(a,b,p));
        double[] features={(target.x-p.x)/1900,(target.y-p.y)/1900,threats.isEmpty()?-2:2};
        double[] distances={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};
        int[] indices={-1,-1,-1};
        for (int i=0;i<samples.size();i++) {
            double d=0; for (int j=0;j<3;j++) { double delta=features[j]-samples.get(i)[j]; d+=delta*delta; }
            for (int k=0;k<3;k++) if (d<distances[k]) {
                for (int z=2;z>k;z--) { distances[z]=distances[z-1]; indices[z]=indices[z-1]; }
                distances[k]=d; indices[k]=i; break;
            }
        }
        int[] votes=new int[5]; for (int i:indices) votes[labels.get(i).ordinal()]++;
        Action raw=Action.LEFT;
        for (Action a:Action.values()) if (votes[a.ordinal()]>votes[raw.ordinal()]) raw=a;
        double confidence=votes[raw.ordinal()]/3.0;
        if (!guarded) return new Decision(raw,raw,confidence,"model");
        List<Action> safe=new ArrayList<Action>();
        for (Action a:Action.values()) {
            if (!legal(p,a)) continue;
            boolean collision=false;
            for (Body f:threats) if (overlaps(p,f,a)) { collision=true; break; }
            if (!collision) safe.add(a);
        }
        if (safe.contains(raw)) return new Decision(raw,raw,confidence,"model");
        if (safe.isEmpty()) return new Decision(Action.STAY,raw,confidence,"no_safe_action");
        Action best=safe.get(0); double bestDistance=clearance(p,threats,best);
        for (Action a:safe) { double d=clearance(p,threats,a); if(d>bestDistance) { best=a;bestDistance=d; } }
        return new Decision(best,raw,confidence,"safety_override");
    }
    private static int compareTarget(Body a,Body b,Body p) {
        double[] ka={square(a.x-p.x)+square(a.y-p.y),a.x,a.y,a.grade,a.width,a.height};
        double[] kb={square(b.x-p.x)+square(b.y-p.y),b.x,b.y,b.grade,b.width,b.height};
        for(int i=0;i<ka.length;i++) {int c=Double.compare(ka[i],kb[i]);if(c!=0)return c;}return 0;
    }
    private static double square(double x) { return x*x; }
    public static double nextX(Body p,Action a) { return p.x+(a==Action.LEFT?-p.speed:a==Action.RIGHT?p.speed:0); }
    public static double nextY(Body p,Action a) { return p.y+(a==Action.UP?-p.ySpeed:a==Action.DOWN?p.ySpeed:0); }
    /** Strict directional limits match Fish.move, including distinct vertical speed. */
    private static boolean legal(Body p,Action a) {
        switch(a) {
            case LEFT:return nextX(p,a)>0;
            case RIGHT:return nextX(p,a)+p.width<1900;
            case UP:return nextY(p,a)>0;
            case DOWN:return nextY(p,a)+p.height<980;
            default:return true;
        }
    }
    private static boolean overlaps(Body p,Body f,Action a) {
        double x=nextX(p,a),y=nextY(p,a);
        return x<f.x+f.width&&x+p.width>f.x&&y<f.y+f.height&&y+p.height>f.y;
    }
    private static double clearance(Body p,List<Body> threats,Action a) {
        if(threats.isEmpty())return 0;
        double min=Double.POSITIVE_INFINITY;
        for(Body f:threats)min=Math.min(min,square(nextX(p,a)+p.width/2-f.x-f.width/2)+square(nextY(p,a)+p.height/2-f.y-f.height/2));
        return min;
    }
}
