package posmicanomaly.LibjsrteRoguelikeExample.Component;

import posmicanomaly.libjsrte.Console.Symbol;
import posmicanomaly.libjsrte.Util.ColorTools;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Level {
    private int height, width;

    private Tile[][] tileArray;

    public Level(int height, int width) {
        this.height = height;
        this.width = width;
        this.tileArray = makeMap(height, width);
    }

    private Tile[][] makeMap(int height, int width) {
        Tile[][] result = new Tile[height][width];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                char symbol = Symbol.MIDDLE_DOT;
                if (y == 0 || y == height - 1) {
                    symbol = '#';
                }

                if (x == 0 || x == width - 1) {
                    symbol = '#';
                }
                result[y][x] = new Tile(y, x, symbol, ColorTools.getRandomColor());
            }
        }

        return result;
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

    public boolean inBounds(int y, int x) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }
}
