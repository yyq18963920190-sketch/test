package fish;

import fish.ai.AiController;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

/** Menu/keyboard adapter. Game state is changed on the Swing event thread. */
public class ActListener implements ActionListener,KeyListener {
    private final GameFrame gameFrame;
    private GameLoop loop;
    public ActListener(GameFrame frame){gameFrame=frame;}
    private FishUI ui(){return gameFrame.getFishUI();}
    private Fish player(){return ui().getMyFish();}
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case "开始游戏":beginGame();break;
            case "选取存档":gameFrame.getRecordPanel().setVisible(true);changeButton(false);break;
            case "关闭存档":
                gameFrame.getRecordPanel().setVisible(false);
                if(player()==null)changeButton(true);else gameFrame.getMenuPanel().setVisible(true);
                break;
            case "record 1":manipulateRecord(1);break;
            case "record 2":manipulateRecord(2);break;
            case "record 3":manipulateRecord(3);break;
            case "排行榜":gameFrame.setRanking();gameFrame.getRankingPanel().setVisible(true);changeButton(false);break;
            case "关闭排行榜":gameFrame.getRankingPanel().setVisible(false);changeButton(true);break;
            case "继续游戏":
                gameFrame.getMenuPanel().setVisible(false);if(loop!=null)loop.resume();ui().requestFocusInWindow();break;
            case "重新开始":case "重新开始游戏":back();beginGame();gameFrame.getMenuPanel().setVisible(false);break;
            case "存档":gameFrame.getMenuPanel().setVisible(false);gameFrame.getRecordPanel().setVisible(true);break;
            case "返回主页面":case "返回主界面":
                gameFrame.getMenuPanel().setVisible(false);back();changeButton(true);break;
            default:break;
        }
    }
    public void beginGame(){
        if(loop!=null)loop.pause();
        ui().setMyFish(new Fish(true));ui().setFishList(new ArrayList<Fish>());
        ui().setDaoJu(null);ui().setM(0);ui().setS(0);ui().setGameButton(false);
        changeButton(false);ui().requestFocusInWindow();
        loop=new GameLoop(ui());loop.start();
    }
    public void back(){
        if(loop!=null)loop.pause();
        if(player()!=null)player().setState(3);
        ui().setMyFish(null);ui().setFishList(new ArrayList<Fish>());ui().setDaoJu(null);
        ui().setGameButton(false);ui().repaint();
    }
    public void changeButton(boolean visible){for(JButton b:gameFrame.getButtons())b.setVisible(visible);}
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_H){ui().cycleAiMode();return;}
        Fish p=player();if(p==null||p.getState()!=1)return;
        if(e.getKeyCode()==KeyEvent.VK_P){
            if(loop!=null)loop.pause();gameFrame.getMenuPanel().setVisible(true);return;
        }
        if(loop==null||!loop.isRunning())return;
        int key=e.getKeyCode();
        if(key==KeyEvent.VK_A||key==KeyEvent.VK_D||key==KeyEvent.VK_W||key==KeyEvent.VK_S){
            if(ui().getAiController().getMode()==AiController.Mode.AUTO){
                ui().getAiController().setMode(AiController.Mode.HINT,p);ui().refreshAiStatus();
            }
        }
        switch(key){
            case KeyEvent.VK_A:p.setLeft(true);p.setDirection(1);break;
            case KeyEvent.VK_D:p.setRight(true);p.setDirection(0);break;
            case KeyEvent.VK_W:p.setUp(true);break;
            case KeyEvent.VK_S:p.setDown(true);break;
            default:break;
        }
    }
    public void keyReleased(KeyEvent e){
        Fish p=player();if(p==null)return;
        switch(e.getKeyCode()){
            case KeyEvent.VK_A:p.setLeft(false);break;
            case KeyEvent.VK_D:p.setRight(false);break;
            case KeyEvent.VK_W:p.setUp(false);break;
            case KeyEvent.VK_S:p.setDown(false);break;
            default:break;
        }
    }
    public void manipulateRecord(int slot){
        try{
            if(player()==null){
                int[] data;
                try(FileReader reader=new FileReader("src/main/resources/record"+(slot-1)+".txt")){
                    data=ui().getRecordData(reader);
                }
                beginGame();player().setScore(data[0]);ui().setM(data[1]);ui().setS(data[2]);ui().upgrade(player());
            }else{
                try(FileWriter writer=new FileWriter("src/main/resources/record"+(slot-1)+".txt")){
                    ui().setRecordData(writer,player().getScore()+","+ui().getM()+":"+ui().getS());
                }
                gameFrame.setRecord();gameFrame.getMenuPanel().setVisible(true);
            }
            gameFrame.getRecordPanel().setVisible(false);ui().requestFocusInWindow();
        }catch(IOException|ReflectiveOperationException|RuntimeException ex){
            JOptionPane.showMessageDialog(gameFrame,"存档操作失败："+ex.getMessage());
        }
    }
}
