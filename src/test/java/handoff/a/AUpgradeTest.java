package handoff.a;

import fish.Fish;
import fish.FishUI;
import handoff.support.AbTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Source: backup myTest/UpgradeTest.java upgradeTest and FishUITestData.xlsx/upgrade.
 * Adaptation: six final scores, deterministic player, state/score assertions,
 * and isolated ranking restoration for the score-50 win side effect.
 */
public class AUpgradeTest extends AbTestBase {
    @Test
    public void DSG_A_009_score4() throws Exception {
        checkUpgrade(4, 4, 77, 53, 1);
    }

    @Test
    public void DSG_A_010_score5() throws Exception {
        checkUpgrade(5, 3, 100, 70, 1);
    }

    @Test
    public void DSG_A_011_score19() throws Exception {
        checkUpgrade(19, 3, 100, 70, 1);
    }

    @Test
    public void DSG_A_012_score20() throws Exception {
        checkUpgrade(20, 2, 130, 90, 1);
    }

    @Test
    public void DSG_A_013_score49() throws Exception {
        checkUpgrade(49, 2, 130, 90, 1);
    }

    @Test
    public void DSG_A_014_score50() throws Exception {
        checkUpgrade(50, 1, 220, 150, 2);
    }

    private void checkUpgrade(int score, int grade, int width, int height, int state)
            throws Exception {
        try (RankingFixture ranking = new RankingFixture("empty.txt")) {
            Fish player = new Fish(true);
            player.setState(1);
            player.setScore(score);
            FishUI ui = uiFor(player);
            ui.upgrade(player);
            assertEquals(grade, player.getGrade());
            assertEquals(width, player.getWidth());
            assertEquals(height, player.getHeight());
            assertEquals(state, player.getState());
            assertEquals(score, player.getScore());
        }
    }
}
