package myTest;

import Data.excelImportTest;
import fish.Fish;
import fish.FishUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class WinTest {
    FishUI aFishUI;

    @Parameterized.Parameter(0)
    public int expectedStatus;
    @Parameterized.Parameter(1)
    public int minutes;
    @Parameterized.Parameter(2)
    public int second;
    @Parameterized.Parameter(3)
    public String rankingListBefore;
    @Parameterized.Parameter(4)
    public String rankingListAfter;



    @Parameterized.Parameters(name="{index}:testGetArea[{0}]={1}")
    public static Collection testData()
    {
        return Arrays.asList(excelImportTest.excelDataTransform("FishUITestData", 3));
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
    public void winTest() {
        Fish aFish = new Fish(true);
        FileReader file = null ;
        String[] rankingListExpected = rankingListAfter.split(";");

        aFishUI.setMyFish(aFish);
        aFishUI.setM(minutes);
        aFishUI.setS(second);
        writeRankingList(rankingListBefore);
        aFishUI.win();

        System.out.println("expect: " + expectedStatus + "\tresult: "+ aFishUI.getMyFish().getState());
        System.out.println();
        assertEquals(aFishUI.getMyFish().getState(), expectedStatus);

        try {
            file = new FileReader("src/main/resources/record/排行榜.txt");
            BufferedReader reader = new BufferedReader(file);
            String line = null;

            for (int i = 0; i < 3 && (line = reader.readLine())!=null;i++)
            {
                System.out.println("expect: " + rankingListExpected[i] + "\tresult: " + line);
                System.out.println();
                assertEquals(line, rankingListExpected[i]);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    排行榜时间记录以;分割，3个数据
     */
    public void writeRankingList(String rankingList) {
        if (null == rankingList)
            return;
        String[] rankingInfo = rankingList.split(";");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/record/排行榜.txt"));
            for (int i =0 ;i<3;i++){
                out.write(rankingInfo[i] + "\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
