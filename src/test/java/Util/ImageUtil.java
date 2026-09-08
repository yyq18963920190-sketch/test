package Util;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class ImageUtil {

    // 将 Image 转换为 BufferedImage
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bufferedImage = new BufferedImage(
            img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    // 比较两个 Image 是否内容相同
    public static boolean imagesAreEqual(Image img1, Image img2) {
        BufferedImage bufferedImg1 = toBufferedImage(img1);
        BufferedImage bufferedImg2 = toBufferedImage(img2);

        if (bufferedImg1.getWidth() != bufferedImg2.getWidth() ||
            bufferedImg1.getHeight() != bufferedImg2.getHeight()) {
            return false;
        }

        for (int y = 0; y < bufferedImg1.getHeight(); y++) {
            for (int x = 0; x < bufferedImg1.getWidth(); x++) {
                if (bufferedImg1.getRGB(x, y) != bufferedImg2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}

