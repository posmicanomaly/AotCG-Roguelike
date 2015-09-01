package posmicanomaly.LibjsrteRoguelikeExample.Component;

import posmicanomaly.libjsrte.Console.Symbol;

import java.awt.*;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 8/31/2015.
 */
public abstract class LevelFactory {
    static final int MAX_ROOM_SIZE = 11;
    static final int MIN_ROOM_SIZE = 5;
    private static Tile[][] makeBlankMap(int height, int width) {
        Tile[][] result = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile.Type type = Tile.Type.WALL;
                Tile tile = new Tile(y, x);
                tile.setType(type);
                result[y][x] = tile;
            }
        }
        processMap(result);
        return result;
    }

    /*
    DEBUG WRAPPERS
     */

    public static void DEBUG_PROCESS_MAP(Tile[][] level) {
        processMap(level, true);
    }

    public static void DEBUG_FLOOD_FILL(Tile[][] level) {
        floodFill(level);
    }

    /*

     */


    /**
     * Wrapper for processMap
     * sets ignoreBuild to false
     * @param level level to process
     */
    private static void processMap(Tile[][] level) {
        processMap(level, false);
    }

    /**
     * Loops through all tiles in supplied level and determines the tile specific information based on the type
     * If ignoreBuild is set to true, it will convert the building types back to their regular types first
     * @param level level to process
     * @param ignoreBuild ignore building types(debug)
     */
    private static void processMap(Tile[][] level, boolean ignoreBuild) {
        for (int y = 0; y < level.length; y++) {
            for (int x = 0; x < level[0].length; x++) {
                Tile t = level[y][x];
                char symbol;
                boolean isBlocked;
                Color color;

                if(!ignoreBuild) {
                /*
                Convert any building types
                 */
                    switch (t.getType()) {
                        case BUILD_FLOOD:
                            t.setType(Tile.Type.FLOOR);
                            break;
                    }
                }
                /*
                Process regular types
                 */
                boolean transparent = true;
                switch (t.getType()) {
                    case FLOOR:
                        symbol = Symbol.MIDDLE_DOT;
                        isBlocked = false;
                        color = Color.gray;
                        break;
                    case WALL:
                        symbol = '#';
                        isBlocked = true;
                        color = Color.white;
                        //
                        transparent = false;
                        break;
                    case PATH:
                        symbol = Symbol.MIDDLE_DOT;
                        isBlocked = false;
                        color = Color.WHITE;
                        break;
                    case BUILD_FLOOD:
                        symbol = Symbol.ALMOST_EQUAL_TO;
                        isBlocked = false;
                        color = Color.ORANGE;
                        break;
                    case WATER:
                        symbol = Symbol.ALMOST_EQUAL_TO;
                        isBlocked = true;
                        color = Color.blue;
                        break;
                    default:
                        symbol = '?';
                        isBlocked = false;
                        color = Color.blue;
                        break;
                }

                t.setSymbol(symbol);
                t.setBlocked(isBlocked);
                t.setColor(color);
                t.setTransparent(transparent);
            }
        }
    }

    /**
     * Fills level with BUILD_FLOOD starting from the first floor tile encountered
     *
     * @param level
     */
    private static void floodFill(Tile[][] level) {
        /*
        Place initial water on the first floor tile encountered, then break
         */
        boolean waterPlaced = false;
        int water = 0;
        for (int y = 0; y < level.length; y++) {
            if (waterPlaced) {
                break;
            }
            for (int x = 0; x < level[0].length; x++) {
                Tile t = level[y][x];
                if (t.getType() == Tile.Type.FLOOR) {
                    t.setType(Tile.Type.BUILD_FLOOD);
                    water++;
                    waterPlaced = true;
                    break;
                }
            }
        }

        System.out.println("Water: " + water);

        /*
        Flood the level
         */
        boolean flooded = false;    // master flooded boolean
        while (!flooded) {          // while we're not flooded yet
            /*
            checkFlooded init as true, if it changes to false that means water was able to spread at some point
            during this current loop. If it is still true by the end, we know that all attempts to spread existing
            water failed, and we are flooded.
             */
            boolean checkFlooded = true;
            // Loop the entire map and spread all the water we can
            for (int y = 0; y < level.length; y++) {
                for (int x = 0; x < level[0].length; x++) {
                    Tile t = level[y][x];
                    // If this is water
                    if (t.getType() == Tile.Type.BUILD_FLOOD) {
                        // Spread the water, store the result
                        boolean spreadSuccess = spreadFlood(t, level);
                        // If we spread this time
                        if (spreadSuccess) {
                            checkFlooded = false;   // Set this false so we check at least one more entire loop
                            //break;    I don't think I need this.
                        }
                    }
                }
            }
            // If the local check as passed, then we went through ALL the water on the level, and NONE of it could
            // spread
            if (checkFlooded) {
                flooded = true; // So we are flooded
            }
        }
    }

    /**
     * Spread BUILD_FLOOD to nearby tiles
     * @param tile tile of Type.BUILD_FLOOD
     * @param level level to use as a reference
     * @return
     */
    private static boolean spreadFlood(Tile tile, Tile[][] level) {
        int x = tile.getX();
        int y = tile.getY();
        Tile tLeft = null;
        Tile tRight = null;
        Tile tUp = null;
        Tile tDown = null;

        // Check bounds to avoid nullReferences
        if (x > 1)
            tLeft = level[y][x - 1];
        if (x < level[0].length - 1)
            tRight = level[y][x + 1];
        if (y > 1)
            tUp = level[y - 1][x];
        if (y < level.length - 1)
            tDown = level[y + 1][x];

        // the return result, init to false
        // If we can spread, we will change this to true
        boolean canSpread = false;

        /*
        For each case, if the tile != null, isBlocked == false, type != WATER
         */
        if (tLeft != null && tLeft.isBlocked() == false && tLeft.getType() != Tile.Type.BUILD_FLOOD) {
            tLeft.setType(Tile.Type.BUILD_FLOOD);
            canSpread = true;
        }
        if (tRight != null && tRight.isBlocked() == false && tRight.getType() != Tile.Type.BUILD_FLOOD) {
            tRight.setType(Tile.Type.BUILD_FLOOD);
            canSpread = true;
        }

        if (tUp != null && tUp.isBlocked() == false && tUp.getType() != Tile.Type.BUILD_FLOOD) {
            tUp.setType(Tile.Type.BUILD_FLOOD);
            canSpread = true;
        }

        if (tDown != null && tDown.isBlocked() == false && tDown.getType() != Tile.Type.BUILD_FLOOD) {
            tDown.setType(Tile.Type.BUILD_FLOOD);
            canSpread = true;
        }


        return canSpread;
    }

    private static boolean canPlaceRoom(int yStart, int xStart, int width, int height, Tile[][] level) {
        if (yStart == 0 || xStart == 0) {
            return false;
        }
        if (yStart + height == level.length - 1) {
            return false;
        }
        if (xStart + width == level[0].length - 1) {
            return false;
        }
        if (yStart + height >= level.length || xStart + width >= level[0].length) {
            return false;
        }

        // Check for overlap with another room
        boolean overlap = false;
        for (int y = yStart; y < yStart + height; y++) {
            for (int x = xStart; x < xStart + width; x++) {
                if (overlap) {
                    return false;
                }
                if (level[y][x].getType() == Tile.Type.FLOOR) {
                    overlap = true;
                }
            }
        }
        return true;
    }

    private static void placeRoom(int yStart, int xStart, int width, int height, Tile[][] level) {
        for (int y = yStart; y < yStart + height; y++) {
            for (int x = xStart; x < xStart + width; x++) {
                level[y][x].setType(Tile.Type.FLOOR);
            }
        }
    }

    private static boolean isFlooded(Tile[][] level) {
        for(int y = 0; y < level.length; y++) {
            for(int x = 0; x < level[0].length; x++) {
                Tile t = level[y][x];
                if(t.getType() == Tile.Type.FLOOR) {
                    return false;
                }
            }
        }
        return true;
    }
    public static Tile[][] makeDefaultLevel(int height, int width) {
        Tile[][] result = null;
        boolean playableMap = false;

        int levelCreationTries = 0;
        while(!playableMap) {
            levelCreationTries++;
            result = makeBlankMap(height, width);
            Random rng = new Random();
            final int NUMBER_OF_ROOMS = height * width;
            for (int r = 0; r < NUMBER_OF_ROOMS; r++) {
                int y = rng.nextInt(height);
                int x = rng.nextInt(width);
                int roomHeight = rng.nextInt((MAX_ROOM_SIZE - MIN_ROOM_SIZE) + 1) + MIN_ROOM_SIZE;
                int roomWidth = rng.nextInt(9) + 3;
                if (canPlaceRoom(y, x, roomHeight, roomWidth, result)) {
                    placeRoom(y, x, roomHeight, roomWidth, result);
                }
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile t = result[y][x];
                    if (t.getType().equals(Tile.Type.WALL)) {
                        Tile tLeft = null;
                        Tile tRight = null;
                        Tile tUp = null;
                        Tile tDown = null;
                        if (x > 1)
                            tLeft = result[y][x - 1];
                        if (x < width - 1)
                            tRight = result[y][x + 1];
                        if (y > 1)
                            tUp = result[y - 1][x];
                        if (y < height - 1)
                            tDown = result[y + 1][x];

                        boolean verticalDivide = false;
                        boolean horizontalDivide = false;
                        if (tUp != null && tDown != null) {
                            if (tUp.getType() == Tile.Type.FLOOR && tDown.getType() == Tile.Type.FLOOR) {
                                verticalDivide = true;
                            }
                        }
                        if (tLeft != null && tRight != null) {
                            if (tLeft.getType() == Tile.Type.FLOOR && tRight.getType() == Tile.Type.FLOOR) {
                                horizontalDivide = true;
                            }
                        }

                        if (horizontalDivide || verticalDivide) {
                            t.setType(Tile.Type.PATH);
                        }
                    }
                }
            }
            processMap(result);
            floodFill(result);
            if(isFlooded(result)) {
                playableMap = true;
            }
        }

        System.out.println("Level took " + levelCreationTries + " tries");
        processMap(result);
        return result;
    }
}
