package handoff.b;

import fish.DaoJu;
import fish.Fish;
import fish.FishUI;
import fish.ImagePool;
import handoff.support.AbTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Source: backup myTest/ImagePoolTest.java testGetImage and
 * myTest/DaoJuTest.java collision-related cases;
 * myTest/CollisionDTest.java collisionDTest.
 * Adaptation: unified JUnit 4 assertions, 9 -> 13.5 floating-point speeds,
 * historical invalid-image-index assertion and second-scan scenario.
 */
public class BDaoJuTest extends AbTestBase {
    @Test
    public void DSG_B_009_invalidImageIndexReturnsNull() {
        ImagePool imagePool = new ImagePool();
        try {
            assertNull("Invalid image index should return null", imagePool.getImage(20));
        } catch (ArrayIndexOutOfBoundsException exception) {
            fail("ImagePool.getImage(20) should return null instead of throwing "
                    + exception.getClass().getSimpleName());
        }
    }

    @Test
    public void DSG_B_010_farProp() {
        checkSingleScan(900, 500, false);
    }

    @Test
    public void DSG_B_011_pickupProp() {
        checkSingleScan(510, 510, true);
    }

    @Test
    public void DSG_B_012_exactRightEdge() {
        checkSingleScan(577, 500, false);
    }

    @Test
    public void DSG_B_013_insideRightEdge() {
        checkSingleScan(576, 500, true);
    }

    @Test
    public void DSG_B_014_noRepeatedAcceleration() {
        Fish player = playerAt(500, 500);
        FishUI ui = uiFor(player);
        DaoJu prop = propAt(510, 510);
        ui.setDaoJu(prop);
        ui.collisionTest();
        assertSpeeds(player, 13.5);
        assertEquals(0, prop.getState());
        assertNull(ui.getDaoJu());
        float firstXSpeed = player.getSpeed();
        float firstYSpeed = player.getY_speed();
        ui.collisionTest();
        assertEquals(firstXSpeed, player.getSpeed(), EPS);
        assertEquals(firstYSpeed, player.getY_speed(), EPS);
        assertSpeeds(player, 13.5);
        assertEquals("Consumed prop must remain consumed after a second scan", 0, prop.getState());
        assertNull(ui.getDaoJu());
    }

    private DaoJu propAt(int x, int y) {
        DaoJu prop = new DaoJu();
        prop.setX(x);
        prop.setY(y);
        prop.setWidth(80);
        prop.setHeight(80);
        prop.setType(1);
        // Collision cases explicitly require an existing prop, unlike constructor case B-009.
        prop.setState(1);
        return prop;
    }

    private void checkSingleScan(int x, int y, boolean shouldConsume) {
        Fish player = playerAt(500, 500);
        FishUI ui = uiFor(player);
        DaoJu prop = propAt(x, y);
        ui.setDaoJu(prop);
        ui.collisionTest();
        if (shouldConsume) {
            assertSpeeds(player, 13.5);
            assertEquals(0, prop.getState());
            assertNull(ui.getDaoJu());
        } else {
            assertSpeeds(player, 9);
            assertEquals(1, prop.getState());
            assertSame(prop, ui.getDaoJu());
        }
    }

    private void assertSpeeds(Fish player, double expected) {
        assertEquals(expected, player.getSpeed(), EPS);
        assertEquals(expected, player.getY_speed(), EPS);
    }
}
