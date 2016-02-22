package posmicanomaly.AotCG.Component;

import posmicanomaly.AotCG.Game.AStar;
import posmicanomaly.AotCG.Game.Roguelike;
import posmicanomaly.libjsrte.Console.Symbol;
import posmicanomaly.libjsrte.Util.ColorTools;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Level {
    private int height, width;
    private AStar astar;
    private Tile[][] tileArray;
    private int turnExited;
    int rootY, rootX;
    int mapLevelDepth;
    private boolean generating;

    private Map.LevelStyle levelStyle;
    private ArrayList<Tile> waterTiles;

    public Level(int height, int width, int mapLevelDepth, Map.LevelStyle levelStyle, int rootY, int rootX, Roguelike roguelike) {
        this.height = height;
        this.width = width;
        this.rootY = rootY;
        this.rootX = rootX;
        astar = new AStar(this, roguelike);
        turnExited = -1;
        this.levelStyle = levelStyle;
        this.mapLevelDepth = mapLevelDepth;
        this.generating = true;
        init(this.levelStyle, roguelike);
    }

    private ArrayList<Tile> findWaterTiles() {
        ArrayList<Tile> result = new ArrayList<>();
        for(int y = 0; y < tileArray.length; y++) {
            for(int x = 0; x < tileArray[y].length; x++) {
                Tile t = tileArray[y][x];
                if(t.getType() == Tile.Type.WATER) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public void finalizeLevel() {
        LevelFactory.refineMap(this);
        waterTiles = findWaterTiles();
        for(int i = 0; i < 100; i++) {
            shimmerWaterTiles(waterTiles);
        }
        generating = false;
    }

    public void shimmerWaterTiles(ArrayList<Tile> waterTiles) {
        for(Tile tile : waterTiles) {
            if (tile.getType() == Tile.Type.WATER) {
                if (Roguelike.rng.nextInt(100) - 95 > 0) {
                    tile.setBackgroundColor(ColorTools.varyColor(Colors.WATER_BG, 0.7, 1.0, ColorTools.BaseColor.RGB));


                }
                if (Roguelike.rng.nextInt(100) - 95 > 0) {
                    tile.setColor(ColorTools.varyColor(Colors.WATER, 0.7, 1.0, ColorTools.BaseColor.RGB));
                    if (tile.getSymbol() == Symbol.ALMOST_EQUAL_TO) {
                        tile.setSymbol('=');
                    } else {
                        tile.setSymbol(Symbol.ALMOST_EQUAL_TO);
                    }
                }
            }
        }
    }

    public ArrayList<Tile> getNearbyTiles(int y, int x) {
        return getNearbyTiles(y, x, true);
    }
    public ArrayList<Tile> getNearbyTiles(int y, int x, boolean allowDiagonal) {
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

        if(allowDiagonal) {
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
        }

        return result;
    }

    /*
    DEBUG FUNCTIONS
     */



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

    private void init(Map.LevelStyle levelStyle, Roguelike roguelike) {
        this.tileArray = makeMap(height, width, levelStyle, roguelike);
    }

    private Tile[][] makeMap(int height, int width, Map.LevelStyle levelStyle, Roguelike roguelike) {
        if(levelStyle == Map.LevelStyle.WORLD) {
            return LevelFactory.makeWorldMap(height, width, roguelike);
        }
        return LevelFactory.makeDefaultLevel(height, width, mapLevelDepth);
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

        int y, x;
        Tile t;
        do {
            y = Roguelike.rng.nextInt(height);
            x = Roguelike.rng.nextInt(width);
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

    public ArrayList<Actor> getActors() {
        ArrayList<Actor> result = new ArrayList<>();
        for(int y = 0; y < tileArray.length; y++) {
            for(int x = 0; x < tileArray[y].length; x++) {
                if(tileArray[y][x].hasActor()) {
                    result.add(tileArray[y][x].getActor());
                }
            }
        }
        return result;
    }

    public boolean inBounds(int y, int x) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

//    public Tile getUpStairs() {
//        return getRandomTile(Tile.Type.STAIRS_UP);
//    }

    public Tile getDownStairs() {
        return getRandomTile(Tile.Type.STAIRS_DOWN);
    }

    public AStar getAstar() {
        return astar;
    }

    public void setTurnExited(int turnExited) {
        this.turnExited = turnExited;
    }

    public int getTurnExited() {
        return turnExited;
    }

    public Tile getEntryTile() {
        switch(this.levelStyle) {
            case WORLD:
                boolean validTile = false;
                while(!validTile) {
                    Tile t = getRandomTile();
                    switch(t.getType()) {
                        case FOREST:
                        case JUNGLE:
                        case PLAINS:
                        case WORLD_GRASS:
                            return t;
                    }
                }
            case DEFAULT:
                return getRandomTile(Tile.Type.STAIRS_UP);
        }
        System.out.println("getEntryTile() :: There is no entry tile");
        return null;
    }

    private Tile getRandomTile() {
        int y = Roguelike.rng.nextInt(height);
        int x = Roguelike.rng.nextInt(width);
        return tileArray[y][x];
    }

    public boolean canNPCActorTakeTurn() {
        for(Actor a : getActors()) {
            if(a.canTakeTurn()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Tile> getWaterTiles() {
        return waterTiles;
    }

    public int getMapLevelDepth() {
        return mapLevelDepth;
    }

    public void setTileArray(Tile[][] tileArray) {
        this.tileArray = tileArray;
    }

    public ArrayList<Tile> getTileArrayAsList() {
        ArrayList<Tile> tiles = new ArrayList<>();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                tiles.add(tileArray[y][x]);
            }
        }
        return tiles;
    }

    public void setGenerating(boolean generating) {
        this.generating = generating;
    }

    public boolean isGenerating() {
        return generating;
    }

    public Map.LevelStyle getLevelStyle() {
        return levelStyle;
    }
}
