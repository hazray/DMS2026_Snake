package com.comp2042;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Game}: scoring and the winning score.
 * A 3x3 board is used so the winning score is small: 3 * 3 - 2 = 7 coins.
 */
class GameTest {

    private static final int SIZE = 3;
    private static final int FULL_BOARD_COINS = SIZE * SIZE - 2;

    private Game newGame() {
        return new Game(SIZE, SIZE, true, 'N');
    }

    private void eatCoins(Game game, int count) {
        for (int i = 0; i < count; i++) {
            game.incScore();
        }
    }

    // ---- Score and points ----

    @Test
    @DisplayName("A new game starts with no points")
    void newGameHasNoPoints() {
        assertEquals(0, newGame().getScorePoints());
    }

    @Test
    @DisplayName("addPoints adds up across coins")
    void addPointsAccumulates() {
        Game game = newGame();
        game.addPoints(5);
        game.addPoints(15);
        assertEquals(20, game.getScorePoints());
    }

    // ---- gameWon boundary ----

    @Test
    @DisplayName("Not won one coin before the board is full")
    void notWonOneCoinBeforeFullBoard() {
        Game game = newGame();
        eatCoins(game, FULL_BOARD_COINS - 1);
        assertFalse(game.gameWon());
    }

    @Test
    @DisplayName("Won exactly when the snake fills the board")
    void wonWhenBoardIsFull() {
        Game game = newGame();
        eatCoins(game, FULL_BOARD_COINS);
        assertTrue(game.gameWon());
    }

    @Test
    @DisplayName("Still counts as won if the score goes past the target")
    void wonPastTheTarget() {
        Game game = newGame();
        eatCoins(game, FULL_BOARD_COINS + 1);
        assertTrue(game.gameWon());
    }

    @Test
    @DisplayName("Smallest possible board (1x2) is already full at the start")
    void smallestBoardIsWonImmediately() {
        Game game = new Game(1, 2, false, 'N');
        assertTrue(game.gameWon());
    }

    // ---- decMaxScore ----

    @Test
    @DisplayName("Each obstacle lowers the winning score by exactly one (regression: was num - 1)")
    void decMaxScoreSubtractsEveryObstacle() {
        Game game = newGame();
        game.decMaxScore(2);
        eatCoins(game, FULL_BOARD_COINS - 3);
        assertFalse(game.gameWon(), "one coin short of the lowered target");
        game.incScore();
        assertTrue(game.gameWon(), "lowered target reached");
    }

    @Test
    @DisplayName("Adding zero obstacles leaves the target unchanged")
    void decMaxScoreByZero() {
        Game game = newGame();
        game.decMaxScore(0);
        eatCoins(game, FULL_BOARD_COINS - 1);
        assertFalse(game.gameWon());
    }
}
