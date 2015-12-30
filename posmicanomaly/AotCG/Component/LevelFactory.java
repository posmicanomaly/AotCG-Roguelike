package posmicanomaly.AotCG.Component;

import posmicanomaly.AotCG.Game.Roguelike;
import posmicanomaly.libjsrte.Console.Symbol;
import posmicanomaly.libjsrte.Util.ColorTools;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 8/31/2015.
 */
public abstract class LevelFactory {
    static final int MAX_ROOM_SIZE = 11;
    static final int MIN_ROOM_SIZE = 3;
    // Setting PERIMETER_THICKNESS to 0 gives me walls of 1. Bad math somewhere.
    static final int PERIMETER_THICKNESS = 0;

    private static Tile[][] makeBlankMap(int height, int width) {
        return makeMapFilledWithType(height, width, Tile.Type.WALL);
    }
    private static Tile[][] makeMapFilledWithType(int height, int width, Tile.Type fill) {
        Tile[][] result = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile.Type type = fill;
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
     *
     * @param level level to process
     */
    private static void processMap(Tile[][] level) {
        processMap(level, false);
    }

    /**
     * Used to init a tile during gameplay, will call to ignoreBuild tiles
     * @param t
     */
    public static void initTile(Tile t) {
        initTile(t, true);
    }

    private static void initTile(Tile t, boolean ignoreBuild) {
        char symbol;
        boolean isBlocked;
        Color color;
        Color backgroundColor;

        if (!ignoreBuild) {
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
        backgroundColor = Color.black;
        switch (t.getType()) {
            case FLOOR:
                symbol = Symbol.MIDDLE_DOT;
                isBlocked = false;
                color = ColorTools.varyColor(Colors.FLOOR, 0.8, 1.0, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.FLOOR_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case WALL:
                symbol = '#';
                isBlocked = true;
                color = Colors.WALL;
                backgroundColor = ColorTools.varyColor(Colors.WALL_BG, 0.7, 1.0, ColorTools.BaseColor.RGB);
                //
                transparent = false;
                break;
            case WALL_SECRET:
                symbol = ' ';
                isBlocked = false;
                color = Colors.WALL;
                backgroundColor = ColorTools.varyColor(Colors.WALL_BG, 0.7, 1.0, ColorTools.BaseColor.RGB);
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
                isBlocked = false;
                color = Colors.WATER;
                backgroundColor = Colors.WATER_BG;
                break;
            case CAVE_GRASS:
                symbol = '"';
                isBlocked = false;
                color = ColorTools.varyColor(Colors.CAVE_GRASS, 0.5, 1.0, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.CAVE_GRASS_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case DOOR:
                //symbol ='+';
                symbol = '\u041f';
                isBlocked = false;
                color = ColorTools.varyColor(Colors.DOOR, 0.5, 1.0, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.DOOR_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                transparent = false;
                break;
            case STAIRS_UP:
                symbol = '<';
                isBlocked = false;
                color = Color.GREEN;
                backgroundColor = ColorTools.varyColor(Colors.FLOOR_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case STAIRS_DOWN:
                symbol = '>';
                isBlocked = false;
                color = Color.RED;
                backgroundColor = ColorTools.varyColor(Colors.FLOOR_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case WORLD_GRASS:
                symbol = Symbol.ALMOST_EQUAL_TO;
                isBlocked = false;
                color = ColorTools.varyColor(Colors.CAVE_GRASS, 0.5, 1.0, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.CAVE_GRASS_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case CAVE_OPENING:
                symbol = 'o';
                isBlocked = false;
                color = Color.WHITE;
                backgroundColor = ColorTools.varyColor(Colors.CAVE_GRASS_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case FOREST:
                symbol = '\u2663';
                if(Roguelike.rng.nextInt(100) < 30) {
                    symbol = '\u2660';
                }
                isBlocked = false;
                color =  ColorTools.varyColor(Colors.FOREST, 0.7, 1.0, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.CAVE_GRASS_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case MOUNTAIN:
                symbol = '\u25B2';
                isBlocked = false;
                color =  ColorTools.varyColor(Colors.MOUNTAIN, 0.9, 1.0, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.CAVE_GRASS_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
                break;
            case SAND:
                symbol = '\u2591';
                isBlocked = false;
                color = ColorTools.varyColor(Colors.SAND, 0.7, .8, ColorTools.BaseColor.RGB);
                backgroundColor = ColorTools.varyColor(Colors.CAVE_GRASS_BG, 0.5, 1.0, ColorTools.BaseColor.RGB);
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
        t.setBackgroundColor(backgroundColor);
        t.setTransparent(transparent);
    }

    /**
     * Loops through all tiles in supplied level and determines the tile specific information based on the type
     * If ignoreBuild is set to true, it will convert the building types back to their regular types first
     *
     * @param level       level to process
     * @param ignoreBuild ignore building types(debug)
     */
    private static void processMap(Tile[][] level, boolean ignoreBuild) {
        for (int y = 0; y < level.length; y++) {
            for (int x = 0; x < level[0].length; x++) {
                Tile t = level[y][x];
                initTile(t, ignoreBuild);
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
                        boolean spreadSuccess = spreadBuildFlood(t, level);
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
     *
     * @param tile  tile of Type.BUILD_FLOOD
     * @param level level to use as a reference
     * @return
     */
    private static boolean spreadBuildFlood(Tile tile, Tile[][] level) {
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

    /**
     * Checks if a room at y, x, with width and height as specified, in level, can be placed without overlapping
     * another floor tile
     *
     * @param yStart
     * @param xStart
     * @param width
     * @param height
     * @param level
     * @return
     */
    private static boolean canPlaceRoom(int yStart, int xStart, int width, int height, Tile[][] level) {
        if (yStart <= PERIMETER_THICKNESS || xStart <= PERIMETER_THICKNESS) {
            return false;
        }
        if (yStart + height == level.length - PERIMETER_THICKNESS) {
            return false;
        }
        if (xStart + width == level[0].length - PERIMETER_THICKNESS) {
            return false;
        }
        if (yStart + height >= level.length - PERIMETER_THICKNESS || xStart + width >= level[0].length - PERIMETER_THICKNESS) {
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

    /**
     * Places a room at y, x, with width and height, in level
     * Sets type to floor
     *
     * @param yStart
     * @param xStart
     * @param width
     * @param height
     * @param level
     */
    private static void placeRoom(int yStart, int xStart, int width, int height, Tile[][] level) {
        for (int y = yStart; y < yStart + height; y++) {
            for (int x = xStart; x < xStart + width; x++) {
                level[y][x].setType(Tile.Type.FLOOR);
            }
        }
    }

    /**
     * Checks if a level is completely flooded, by returning false if any floor is present
     *
     * @param level
     * @return
     */
    private static boolean isFlooded(Tile[][] level) {
        for (int y = 0; y < level.length; y++) {
            for (int x = 0; x < level[0].length; x++) {
                Tile t = level[y][x];
                if (t.getType() == Tile.Type.FLOOR) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Adds a Tile.Type in a pooling manner, which means it spreads in cardinal directions until the amount is reached
     * @param level
     * @param amount
     * @param feature
     */
    private static void addPoolFeature(Tile[][] level, int amount, Tile.Type feature) {
        Random rng = Roguelike.rng;
        for(int i = 0; i < amount; i++) {
            boolean startTileFound = false;
            Tile startTile = null;
            while(!startTileFound) {
                int y = rng.nextInt(level.length);
                int x = rng.nextInt(level[0].length);
                if(!level[y][x].isBlocked()) {
                    startTileFound = true;
                    startTile = level[y][x];
                }
            }
            startTile.setType(feature);
            if(startTileFound) {
                int strength = rng.nextInt(15);
                spreadPool(level, startTile, feature, 0, strength);
            }
        }
    }

    /**
     * recursive spreadPool method to use with addPoolFeature. Uses a current and max value to know when to stop.
     * Spreads the feature in cardinal directions if the tile is not null or blocked.
     * @param level
     * @param t
     * @param feature
     * @param current
     * @param max
     */
    private static void spreadPool(Tile[][] level, Tile t, Tile.Type feature, int current, int max) {
        if(current == max) {
            return;
        }
        int x = t.getX();
        int y = t.getY();
        Tile tLeft = null;
        Tile tRight = null;
        Tile tUp = null;
        Tile tDown = null;

        // Check bounds to avoid nullReferences
        if (x > 0)
            tLeft = level[y][x - 1];
        if (x < level[0].length - 1)
            tRight = level[y][x + 1];
        if (y > 0)
            tUp = level[y - 1][x];
        if (y < level.length - 1)
            tDown = level[y + 1][x];

        ArrayList<Tile> tiles = new ArrayList<Tile>();
                    /*
                    For each case, if the tile != null, isBlocked == false, type != WATER
                     */
        if (tLeft != null && tLeft.isBlocked() == false && tLeft.getType() != feature) {
            tLeft.setType(feature);
            tiles.add(tLeft);
        }
        if (tRight != null && tRight.isBlocked() == false && tRight.getType() != feature) {
            tRight.setType(feature);
            tiles.add(tRight);
        }

        if (tUp != null && tUp.isBlocked() == false && tUp.getType() != feature) {
            tUp.setType(feature);
            tiles.add(tUp);
        }

        if (tDown != null && tDown.isBlocked() == false && tDown.getType() != feature) {
            tDown.setType(feature);
            tiles.add(tDown);
        }
        for(Tile nextTile : tiles) {
            spreadPool(level, nextTile, feature, current + 1, max);
        }
    }

    public static Tile[][] makeWorldMap(int height, int width) {
        System.out.println("makeWorldMap");
        Tile[][] result = null;

        result = makeMapFilledWithType(height, width, Tile.Type.WORLD_GRASS);

        processMap(result);

        Random rng = Roguelike.rng;
        //addPoolFeature(result, rng.nextInt(10) + 5, Tile.Type.WATER);
        //addStairs(result);
        addPoolFeature(result, rng.nextInt(20) + 5, Tile.Type.FOREST);
        addPoolFeature(result, rng.nextInt(20) + 5, Tile.Type.MOUNTAIN);
        addPoolFeature(result, rng.nextInt(20) + 5, Tile.Type.WATER);
        addCaveOpenings(result);
        addShores(result);
        processMap(result);
        System.out.println("makeWorldMap done");
        return result;
    }

    /**
     * Returns a Tile[][] of "defaultLevel"
     * defaultLevel is rooms between MIN_ROOM_SIZE and MAX_ROOM_SIZE being placed in non overlapping fashion using
     * random coordinates.
     * The walls are then checked if they're "thin", and then replaced with a floor if they are.
     * "Thin" meaning two floor tiles are adjacent horizontally or vertically
     * Then the map is flooded with BUILD_FLOOD to check that all rooms are accessible.
     * If it is, then the Tile[][] is returned
     * If it is not, the process repeats until it is.
     *
     * @param height height of map
     * @param width  width of map
     * @return
     */
    public static Tile[][] makeDefaultLevel(int height, int width) {
        Tile[][] result = null;
        boolean playableMap = false;

        int levelCreationTries = 0;
        // While map is not playable(rooms not accessible)
        while (!playableMap) {
            levelCreationTries++;
            // Make a blank map filled with walls
            result = makeBlankMap(height, width);
            Random rng = Roguelike.rng;
            // Number of rooms to try and place
            final int NUMBER_OF_ROOMS = height * width;

            for (int r = 0; r < NUMBER_OF_ROOMS; r++) {
                // Random coordinates
                int y = rng.nextInt(height);
                int x = rng.nextInt(width);

                // Random dimensions
                int roomHeight = rng.nextInt((MAX_ROOM_SIZE - MIN_ROOM_SIZE) + 1) + MIN_ROOM_SIZE;
                int roomWidth = rng.nextInt((MAX_ROOM_SIZE - MIN_ROOM_SIZE) + 1) + MIN_ROOM_SIZE;

                // Check and place if possible
                if (canPlaceRoom(y, x, roomHeight, roomWidth, result)) {
                    placeRoom(y, x, roomHeight, roomWidth, result);
                }
            }

            /*
            Find the thin walls
             */

            ArrayList<Tile> thinWalls = new ArrayList<Tile>();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile t = result[y][x];

                    // If tile is a WALL
                    if (t.getType().equals(Tile.Type.WALL)) {
                        // Get the surrounding tiles
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

                        // Check vertical divide
                        if (tUp != null && tDown != null) {
                            if (tUp.getType() == Tile.Type.FLOOR && tDown.getType() == Tile.Type.FLOOR) {
                                verticalDivide = true;
                            }
                        }

                        // Check horizontal divide
                        if (tLeft != null && tRight != null) {
                            if (tLeft.getType() == Tile.Type.FLOOR && tRight.getType() == Tile.Type.FLOOR) {
                                horizontalDivide = true;
                            }
                        }

                        // If either are true
                        if (horizontalDivide || verticalDivide) {
                            // Set it as a PATH (DEBUG)
                            thinWalls.add(t);
                        }
                    }
                }
            }
            int maxAttempts = 50;
            for(int i = 0; i < maxAttempts; i++) {
                // Process the map to set tile properties
                processMap(result);
                // FloodFill the map
                floodFill(result);

                // If the entire map is flooded, map is playable
                if (isFlooded(result)) {
                    playableMap = true;
                    break;
                } else {
                    int findTimeout = 0;
                    Tile t;
                    do {
                        // Pick a random thin wall and break it
                        t = thinWalls.get(rng.nextInt(thinWalls.size()));
                        findTimeout++;
                    } while(!hasFloodAndFloorNeighborTiles(t, result, height, width) && findTimeout < 50);

                    if(hasFloodAndFloorNeighborTiles(t, result, height, width)) {
                        // Break the wall
                        t.setType(Tile.Type.PATH);
                        thinWalls.remove(t);
                    }
                }
            }
        }

        System.out.println("Level took " + levelCreationTries + " tries");
        processMap(result);

        addMonsters(result);
        Random rng = Roguelike.rng;
        addPoolFeature(result, rng.nextInt(30) + 5, Tile.Type.CAVE_GRASS);
        addPoolFeature(result, rng.nextInt(10) + 5, Tile.Type.WATER);
        addDoorways(result, 4);
        addStairs(result);
        processMap(result);
        return result;
    }

    private static void addDoorways(Tile[][] result, int secret) {
        ArrayList<Tile> eligibileDoorways = getEligibleDoorways(result);

        int secretDoorwaysPlaced = 0;
        if(eligibileDoorways.size() - secret < 0) {
            secret = 0;
        }
        for(Tile t : eligibileDoorways) {
            if(secretDoorwaysPlaced < secret) {
                t.setType(Tile.Type.WALL_SECRET);
                secretDoorwaysPlaced++;
            } else {
                t.setType(Tile.Type.DOOR);
            }
        }
    }

    private static void addMonsters(Tile[][] result) {
        int height = result.length;
        int width = result[0].length;
        boolean giantAdded = false;
        int actorsToAdd = 10;
        int actorsAdded = 0;
        do{
            Random rng = Roguelike.rng;
            int y = rng.nextInt(height);
            int x = rng.nextInt(width);
            if (result[y][x].getType() == Tile.Type.FLOOR && !result[y][x].hasActor()) {
                Actor actor;
                if(!giantAdded) {
                    actor = ActorFactory.createActor(ActorFactory.TYPE.GIANT, result[y][x]);
                    giantAdded = true;
                } else {
                    int rngResult = rng.nextInt(100);
                    if(rngResult < 50) {
                        actor = ActorFactory.createActor(ActorFactory.TYPE.RAT, result[y][x]);
                    } else {
                        actor = ActorFactory.createActor(ActorFactory.TYPE.BAT, result[y][x]);
                    }
                }

                result[y][x].setActor(actor);
                actorsAdded++;
            }
        } while(actorsAdded < actorsToAdd);
    }

    private static ArrayList<Tile> getEligibleDoorways(Tile[][] result) {
        ArrayList<Tile.Type> validBaseTypes = new ArrayList<Tile.Type>();
        validBaseTypes.add(Tile.Type.CAVE_GRASS);
        validBaseTypes.add(Tile.Type.FLOOR);

        ArrayList<Tile> tiles = new ArrayList<Tile>();
        int height = result.length;
        int width = result[0].length;
        for(int y = 0; y < result.length; y++) {
            for(int x = 0; x < result[y].length; x++) {
                Tile currentTile = result[y][x];
                boolean validType = false;
                for(Tile.Type type : validBaseTypes) {
                    if(currentTile.getType() == type) {
                        validType = true;
                        break;
                    }
                }
                if(validType) {

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
                    ArrayList<Tile> neighborTiles = new ArrayList<Tile>();

                    boolean horizontalWalls = false;
                    boolean verticalWalls = false;
                    if(tLeft != null && tLeft.getType() == Tile.Type.WALL) {
                        if(tRight != null && tRight.getType() == Tile.Type.WALL) {
                            horizontalWalls = true;
                        }
                    }

                    if(tUp != null && tUp.getType() == Tile.Type.WALL) {
                        if(tDown != null && tDown.getType() == Tile.Type.WALL) {
                            verticalWalls = true;
                        }
                    }

                    if(horizontalWalls && verticalWalls) {
                        // Don't put a door here, would look bad
                    } else if(horizontalWalls || verticalWalls) {
                        tiles.add(currentTile);
                    }
                }
            }
        }

        return tiles;
    }

    private static void addShores(Tile[][] result) {
        for(int y = 0; y < result.length; y++) {
            for(int x = 0; x < result[y].length; x++) {
                Tile t = result[y][x];
                if(t.getType() == Tile.Type.WATER) {
                    continue;
                }
                for(Tile nextTile : getNearbyTiles(t, result)) {
                    if(nextTile == null)
                        continue;
                    if(nextTile.getType() == Tile.Type.WATER) {
                        t.setType(Tile.Type.SAND);
                        break;
                    }
                }
            }
        }
    }

    private static ArrayList<Tile> getNearbyTiles(Tile t, Tile[][] result) {
        // Get the surrounding tiles
        int width = result[0].length;
        int height = result.length;
        int x = t.getX();
        int y = t.getY();
        Tile tLeft = null;
        Tile tRight = null;
        Tile tUp = null;
        Tile tDown = null;
        Tile ULeft = null;
        Tile URight = null;
        Tile DLeft = null;
        Tile DRight = null;

        if (x > 0)
            tLeft = result[y][x - 1];
        if (x < width - 1)
            tRight = result[y][x + 1];

        if (y > 0) {
            tUp = result[y - 1][x];
            if (x > 0)
                ULeft = result[y - 1][x - 1];
            if (x < width - 1)
                URight = result[y - 1][x + 1];
        }

        if (y < height - 1) {
            tDown = result[y + 1][x];
            if (x > 0)
                DLeft = result[y + 1][x - 1];
            if (x < width - 1)
                DRight = result[y + 1][x + 1];
        }

        ArrayList<Tile> tiles = new ArrayList<Tile>();

        tiles.add(tLeft);
        tiles.add(tRight);
        tiles.add(tUp);
        tiles.add(tDown);
        tiles.add(ULeft);
        tiles.add(URight);
        tiles.add(DLeft);
        tiles.add(DRight);

        return tiles;
    }

    private static boolean hasFloodAndFloorNeighborTiles(Tile t, Tile[][] result, int height, int width) {
        // Get the surrounding tiles
        int x = t.getX();
        int y = t.getY();
        Tile tLeft = null;
        Tile tRight = null;
        Tile tUp = null;
        Tile tDown = null;
        if (x > 0)
            tLeft = result[y][x - 1];
        if (x < width - 1)
            tRight = result[y][x + 1];
        if (y > 0)
            tUp = result[y - 1][x];
        if (y < height - 1)
            tDown = result[y + 1][x];

        ArrayList<Tile> tiles = new ArrayList<Tile>();

        tiles.add(tLeft);
        tiles.add(tRight);
        tiles.add(tUp);
        tiles.add(tDown);
        boolean nearFloor = false;
        boolean nearFlood = false;
        for(Tile tile : tiles) {
            if(tile != null) {
                if(tile.getType() == Tile.Type.FLOOR) {
                    nearFloor = true;
                } else if(tile.getType() == Tile.Type.BUILD_FLOOD) {
                    nearFlood = true;
                }
            }
        }

        return nearFloor && nearFlood;
    }


    private static void addCaveOpenings(Tile[][] result) {
        Random rng = Roguelike.rng;
        int y, x;
        Tile t;

        int openingsToAdd = 50;
        int openingsAdded = 0;
        boolean validTile = false;

        do {
            y = rng.nextInt(result.length);
            x = rng.nextInt(result[0].length);
            t = result[y][x];
            switch(t.getType()) {
                case WORLD_GRASS:
                    validTile = true;
                    break;
                default:
                    break;
            }

            if(validTile) {
                t.setType(Tile.Type.CAVE_OPENING);
                openingsAdded++;
            }
        } while (openingsAdded < openingsToAdd);

    }

    private static void addStairs(Tile[][] result) {
        Random rng = Roguelike.rng;
        int y, x;
        Tile t;
        boolean validTile = false;
        do {
            y = rng.nextInt(result.length);
            x = rng.nextInt(result[0].length);
            t = result[y][x];
            switch(t.getType()) {
                case FLOOR:
                case CAVE_GRASS:
                case WORLD_GRASS:
                    validTile = true;
                    break;
                default:
                    break;
            }
        } while (!validTile);
        t.setType(Tile.Type.STAIRS_UP);
        validTile = false;
        do {
            y = rng.nextInt(result.length);
            x = rng.nextInt(result[0].length);
            t = result[y][x];
            switch(t.getType()) {
                case FLOOR:
                case CAVE_GRASS:
                case WORLD_GRASS:
                    validTile = true;
                    break;
                default:
                    break;
            }
        } while (!validTile);
        t.setType(Tile.Type.STAIRS_DOWN);
    }
}
