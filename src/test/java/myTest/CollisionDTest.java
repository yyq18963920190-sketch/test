package myTest;

import Data.excelImportTest;
import fish.DaoJu;
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
public class CollisionDTest {
    FishUI aFishUI;

    @Parameterized.Parameter(0)
    public int myFishX;
    @Parameterized.Parameter(1)
    public int myFishY;
    @Parameterized.Parameter(2)
    public int myFishWidth;
    @Parameterized.Parameter(3)
    public int myFishHeight;
    @Parameterized.Parameter(4)
    public int myFishSpeed;
    @Parameterized.Parameter(5)
    public int myFishSpeedY;
    @Parameterized.Parameter(6)
    public int propX;
    @Parameterized.Parameter(7)
    public int propY;
    @Parameterized.Parameter(8)
    public int expectedPropStatus;
    @Parameterized.Parameter(9)
    public int expectedSpeed;
    @Parameterized.Parameter(10)
    public int expectedSpeedY;

    @Parameterized.Parameters(name="{index}:testGetArea[{0}]={1}")
    public static Collection collisionTestData()
    {
        return Arrays.asList(excelImportTest.excelDataTransform("FishUITestData", 2));
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
    public void collisionDTest() {
        Fish myFish = new Fish();
        DaoJu prop = new DaoJu();

        //注入主角小鱼的属性
        myFish.setX(myFishX);
        myFish.setY(myFishY);
        myFish.setWidth(myFishWidth);
        myFish.setHeight(myFishHeight);
        myFish.setSpeed(myFishSpeed);
        myFish.setY_speed(myFishSpeedY);

        //注入道具的属性
        prop.setX(propX);
        prop.setY(propY);
        prop.setState(1);

        aFishUI.setMyFish(myFish);
        aFishUI.setDaoJu(prop);
        ArrayList fishList = new ArrayList<>();
        aFishUI.setFishList(fishList);
        aFishUI.collisionTest();

        System.out.println("expectedPropStatus: " + expectedPropStatus + "\tresult: " + prop.getState());
        System.out.println("expectedSpeed: " + expectedSpeed + "\tresult: " + myFish.getSpeed());
        System.out.println("expectedSpeedY: " + expectedSpeedY + "\tresult: " + myFish.getY_speed());
        System.out.println();

        assertEquals(prop.getState(), expectedPropStatus);
        assertEquals(myFish.getSpeed(), expectedSpeed,0.01);
        assertEquals(myFish.getY_speed(), expectedSpeedY,0.01);
    }
}

