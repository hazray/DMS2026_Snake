package com.comp2042;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Snake}: movement, growth, wrapping and the ways it can die.
 * The board is 10x10, so a new snake starts with its head at (5,5) and one body segment at (6,5).
 */
class SnakeTest {

    private static final int SIZE = 10;
    /** A cell none of these tests walk through, used for a coin the snake never reaches. */
    private static final Point UNREACHED_CELL = new Point(9, 0);

    private Obstacle obstacle;

    @BeforeEach
    void setUp() {
        obstacle = new Obstacle(SIZE, SIZE);
    }

    private Snake newSnake(boolean noBorder) {
        return new Snake(SIZE, SIZE, noBorder, obstacle);
    }

    /**
     * One game tick without eating, the same way GameController.tick() does it:
     * move, then (if still alive) let ateCoin drop the tail segment.
     */
    private void step(Snake snake, char dir, int times) {
        Coin coin = new FixedCoin(UNREACHED_CELL, snake, obstacle);
        for (int i = 0; i < times; i++) {
            snake.move(dir);
            if (!snake.getDead()) {
                snake.ateCoin(coin);
            }
        }
    }

    /** Test double: a coin at a chosen cell, so tests don't depend on random placement. */
    private static class FixedCoin extends Coin {
        private final Point fixedPos;

        FixedCoin(Point pos, Snake snake, Obstacle obstacle) {
            super(SIZE, SIZE, snake, obstacle);
            this.fixedPos = new Point(pos);
        }

        @Override
        public Point getPos() {
            return new Point(fixedPos);
        }
    }

    // ---- Starting state ----

    @Test
    @DisplayName("Starts in the middle with one body segment behind the head, facing left")
    void startingState() {
        Snake snake = newSnake(false);
        assertEquals(new Point(5, 5), snake.getHead());
        assertEquals(List.of(new Point(6, 5)), snake.getBody());
        assertEquals(270, snake.getSnakeDir());
        assertFalse(snake.getDead());
    }

    // ---- Movement and growth ----

    @ParameterizedTest(name = "{0} moves the head to ({1},{2}) facing {3} degrees")
    @CsvSource({"U, 5, 4, 0", "D, 5, 6, 180", "L, 4, 5, 270", "R, 6, 5, 90"})
    @DisplayName("Each direction moves the head one cell and sets the sprite angle")
    void moveInEachDirection(char dir, int x, int y, int angle) {
        Snake snake = newSnake(false);
        snake.move(dir);
        assertEquals(new Point(x, y), snake.getHead());
        assertEquals(angle, snake.getSnakeDir());
    }

    @Test
    @DisplayName("Moving without eating keeps the length and drags the body along")
    void moveWithoutEatingKeepsLength() {
        Snake snake = newSnake(false);
        step(snake, 'L', 1);
        assertEquals(new Point(4, 5), snake.getHead());
        assertEquals(List.of(new Point(5, 5)), snake.getBody());
    }

    @Test
    @DisplayName("Eating a coin grows the snake by one segment")
    void eatingCoinGrows() {
        Snake snake = newSnake(false);
        Coin coin = new FixedCoin(new Point(4, 5), snake, obstacle);
        snake.move('L');
        assertTrue(snake.ateCoin(coin));
        assertEquals(List.of(new Point(6, 5), new Point(5, 5)), snake.getBody());
    }

    // ---- Walls ----

    @Test
    @DisplayName("With walls on, the snake survives on the last cell before the wall")
    void survivesOnEdgeCell() {
        Snake snake = newSnake(false);
        step(snake, 'L', 5);
        assertEquals(new Point(0, 5), snake.getHead());
        assertFalse(snake.getDead());
    }

    @Test
    @DisplayName("With walls on, moving off the board kills the snake and keeps the head on the board")
    void diesAtWall() {
        Snake snake = newSnake(false);
        step(snake, 'L', 6);
        assertTrue(snake.getDead());
        assertEquals(new Point(0, 5), snake.getHead());
    }

    // ---- Wrapping ----

    @Test
    @DisplayName("With no walls, leaving the left edge wraps to the right edge")
    void wrapsHorizontally() {
        Snake snake = newSnake(true);
        step(snake, 'L', 6);
        assertFalse(snake.getDead());
        assertEquals(new Point(9, 5), snake.getHead());
    }

    @Test
    @DisplayName("With no walls, leaving the top edge wraps to the bottom edge")
    void wrapsVertically() {
        Snake snake = newSnake(true);
        step(snake, 'U', 6);
        assertFalse(snake.getDead());
        assertEquals(new Point(5, 9), snake.getHead());
    }

    @Test
    @DisplayName("The Blue coin power-up (ignoreBorders) wraps even when walls are on")
    void ignoreBordersWrapsWithWallsOn() {
        Snake snake = newSnake(false);
        snake.ignoreBorders(true);
        step(snake, 'L', 6);
        assertFalse(snake.getDead());
        assertEquals(new Point(9, 5), snake.getHead());
    }

    @Test
    @DisplayName("Once the power-up ends, the walls kill again")
    void wallsKillAfterPowerUpEnds() {
        Snake snake = newSnake(false);
        snake.ignoreBorders(true);
        snake.ignoreBorders(false);
        step(snake, 'L', 6);
        assertTrue(snake.getDead());
    }

    // ---- Obstacles ----

    @Test
    @DisplayName("Running into an obstacle kills the snake without moving onto it")
    void diesOnObstacle() {
        obstacle.getObstacles().add(new Point(4, 5));
        Snake snake = newSnake(false);
        step(snake, 'L', 1);
        assertTrue(snake.getDead());
        assertEquals(new Point(5, 5), snake.getHead());
    }

    @Test
    @DisplayName("Wrapping onto an obstacle kills the snake (regression: it used to pass through)")
    void diesWhenWrappingOntoObstacle() {
        obstacle.getObstacles().add(new Point(9, 5));
        Snake snake = newSnake(true);
        step(snake, 'L', 5);
        assertFalse(snake.getDead(), "still alive on the left edge");
        step(snake, 'L', 1);
        assertTrue(snake.getDead());
        assertEquals(new Point(0, 5), snake.getHead());
    }

    // ---- Self collision ----

    @Test
    @DisplayName("Turning back into its own body kills the snake")
    void diesHittingItself() {
        Snake snake = newSnake(false);
        // Calling move() without ateCoin() keeps every segment, as if a coin were eaten each tick.
        for (char dir : "LLLLUR".toCharArray()) {
            snake.move(dir);
        }
        assertFalse(snake.getDead(), "still alive before closing the loop");
        snake.move('D'); // into (2,5), which is part of the body
        assertTrue(snake.getDead());
    }

    // ---- Encapsulation ----

    @Test
    @DisplayName("getHead and getBody return copies, so callers cannot move the snake")
    void gettersReturnCopies() {
        Snake snake = newSnake(false);
        snake.getHead().translate(3, 3);
        snake.getBody().clear();
        assertEquals(new Point(5, 5), snake.getHead());
        assertEquals(1, snake.getBody().size());
    }
}
