package handoff.b;

import fish.Fish;
import fish.FishUI;
import handoff.support.AbTestBase;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Source: backup myTest/CollisionTest.java collisionTest.
 * Reuses explicit attribute injection, single-fish list and collisionTest call.
 * Final inputs replace old spreadsheet rows; adds explicit states and other-fish assertion.
 */
public class BFishCollisionTest extends AbTestBase {
    @Test
    public void DSG_B_001_playerLarger() {
        checkCollision(2, 20, 130, 90, 4, 1, 55, 30, 510, 510, 1, 21, 0);
    }

    @Test
    public void DSG_B_002_playerSmaller() {
        checkCollision(4, 0, 77, 53, 3, 2, 90, 50, 510, 490, 0, 0, 1);
    }

    @Test
    public void DSG_B_003_sameGrade() {
        checkCollision(4, 0, 77, 53, 4, 1, 55, 30, 500, 500, 1, 1, 0);
    }

    @Test
    public void DSG_B_004_upperLeft() {
        checkCollision(4, 0, 77, 53, 4, 1, 55, 30, 490, 490, 1, 1, 0);
    }

    @Test
    public void DSG_B_005_lowerLeft() {
        checkCollision(4, 0, 77, 53, 4, 1, 55, 30, 490, 510, 1, 1, 0);
    }

    @Test
    public void DSG_B_006_distance75() {
        checkCollision(4, 0, 77, 53, 4, 1, 55, 30, 575, 500, 1, 1, 0);
    }

    @Test
    public void DSG_B_007_distance76() {
        checkCollision(4, 0, 77, 53, 4, 1, 55, 30, 576, 500, 1, 0, 1);
    }

    @Test
    public void DSG_B_008_distance77() {
        checkCollision(4, 0, 77, 53, 4, 1, 55, 30, 577, 500, 1, 0, 1);
    }

    private void checkCollision(int playerGrade, int playerScore, int playerWidth,
            int playerHeight, int otherGrade, int otherScore, int otherWidth,
            int otherHeight, int otherX, int otherY, int expectedPlayerState,
            int expectedPlayerScore, int expectedOtherState) {
        Fish player = playerAt(500, 500);
        player.setGrade(playerGrade);
        player.setScore(playerScore);
        player.setWidth(playerWidth);
        player.setHeight(playerHeight);

        Fish otherFish = new Fish();
        otherFish.setX(otherX);
        otherFish.setY(otherY);
        otherFish.setGrade(otherGrade);
        otherFish.setScore(otherScore);
        otherFish.setWidth(otherWidth);
        otherFish.setHeight(otherHeight);
        otherFish.setState(1);
        // These attributes do not affect collisionTest, but eliminate random fixture values.
        otherFish.setDirection(0);
        otherFish.setSpeed(0);
        otherFish.setY_speed(0);
        otherFish.setUp(false);
        otherFish.setDown(false);
        otherFish.setLeft(false);
        otherFish.setRight(false);

        FishUI ui = uiFor(player);
        ArrayList<Fish> otherFishList = new ArrayList<>();
        otherFishList.add(otherFish);
        ui.setFishList(otherFishList);
        ui.setDaoJu(null);
        ui.collisionTest();

        assertEquals(expectedPlayerState, player.getState());
        assertEquals(expectedPlayerScore, player.getScore());
        assertEquals(expectedOtherState, otherFish.getState());
    }
}
