package fish;

import java.awt.*;
import java.util.Random;

public class DaoJu {
    private int x;
    private int y;
    private int width;
    private int height;
    private int state;  // 1 存在 0 被吃或时间到消失
    private int type; //  1 加速 0 护盾

    public DaoJu(){
        this.width=80;
        this.height=80;
        this.type=1;
        x=random_x();
        y=random_y();
    }

    public int random_x(){
        Random random=new Random();
        return random.nextInt((10000) + 1)%1870;
    }

    public int random_y(){
        Random random=new Random();
        return random.nextInt((10000) + 1)%970;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
