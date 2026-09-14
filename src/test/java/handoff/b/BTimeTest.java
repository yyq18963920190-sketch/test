package handoff.b;

import fish.FishUI;
import handoff.support.AbTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Source: backup myTest/FishUiOtherTest.java gameTime_1..3;
 * myTest/FishUITest.java testSetMandS.
 * Adaptation: final inputs 10 minutes and 0/9/59/60 seconds, one method per case.
 * B-018's normalization contract must be confirmed; it is an active candidate assertion.
 */
public class BTimeTest extends AbTestBase {
    @Test
    public void DSG_B_015_seconds0() {
        checkTime(0, 10, 0, "10:00");
    }

    @Test
    public void DSG_B_016_seconds9() {
        checkTime(9, 10, 9, "10:09");
    }

    @Test
    public void DSG_B_017_seconds59() {
        checkTime(59, 10, 59, "10:59");
    }

    @Test
    public void DSG_B_018_seconds60() {
        // Candidate DEF-B-02. No @Ignore or fabricated successful result.
        checkTime(60, 11, 0, "11:00");
    }

    private void checkTime(int inputSeconds, int expectedMinutes,
            int expectedSeconds, String expectedDisplay) {
        FishUI ui = new FishUI();
        ui.setM(10);
        ui.setS(inputSeconds);
        String actualDisplay = ui.gameTime();
        String context = "input=10:" + inputSeconds + ", actual display=" + actualDisplay
                + ", actual m=" + ui.getM() + ", actual s=" + ui.getS();
        assertEquals(context, expectedDisplay, actualDisplay);
        assertEquals(expectedMinutes, ui.getM());
        assertEquals(expectedSeconds, ui.getS());
    }
}
