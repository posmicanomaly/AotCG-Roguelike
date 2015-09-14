package posmicanomaly.AotCG.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Level {
    private int height, width;

    private Tile[][] tileArray;

    public Level(int height, int width) {
        this.height = height;
        this.width = width;
        init();
    }

    public ArrayList<Tile> getNearbyTiles(int y, int x) {
        ArrayList<Tile> result = new ArrayList<Tile>();
        if (!inBounds(y, x)) {
            return null;
        }
        Tile tLeft = getTile(y, x - 1);
        Tile tRight = getTile(y, x + 1);
        Tile tUp = getTile(y - 1, x);
        Tile tDown = getTile(y + 1, x);

        Tile tNW = getTile(y - 1, x - 1);
        Tile tNE = getTile(y - 1, x + 1);
        Tile tSW = getTile(y + 1, x - 1);
        Tile tSE = getTile(y + 1, x + 1);

        if (tLeft != null) {
            result.add(tLeft);
        }
        if (tRight != null) {
            result.add(tRight);
        }
        if (tUp != null) {
            result.add(tUp);
        }
        if (tDown != null) {
            result.add(tDown);
        }

        if (tNW != null) {
            result.add(tNW);
        }

        if (tNE != null) {
            result.add(tNE);
        }

        if (tSW != null) {
            result.add(tSW);
        }

        if (tSE != null) {
            result.add(tSE);
        }

        return result;
    }

    /*
    DEBUG FUNCTIONS
     */

    public void DEBUG_INIT() {
        init();
    }

    /*

     */

    public void toggleAllTilesVisible(boolean visible) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = tileArray[y][x];
                t.setVisible(visible);
            }
        }
    }

    private void init() {
        this.tileArray = makeMap(height, width);
    }

    private Tile[][] makeMap(int height, int width) {
        return LevelFactory.makeDefaultLevel(height, width);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public char getSymbol(int y, int x) {
        if (inBounds(y, x))
            return tileArray[y][x].getSymbol();
        return '?';
    }

    public Color getColor(int y, int x) {
        if (inBounds(y, x))
            return tileArray[y][x].getColor();
        return null;
    }

    public Tile getTile(int y, int x) {
        if (inBounds(y, x))
            return tileArray[y][x];
        return null;
    }

    public Tile getRandomTile(Tile.Type type) {
        Random rng = new Random();
        int y, x;
        Tile t;
        do {
            y = rng.nextInt(height);
            x = rng.nextInt(width);
            t = tileArray[y][x];
        } while(t.getType() != type);

        return t;
    }
    /**
     * Debug function
     *
     * @return
     */
    public Tile[][] getTileArray() {
        return tileArray;
    }

    public boolean inBounds(int y, int x) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }
}
