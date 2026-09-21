package fish;

import javax.swing.Timer;
import fish.ai.AiController;

/** All scene mutations and AI consumption happen serially on Swing's EDT. */
public final class GameLoop {
    private final FishUI ui;
    private final Timer movement,spawn,clock;
    private int itemSeconds,boostSeconds;
    public GameLoop(FishUI ui){
        this.ui=ui;
        movement=new Timer(20,e->step());
        spawn=new Timer(1000,e->{if(active()&&ui.getFishList().size()<9)ui.getFishList().add(new Fish());});
        clock=new Timer(1000,e->second());
    }
    private boolean active(){return ui.getMyFish()!=null&&ui.getMyFish().getState()==1;}
    public void start(){itemSeconds=0;boostSeconds=0;resume();}
    public void resume(){if(active()){movement.start();spawn.start();clock.start();}}
    public void pause(){movement.stop();spawn.stop();clock.stop();AiController.clear(ui.getMyFish());}
    public boolean isRunning(){return movement.isRunning();}
    public void step(){
        if(!active()){pause();return;}
        ui.otherFishMove();ui.otherFishRemove();
        ui.getAiController().tick(ui.getMyFish(),ui.getFishList());
        ui.getMyFish().move();
        ui.collisionTest();
        ui.refreshAiStatus();ui.repaint();
        if(!active())pause();
    }
    public void second(){
        if(!active()){pause();return;}
        ui.setS(ui.getS()+1);
        if(++itemSeconds%10==0)ui.setDaoJu(new DaoJu());
        if(ui.getMyFish().getSpeed()>10){
            itemSeconds=0;
            if(++boostSeconds>=4){ui.getMyFish().setSpeed(9);ui.getMyFish().setY_speed(9);boostSeconds=0;}
        }
    }
}
