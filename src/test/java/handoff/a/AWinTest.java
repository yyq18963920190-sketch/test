package handoff.a;

import fish.Fish;
import fish.FishUI;
import handoff.support.AbTestBase;
import org.junit.Test;

import javax.swing.JButton;
import static org.junit.Assert.*;

/**
 * Source: backup myTest/WinTest.java winTest/writeRankingList;
 * button assertions are adapted from myTest/FishUITest.java testSetGameButtonVisibility.
 * New final cases A-017/A-018 and their data are not recovered old implementations.
 */
public class AWinTest extends AbTestBase {
    @Test
    public void DSG_A_015_upgradeWritesEmptyRanking() throws Exception {
        try (RankingFixture ranking = new RankingFixture("empty.txt")) {
            Fish player = new Fish(true);
            player.setScore(50);
            FishUI ui = uiFor(player);
            assertEquals(2, ui.getGameButton().length);
            for (JButton button : ui.getGameButton()) {
                assertFalse(button.isVisible());
            }
            ui.upgrade(player);
            assertEquals(2, player.getState());
            for (JButton button : ui.getGameButton()) {
                assertTrue(button.isVisible());
            }
            ranking.assertMatches("after-empty.txt");
        }
    }

    @Test
    public void DSG_A_016_insertFasterRanking() throws Exception {
        try (RankingFixture ranking = new RankingFixture("sorted.txt")) {
            FishUI ui = uiFor(new Fish(true));
            ui.win();
            ranking.assertMatches("after-sorted.txt");
        }
    }

    @Test
    public void DSG_A_017_keepWholeMinuteRecord() throws Exception {
        // Candidate DEF-A-01; confirm that only 0:0 is an empty record.
        try (RankingFixture ranking = new RankingFixture("whole-minute.txt")) {
            FishUI ui = uiFor(new Fish(true));
            ui.win();
            ranking.assertMatches("after-whole-minute.txt");
        }
    }

    @Test
    public void DSG_A_018_preserveCurrentTimeAfterWin() throws Exception {
        // Candidate DEF-A-02. Do not change the expected values to match a defect.
        try (RankingFixture ranking = new RankingFixture("sorted.txt")) {
            FishUI ui = uiFor(new Fish(true));
            int minutesBefore = ui.getM();
            int secondsBefore = ui.getS();
            assertEquals(1, minutesBefore);
            assertEquals(30, secondsBefore);
            ui.win();
            assertEquals(minutesBefore, ui.getM());
            assertEquals(secondsBefore, ui.getS());
            assertEquals("1:30", ui.gameTime());
        }
    }
}
