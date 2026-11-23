package com.comp2042;

import javafx.scene.paint.Color;

public class Game {
    private Color color = Color.web("feffe4");
    private int width;
    private int height;
    private int score;
    private int maxScore;
    private int initialMaxScore;
    private double multiplier;
    private int scorePoints;
    private double speed;
    private boolean powerUp;
    private boolean noBorder;

    public Game(int x, int y, boolean powerUp, char difficulty) {
        if (difficulty == 'E') {
            speed = 8.5;
            noBorder = true;
        } else if (difficulty == 'N') {
            speed = 8.5;
            noBorder = false;
        } else if (difficulty == 'H') {
            speed = 13;
            noBorder = false;
        }
        this.powerUp = powerUp;
        width = x;
        height = y;
        // The snake starts 2 cells long and grows by 1 per coin, so filling the board takes x*y - 2 coins.
        initialMaxScore = x * y - 2;
        maxScore = initialMaxScore;
        if (height > 50) {
            multiplier = 10;
        } else {
            multiplier = 20;
        }
    }

    public boolean gameWon() {
        return score >= maxScore;
    }

    public void incScore() {
        score++;
    }

    /**
     * Lowers the winning score when obstacles are added, because each obstacle
     * permanently blocks one cell the snake could otherwise fill.
     */
    public void decMaxScore(int num) {
        this.maxScore -= num;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetScore() {
        score = 0;
        scorePoints = 0;
        // Obstacles are cleared on reset, so the winning score goes back to the full board.
        maxScore = initialMaxScore;
    }

    public Color getColor() {
        return color;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void addPoints(int n) {
        scorePoints += n;
    }

    public int getScorePoints() {
        return scorePoints;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isNoBorder() {
        return noBorder;
    }
}
