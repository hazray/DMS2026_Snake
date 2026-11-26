package com.comp2042;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Obstacle#newObstacles}: obstacles land only on free cells inside the board.
 * On a 4x4 board the snake takes 2 cells and the coin 1, leaving 13 free cells.
 */
class ObstacleTest {

    private static final int SMALL = 4;
    private static final int FREE_ON_SMALL_BOARD = SMALL * SMALL - 3;

    private record Board(Obstacle obstacle, Snake snake, Coin coin) {
    }

    private Board newBoard(int size) {
        Obstacle obstacle = new Obstacle(size, size);
        Snake snake = new Snake(size, size, false, obstacle);
        Coin coin = new Coin(size, size, snake, obstacle);
        return new Board(obstacle, snake, coin);
    }

    @Test
    @DisplayName("Adds the requested number of obstacles and reports how many")
    void addsRequestedAmount() {
        Board board = newBoard(10);
        assertEquals(5, board.obstacle().newObstacles(board.snake(), board.coin(), 5));
        assertEquals(5, board.obstacle().getObstacles().size());
    }

    @Test
    @DisplayName("Asking for zero obstacles adds none")
    void zeroAmountAddsNothing() {
        Board board = newBoard(10);
        assertEquals(0, board.obstacle().newObstacles(board.snake(), board.coin(), 0));
        assertTrue(board.obstacle().getObstacles().isEmpty());
    }

    @Test
    @DisplayName("Filling every free cell: all on the board, column 0 used, no overlaps (regression: x was nextInt(x) + 1)")
    void fillsEveryFreeCellCorrectly() {
        Board board = newBoard(SMALL);
        int added = board.obstacle().newObstacles(board.snake(), board.coin(), FREE_ON_SMALL_BOARD);
        List<Point> obstacles = board.obstacle().getObstacles();

        assertEquals(FREE_ON_SMALL_BOARD, added);
        for (Point p : obstacles) {
            assertTrue(p.x >= 0 && p.x < SMALL && p.y >= 0 && p.y < SMALL, "off the board: " + p);
        }
        assertTrue(obstacles.stream().anyMatch(p -> p.x == 0), "column 0 should be usable");
        assertEquals(obstacles.size(), new HashSet<>(obstacles).size(), "no two obstacles on the same cell");
    }

    @Test
    @DisplayName("Never places an obstacle on the snake or the coin")
    void avoidsSnakeAndCoin() {
        Board board = newBoard(SMALL);
        board.obstacle().newObstacles(board.snake(), board.coin(), FREE_ON_SMALL_BOARD);
        List<Point> obstacles = board.obstacle().getObstacles();

        assertFalse(obstacles.contains(board.snake().getHead()));
        assertFalse(obstacles.stream().anyMatch(board.snake().getBody()::contains));
        assertFalse(obstacles.contains(board.coin().getPos()));
    }

    @Test
    @DisplayName("Asking for more obstacles than free cells stops when the board is full instead of hanging")
    void moreThanFreeCellsStopsAtFull() {
        Board board = newBoard(SMALL);
        int added = assertTimeoutPreemptively(Duration.ofSeconds(2),
                () -> board.obstacle().newObstacles(board.snake(), board.coin(), 100));
        assertEquals(FREE_ON_SMALL_BOARD, added);
    }

    @Test
    @DisplayName("A second batch never overlaps the first")
    void secondBatchDoesNotOverlap() {
        Board board = newBoard(SMALL);
        board.obstacle().newObstacles(board.snake(), board.coin(), 6);
        int secondBatch = board.obstacle().newObstacles(board.snake(), board.coin(), 100);
        List<Point> obstacles = board.obstacle().getObstacles();

        assertEquals(FREE_ON_SMALL_BOARD - 6, secondBatch);
        assertEquals(obstacles.size(), new HashSet<>(obstacles).size());
    }

    @Test
    @DisplayName("No more obstacles are added once there are over 50")
    void capsAtFifty() {
        Board board = newBoard(20);
        board.obstacle().newObstacles(board.snake(), board.coin(), 51);
        assertEquals(0, board.obstacle().newObstacles(board.snake(), board.coin(), 5));
        assertEquals(51, board.obstacle().getObstacles().size());
    }

    @Test
    @DisplayName("reset removes every obstacle")
    void resetClears() {
        Board board = newBoard(10);
        board.obstacle().newObstacles(board.snake(), board.coin(), 5);
        board.obstacle().reset();
        assertTrue(board.obstacle().getObstacles().isEmpty());
    }
}
