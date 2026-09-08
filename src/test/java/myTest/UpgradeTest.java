package myTest;

import Data.excelImportTest;
import fish.Fish;
import fish.FishUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UpgradeTest {
    FishUI aFishUI;


    @Parameterized.Parameter(0)
    public int grade;
    @Parameterized.Parameter(1)
    public int width;
    @Parameterized.Parameter(2)
    public int height;
    @Parameterized.Parameter(3)
    public int score;
    @Parameterized.Parameter(4)
    public int predictedGrade;
    @Parameterized.Parameter(5)
    public int predictedWidth;
    @Parameterized.Parameter(6)
    public int predictedHeight;

    @Parameterized.Parameters(name="{index}:testGetArea[{0}]={1}")
    public static Collection testData()
    {
        return Arrays.asList(excelImportTest.excelDataTransform("FishUITestData", 0));
    }

    @Before
    public void setup(){
        aFishUI = new FishUI();
    }

    @After
    public void teardown(){
        aFishUI = null;
    }
    @Test
    public void upgradeTest(){

        Fish aFish = new Fish();
        aFish.setGrade(grade);
        aFish.setWidth(width);
        aFish.setHeight(height);//这里width和height都是乱写的 和真实不一样为了测试upgrade是不是正确运行
        aFish.setScore(score);
        aFishUI.setMyFish(aFish);
        aFishUI.upgrade(aFish);

        System.out.println("predictedGrade: " + predictedGrade + "\tresult: "+ aFish.getGrade());
        System.out.println("predictedWidth: " + predictedWidth + "\tresult: "+ aFish.getWidth());
        System.out.println("predictedHeight: " + predictedHeight + "\tresult: "+ aFish.getHeight());
        System.out.println();

        assertEquals(aFish.getGrade(), predictedGrade);
        assertEquals(aFish.getWidth(), predictedWidth);
        assertEquals(aFish.getHeight(), predictedHeight);
    }
}
