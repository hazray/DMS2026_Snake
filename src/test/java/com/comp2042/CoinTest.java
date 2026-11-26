package com.comp2042;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Coin} and its subclasses: placement on free cells, and points.
 * A 3x1 board is used where possible: the snake sits on (1,0) and (2,0), so (0,0) is the only free cell.
 */
class CoinTest {

    private Obstacle obstacle(int width, int height) {
        return new Obstacle(width, height);
    }

    // ---- Placement ----

    @Test
    @DisplayName("Uses the only free cell when there is just one")
    void usesOnlyFreeCell() {
        Obstacle obstacle = obstacle(3, 1);
        Snake snake = new Snake(3, 1, false, obstacle);
        assertEquals(new Point(0, 0), new Coin(3, 1, snake, obstacle).getPos());
    }

    @Test
    @DisplayName("Skips cells that hold an obstacle")
    void skipsObstacles() {
        Obstacle obstacle = obstacle(4, 1);
        obstacle.getObstacles().add(new Point(0, 0));
        Snake snake = new Snake(4, 1, false, obstacle); // head (2,0), body (3,0)
        assertEquals(new Point(1, 0), new Coin(4, 1, snake, obstacle).getPos());
    }

    @Test
    @DisplayName("Throws IllegalStateException when no cell is free (regression: used to loop forever)")
    void fullBoardThrows() {
        Obstacle obstacle = obstacle(3, 1);
        obstacle.getObstacles().add(new Point(0, 0));
        Snake snake = new Snake(3, 1, false, obstacle);
        assertThrows(IllegalStateException.class, () -> new Coin(3, 1, snake, obstacle));
    }

    @Test
    @DisplayName("Over many random placements, always on the board and never on the snake")
    void randomPlacementAlwaysValid() {
        Obstacle obstacle = obstacle(10, 10);
        Snake snake = new Snake(10, 10, false, obstacle);
        for (int i = 0; i < 500; i++) {
            Point p = new Coin(10, 10, snake, obstacle).getPos();
            assertTrue(p.x >= 0 && p.x < 10 && p.y >= 0 && p.y < 10, "off the board: " + p);
            assertNotEquals(snake.getHead(), p);
            assertFalse(snake.getBody().contains(p));
        }
    }

    @Test
    @DisplayName("Every special coin uses the same free-cell placement")
    void specialCoinsUseFreeCells() {
        Obstacle obstacle = obstacle(3, 1);
        Snake snake = new Snake(3, 1, false, obstacle);
        Point onlyFree = new Point(0, 0);
        assertEquals(onlyFree, new GoldCoin(3, 1, snake, obstacle).getPos());
        assertEquals(onlyFree, new BlueCoin(3, 1, snake, obstacle).getPos());
        assertEquals(onlyFree, new SilverCoin(3, 1, snake, obstacle).getPos());
        assertEquals(onlyFree, new RainbowCoin(3, 1, snake, obstacle).getPos());
    }

    @Test
    @DisplayName("getPos returns a copy, so callers cannot move the coin")
    void getPosReturnsCopy() {
        Obstacle obstacle = obstacle(3, 1);
        Snake snake = new Snake(3, 1, false, obstacle);
        Coin coin = new Coin(3, 1, snake, obstacle);
        coin.getPos().translate(5, 5);
        assertEquals(new Point(0, 0), coin.getPos());
    }

    // ---- Points and bonuses ----

    @Test
    @DisplayName("Gold is worth 15 points; red, blue, silver and rainbow are worth 5")
    void pointValues() {
        Obstacle obstacle = obstacle(10, 10);
        Snake snake = new Snake(10, 10, false, obstacle);
        assertEquals(5, new Coin(10, 10, snake, obstacle).getPoints());
        assertEquals(15, new GoldCoin(10, 10, snake, obstacle).getPoints());
        assertEquals(5, new BlueCoin(10, 10, snake, obstacle).getPoints());
        assertEquals(5, new SilverCoin(10, 10, snake, obstacle).getPoints());
        assertEquals(5, new RainbowCoin(10, 10, snake, obstacle).getPoints());
    }

    @Test
    @DisplayName("Rainbow coin gives a speed bonus of 5")
    void rainbowSpeedBonus() {
        Obstacle obstacle = obstacle(10, 10);
        Snake snake = new Snake(10, 10, false, obstacle);
        assertEquals(5, new RainbowCoin(10, 10, snake, obstacle).getSpeedBonus());
    }
}
