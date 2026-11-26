package com.comp2042;

import javafx.scene.image.Image;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Coin {
    private Point pos;
    // Loaded on first use (see getImage) so a Coin can be created without JavaFX running, e.g. in unit tests.
    private Image image;

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

    /**
     * Returns this coin's sprite, loading it the first time it is needed.
     */
    public Image getImage() {
        if (image == null) {
            image = new Image(getImagePath());
        }
        return image;
    }

    /**
     * Classpath location of this coin's sprite. Subclasses override this to use their own image.
     */
    protected String getImagePath() {
        return "resources/views/res/redcoin.png";
    }
}
