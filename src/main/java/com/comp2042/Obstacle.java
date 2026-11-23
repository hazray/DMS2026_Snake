package com.comp2042;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Obstacle {
    private Color color = Color.BLACK;
    private ArrayList<Point> obstacles = new ArrayList<>();
    private int x;
    private int y;

    public Obstacle(int x, int y) {
       this.x = x;
       this.y = y;
    }

    /**
     * Places up to {@code amount} new obstacles on random free cells of the board.
     * A cell is free if it holds no snake part, no coin and no existing obstacle,
     * so obstacles never overlap and never land outside the grid.
     *
     * @return how many obstacles were actually added (fewer than requested if the board runs out of free cells)
     */
    public int newObstacles(Snake snek, Coin coin, int amount) {
        if (obstacles.size() > 50) {
            return 0;
        }

        Point head = snek.getHead();
        List<Point> body = snek.getBody();
        Point coinPos = coin.getPos();
        List<Point> freeCells = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Point p = new Point(i, j);
                if (!p.equals(head) && !p.equals(coinPos) && !body.contains(p) && !obstacles.contains(p)) {
                    freeCells.add(p);
                }
            }
        }

        Random rnd = new Random();
        int added = 0;
        while (added < amount && !freeCells.isEmpty()) {
            obstacles.add(freeCells.remove(rnd.nextInt(freeCells.size())));
            added++;
        }
        return added;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Point> getObstacles() {
        return obstacles;
    }

    public void reset() {
        obstacles.clear();
    }
}
