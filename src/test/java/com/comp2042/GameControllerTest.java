package com.comp2042;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GameController#isValidTurn}, which stops the snake reversing into itself.
 */
class GameControllerTest {

    @Test
    @DisplayName("Moving UP, pressing DOWN is rejected (regression: DOWN was checked against DOWN)")
    void upThenDownIsRejected() {
        assertFalse(GameController.isValidTurn(KeyCode.UP, KeyCode.DOWN));
    }

    @ParameterizedTest(name = "moving {0}, pressing {1} is rejected")
    @CsvSource({"UP, DOWN", "DOWN, UP", "LEFT, RIGHT", "RIGHT, LEFT"})
    @DisplayName("Reversing straight back is never allowed")
    void reversingIsRejected(KeyCode current, KeyCode requested) {
        assertFalse(GameController.isValidTurn(current, requested));
    }

    @ParameterizedTest(name = "moving {0}, pressing {1} is allowed")
    @CsvSource({
            "UP, LEFT", "UP, RIGHT", "DOWN, LEFT", "DOWN, RIGHT",
            "LEFT, UP", "LEFT, DOWN", "RIGHT, UP", "RIGHT, DOWN"
    })
    @DisplayName("Turning 90 degrees is allowed")
    void perpendicularTurnsAreAllowed(KeyCode current, KeyCode requested) {
        assertTrue(GameController.isValidTurn(current, requested));
    }

    @ParameterizedTest(name = "moving {0}, pressing {0} again is allowed")
    @EnumSource(value = KeyCode.class, names = {"UP", "DOWN", "LEFT", "RIGHT"})
    @DisplayName("Pressing the current direction again is allowed (DOWN used to be rejected)")
    void sameDirectionIsAllowed(KeyCode direction) {
        assertTrue(GameController.isValidTurn(direction, direction));
    }

    @ParameterizedTest(name = "{0} is not a turn")
    @EnumSource(value = KeyCode.class, names = {"P", "TAB", "ESCAPE", "SPACE", "W", "ENTER"})
    @DisplayName("Keys that are not arrow keys never count as a turn")
    void nonArrowKeysAreRejected(KeyCode key) {
        assertFalse(GameController.isValidTurn(KeyCode.LEFT, key));
    }
}
