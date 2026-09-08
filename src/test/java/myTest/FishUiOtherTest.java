package myTest;

import fish.Fish;
import fish.FishUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


import static org.junit.Assert.*;

public class FishUiOtherTest {

    FishUI aFishUI;
    @Before
    public void setup(){
        aFishUI = new FishUI();
    }

    @After
    public void teardown(){
        aFishUI = null;
    }

    /**
     * 游戏失败测试
     */
    @Test
    public void  fail() {
        Fish aFish = new Fish(true);
        aFishUI.setMyFish(aFish);
        aFishUI.fail();
        assertTrue(aFishUI.getMyFish().getState() == 0);
    }

    /**
     * 游戏得分测试
     */
    @Test
    public void myScoreToString1() {
        Fish aFish = new Fish();
        aFish.setScore(45);
        aFishUI.setMyFish(aFish);
        assertTrue(aFishUI.myScoreToString().equals("your score is 45"));
    }

    @Test
    public void myScoreToString2() {
        Fish aFish = new Fish();
        aFish.setScore(12);
        aFishUI.setMyFish(aFish);
        assertTrue(aFishUI.myScoreToString().equals("your score is 12"));
    }

    @Test
    public void myScoreToString3() {
        Fish aFish = new Fish();
        aFish.setScore(50);
        aFishUI.setMyFish(aFish);
        assertTrue(aFishUI.myScoreToString().equals("your score is 50"));
    }

    /**
     * 游戏时间测试
     */
    @Test
    public void gameTime_1() {
        aFishUI.setS(11);
        aFishUI.setM(10);
        assertTrue(aFishUI.gameTime().equals("10:11"));
    }

    @Test
    public void gameTime_2() {
        aFishUI.setS(5);
        aFishUI.setM(50);
        assertTrue(aFishUI.gameTime().equals("50:05"));
    }

    @Test
    public void gameTime_3() {
        aFishUI.setS(27);
        aFishUI.setM(20);
        assertTrue(aFishUI.gameTime().equals("20:27"));
    }

    /**
     * 存档记录测试
     */
    @Test
    public void RecordDataTest(){
        int score = 0, m = 0, s = 0;
        int[] data;
        Random random = new Random();
        for (int i = 0; i < 3; i++)
        {
            score = random.nextInt(50) + 1;
            m = random.nextInt(60) + 1;
            s = random.nextInt(60) + 1;

            try {
                aFishUI.setRecordData(new FileWriter("src/main/resources/record"+i+".txt"),score+","+m+":"+s);
                data = aFishUI.getRecordData(new FileReader("src/main/resources/record"+i+".txt"));

                assertEquals(data[0], score);
                assertEquals(data[1], m);
                assertEquals(data[2], s);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (NoSuchFieldException e) {
	            throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
	            throw new RuntimeException(e);
            }
        }
    }
}
