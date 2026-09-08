package fish;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImagePool {
    private Image[] images;

    public  ImagePool(){
        images = new Image[20];
        for (int i=0;i<11;i++){
            try {
                images[i] = ImageIO.read(new File("src/main/resources/resource/"+i+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i =1;i<=7;i++){
            try {
                images[i+10] = ImageIO.read(new File("src/main/resources/resource/no"+i+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            images[18] = ImageIO.read(new File("src/main/resources/resource/BG1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            images[19] = ImageIO.read(new File("src/main/resources/resource/daoju.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image getImage(int i) {
        return images[i];
    }

    public void setImages(Image[] images) {
        this.images = images;
    }
}
