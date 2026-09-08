package myTest;

import Data.excelImportTest;
import fish.Fish;
import fish.FishUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CollisionTest {

    FishUI aFishUI;

    @Parameterized.Parameter(0)
    public int myFishX;
    @Parameterized.Parameter(1)
    public int myFishY;
    @Parameterized.Parameter(2)
    public int myFishGrade;
    @Parameterized.Parameter(3)
    public int myFishWidth;
    @Parameterized.Parameter(4)
    public int myFishHeight;
    @Parameterized.Parameter(5)
    public int myFishScore;
    @Parameterized.Parameter(6)
    public int fishX;
    @Parameterized.Parameter(7)
    public int fishY;
    @Parameterized.Parameter(8)
    public int fishGrade;
    @Parameterized.Parameter(9)
    public int fishWidth;
    @Parameterized.Parameter(10)
    public int fishHeight;
    @Parameterized.Parameter(11)
    public int fishScore;
    @Parameterized.Parameter(12)
    public int expectedStatus;
    @Parameterized.Parameter(13)
    public int expectedScore;

    @Parameterized.Parameters(name="{index}:testGetArea[{0}]={1}")
    public static Collection testData()
    {
        return Arrays.asList(excelImportTest.excelDataTransform("FishUITestData", 1));
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
    public void collisionTest() {
        Fish myFish = new Fish();
        Fish otherFish = new Fish();

        //注入主角小鱼的属性
        myFish.setX(myFishX);
        myFish.setY(myFishY);
        myFish.setGrade(myFishGrade);
        myFish.setWidth(myFishWidth);
        myFish.setHeight(myFishHeight);
        myFish.setScore(myFishScore);
        System.out.println();
        //注入敌人小鱼的属性
        otherFish.setX(fishX);
        otherFish.setY(fishY);
        otherFish.setGrade(fishGrade);
        otherFish.setWidth(fishWidth);
        otherFish.setHeight(fishHeight);
        otherFish.setScore(fishScore);

        aFishUI.setMyFish(myFish);
        ArrayList<Fish> otherFishList = new ArrayList<Fish>();
        otherFishList.add(otherFish);
        aFishUI.setFishList(otherFishList);
        aFishUI.collisionTest();

        System.out.println("expect: " + expectedStatus + "\tresult: " + myFish.getState());
        System.out.println("expect: " + expectedScore + "\tresult: " + myFish.getScore());
        System.out.println();

        assertEquals(myFish.getState(), expectedStatus);
        assertEquals(myFish.getScore(), expectedScore);
    }
}
