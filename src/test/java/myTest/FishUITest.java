package myTest;

import fish.DaoJu;
import fish.Fish;
import fish.FishUI;
import fish.ImagePool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


public class FishUITest {
    FishUI aFishUI;
    @Before
    public void setup() throws IOException {
        aFishUI = new FishUI();
    }

    @After
    public void teardown(){
        aFishUI = null;
    }
    @Test
    public void testGetRangkingFile() {
        String content = "0:11\n1:5\n2:0"; // 示例内容
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src/main/resources/record/排行榜.txt"), "UTF-8"))) {
            writer.write(content);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int[][] expectedData = {
            {0, 11},
            {1, 5},
            {2, 0}
        };

        // 调用方法并获取返回数据
        int[][] actualData = aFishUI.getRangkingFile();

        // 使用 assertArrayEquals 比较数组
        for (int i = 0; i < expectedData.length; i++) {
            assertArrayEquals(expectedData[i], actualData[i]);
        }
    }

     @Test
    public void testSetGameButtonVisibility() {
        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton("Start");
        buttons[1] = new JButton("Stop");
        aFishUI.setGameButton(buttons);

        // Initially, set all buttons to be visible
        aFishUI.setGameButton(true);
        for (JButton button : aFishUI.getGameButton()) {
            assertTrue("按钮应可见", button.isVisible());
        }

        // Now set all buttons to be invisible
        aFishUI.setGameButton(false);
        for (JButton button : aFishUI.getGameButton()) {
            assertFalse("按钮应不可见", button.isVisible());
        }
    }
    @Test
    public void testGetSetFishList() {
        ArrayList<Fish> fishList = new ArrayList<>();
        Fish fish = new Fish(100.0f, 1, 5.0f, 0);
        fishList.add(fish);
        aFishUI.setFishList(fishList);

        assertEquals("获取的鱼列表不符合预期", fishList, aFishUI.getFishList());
    }

    @Test
    public void testGetSetMyFish() {
        Fish myFish = new Fish(200.0f, 1, 5.0f, 0);
        aFishUI.setMyFish(myFish);
        assertEquals("获取的我的鱼不符合预期", myFish, aFishUI.getMyFish());
    }

    @Test
    public void testGetSetImages() {
        ImagePool images = new ImagePool(); // 如果需要具体的初始化，请调整
        aFishUI.setImages(images);
        assertEquals("获取的图像池不符合预期", images, aFishUI.getImages());
    }

    @Test
    public void testGetSetGameButton() {
        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton("Start");
        buttons[1] = new JButton("Stop");
        aFishUI.setGameButton(buttons);

        assertEquals("获取的游戏按钮数组不符合预期", buttons, aFishUI.getGameButton());
    }

    @Test
    public void testSetMandS() {
        int times [][] =new int[][]{
                {20,-1},
                {20,20},
                {20,60},
                {-1,20},
                {60,20}
        };
        int timesExpected [][] =new int[][]{
                {19,59},
                {20,20},
                {21,00},
                {0,0},
                {60,20}
        };
        for(int i = 0; i < times.length; i++){
            aFishUI.setM(times[i][0]);
            aFishUI.setS(times[i][1]);
            assertEquals( "获取的 m 不符合预期",timesExpected[i][0], aFishUI.getM());
            assertEquals( "获取的 s 不符合预期",timesExpected[i][1], aFishUI.getS());
        }
    }


    @Test
    public void testGetSetDaoJu() {
        DaoJu daoJu = new DaoJu(); // 确保 DaoJu 类有一个无参构造函数
        aFishUI.setDaoJu(daoJu);

        assertEquals("获取的道具不符合预期", daoJu, aFishUI.getDaoJu());
    }
    @Test
    public void testOtherFishMove() {
        // 定义测试数据：每个数组包含[初始 x 坐标, 速度, 移动方向, 预期 x 坐标]
        Object[][] testCases = {
            {200.0, 5.0, 0, 205.0,1},       // 原 otherFishMove_1
            {200.0, 5.0, 1, 195.0,1},       // 原 otherFishMove_2
            {2001.0, 5.0, 0, 2006.0,0},     // 原 otherFishMove_3
            {-201.0, 5.0, 1, -206.0,0}      // 原 otherFishMove_4
        };

        for (Object[] testCase : testCases) {
            // Arrange: 使用测试数据初始化 Fish 对象和鱼列表
            float initialX = ((Number) testCase[0]).floatValue();
            float speed = ((Number) testCase[1]).floatValue();
            int direction = (int) testCase[2];
            float expectedX = ((Number) testCase[3]).floatValue();
            int expectedState = (int) testCase[4];

            Fish aFish = new Fish(initialX, 1, speed, direction);
            aFish.setState(1);
            ArrayList<Fish> currentFishList = new ArrayList<>();
            currentFishList.add(aFish);
            aFishUI.setFishList(currentFishList);

            // Act: 执行鱼的移动操作
            aFishUI.otherFishMove();

            // Assert: 验证鱼的 x 坐标是否与预期一致
            assertEquals("鱼的 x 坐标不符合预期", expectedX, aFish.getX(), 0.001);
            assertEquals("鱼的状态不符合预期", expectedState, aFish.getState());
        }
    }

    @Test
    public void testOtherFishRemove() {
        ArrayList<Fish> fishList = new ArrayList<>();
        
        // 创建 Fish 对象并添加到列表
        fishList.add(new Fish()); // state 为 0，应该被移除
        fishList.add(new Fish()); // state 不为 0，不移除
        fishList.add(new Fish()); // state 不为 0，不移除
        fishList.get(0).setState(0);
        fishList.get(1).setState(1);
        fishList.get(2).setState(2);
        // 设置 fishList 到 aFishUI 中
        aFishUI.setFishList(fishList);
        // 执行移除操作
        aFishUI.otherFishRemove();

        // 验证列表中的对象数量
        assertEquals("fishList 应该包含 2 个元素", 2, aFishUI.getFishList().size());

        // 验证列表中的对象
        for (Fish fish : aFishUI.getFishList()) {
            assertNotEquals("fishList 中不应包含 state 为 0 的鱼", 0, fish.getState());
        }
    }





}
