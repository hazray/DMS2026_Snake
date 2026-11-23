package com.comp2042;

import javafx.scene.image.Image;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Coin {
    private Point pos;
    private Image image = new Image("resources/views/res/redcoin.png");

    /**
     * Places the coin on a random cell that holds no snake part and no obstacle.
     * Picking from the list of free cells (instead of retrying random cells) means
     * this can never loop forever when the board is nearly full.
     *
     * @throws IllegalStateException if there is no free cell left
     */
    public Coin(int x, int y, Snake snek, Obstacle obstacle) {
        Point head = snek.getHead();
        List<Point> body = snek.getBody();
        List<Point> blocked = obstacle.getObstacles();
        List<Point> freeCells = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Point p = new Point(i, j);
                if (!p.equals(head) && !body.contains(p) && !blocked.contains(p)) {
                    freeCells.add(p);
                }
            }
        }
        if (freeCells.isEmpty()) {
            throw new IllegalStateException("No free cell left to place a coin");
        }
        pos = freeCells.get(new Random().nextInt(freeCells.size()));
    }

    public Point getPos() {
        return new Point(pos);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }
}
