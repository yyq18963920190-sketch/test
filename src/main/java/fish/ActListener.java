package fish;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ActListener implements ActionListener,KeyListener{
    private GameFrame gameFrame;
    private Fish myFish;
    private ArrayList<Fish> otherFishs;
    private DaoJu daoJu;

    private Thread myThread;
    private Thread otherFishThread;
    private Thread timeThread;
    private ImagePool imagePool = new ImagePool();
    private int m=0,s=0;

    public ActListener(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
//        myFish = new Fish(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getActionCommand();
        if (source.equals("开始游戏")) {
            beginGame();
        }
        else if (source.equals("选取存档")) {//还要向各个存档传值，画排行榜背景
            gameFrame.getRecordPanel().setVisible(true);
            changeButton(false);
//            gameFrame.getRecordPanel().getGraphics().drawImage(imagePool.getImages(18),0,0,600,600,null);//
//            gameFrame.getRecordPanel().repaint();
        }
        else if(source.equals("关闭存档")){
            gameFrame.getRecordPanel().setVisible(false);
            changeButton(true);
            if(myFish!=null){
                gameFrame.getMenuPanel().setVisible(true);
            }
        }
        else if (source.equals("record 1")){
           manipulateRecord(1);
           gameFrame.getRecordPanel().setVisible(false);
        }
        else if (source.equals("record 2")){
            manipulateRecord(2);
            gameFrame.getRecordPanel().setVisible(false);
        }
        else if (source.equals("record 3")){
            manipulateRecord(3);
            gameFrame.getRecordPanel().setVisible(false);
        }
        else if (source.equals("排行榜")) {
            gameFrame.setRanking();
            gameFrame.getRankingPanel().setVisible(true);
            changeButton(false);
        }
        else if (source.equals("关闭排行榜")){
            gameFrame.getRankingPanel().setVisible(false);
            changeButton(true);
        }
        else if(source.equals("继续游戏")){
            gameFrame.getMenuPanel().setVisible(false);
//            for(Fish f:gameFrame.getFishUI().getFishList()){
//                f.setSpeed(f.rondowSpeed());
//            }
            gameFrame.getFishUI().requestFocus(true);
            myThread.resume();
            otherFishThread.resume();
            timeThread.resume();
        }
        else if(source.equals("重新开始")){         //暂停时的重新开始
            back();
            timeThread.stop();
            beginGame();
            gameFrame.getMenuPanel().setVisible(false);
            m=0;
            s=0;
        }
        else if(source.equals("存档")){                           //还没写
            gameFrame.getMenuPanel().setVisible(false);
            gameFrame.getRecordPanel().setVisible(true);
        }
        else if(source.equals("返回主页面")){
            gameFrame.getMenuPanel().setVisible(false);
            back();
            changeButton(true);
        }
        else if (source.equals("重新开始游戏")){
            back();
//            changeButton(false);
            timeThread.stop();
            beginGame();
            m=0;
            s=0;
        }
        else if (source.equals("返回主界面")){
            changeButton(true);
            back();
//            changeButton(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //按键盘加力，直接作用于速度
    @Override
    public void keyPressed(KeyEvent e) {
        int i =e.getKeyCode();
        Fish fish = gameFrame.getFishUI().getMyFish();
        if(fish!=null){
            switch (i){
                case KeyEvent.VK_A:
                    fish.setLeft(true);
                    fish.setDirection(1);
                    break;
                case KeyEvent.VK_D:
                    fish.setRight(true);
                    fish.setDirection(0);
                    break;
                case KeyEvent.VK_W:
                    fish.setUp(true);
                    break;
                case KeyEvent.VK_S:
                    fish.setDown(true);
                    break;
                case KeyEvent.VK_P:
                    gameFrame.getMenuPanel().setVisible(true);
//                    for(Fish f:gameFrame.getFishUI().getFishList()){
//                        f.setSpeed(0);
//                    }
//                    fish.setSpeed(0);
//                    fish.setY_speed(0);
//                    gameFrame.getMenuPanel().requestFocus(true);
                    myThread.suspend();
                    otherFishThread.suspend();
                    timeThread.suspend();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int i =e.getKeyCode();
        Fish fish = gameFrame.getFishUI().getMyFish();
        if(fish!=null){
            switch (i){
                case KeyEvent.VK_A:
                    fish.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    fish.setRight(false);
                    break;
                case KeyEvent.VK_W:
                    fish.setUp(false);
                    break;
                case KeyEvent.VK_S:
                    fish.setDown(false);
                    break;

            }
        }
    }

    public void changeButton(boolean bool){
        for(JButton b : gameFrame.getButtons()) {
            b.setVisible(bool);
        }
    }

    public void beginGame(){
        myFish = new Fish(true);
        gameFrame.getFishUI().setMyFish(myFish);

        otherFishs = new ArrayList<Fish>();
        gameFrame.getFishUI().setFishList(otherFishs);

        gameFrame.getFishUI().requestFocus();
        changeButton(false);
        //游戏线程
        game();
        //鱼生成线程
        otherFish();
        //游戏计时
        time();
    }

    public void back(){
        if (myFish!=null){
            myFish.setState(3);
        }
        myFish = null;
        otherFishs.clear();
        gameFrame.getFishUI().setMyFish(null);

        gameFrame.getFishUI().setGameButton(false);
        gameFrame.getFishUI().repaint();
//        gameFrame.getGraphics().drawImage(imagePool.getImages(10),9,35,1900,1000,null);
    }

    public void game(){
        myThread = new Thread(() -> {
            while (true){
                if(!gameFrame.getFishUI().getFishList().isEmpty()){
                    gameFrame.getFishUI().otherFishMove();
                    gameFrame.getFishUI().otherFishRemove();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                gameFrame.getFishUI().repaint();
                if (myFish!=null){
                    myFish.move();
                }
                gameFrame.getFishUI().collisionTest();
                if (myFish==null||myFish.getState()!=1){
                    break;
                }
            }
        });
        myThread.setName("myThread");
        myThread.start();
    }

    public void otherFish(){
        otherFishThread = new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if(otherFishs.size()<=8){
                    otherFishs.add(new Fish());
                }
                if (myFish==null||myFish.getState()!=1){
                    break;
                }
            }
        });
        otherFishThread.setName("otherFishThread");
        otherFishThread.start();
    }

    public void time(){
        timeThread = new Thread(() -> {
            int i=0 ;    //道具生成计时
            int con = 0 ;  //道具持续时间
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                s+=1;
                if (s%60 == 0 ){
                    m+=1;
                }
                gameFrame.getFishUI().setM(m);
                gameFrame.getFishUI().setS(s);

                i+=1;
                if (i%10==0){
                    daoJu = new DaoJu();
                    gameFrame.getFishUI().setDaoJu(daoJu);
                }
                if (myFish.getSpeed()>10){
                    i = 0;                      //道具效果结束时开始计时下一个道具的生成
                    con+=1;
                    if (con==4){
                        myFish.setY_speed(9);
                        myFish.setSpeed(9);
                        con = 0;
                    }
                }

                if (myFish==null||myFish.getState()!=1){
                    break;
                }
            }
        });
        timeThread.setName("timeThread");
        timeThread.start();
    }

    public void manipulateRecord(int i){
        if (myFish==null){
            int[] data = new int[3];
            try {
                data = gameFrame.getFishUI().getRecordData(new FileReader("src/main/resources/record" + (i - 1) + ".txt"));
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (NoSuchFieldException e) {
	            throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
	            throw new RuntimeException(e);
            }
	        beginGame();
            myFish.setScore(data[0]);
            gameFrame.getFishUI().upgrade(myFish);
            m=data[1];
            s=data[2];
            gameFrame.getFishUI().setM(m);
            gameFrame.getFishUI().setS(s);
        }else {
            String score = myFish.getScore()+","+m+":"+s;
            try {
                gameFrame.getFishUI().setRecordData(new FileWriter("src/main/resources/record" + (i - 1) + ".txt"),score);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            gameFrame.setRecord();
        }
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public Fish getMyFish() {
        return myFish;
    }

    public void setMyFish(Fish myFish) {
        this.myFish = myFish;
    }

    public Thread getMyThread() {
        return myThread;
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }

    public Thread getOtherFishThread() {
        return otherFishThread;
    }

    public void setOtherFishThread(Thread otherFishThread) {
        this.otherFishThread = otherFishThread;
    }

    public Thread getTimeThread() {
        return timeThread;
    }

    public void setTimeThread(Thread timeThread) {
        this.timeThread = timeThread;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

//        public void gameFailed(){
//        if (!myFish.isAlive()){
//            if (otherFishThread.getState() == Thread.State.RUNNABLE ){
//                otherFishThread.interrupt();
//            }
//            if (timeThread.getState() == Thread.State.RUNNABLE){
//                timeThread.interrupt();
//            }
//            if (timeThread.getState()== Thread.State.TERMINATED&& otherFishThread.getState()== Thread.State.TERMINATED){
//                myThread.interrupt();
//            }
//        }
//    }
//
//    public boolean boundary(){
//        return true;
//    }
}
