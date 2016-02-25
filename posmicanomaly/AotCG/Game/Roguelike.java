package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.*;
import posmicanomaly.AotCG.Screen.Title;
import posmicanomaly.libjsrte.Console.Console;
import posmicanomaly.libjsrte.Window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 *
 * TODO features:
 * - Items, should be able to pick up if strength allows. Strength should be equal to lbs you can lift
 * - Projectiles, should be able to throw things at least.
 * - Towns, maybe rest here, shops
 * - Merchants, should be able to buy stuff
 * - How big is a world tile? 1 mile, 2 miles?
 * - Time resolution
 * - Energy per turn, so some things can move faster than you
 * - Idle function
 * - Horizontal dungeon movement
 * - What is a dungeon anyway? Why is it here?
 * - Get rid of the G
 * - Get a real name, AotCG sucks
 * - Drinking to stay hydrated
 * - Eating to stay satiated
 * - Should auto eat and drink from things marked as safe, like EQ2 where you had auto slot for desired drink/food?
 * - Not eating/drinking leads to sickness?
 * - HP should be a means of how healthy someone is. Lower HP, harder to do things
 * - Mouse (in)
 * - Multiple items on single tile
 * - Player pick up items
 * - Fix opacity problem with windows on redraw, clear console probably works best.
 */
public class Roguelike {
    private static final int MAP_DEPTH = 20;
    public static final boolean SHOW_MAP_CREATION = false;
    public static boolean RENDER_BETWEEN_TURNS = false;

    public static Random rng;
    public static int turns;
    public static MapSymbols mapSymbols = new MapSymbols("config/symbols.txt");


    protected Console menuWindow;
    protected Gui gui;
    protected Title title;
    protected State currentState;

    Window window;
    int fontSize = 12;
    int windowHeight = 70;
    //int windowWidth = 135;
    int windowWidth = 106;
    int messageHeight = 18;
    int messageWidth;
    int mapHeight;
    int mapWidth;

    // mouse testing
    int mx;
    int my;
    int lastMx;
    int lastMy;

    long lastRenderTime;
    long defaultRefreshIntervalMs = 1000;
    long refreshIntervalMs;
    long minFrameSpeed = 40;
    long lastFrameDrawTime;
    int currentFrames;
    int lastFramesPerSecond;
    long fpsTimerStart;
    int gameInformationConsoleHeight = windowHeight;
    int gameInformationConsoleWidth = 14;
    boolean showMenu;
    boolean showInventory;
    boolean giantSlain;
    boolean showVictoryConsole;
    boolean showDefeatConsole;
    Map map;
    boolean redrawGame;
    private Console mapConsole;
    private Input input;
    private Process process;
    private Actor player;
    public boolean runPlayerBot = false;

    private int playerMapX;
    private int playerMapY;
    private int playerMapZ;

    private KeyEvent lastKeyEvent;
    private MouseEvent lastMouseEvent;
    private int gameLoopsWithoutInput;
    private Console rootConsole;
    private Render render;

    public Roguelike() {
        System.setProperty("sun.java2d.opengl", "true");

        this.window = new Window(this.windowHeight, this.windowWidth, "AotCG", fontSize);
        rootConsole = this.window.getMainPanel().getRootConsole();
        //this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - gameInformationConsoleWidth;

        // HACK ADJUST
        int MAP_WIDTH_ADJ = 0;
        int MAP_HEIGHT_ADJ = 0;
        this.mapWidth = this.windowWidth - MAP_WIDTH_ADJ - gameInformationConsoleWidth;
        this.mapHeight = this.windowHeight - MAP_HEIGHT_ADJ - this.messageHeight;

        this.messageWidth = mapWidth;
        this.gui = new Gui(this);

        rng = new Random();
        input = new Input(this);
        process = new Process(this);

        refreshIntervalMs = defaultRefreshIntervalMs;
        gameLoopsWithoutInput = 0;

        initializeGameEnvironment();
        enableRendering();

        render = new Render(this);
        if (RENDER_BETWEEN_TURNS) {
            render.start();
        }

        LevelFactory.setRoguelike(this);

        while (true) {
            gameLoop();
        }
    }

    protected void initializeGameEnvironment() {
        currentState = State.TITLE;
        showVictoryConsole = false;
        showDefeatConsole = false;
        runPlayerBot = false;
        turns = 0;
        lastRenderTime = 0;

        // Set seed
        //rng.setSeed(12345);

        initTitleScreen();

        // Set up map
        this.mapConsole = new Console(this.mapHeight, this.mapWidth);
        // Map console border doesn't play nice, need to fix this either in libjstre or make it an enhancedconsole
        mapConsole.setBorder(true);
        mapConsole.setBorderColor(Color.gray);
        mapConsole.setBorderStyle(Console.BorderStyle.DOUBLE);

        // Initialize the GUI
        gui.initGui();
        input.connectToGui(gui);
    }

    protected void enableRendering() {
        this.window.getMainPanel().setRender(true);
    }

    protected void initializeMap() {
        if(mapConsole.hasBorder()) {
            this.map = new Map(mapHeight - 2, mapWidth - 2, Roguelike.MAP_DEPTH, this);
        } else {
            this.map = new Map(mapHeight, mapWidth, Roguelike.MAP_DEPTH, this);
        }

        map.getCurrentLevel().finalizeLevel();
    }

    protected void setupNewGame() {
        initializeMap();
        initializePlayer();
        playerMapX = player.getTile().getX();
        playerMapY = player.getTile().getY();
        gui.connectPlayer();
        gui.connectMap();

        // Calculate vision for all actors on the map
        for (Actor a : map.getCurrentLevel().getActors()) {
            process.calculateVision(a);
        }
    }

    protected void startGame() {
        // Copy the map to mapConsole(buffer)
        this.copyMapToBuffer();
        // Allow rendering
        this.window.getMainPanel().setRender(true);

        // Set lastKeyEvent so we can reference a change
        this.lastKeyEvent = this.window.getLastKeyEvent();
        this.lastMouseEvent = this.window.getMouse().getLastMouseEvent();

        redrawGame = true;

        // Welcome message
        String welcome;
        switch(player.getTile().getType()) {
            case JUNGLE:
                welcome = "You stand amongst the towering trees of a jungle, vines surrounding you at every turn.";
                break;
            case FOREST:
                welcome = "The forest obscures your immediate vision.";
                break;
            case HILL:
                welcome = "You stand on top of a hill, overlooking the land below.";
                break;
            case MOUNTAIN:
                welcome = "Mountain start";
                break;
            case PLAINS:
                welcome = "Vast plains stretch out as far as the eye can see. The world opens up before you.";
                break;
            default:
                welcome = "I don't know where you are.";
                break;
        }
        gui.getMessageConsole().addMessage(welcome, Colors.EXPERIENCE);
    }

    //==========================================================================================================
    //                                  gameLoop()
    //==========================================================================================================
    private void gameLoop() {
        // Check for key input
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Mouse testing
        boolean mouseCoordinatesChanged = updateMouseLocation();
        if(!this.window.getMouse().getLastMouseEvent().equals(this.lastMouseEvent)){
            // mouse event? click?
            int y = lastMy;
            int x = lastMx;
            String message = "Clicked ";
            if(currentState == State.PLAYING) {
                if (isMouseOnMap()) {
                    x = getMouseOnMapX();
                    y = getMouseOnMapY();
                    Tile clickedTile = map.getCurrentLevel().getTile(y, x);
                    message += "map at ";
                    boolean allowMoveIntoUnexplored = false;
                    boolean allowMove = false;
                    if (allowMoveIntoUnexplored) {
                        if (!clickedTile.isBlocked()) {
                            allowMove = true;
                        } else {
                            gui.getMessageConsole().addMessage("Can't move there", Color.red);
                        }
                    } else {
                        if (clickedTile.isExplored() && !clickedTile.isBlocked()) {
                            allowMove = true;
                        } else {
                            gui.getMessageConsole().addMessage("Can't move there", Color.red);
                        }
                    }
                    if (allowMove) {
                        player.setCurrentPath(map.getCurrentLevel().getAstar().getShortestPath(player.getTile(), clickedTile));
                        if (player.getCurrentPath() == null) {
                            gui.getMessageConsole().addMessage("Can't move there: there is no path?", Color.red);
                        }
                        if (player.getCurrentPath().size() == 0) {
                            System.out.println("Player path is 0, setting to null");
                            player.setCurrentPath(null);
                        }
                    }
                }
                System.out.println(message + y + "x" + x);
            }
            this.lastMouseEvent = this.window.getMouse().getLastMouseEvent();
            render.drawGame(getRootConsole());
        }
        else if(mouseCoordinatesChanged) {
            render.drawGame(getRootConsole(), Render.Reason.MOUSE_MOVED);
            // Mouse
        }
        if (runPlayerBot) {
            PlayerAIDecision playerAIDecision = makePlayerAIDecision();
            switch (playerAIDecision) {
                case ACTUATETILE:
                    turns++;
                    redrawGame = true;
                    process.calculateVision(player);
                    break;
            }
        }

        // Check if not equal to the last keyevent, or if the player has a current path
        // HOWEVER, during game init, there is no player, so first check that the player is not null.
        // todo: continue to decouple the initialization code.
        if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent) || (player != null && player.getCurrentPath() != null)) {
            /**
             * Check for game state or victory/defeat console displayed
             */
            if(currentState == State.PLAYING) {
                checkWinConditions();
            }
            processGameState(currentState);

            // Set lastKeyEvent to this one that we just used, so we do not enter loop again
            this.lastKeyEvent = this.window.getLastKeyEvent();

        }


        if (!RENDER_BETWEEN_TURNS) {
            if (redrawGame) {
                render.renderSingleFrame();
            }
        }
    }

    //==========================================================================================================

    private ArrayList<Tile> getEdgeOfExploredTiles(int range, boolean allowBlockedTiles) {
        ArrayList<Tile> edges = new ArrayList<>();
        Level level = map.getCurrentLevel();
        int tilesAdded = 0;
        int tilesBlocked = 0;
        int tilesOutOfRange = 0;
        // Check all explored tiles
        for(Tile t : getExploredTiles()) {
            // Check if its in range to check
            // -1 bypasses range check, checks everything
            if(range != -1) {
                int d = Math.abs(player.getTile().getX() - t.getX()) + Math.abs(player.getTile().getY() - t.getY());
                if (d > range) {
                    tilesOutOfRange++;
                    continue;
                }
            }
            // Check if explored tile has an unexplored neighbor
            for(Tile n : level.getNearbyTiles(t.getY(), t.getX())) {
                boolean add = false;
                // If a neighbor is unexplored, this is an "edge" tile
                if(!n.isExplored()) {
                    if(allowBlockedTiles) {
                        add = true;
                    }
                    else {
                        if(!n.isBlocked()) {
                           add = true;
                        }
                    }
                    // We should add
                    if(add) {
                        // If it doesn't already have it
                        if (!edges.contains(t)) {
                            edges.add(t);
                            tilesAdded++;
                            // We only need to see one unexplored tile to consider it a neighbor
                            break;
                        }
                    } else {
                        // Sanity check
                        if(edges.contains(t)) {
                            System.out.println("A tile not added was previously added");
                        }
                        tilesBlocked++;
                    }
                }
            }
        }
        //System.out.println("getEdgeOfExploredTiles() :: tileAdded: " + tilesAdded + " tilesBlocked: " + tilesBlocked + " tilesOutOfRange: " + tilesOutOfRange);
        return edges;
    }

    private ArrayList<Tile> getExploredTiles() {
        ArrayList<Tile> explored = new ArrayList<>();
        Level level = map.getCurrentLevel();
        for(int y = 0; y < level.getTileArray().length; y++) {
            for(int x = 0; x < level.getTileArray()[y].length; x++) {
                Tile t = level.getTile(y, x);
                if(t.isExplored()) {
                    explored.add(t);
                }
            }
        }
        return explored;
    }

    private ArrayList<Tile> getUnexploredTiles() {
        ArrayList<Tile> unexplored = new ArrayList<>();
        Level level = map.getCurrentLevel();
        for(int y = 0; y < level.getTileArray().length; y++) {
            for(int x = 0; x < level.getTileArray()[y].length; x++) {
                Tile t = level.getTile(y, x);
                if(!t.isExplored()) {
                    unexplored.add(t);
                }
            }
        }
        return unexplored;
    }

    private Tile getClosestTileFromList(Tile source, ArrayList<Tile> list, boolean allowBlockedTiles) {
        Tile closest = null;
        int closestDistance = 0;
        for(Tile t : list) {
            if(!allowBlockedTiles) {
                if(t.isBlocked()) {
                    continue;
                }
            }
            if (t.equals(source)) {
                continue;
            }
            if (closest == null) {
                closest = t;
                closestDistance = Math.abs(source.getX() - t.getX()) + Math.abs(source.getY() - t.getY());
            }
            else {
                int newDistance = Math.abs(source.getX() - t.getX()) + Math.abs(source.getY() - t.getY());
                if (newDistance < closestDistance) {
                    closest = t;
                    closestDistance = newDistance;
                }
            }
        }
        return closest;
    }

    private ArrayList<Tile> getShortestPathOfPaths(ArrayList<ArrayList<Tile>> pathList) {
        ArrayList<Tile> shortestPath = null;
        for(ArrayList<Tile> a : pathList) {
            if(a == null) {
                System.out.println("*****************************************************************A is null");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if(shortestPath == null) {
                shortestPath = a;
            } else {
                if(a.size() < shortestPath.size()) {
                    shortestPath = a;
                }
            }
        }
        return shortestPath;
    }

    private boolean isLevelExplored(Level level) {
        for(int y = 0; y < level.getTileArray().length; y++) {
            for(int x = 0; x < level.getTileArray()[y].length; x++) {
                Tile t = level.getTile(y, x);
                if(!t.isExplored()) {
                    if(!t.isBlocked()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean allCavesExplored() {
        ArrayList<Tile> caveOpenings = new ArrayList<>();
        for(int y = 0; y < map.getWorldMap().getTileArray().length; y++) {
            for(int x = 0; x < map.getWorldMap().getTileArray()[y].length; x++) {
                Tile t = map.getWorldMap().getTile(y, x);
                if(t.getType() == Tile.Type.CAVE_OPENING) {
                    caveOpenings.add(t);
                }
            }
        }
        for(Tile t : caveOpenings) {
            Level level = map.getLowestLevel(t.getY(), t.getX());
            if(level == null) {
                return false;
            }
        }
        return true;
    }

    public enum PlayerAIDecision {RANDOMPATH, CONTINUEPATH, ACTUATETILE, USE_HEALTH_POTION, ERROR}

    private enum PlayerAITask {USE_HEALTH_POTION, LOOT, KILL, EXPLORE, GO_DEEPER, GO_HIGHER, IDLE, CONTINUEPATH}

    private boolean playerAICanPerformTask(PlayerAITask task) {
        Tile source = player.getTile();
        switch(task) {
            case USE_HEALTH_POTION:
                double remaining = ((double)player.getCurrentHp() / player.getMaxHp());
                return player.hasItemString("Health Potion") && remaining < 0.2;
            case LOOT:
                ArrayList<Tile> itemTiles = new ArrayList<>();
                for(Tile t : player.getVisibleTiles()) {
                    if(t.hasItem()) {
                        itemTiles.add(t);
                    }
                }
                boolean viableLoot = false;
                for(Tile t : itemTiles) {
                    if(t.getItem().getName().equals("Health Potion")) {
                        viableLoot = true;
                    }
                }
                return viableLoot;
            case KILL:
                boolean monsterInView = false;
                ArrayList<Actor> monsters = new ArrayList<>();
                for(Tile t : player.getVisibleTiles()) {
                    if(t.hasActor() && !t.getActor().equals(player)) {
                        monsters.add(t.getActor());
                    }
                }
                monsterInView = monsters.size() > 0;
                return monsterInView;
            case CONTINUEPATH:
                return player.getCurrentPath() != null;
            case EXPLORE:
                return !isLevelExplored(map.getCurrentLevel());
            case GO_DEEPER:
                return isLevelExplored(map.getCurrentLevel()) && !allCavesExplored();
            case GO_HIGHER:
                Level lowestLevel = map.getLowestLevel(getPlayerMapY(), getPlayerMapX());
                if(lowestLevel != null) {
                    return isLevelExplored(lowestLevel) && map.getCurrentDepth() > 0;
                }
                break;
            case IDLE:
                return true;
        }
        return false;
    }

    private PlayerAIDecision playerAIPerformTask(PlayerAITask task) {
        Tile target = null;
        Tile source = player.getTile();
        AStar astar = map.getCurrentLevel().getAstar();
        switch(task) {
            case USE_HEALTH_POTION:
                //System.out.println("BOT: Wants to use health potion");
                if(playerAICanPerformTask(PlayerAITask.USE_HEALTH_POTION)) {
                    Item item = player.getItem("Health Potion");
                    useItem(item, player, player);
                    return PlayerAIDecision.USE_HEALTH_POTION;
                }
                break;
            case LOOT:
               // System.out.println("BOT: Wants to loot");
                if(playerAICanPerformTask(PlayerAITask.LOOT)) {
                    if(source.hasItem() && source.getItem().getName().equals("Health Potion")) {
                        process.actuateTile(player);
                        return PlayerAIDecision.ACTUATETILE;
                    }
                    else if(player.getCurrentPath() != null && player.getCurrentPath().get(player.getCurrentPath().size() - 1).hasItem()) {
                        return PlayerAIDecision.CONTINUEPATH;
                    }
                    else {
                        Item closestItem = null;
                        int d = 0;
                        ArrayList<Tile> itemTiles = new ArrayList<>();
                        for (Tile t : player.getVisibleTiles()) {
                            if (t.hasItem()) {
                                if(t.getItem().getName().equals("Health Potion")) {
                                    itemTiles.add(t);
                                }
                            }
                        }
                        for (Tile t : itemTiles) {
                            if (closestItem == null) {
                                closestItem = t.getItem();
                                d = Math.abs(source.getX() - t.getX()) + Math.abs(source.getY() - t.getY());
                            } else {
                                int nd = Math.abs(source.getX() - t.getX()) + Math.abs(source.getY() - t.getY());
                                if (nd < d) {
                                    closestItem = t.getItem();
                                }
                            }
                        }
                        target = closestItem.getTile();
                    }
                }
                break;
            case KILL:
               // System.out.println("BOT: Wants to kill");
                if(playerAICanPerformTask(PlayerAITask.KILL)) {
                    Actor closestMonster = null;
                    int d = 0;
                    ArrayList<Actor> monsters = new ArrayList<>();
                    for(Tile t : player.getVisibleTiles()) {
                        if(t.hasActor() && !t.getActor().equals(player)) {
                            monsters.add(t.getActor());
                        }
                    }
                    for (Actor a : monsters) {
                        if (closestMonster == null) {
                            closestMonster = a;
                            d = Math.abs(source.getX() - a.getTile().getX()) + Math.abs(source.getY() - a.getTile().getY());
                        } else {
                            int nd = Math.abs(source.getX() - a.getTile().getX()) + Math.abs(source.getY() - a.getTile().getY());
                            if (nd < d) {
                                closestMonster = a;
                                d = nd;
                            }
                        }
                    }
                    target = closestMonster.getTile();
                }
                break;
            case CONTINUEPATH:
                //System.out.println("BOT: Continue path");
                if(playerAICanPerformTask(PlayerAITask.CONTINUEPATH)) {
                    return PlayerAIDecision.CONTINUEPATH;
                }
                break;
            case EXPLORE:
                //System.out.println("BOT: Wants to explore");
                if(playerAICanPerformTask(PlayerAITask.EXPLORE)) {
                    int range;
                    switch (player.getTile().getType()) {
                        case FOREST:
                        case MOUNTAIN:
                        case CAVE_GRASS:
                            range = 4;
                            break;
                        default:
                            range = 16;
                    }
                    ArrayList<Tile> edgeOfExploredTiles = null;
                    ArrayList<ArrayList<Tile>> paths = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
                        // If second run
                        if (i > 0 && paths.size() == 0) {
                            range = -1;
                        }
                        edgeOfExploredTiles = getEdgeOfExploredTiles(range, false);
                        for (Tile t : edgeOfExploredTiles) {
                            if (!t.isBlocked()) {
                                switch (t.getType()) {
                                    case CAVE_OPENING:
                                    case STAIRS_DOWN:
                                    case STAIRS_UP:
                                        continue;
                                }
                                paths.add(astar.getShortestPath(source, t));
                            }
                        }
                        // We have paths
                        if (paths.size() > 0) {
                            break;
                        }
                    }

                    if (paths.size() == 0) {
                        System.out.println("BOT: There are no paths after expansion attempts");
                    }


                    ArrayList<Tile> shortestPath = getShortestPathOfPaths(paths);
                    //System.out.println("BOT: Evaluated " + paths.size() + " paths.");
                    if (shortestPath == null) {
                        System.out.println("BOT: shortestPath: null");
                        ArrayList<Tile> debugEdge = new ArrayList<>();
                        for (Tile te : getUnexploredTiles()) {
                            debugEdge.add(te);
                        }
                        player.setCurrentPath(debugEdge);
                        System.out.println("edge size: " + edgeOfExploredTiles.size());
                        while (true) {
                            for (Tile te : getUnexploredTiles()) {
                                debugEdge.add(te);
                            }
                            player.setCurrentPath(debugEdge);
                            map.getCurrentLevel().toggleAllTilesVisible(true);
                            render.drawGame(getRootConsole());
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            debugEdge = new ArrayList<>();
                            for (Tile te : edgeOfExploredTiles) {
                                debugEdge.add(te);
                            }
                            player.setCurrentPath(debugEdge);
                            render.drawGame(getRootConsole());
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (shortestPath.size() == 0) {
                        System.out.println("BOT: shortestPath size: 0.");
                        System.out.println("edge size: " + edgeOfExploredTiles.size());
                        map.getCurrentLevel().toggleAllTilesVisible(true);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        render.drawGame(getRootConsole());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        target = shortestPath.get(shortestPath.size() - 1);
                        //System.out.println("BOT: move to (random)");
                    }
                }
                break;
            case GO_DEEPER:
                System.out.println("BOT: Wants to go deeper");
                if(playerAICanPerformTask(PlayerAITask.GO_DEEPER)) {
                    if (map.getCurrentDepth() == 0) {
                        // Standing on a cave with the map explored?
                        // and
                        // lowest level below is is not fully explored
                        boolean skipThisCave = false;
                        Level lowestLevel = map.getLowestLevel(playerMapY, playerMapX);
                        if(lowestLevel != null) {
                            if(isLevelExplored(lowestLevel)) {
                                skipThisCave = true;
                            }
                        }

                        if (player.getTile().getType() == Tile.Type.CAVE_OPENING && !skipThisCave) {
                            process.actuateTile(player);
                            return PlayerAIDecision.ACTUATETILE;
                        }
                        // Get a path to a cave
                        else {
                            ArrayList<Tile> caveOpenings = new ArrayList<>();
                            for (Tile t : getExploredTiles()) {
                                if (t.equals(source)) {
                                    continue;
                                }
                                if (t.getType() == Tile.Type.CAVE_OPENING) {
                                    caveOpenings.add(t);
                                }
                            }
                            target = caveOpenings.get(rng.nextInt(caveOpenings.size()));
                            System.out.println("BOT: move to cave");
                            break;
                        }
                    }
                    else {
                        if(player.getTile().getType() == Tile.Type.STAIRS_DOWN) {
                            process.actuateTile(player);
                            return PlayerAIDecision.ACTUATETILE;
                        }
                        // get a path to a stairs down
                        else {
                            for(Tile t : getExploredTiles()) {
                                if(t.equals(source)) {
                                    continue;
                                }
                                if(t.getType() == Tile.Type.STAIRS_DOWN) {
                                    target = t;
                                    System.out.println("BOT: move to stairs down");
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case GO_HIGHER:
                System.out.println("BOT: Wants to go higher");
                if(playerAICanPerformTask(PlayerAITask.GO_HIGHER)) {
                    if(player.getTile().getType() == Tile.Type.STAIRS_UP) {
                        process.actuateTile(player);
                        return PlayerAIDecision.ACTUATETILE;
                    }
                    // get a path to a stairs down
                    else {
                        for(Tile t : getExploredTiles()) {
                            if(t.equals(source)) {
                                continue;
                            }
                            if(t.getType() == Tile.Type.STAIRS_UP) {
                                target = t;
                                System.out.println("BOT: move to stairs up");
                                break;
                            }
                        }
                    }
                }
                break;
            case IDLE:
                System.out.println("BOT: Wants to idle");
                break;
            default:
                System.out.println("BOT: default (error)");
                break;
        }
        // Set the new path
        ArrayList<Tile> newPath = null;
        if(target != null) {
            newPath = astar.getShortestPath(source, target);
        }

        if (newPath == null) {
            System.out.println("newPath is null");
        }
        else if (newPath.size() == 0) {
            System.out.println("newPath.size(): 0.");
            if (target == null) {
                System.out.println("target is null");
            } else {
                System.out.println("Target tile: " + target.getY() + " " + target.getX());
                System.out.println("Type: " + target.getTypeString());
            }
        } else {
            // Todo, simulate mouse click
            player.setCurrentPath(newPath);
            return PlayerAIDecision.RANDOMPATH;
        }
        return PlayerAIDecision.ERROR;
    }

    private PlayerAIDecision makePlayerAIDecision() {
        ArrayList<PlayerAITask> priorityList = new ArrayList<>();
        priorityList.add(PlayerAITask.USE_HEALTH_POTION);
        priorityList.add(PlayerAITask.KILL);
        priorityList.add(PlayerAITask.LOOT);
        priorityList.add(PlayerAITask.CONTINUEPATH);
        priorityList.add(PlayerAITask.GO_HIGHER);
        priorityList.add(PlayerAITask.GO_DEEPER);
        priorityList.add(PlayerAITask.EXPLORE);
        priorityList.add(PlayerAITask.IDLE);

        for (PlayerAITask task : priorityList) {
            if (playerAICanPerformTask(task)) {
                return playerAIPerformTask(task);
            }
        }

        System.out.println("Error");
        return PlayerAIDecision.ERROR;
    }



    private boolean updateMouseLocation() {
        if(window.getMouse() == null) {
            System.out.println("updateMouseLocation() :: window.getMouse returned null");
            return false;
        }
        mx = window.getMouse().getCellX();
        my = window.getMouse().getCellY();


        boolean mouseCoordinatesChanged = false;
        if(mx != lastMx) {
            if(mx < 0 || mx > windowWidth) {
                // Outside of window
            } else {
                lastMx = mx;
                mouseCoordinatesChanged = true;
            }
        }
        if(my != lastMy) {
            if(my < 0 || my > windowHeight) {
                // Outside of window
            } else {
                lastMy = my;
                mouseCoordinatesChanged = true;
            }
        }
        return mouseCoordinatesChanged;
    }

    protected boolean isMouseOnMap() {
        if(lastMx < windowWidth - 1 && lastMx > gameInformationConsoleWidth
                && lastMy < windowHeight - messageHeight - 1 && lastMy > 0) {
            return true;
        }
        return false;
    }

    protected int getMouseOnMapY() {
        return lastMy - 1;
    }

    protected int getMouseOnMapX() {
        return lastMx - gameInformationConsoleWidth - 1;
    }

    private void initTitleScreen() {
        title = new Title(windowHeight, windowWidth);
    }

    private void initializePlayer() {
        // Set up starting tile for player
        Tile startingTile = map.getCurrentLevel().getEntryTile();

        // Create player at that tile and set them up
        player = ActorFactory.createActor(ActorFactory.TYPE.PLAYER, startingTile);
        startingTile.setActor(player);
    }

    protected void copyMapToBuffer() {
        for (int y = 0; y < map.getCurrentLevel().getHeight(); ++y) {
            for (int x = 0; x < map.getCurrentLevel().getWidth(); ++x) {
                Tile t = map.getCurrentLevel().getTile(y, x);
                refreshTile(t);
            }
        }

    }

    private void checkWinConditions() {
        /**
         Check win condition
         if giant was killed
         and victoryConsole is null(not initialized yet)

         If it is not null, then we've seen it and likely hit ESCAPE to keep playing
         */
        if (giantSlain && gui.getVictoryConsole() == null) {
            gui.initVictoryConsole();
            showVictoryConsole = true;
            redrawGame = true;
        }

        if (!player.isAlive()) {
            gui.initDefeatConsole();
            showDefeatConsole = true;
            redrawGame = true;
        }
    }

    private void processGameState(State currentState) {
        // Title Screen
        // .
        // .

        if (currentState == State.TITLE) {
            boolean shouldRedraw = true;
            switch (this.window.getLastKeyEvent().getKeyCode()) {
                case KeyEvent.VK_UP:
                    title.scrollUp();
                    break;
                case KeyEvent.VK_DOWN:
                    title.scrollDown();
                    break;
                case KeyEvent.VK_ENTER:
                    if (title.getSelectedItem().equals("New Game")) {
                        this.currentState = State.PLAYING;
                        setupNewGame();
                        startGame();
                        // start bot
                        //runPlayerBot = true;
                    }
                    break;
                default:
                    shouldRedraw = false;
                    break;
            }
            redrawGame = shouldRedraw;
        }
        else if (currentState == State.PLAYING) {
            // Victory Achieved
            // .
            // .

            if (showVictoryConsole) {
                switch (this.window.getLastKeyEvent().getKeyCode()) {
                    // Player decides to keep playing
                    case KeyEvent.VK_ESCAPE:
                        showVictoryConsole = false;
                        break;
                    // Player decides to start new game
                    case KeyEvent.VK_R:
                        initializeGameEnvironment();
                    default:
                        break;
                }
            }
            // Defeated
            // .
            // .

            else if (showDefeatConsole) {
                switch (this.window.getLastKeyEvent().getKeyCode()) {
                    case KeyEvent.VK_R:
                        initializeGameEnvironment();
                        break;
                    default:
                        break;
                }
            }


            // Main portion of game loop
            // .
            // .

            else {
                // By default, do not recalculate field of vision unless we need to
                boolean recalculateFOV = false;
                int prevTurns = turns;
                // double check the key event, because otherwise moving and mouse moving don't like each other
                // can the player take a turn?
                if(player.canTakeTurn()) {
                    if (window.getLastKeyEvent() != lastKeyEvent) {
                        KeyEvent key = this.window.getLastKeyEvent();

                        // Obtain the command related to the keypress determined by game state
                        Input.Command command = input.processKey(key);

                        // Check command and act upon it
                        if (command != null) {
                            switch (command) {

                                case MOVEMENT:
                                    Input.Direction direction = input.getPlayerMovementDirection(key);
                                    switch (process.moveActor(player, direction)) {
                                        case MOVED:
                                        case COMBAT:
                                        case BUMPED:
                                            recalculateFOV = true;
                                    }
                                    redrawGame = true;
                                    turns++;
                                    break;

                                case ACTUATE:
                                    boolean tileActuated = false;
                                    tileActuated = process.actuateTile(player);
                                    if (tileActuated) {
                                        redrawGame = true;
                                        recalculateFOV = true;
                                        turns++;
                                    } else {
                                        gui.getMessageConsole().addMessage("Tile can't be actuated", Color.red);
                                    }
                                    break;

                                case DEBUG:
                                    input.processDebugCommand(key, this);
                                    redrawGame = true;
                                    break;

                                case INVENTORY:
                                    input.processInventoryCommand(key, this);
                                    redrawGame = true;
                                    break;

                                case MENU:
                                    input.processMenuCommand(key, this);
                                    redrawGame = true;
                                    break;

                                default:
                                    break;
                            }
                        }
                    } else if (player.getCurrentPath() != null) {
                        switch (process.moveActor(player, player.getCurrentPath().get(0))) {
                            case MOVED:
                                player.getCurrentPath().remove(0);
                                if (player.getCurrentPath().size() == 0) {
                                    player.setCurrentPath(null);
                                }
                                recalculateFOV = true;
                                break;
                        }
                        redrawGame = true;
                        turns++;
                    }
                }
                // Recalculate field of vision if it was set to true
                // This allows us to know what we're seeing next, since NPC turn is after player
                // NPC need to know if they now see the player
                if (recalculateFOV) {
                    //
                    process.calculateVision(player);
                    render.drawGame(getRootConsole());
                }

                ArrayList<Actor> actors = map.getCurrentLevel().getActors();
                while(!player.canTakeTurn()) {
                    // can any NPC actor take a turn?
                    if (map.getCurrentLevel().canNPCActorTakeTurn()) {

                        for(Actor a : actors) {
                            if(!a.equals(player)) {
                                if(process.processNpcActor(a)) {
                                    process.calculateVision(player);
                                    render.drawGame(getRootConsole());
//                                    if (process.actorCanSeeActor(a, player)) {
//                                        try {
//                                            Thread.sleep(17);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
                                }
                            }
                        }
                    }
                    for(Actor a : map.getCurrentLevel().getActors()) {
                        a.recoverEnergy(100);
                    }
                }

//                // Recalculate field of vision if it was set to true
//                // This allows us to know what we're seeing next, since NPC turn is after player
//                // NPC need to know if they now see the player
//                if (recalculateFOV) {
//                    //
//                    process.calculateVision(player);
//                    redrawGame = true;
//                }
//                // Was there a turn just there?
//                if (prevTurns != turns) {
//                    // Then we need to do the NPC moves
//                    // if we do it in MOVEMENT, then we don't get a path immediately.
//                    //process.processNpcActors();
//
//                    // Following all the NPC movement, we need to recalculate player view to see any new NPC
//                    process.calculateVision(player);
//                    redrawGame = true;
//                }


            }
        }
    }

    /**
     * refreshTile
     *
     * calls mapConsole setChar and setColor at the tile's Y X location, using the tile's symbol and color
     *
     * @param tile
     */
    private void refreshTile(Tile tile) {


        /*
        Determine what is displayed for this tile:
        Map, Item, Corpse, Player, Monster
         */
        char glyph;
        Color color;
        Color bgColor;

        // Tile is visible to the player
        if (tile.isVisible() || (SHOW_MAP_CREATION && map.getCurrentLevel().isGenerating())) {
            // Tile has an actor(player/monster)
            if (tile.hasActor()) {
                Actor actor = tile.getActor();
                glyph = actor.getSymbol();
                color = actor.getColor();
            }
            // Tile has an item
            else if (tile.hasItem()) {
                Item item = tile.getItem();
                glyph = item.getSymbol();
                color = item.getColor();
            }
            // Tile has nothing, just show the map
            else {
                glyph = tile.getSymbol();
                color = tile.getColor();
            }
            bgColor = tile.getBackgroundColor();
        }

        // Tile is not visible, but has been explored by the player
        else if (tile.isExplored()) {
            glyph = tile.getSymbol();
            color = tile.getColor();
            bgColor = tile.getBackgroundColor();
            if(getMap().getCurrentDepth() > 0) {
                bgColor = Colors.shroud(bgColor);
                color = Colors.shroud(color);
            } else {
                bgColor = bgColor.darker().darker().darker().darker();
                color = color.darker().darker().darker();
            }
        }

        // Tile is not visible, and has not been explored by the player
        else {
            glyph = ' ';
            color = Color.black;
            bgColor = Color.black;
        }


        int yAdj = 0;
        int xAdj = 0;
        if(mapConsole.hasBorder()) {
            yAdj = 1;
            xAdj = 1;
        }

        int tileY = tile.getY() + yAdj;
        int tileX = tile.getX() + xAdj;
        mapConsole.setColor(tileY, tileX, color);
        mapConsole.setBgColor(tileY, tileX, bgColor);
        mapConsole.setChar(tileY, tileX, glyph);
    }

    public boolean useItem(Item item, Actor source, Actor target) {
        return source.useItem(item, target);
    }

    public static void main(String[] args) {
        new Roguelike();
    }

    private Input.Direction getDirectionTowardsActor(Actor source, Actor target) {
        Tile sourceTile = source.getTile();
        Tile targetTile = target.getTile();
        return getDirectionTowardsTile(sourceTile, targetTile);
    }

    private Input.Direction getDirectionTowardsTile(Tile sourceTile, Tile targetTile) {
        int yd = sourceTile.getY() - targetTile.getY();
        int xd = sourceTile.getX() - targetTile.getX();

        if (Math.abs(yd) > Math.abs(xd)) {
            if (yd < 0) {
                return Input.Direction.DOWN;
            } else {
                return Input.Direction.UP;
            }
        } else if (Math.abs(yd) < Math.abs(xd)) {
            if (xd < 0) {
                return Input.Direction.RIGHT;
            } else {
                return Input.Direction.LEFT;
            }
        } else if (Math.abs(yd) == Math.abs(xd)) {
            if (yd < 0 && xd < 0) {
                return Input.Direction.SE;
            } else if (yd > 0 && xd > 0) {
                return Input.Direction.NW;
            } else if (yd < 0 && xd > 0) {
                return Input.Direction.SW;
            } else if (yd > 0 && xd < 0) {
                return Input.Direction.NE;
            }
        }
        return null;
    }



    public Console getRootConsole() {
        return rootConsole;
    }

    public int getGameInformationConsoleWidth() {
        return gameInformationConsoleWidth;
    }

    public int getTurns() {
        return turns;
    }

    public int getCurrentFrames() {
        return currentFrames;
    }

    public int getLastFramesPerSecond() {
        return lastFramesPerSecond;
    }

    public Console getMenuWindow() {
        return menuWindow;
    }

    public Actor getPlayer() {
        return player;
    }

    public Map getMap() {
        return map;
    }

    public int getGameInformationConsoleHeight() {
        return gameInformationConsoleHeight;
    }

    public Console getMapConsole() {
        return mapConsole;
    }

    public Gui getGui() {
        return gui;
    }

    public int getPlayerMapY() {
        return playerMapY;
    }

    public int getPlayerMapX() {
        return playerMapX;
    }

    public void setPlayerMapY(int playerMapY) {
        this.playerMapY = playerMapY;
    }

    public void setPlayerMapX(int playerMapX) {
        this.playerMapX = playerMapX;
    }

    public Window getWindow() {
        return window;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getLastMy() {
        return lastMy;
    }

    public int getLastMx() {
        return lastMx;
    }


    public enum State {TITLE, PLAYING, VICTORY}

    public Render getRender() {
        return render;
    }
}


