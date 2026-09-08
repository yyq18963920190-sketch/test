package myTest;

import fish.DaoJu;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DaoJuTest {
	DaoJu daoju;
    @Before
    public void setup() throws IOException {
        daoju = new DaoJu();
    }
	@Test
    public void testDaoJuConstructor() {
        // 检查宽度和高度的初始化
        assertEquals( 80, daoju.getWidth());
        assertEquals(80, daoju.getHeight());

        // 检查类型的初始化
        assertEquals( 1, daoju.getType());

        // 检查 x 和 y 是否在预期范围内
        assertTrue( daoju.getX() >= 0 && daoju.getX() < 1870);
        assertTrue( daoju.getY() >= 0 && daoju.getY() < 970);
    }
	@Test
    public void testRandomX() {
        int maxRangeX = 1870;
        boolean inRange = true;

        // 测试多次以确保返回的值始终在指定范围内
        for (int i = 0; i < 1000; i++) {
            int x = daoju.random_x();
            if (x < 0 || x >= maxRangeX) {
                inRange = false;
                break;
            }
        }

        assertTrue(inRange,"random_x() 生成的值应在 0 到 " + (maxRangeX - 1) + " 之间");
    }

    @Test
    public void testRandomY() {
        int maxRangeY = 970;
        boolean inRange = true;

        // 测试多次以确保返回的值始终在指定范围内
        for (int i = 0; i < 1000; i++) {
            int y = daoju.random_y();
            if (y < 0 || y >= maxRangeY) {
                inRange = false;
                break;
            }
        }

        assertTrue(inRange,"random_y() 生成的值应在 0 到 " + (maxRangeY - 1) + " 之间");
    }
	@Test
    public void testGetXAndSetX() {
        daoju.setX(100);
        assertEquals(100, daoju.getX());
    }

    @Test
    public void testGetYAndSetY() {
        daoju.setY(200);
        assertEquals( 200, daoju.getY());
    }

    @Test
    public void testGetWidthAndSetWidth() {
        daoju.setWidth(150);
        assertEquals(150, daoju.getWidth());
    }

    @Test
    public void testGetHeightAndSetHeight() {
        daoju.setHeight(300);
        assertEquals( 300, daoju.getHeight());
    }

    @Test
    public void testGetStateAndSetState() {
        daoju.setState(1);
        assertEquals( 1, daoju.getState());
    }

    @Test
    public void testGetTypeAndSetType() {
        daoju.setType(2);
        assertEquals( 2, daoju.getType());
    }
}