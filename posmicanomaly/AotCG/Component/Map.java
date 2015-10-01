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

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        levelList = new ArrayList<>();
        Level firstLevel = makeMap(height, width);
        levelList.add(firstLevel);
        currentLevel = levelList.get(0);
    }

    public boolean goDeeper() {
        int currentLevelDepth = levelList.indexOf(currentLevel);
        if(currentLevelDepth == levelList.size() - 1) {
            System.out.println("Making new level to go deeper");
            levelList.add(makeMap(height, width));
        }
        currentLevel = levelList.get(currentLevelDepth + 1);
        return true;
    }

    public boolean goHigher() {
        int currentLevelDepth = levelList.indexOf(currentLevel);
        if(currentLevelDepth == 0) {
            System.out.println("Can't go up");
            return false;
        } else {
            currentLevel = levelList.get(currentLevelDepth - 1);
            return true;
        }
    }

    private Level makeMap(int height, int width) {
            return new Level(height, width);
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
