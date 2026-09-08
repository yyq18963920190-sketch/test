package myTest;

import fish.ImagePool;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ImagePoolTest {

	private ImagePool imagePoolUnderTest;

	@Before
	public void setUp() {
		imagePoolUnderTest = new ImagePool();
	}

	@Test
	public void testImagesLoaded() {
		// 检查前11张图片是否加载成功
		for (int i = 0; i < 11; i++) {
			Image img = imagePoolUnderTest.getImage(i);
			assertNotNull("Image " + i + ".png should be loaded.", img);
		}

		// 检查 "no" 前缀的7张图片是否加载成功
		for (int i = 1; i <= 7; i++) {
			Image img = imagePoolUnderTest.getImage(i + 10);
			assertNotNull("Image no" + i + ".png should be loaded.", img);
		}

		// 检查单独的 BG1 和 daoju 图片是否加载成功
		assertNotNull("Image BG1.png should be loaded.", imagePoolUnderTest.getImage(18));
		assertNotNull("Image daoju.png should be loaded.", imagePoolUnderTest.getImage(19));
	}

	@Test
    public void testGetImage() {
        // 测试获取特定图像
        Image img0 = imagePoolUnderTest.getImage(0);
        assertNotNull("Image 0 should be loaded", img0);

        Image img1 = imagePoolUnderTest.getImage(1);
        assertNotNull("Image 1 should be loaded", img1);

		Image imgInvalid1 = imagePoolUnderTest.getImage(-1);
        assertNotNull("Image 1 should be loaded", imgInvalid1);
        // 测试获取不存在的索引
        Image imgInvalid2 = imagePoolUnderTest.getImage(20); // 应该返回 null
        assertNull("Image 20 should not exist", imgInvalid2);
    }
}
