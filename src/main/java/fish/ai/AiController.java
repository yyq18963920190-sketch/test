package fish.ai;

import fish.Fish;
import java.io.IOException;
import java.util.*;

/** Used by the real game loop and headless integration tests. No subprocesses. */
public final class AiController {
    public enum Mode { OFF, HINT, AUTO }
    private Mode mode=Mode.OFF;
    private AiAdvisor advisor;
    private String status;
    public AiController() {
        try { advisor=new AiAdvisor();status="AI模型就绪："+advisor.sampleCount()+"条训练样本"; }
        catch(IOException ex) { status="AI不可用："+ex.getMessage(); }
    }
    public boolean available(){return advisor!=null;}
    public Mode getMode(){return mode;}
    public String getStatus(){return status;}
    public void setMode(Mode next,Fish player){
        mode=available()?next:Mode.OFF;
        clear(player);
    }
    public void cycle(Fish player){ setMode(Mode.values()[(mode.ordinal()+1)%3],player); }
    public AiAdvisor.Decision tick(Fish player,List<Fish> fish){
        if(mode==Mode.OFF||advisor==null)return AiAdvisor.reject("disabled");
        List<AiAdvisor.Body> bodies=new ArrayList<AiAdvisor.Body>();
        if(fish!=null) for(Fish f:fish) bodies.add(f==null?null:AiAdvisor.Body.of(f));
        AiAdvisor.Decision d=advisor.advise(player==null?null:AiAdvisor.Body.of(player),fish==null?null:bodies,true);
        if(mode==Mode.AUTO)apply(player,d.action);
        status=String.format(Locale.ROOT,"建议 %s | 模型 %s | 投票 %.2f | %s",d.action,d.raw,d.confidence,d.reason);
        return d;
    }
    public static void clear(Fish p){if(p!=null){p.setLeft(false);p.setRight(false);p.setUp(false);p.setDown(false);}}
    public static void apply(Fish p,AiAdvisor.Action a){
        clear(p);if(p==null||p.getState()!=1)return;
        switch(a){
            case LEFT:p.setLeft(true);p.setDirection(1);break;
            case RIGHT:p.setRight(true);p.setDirection(0);break;
            case UP:p.setUp(true);break;
            case DOWN:p.setDown(true);break;
            default:break;
        }
    }
}
