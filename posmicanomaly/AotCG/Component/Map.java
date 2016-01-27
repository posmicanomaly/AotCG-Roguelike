package posmicanomaly.AotCG.Component;

import posmicanomaly.AotCG.Game.Roguelike;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Map {
    private int height, width, depth;
    private Level currentLevel;
    public enum LevelStyle {WORLD, DEFAULT}
    private int currentDepth;

    private Level[][][] level3dArray;
    private Level worldMap;

    public Map(int height, int width, int depth) {
        this.currentDepth = 0;
        this.height = height;
        this.width = width;
        this.depth = depth;
        worldMap = new Level(height, width, LevelStyle.WORLD, 0, 0);

        level3dArray = new Level[height][width][depth];
        currentLevel = worldMap;
    }

    public boolean goDeeper(int rootY, int rootX) {
        System.out.println("goDeeper(" + rootY + ", " + rootX + "); currentDepth = " + currentDepth);
        if(currentDepth == this.depth - 1) {
            System.out.println("can't go deeper, max depth reached");
            return false;
        }

        Level nextLevel = level3dArray[rootY][rootX][currentDepth + 1];
        // Make a new level
        if(nextLevel == null) {
            System.out.println("nextLevel == null, making new level");
            nextLevel = new Level(this.height, this.width, LevelStyle.DEFAULT, rootY, rootX);
            level3dArray[rootY][rootX][currentDepth + 1] = nextLevel;
        }

        currentLevel.setTurnExited(Roguelike.turns);
        currentLevel = nextLevel;
        currentDepth++;
        return true;
    }

    public boolean goHigher(int rootY, int rootX) {
        System.out.println("goHigher(" + rootY + ", " + rootX + "); currentDepth = " + currentDepth);
        if(currentDepth == 0) {
            System.out.println("can't go higher, at 0");
            return false;
        }

        Level prevLevel;
        if(currentDepth - 1 == 0) {
            System.out.println("Entering world map");
            prevLevel = worldMap;
        }
        else {
            prevLevel = level3dArray[rootY][rootX][currentDepth - 1];
            // Make a new level
            if(prevLevel == null) {
                System.out.println("prevLevel == null, making new level");
                prevLevel = new Level(this.height, this.width, LevelStyle.DEFAULT, rootY, rootX);
            }
        }

        currentLevel.setTurnExited(Roguelike.turns);
        currentLevel = prevLevel;
        currentDepth--;
        return true;
    }
//    public boolean goDeeper() {
//        // Need to generate a new level
//        if(getCurrentDepth() == levelList.size() - 1) {
//            System.out.println("Making new level to go deeper");
//            levelList.add(makeMap(height, width, LevelStyle.DEFAULT));
//        }
//
//        // Set the turn we exited
//        currentLevel.setTurnExited(Roguelike.turns);
//        // Set the current level to the next level after this one (+1)
//        currentLevel = levelList.get(getCurrentDepth() + 1);
//        return true;
//    }
//
//    public boolean goHigher() {
//        // Can't go higher than 0, the world map
//        if(getCurrentDepth() == 0) {
//            System.out.println("Can't go up");
//            return false;
//        }
//
//        // Go back to the previous level (-1)
//        else {
//            currentLevel.setTurnExited(Roguelike.turns);
//            currentLevel = levelList.get(getCurrentDepth() - 1);
//            return true;
//        }
//    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }



    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }
}
