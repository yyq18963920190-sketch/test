package fish.ai;

import fish.*;
import handoff.support.AbTestBase;
import org.junit.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.Assert.*;

/** Integration assertions execute on EDT using the same UI/controller/game loop as production. */
public class AiIntegrationTest extends AbTestBase {
    private Fish prey(float x,float y,int grade){
        Fish f=new Fish();f.setX(x);f.setY(y);f.setWidth(55);f.setHeight(30);
        f.setGrade(grade);f.setState(1);f.setSpeed(0);return f;
    }
    private FishUI scene(){
        FishUI ui=uiFor(playerAt(800,400));
        ui.getFishList().add(prey(1100,400,4));return ui;
    }
    @Test public void modelLoadsAll1600Samples()throws Exception{assertEquals(1600,new AiAdvisor().sampleCount());}
    @Test public void missingModelFailsExplicitly()throws Exception{
        try{new AiAdvisor((InputStream)null);fail("missing model accepted");}catch(IOException expected){assertTrue(expected.getMessage().contains("missing"));}
    }
    @Test public void corruptModelFailsExplicitly()throws Exception{
        for(String s:Arrays.asList("bad","deep-sea-knn-v1\tk=3\nNaN\t0\t2\tLEFT\n","deep-sea-knn-v1\tk=3\n")){
            try{new AiAdvisor(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));fail("bad model accepted");}
            catch(IOException expected){assertNotNull(expected.getMessage());}
        }
    }
    @Test public void defaultModeIsOff(){assertEquals(AiController.Mode.OFF,scene().getAiController().getMode());}
    @Test public void offPreservesManualMovement(){
        FishUI ui=scene();Fish p=ui.getMyFish();p.setLeft(true);new GameLoop(ui).step();assertEquals(791,p.getX(),EPS);
    }
    @Test public void hintPredictsWithoutTakingControl(){
        FishUI ui=scene();ui.getAiController().setMode(AiController.Mode.HINT,ui.getMyFish());
        new GameLoop(ui).step();assertEquals(800,ui.getMyFish().getX(),EPS);assertTrue(ui.getAiController().getStatus().contains("RIGHT"));
    }
    @Test public void autoActuallyMovesFish(){
        FishUI ui=scene();ui.getAiController().setMode(AiController.Mode.AUTO,ui.getMyFish());
        new GameLoop(ui).step();assertEquals(809,ui.getMyFish().getX(),EPS);
    }
    @Test public void modeSwitchClearsOldKeys(){
        FishUI ui=scene();ui.getMyFish().setLeft(true);ui.getMyFish().setUp(true);
        ui.getAiController().setMode(AiController.Mode.AUTO,ui.getMyFish());
        assertFalse(ui.getMyFish().isLeft());assertFalse(ui.getMyFish().isUp());
    }
    @Test public void buttonCycleCoversAllModes(){
        FishUI ui=scene();ui.cycleAiMode();assertEquals(AiController.Mode.HINT,ui.getAiController().getMode());
        ui.cycleAiMode();assertEquals(AiController.Mode.AUTO,ui.getAiController().getMode());
        ui.cycleAiMode();assertEquals(AiController.Mode.OFF,ui.getAiController().getMode());
    }
    @Test public void actualBoundaryBlocksUnsafeSuggestion(){
        FishUI ui=scene();Fish p=ui.getMyFish();p.setX(0);ui.getFishList().clear();ui.getFishList().add(prey(300,400,1));
        ui.getAiController().setMode(AiController.Mode.AUTO,p);new GameLoop(ui).step();assertTrue(p.getX()>=0);assertFalse(p.isLeft());
    }
    @Test public void strictFishMoveBoundaryIsRespected(){
        FishUI ui=scene();Fish p=ui.getMyFish();p.setX(9);ui.getFishList().clear();ui.getFishList().add(prey(300,400,1));
        ui.getAiController().setMode(AiController.Mode.AUTO,p);
        assertNotEquals(AiAdvisor.Action.LEFT,ui.getAiController().tick(p,ui.getFishList()).action);
    }
    @Test public void verticalSpeedUsesActualFishValue(){
        FishUI ui=scene();Fish p=ui.getMyFish();p.setY_speed(18);ui.getFishList().clear();ui.getFishList().add(prey(800,700,4));
        ui.getAiController().setMode(AiController.Mode.AUTO,p);new GameLoop(ui).step();assertEquals(418,p.getY(),EPS);
    }
    @Test public void verticalBoundaryUsesVerticalSpeed(){
        FishUI ui=scene();Fish p=ui.getMyFish();p.setY(910);p.setY_speed(30);ui.getFishList().clear();ui.getFishList().add(prey(800,600,1));
        ui.getAiController().setMode(AiController.Mode.AUTO,p);
        assertNotEquals(AiAdvisor.Action.DOWN,ui.getAiController().tick(p,ui.getFishList()).action);
    }
    @Test public void pauseStopsAllTimersAndMotion(){
        FishUI ui=scene();GameLoop loop=new GameLoop(ui);loop.start();assertTrue(loop.isRunning());
        ui.getMyFish().setRight(true);loop.pause();assertFalse(loop.isRunning());assertFalse(ui.getMyFish().isRight());
    }
    @Test public void resumeWorksAfterPause(){
        GameLoop loop=new GameLoop(scene());try{loop.start();loop.pause();loop.resume();assertTrue(loop.isRunning());}finally{loop.pause();}
    }
    @Test public void terminalPlayerDoesNotMove(){
        FishUI ui=scene();ui.getMyFish().setState(0);ui.getMyFish().setRight(true);new GameLoop(ui).step();assertEquals(800,ui.getMyFish().getX(),EPS);
    }
    @Test public void emptySceneAutoClearsOldAction(){
        FishUI ui=scene();ui.getFishList().clear();ui.getAiController().setMode(AiController.Mode.AUTO,ui.getMyFish());
        ui.getMyFish().setRight(true);new GameLoop(ui).step();assertEquals(800,ui.getMyFish().getX(),EPS);
    }
    @Test public void timerRollsSecondsOver(){FishUI ui=scene();ui.setM(10);ui.setS(59);new GameLoop(ui).second();assertEquals("11:00",ui.gameTime());}
    @Test public void allDirectionFlagsAreExclusive(){
        Fish p=playerAt(800,400);
        for(AiAdvisor.Action action:AiAdvisor.Action.values()){
            p.setLeft(true);p.setRight(true);p.setUp(true);p.setDown(true);AiController.apply(p,action);
            int active=(p.isLeft()?1:0)+(p.isRight()?1:0)+(p.isUp()?1:0)+(p.isDown()?1:0);
            assertEquals(action==AiAdvisor.Action.STAY?0:1,active);
        }
    }
    @Test public void uiPaintsHeadlesslyWithModelStatus()throws Exception{
        FishUI ui=scene();ui.cycleAiMode();ui.getAiController().tick(ui.getMyFish(),ui.getFishList());ui.refreshAiStatus();
        java.awt.image.BufferedImage image=new java.awt.image.BufferedImage(1900,1000,java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g=image.createGraphics();try{ui.paint(g);}finally{g.dispose();}
        assertNotEquals(0,image.getRGB(500,300)&0xffffff);
        String preview=System.getProperty("ai.preview.path");
        if(preview!=null)javax.imageio.ImageIO.write(image,"png",new File(preview));
    }
}
