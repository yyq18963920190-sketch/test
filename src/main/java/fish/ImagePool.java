package fish;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

/** Cache immutable images once; load from classpath for both source and JAR launches. */
public class ImagePool {
    private static final Image[] SHARED=load();
    private Image[] images=SHARED;
    private static Image[] load(){
        Image[] all=new Image[20];
        for(int i=0;i<20;i++){
            String name=i<=10?i+".png":i<=17?"no"+(i-10)+".png":i==18?"BG1.png":"daoju.png";
            URL url=ImagePool.class.getResource("/resource/"+name);
            if(url==null)throw new IllegalStateException("Missing image: "+name);
            try{all[i]=ImageIO.read(url);if(all[i]==null)throw new IOException("Unsupported image");}
            catch(IOException ex){throw new IllegalStateException("Cannot load "+name,ex);}
        }
        return all;
    }
    public ImagePool(){}
    public Image getImage(int i){return i<0||i>=images.length?null:images[i];}
    public void setImages(Image[] images){this.images=images;}
}
