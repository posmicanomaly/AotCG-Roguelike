package posmicanomaly.AotCG.Component;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Map {
    private int height, width, depth;
    private Level currentLevel;

    private ArrayList<Level> levelList;

    public Map(int height, int width, int depth) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.levelList = makeMap(height, width, depth);
        currentLevel = levelList.get(0);
    }

    private ArrayList<Level> makeMap(int height, int width, int depth) {
        levelList = new ArrayList<Level>();
        for(int z = 0; z < depth; z++) {
            levelList.add(new Level(height, width));
        }

        return levelList;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public char getSymbol(int y, int x, int z) {
        return levelList.get(z).getSymbol(y, x);
    }

    public Color getColor(int y, int x, int z) {
        return levelList.get(z).getColor(y, x);
    }

    public Level getLevel(int i) {
        return levelList.get(i);
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
