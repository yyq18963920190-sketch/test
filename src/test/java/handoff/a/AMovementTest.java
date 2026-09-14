package handoff.a;

import fish.Fish;
import handoff.support.AbTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Source: backup src/test/java/myTest/FishTest.java, moveTest1 through moveTest6.
 * Adaptation: final A-001..008 inputs, full player constructor checks, fixed flags.
 * No original test exactly covers this full set; new assertions are identified in the handoff.
 */
public class AMovementTest extends AbTestBase {
    @Test
    public void DSG_A_001_playerInitialAttributes() {
        // Do not call playerAt here: setters would mask constructor defects.
        Fish player = new Fish(true);
        assertEquals(800, player.getX(), EPS);
        assertEquals(500, player.getY(), EPS);
        assertEquals(0, player.getScore());
        assertEquals(9, player.getSpeed(), EPS);
        assertEquals(9, player.getY_speed(), EPS);
        assertEquals(4, player.getGrade());
        assertEquals(1, player.getState());
        assertEquals(77, player.getWidth());
        assertEquals(53, player.getHeight());
    }

    @Test
    public void DSG_A_002_noDirection() {
        Fish player = playerAt(500, 500);
        player.move();
        assertPosition(player, 500, 500);
    }

    @Test
    public void DSG_A_003_moveRight() {
        Fish player = playerAt(500, 500);
        player.setRight(true);
        player.move();
        assertPosition(player, 509, 500);
    }

    @Test
    public void DSG_A_004_moveUp() {
        Fish player = playerAt(500, 500);
        player.setUp(true);
        player.move();
        assertPosition(player, 500, 491);
    }

    @Test
    public void DSG_A_005_rightInsideBoundary() {
        Fish player = playerAt(1813, 500);
        player.setRight(true);
        player.move();
        assertPosition(player, 1822, 500);
    }

    @Test
    public void DSG_A_006_rightAtBoundary() {
        Fish player = playerAt(1814, 500);
        player.setRight(true);
        player.move();
        assertPosition(player, 1814, 500);
    }

    @Test
    public void DSG_A_007_leftInsideBoundary() {
        Fish player = playerAt(10, 500);
        player.setLeft(true);
        player.move();
        assertPosition(player, 1, 500);
    }

    @Test
    public void DSG_A_008_leftAtBoundary() {
        Fish player = playerAt(9, 500);
        player.setLeft(true);
        player.move();
        assertPosition(player, 9, 500);
    }

    private void assertPosition(Fish player, float expectedX, float expectedY) {
        assertEquals(expectedX, player.getX(), EPS);
        assertEquals(expectedY, player.getY(), EPS);
    }
}
