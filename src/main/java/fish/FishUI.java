package fish;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;


public class FishUI extends JPanel
{
    private ArrayList<Fish> fishList;
    private Fish myFish;
    private DaoJu daoJu;
    private ImagePool images = new ImagePool();
    private JButton[] gameButton = new JButton[2];
    private int m,s;

    public FishUI(){
        this.setBounds(0,0,1900,1000);
        this.setLayout(null);

        JButton renew = new JButton("重新开始游戏");
        JButton back = new JButton("返回主界面");
        renew.setBounds(600,670,300,70);
        back.setBounds(1000,670,300,70);
        this.add(renew);
        this.add(back);
        gameButton[0] = renew;
        gameButton[1] = back;
//        renew.addActionListener(gameFrame.getActListener());
//        back.addActionListener(gameFrame.getActListener());
        setGameButton(false);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font f = new Font("Serif",Font.BOLD,36);
        g.setFont(f);
        g.drawImage(images.getImage(10),0,0,1900,1000,null);
        if (daoJu!=null){
            g.drawImage(images.getImage(19), daoJu.getX(),daoJu.getY(),daoJu.getWidth(),daoJu.getHeight(),null);
        }
        if (fishList!=null){
            for(int i=0;i<fishList.size();i++){
                Image image=fishList.get(i).getImage();
                float x=fishList.get(i).getX();
                float y=fishList.get(i).getY();
                int width=fishList.get(i).getWidth();
                int height=fishList.get(i).getHeight();
                g.drawImage(image,(int)x,(int)y,width,height,null);
            }
        }
        if(myFish!=null){
            g.drawImage(myFish.getImage(), (int)myFish.getX(),(int)myFish.getY(),myFish.getWidth(),myFish.getHeight(),null);
            g.drawString(myScoreToString(),30,30);
            g.drawString(gameTime(),850,30);
        }
        if(myFish!=null&&myFish.getState()==0){
            g.drawImage(images.getImage(17),700,230,500,375,null);
        }
        if(myFish!=null&&myFish.getState()==2){
            g.drawImage(images.getImage(16),700,230,500,375,null);
        }
    }

    public ArrayList<Fish> getFishList() {
        return fishList;
    }

    public void setFishList(ArrayList<Fish> fishList) {
        this.fishList = fishList;
    }

    public Fish getMyFish() {
        return myFish;
    }

    public void setMyFish(Fish myFish) {
        this.myFish = myFish;
    }

    public ImagePool getImages() {
        return images;
    }

    public void setImages(ImagePool images) {
        this.images = images;
    }

    public JButton[] getGameButton() {
        return gameButton;
    }

    public void setGameButton(JButton[] gameButton) {
        this.gameButton = gameButton;
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

    public DaoJu getDaoJu() {
        return daoJu;
    }

    public void setDaoJu(DaoJu daoJu) {
        this.daoJu = daoJu;
    }

    public void setGameButton(boolean bool) {
        for (JButton b :gameButton){
            b.setVisible(bool);
        }
    }

    //其它鱼的移动以及出边界自动删除
    public void otherFishMove(){
        for(Fish fish:fishList){
            if(fish.getDirection()==0){
                fish.setX(fish.getX()+fish.getSpeed());
            }else {
                fish.setX(fish.getX()-fish.getSpeed());
            }
            if(fish.getDirection()==0&&fish.getX()>=2000){
                fish.setState(0);
            }else if(fish.getDirection()==1&&fish.getX()<=-200){
                fish.setState(0);
            }
        }
    }
    //删除鱼
    public void otherFishRemove(){
        for(Fish fish : fishList){
            if(fish.getState()==0){
                fishList.remove(fish);
                break;
            }
        }
    }

    //碰撞检测
    public void collisionTest(){
        for(Fish fish: fishList){
            if(fish.getX()<myFish.getX()&&fish.getY()<myFish.getY()){
                if(myFish.getX()-fish.getX()<fish.getWidth()-16/fish.getGrade()/fish.getGrade() &&myFish.getY()-fish.getY()<fish.getHeight()-16/fish.getGrade()/fish.getGrade()){
                    collision(myFish,fish);
                }
            }else if (fish.getX()<myFish.getX()&&fish.getY()>=myFish.getY()){
                if(myFish.getX()-fish.getX()<fish.getWidth()-16/fish.getGrade()/fish.getGrade() &&fish.getY()-myFish.getY()<myFish.getHeight()-16/fish.getGrade()/fish.getGrade()){
                    collision(myFish,fish);
                }
            }else if (fish.getX()>=myFish.getX()&&fish.getY()>=myFish.getY()){
                if(fish.getX()-myFish.getX()<myFish.getWidth()-16/fish.getGrade()/fish.getGrade() &&fish.getY()-myFish.getY()<myFish.getHeight()-16/fish.getGrade()/fish.getGrade()){
                    collision(myFish,fish);
                }
            }else if (fish.getX()>=myFish.getX()&&fish.getY()<myFish.getY()){
                if(fish.getX()-myFish.getX()<myFish.getWidth()-16/fish.getGrade()/fish.getGrade() &&myFish.getY()-fish.getY()<fish.getHeight()-16/fish.getGrade()/fish.getGrade()){
                    collision(myFish,fish);
                }
            }
        }

        if (daoJu!=null){
            if(daoJu.getX()<myFish.getX()&&daoJu.getY()<myFish.getY()){
                if(myFish.getX()-daoJu.getX()<daoJu.getWidth() &&myFish.getY()-daoJu.getY()<daoJu.getHeight()){
                    collision_daoju();
                }
            }else if (daoJu.getX()<myFish.getX()&&daoJu.getY()>=myFish.getY()){
                if(myFish.getX()-daoJu.getX()<daoJu.getWidth() &&daoJu.getY()-myFish.getY()<myFish.getHeight()){
                    collision_daoju();
                }
            }else if (daoJu.getX()>=myFish.getX()&&daoJu.getY()>=myFish.getY()){
                if(daoJu.getX()-myFish.getX()<myFish.getWidth() &&daoJu.getY()-myFish.getY()<myFish.getHeight()){
                    collision_daoju();
                }
            }else if (daoJu.getX()>=myFish.getX()&&daoJu.getY()<myFish.getY()){
                if(daoJu.getX()-myFish.getX()<myFish.getWidth() &&myFish.getY()-daoJu.getY()<daoJu.getHeight()){
                    collision_daoju();
                }
            }
        }

    }
    //碰撞效果 //yu
    public void collision(Fish myFish,Fish otherFish){
        if(myFish.getGrade()<=otherFish.getGrade()){
            myFish.setScore(myFish.getScore()+otherFish.getScore());
            upgrade(myFish);
            otherFish.setState(0);
        }else {
            fail();
        }
    }

    public void collision_daoju(){
        float speed = (float) (myFish.getSpeed()*1.5);
        myFish.setSpeed(speed);
        myFish.setY_speed(speed);
        daoJu.setState(0);
        daoJu = null;
    }

    public void upgrade(Fish myFish){
        int score=myFish.getScore();
        if(score<5){
            myFish.setGrade(4);
            myFish.setWidth(77);
            myFish.setHeight(53);
        } else if(score>=5&&score<20){
            myFish.setGrade(3);
            myFish.setWidth(100);
            myFish.setHeight(70);
        }else if(score>=20&&score<50){
            myFish.setGrade(2);
            myFish.setWidth(130);
            myFish.setHeight(90);
        }else if(score>=50){
            myFish.setGrade(1);
            myFish.setWidth(220);
            myFish.setHeight(150);
            win();
        }
    }

    public void win(){
        myFish.setState(2);
        for (JButton b : gameButton){
            b.setVisible(true);
        }

        int[][] data = getRangkingFile();
        for(int i=0;i<3;i++){
            if(data[i][0]!=0&&data[i][1]!=0){
                if (m>data[i][0]){
                    continue;
                }
                else if (m< data[i][0]){
                    int temp_m = data[i][0];
                    int temp_s = data[i][1];
                    data[i][0] = m;
                    data[i][1] = s;
                    m = temp_m;
                    s = temp_s;
                }
                else if (m == data[i][0]){
                    if (s<=data[i][1]){
                        int temp_m = data[i][0];
                        int temp_s = data[i][1];
                        data[i][0] = m;
                        data[i][1] = s;
                        m = temp_m;
                        s = temp_s;
                    }else continue;
                }
            }else {
                data[i][0]=m;
                data[i][1]=s;
                break;
            }

        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/record/排行榜.txt"));
            for (int i =0 ;i<3;i++){
                out.write(String.valueOf(data[i][0])+":"+String.valueOf(data[i][1])+"\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void fail(){
        myFish.setState(0);
        for (JButton b : gameButton){
            b.setVisible(true);
        }
    }

    public String myScoreToString(){
        String gewei = String.valueOf(myFish.getScore()%10);
        String shiwei = String.valueOf(myFish.getScore()/10%10);
        return "your score is "+ shiwei+gewei;
    }

    public String gameTime(){
        if (s<10){
            return String.valueOf(m)+":0"+String.valueOf(s);
        }
        else return String.valueOf(m)+":"+String.valueOf(s);
    }

    public int[][] getRangkingFile(){
        FileReader file = null ;
        int[][] data = new int[3][2];
        int i = 0;
        try {
            file = new FileReader("src/main/resources/record/排行榜.txt");
            BufferedReader reader = new BufferedReader(file);
            String line = null;
            while((line = reader.readLine())!=null){
                String[] temp= line.split(":");
                data[i][0] = Integer.parseInt(temp[0]);
                data[i][1] = Integer.parseInt(temp[1]);
                i+=1;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public int[] getRecordData(FileReader fileReader) throws NoSuchFieldException, IllegalAccessException {
        int[] data = new int[3];

        try {
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            String[] scoreAndTime = line.split(",");
            String[] time = scoreAndTime[1].split(":");
            data[0] = Integer.parseInt(scoreAndTime[0]);
            data[1] = Integer.parseInt(time[0]);
            data[2] = Integer.parseInt(time[1]);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setRecordData(FileWriter fileWriter,String scoreAndTime){
        BufferedWriter out = new BufferedWriter(fileWriter);
        try {
            out.write(scoreAndTime);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
