package fish.ai;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Line protocol for cross-language integration tests, not the game runtime. */
public final class AdvisorCli {
    private static AiAdvisor.Body body(String text,boolean player){
        String[] s=text.split(",",-1);
        if(s.length!=(player?8:6))throw new IllegalArgumentException("shape");
        return new AiAdvisor.Body(Double.parseDouble(s[0]),Double.parseDouble(s[1]),Double.parseDouble(s[2]),Double.parseDouble(s[3]),
            player?Double.parseDouble(s[4]):0,player?Double.parseDouble(s[5]):0,
            Integer.parseInt(s[player?6:4]),Integer.parseInt(s[player?7:5]));
    }
    public static void main(String[] args)throws Exception{
        AiAdvisor model=new AiAdvisor();
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8));
        String line;
        while((line=in.readLine())!=null){
            AiAdvisor.Decision d;
            try{
                String[] s=line.split("\\|",-1);
                if(s.length!=3||(!s[0].equals("RAW")&&!s[0].equals("GUARDED")))throw new IllegalArgumentException("request");
                List<AiAdvisor.Body> fish=new ArrayList<AiAdvisor.Body>();
                if(!s[2].isEmpty())for(String f:s[2].split(";",-1))fish.add(body(f,false));
                d=model.advise(body(s[1],true),fish,s[0].equals("GUARDED"));
            }catch(IllegalArgumentException ex){d=AiAdvisor.reject("invalid_input");}
            System.out.println(d.protocol());
        }
    }
}
