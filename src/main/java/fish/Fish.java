package fish;

import java.awt.*;
import java.util.Random;

public class Fish {
    private float x;
    private float y;
    private boolean isMyFish;
    private int score;
    private int grade;      //等级
    private float speed;      //速度
    private float y_speed;
    private int direction;  //01 左右  23 上下
    private int width;      //图片大小
    private int height;
    private Image image;
    private int state;  //0 失败 1 存活 2 胜利 3 重新开始 4 吃道具
    private ImagePool imagePool=new ImagePool();
    private boolean up=false;
    private boolean down=false;
    private boolean left=false;
    private boolean right=false;

    public Fish(float x, int grade , float speed,int direction){
        this.x=x;
        this.grade = grade;
        this.speed = speed;
        this.direction = direction;
    }

    public Fish() {
        this.isMyFish = false;
        this.state=1;
        this.grade = this.rondowGrade();
        this.direction = this.rondowDirection();
        switch (grade) {
            case 1:
                this.score = 8;
                this.width = 220;
                this.height = 120;
                if (direction==1){
                    this.image=imagePool.getImage(0);
                }
                else this.image=imagePool.getImage(1);
                break;
            case 2:
                this.score = 4;
                this.width = 120;
                this.height = 84;
                if (direction==1){
                    this.image=imagePool.getImage(2);
                }
                else this.image=imagePool.getImage(3);
                break;
            case 3:
                this.score = 2;
                this.width = 90;
                this.height = 50;
                if (direction==1){
                    this.image=imagePool.getImage(4);
                }
                else this.image=imagePool.getImage(5);
                break;
            case 4:
                this.score = 1;
                this.width = 55;
                this.height = 30;
                if (direction==1){
                    this.image=imagePool.getImage(6);
                }
                else this.image=imagePool.getImage(7);
                break;
        }
        if (this.direction == 0) {
            this.x = -100;
            this.y = this.rondowY();
        }else {
            this.x=2000;
            this.y=this.rondowY();
        }
        this.speed = this.rondowSpeed();

    }

    public Fish(boolean isMyFish){
        this.x=800;
        this.y=500;
        this.isMyFish=isMyFish;
        this.score=0;
        this.speed=9;
        this.y_speed=9;
        this.grade=4;
        this.state=1;
        this.direction=0;
        this.width=77;
        this.height=53;
        this.image=imagePool.getImage(8);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isMyFish() {
        return isMyFish;
    }

    public void setMyFish(boolean myFish) {
        isMyFish = myFish;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public ImagePool getImagePool() {
        return imagePool;
    }

    public void setImagePool(ImagePool imagePool) {
        this.imagePool = imagePool;
    }

    public float getY_speed() {
        return y_speed;
    }

    public void setY_speed(float y_speed) {
        this.y_speed = y_speed;
    }

    public int rondowDirection() {
        Random random=new Random();
        return random.nextInt((10000) + 1) % 2;
    }

    public int rondowSpeed() {
        Random random=new Random();
        return random.nextInt((10000) + 1) % 5 + 4;
    }

    public int rondowGrade() {
        Random random=new Random();
        return random.nextInt((10000) + 1) % 4 + 1;
    }

    public int rondowY() {
        Random random=new Random();
        int i = random.nextInt((10000) + 1)%1000;
        if (i < 800) {
            return i;
        } else return i - 200;
    }

    public void move(){
        if(x+speed+width<1900&&right){
            x+=speed;
        }else if (x-speed>0&&left){
            x-=speed;
        }
        if(y+y_speed+height<980&&down){
            y+=y_speed;
        }else if (y-y_speed>0&&up){
            y-=y_speed;
        }
        if(direction==1){
            image=imagePool.getImage(8);
        }else if (direction==0){
            image=imagePool.getImage(9);
        }
    }
}
