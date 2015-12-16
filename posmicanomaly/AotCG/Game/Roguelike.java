package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.*;
import posmicanomaly.AotCG.Gui.Component.EnhancedConsole;
import posmicanomaly.AotCG.Gui.Component.GameInformationConsole;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;
import posmicanomaly.AotCG.Screen.Title;
import posmicanomaly.libjsrte.Console.Console;
import posmicanomaly.libjsrte.Console.Symbol;
import posmicanomaly.libjsrte.Util.ColorTools;
import posmicanomaly.libjsrte.Window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Roguelike {
    public static boolean RENDER_BETWEEN_TURNS = false;

    public static Random rng;
    public static int turns;
    protected Console victoryConsole;
    protected Console defeatConsole;
    protected MessageConsole messageConsole;
    protected GameInformationConsole gameInformationConsole;
    protected EnhancedConsole inventorySideConsole;
    protected Console menuWindow;
    Window window;
    int fontSize = 20;
    int windowHeight = 40;
    int windowWidth = 135;
    int messageHeight = 10;
    int messageWidth;
    int mapHeight;
    int mapWidth;
    long lastRenderTime;
    long defaultRefreshIntervalMs = 1000;
    long refreshIntervalMs;
    long minFrameSpeed = 40;
    long lastFrameDrawTime;
    int currentFrames;
    int lastFramesPerSecond;
    long fpsTimerStart;
    int gameInformationConsoleHeight = windowHeight;
    int gameInformationConsoleWidth = 20;
    boolean showMenu;
    boolean showInventory;
    boolean giantSlain;
    boolean showVictoryConsole;
    boolean showDefeatConsole;
    Map map;
    boolean redrawGame;
    private Console mapConsole;
    protected Gui gui;
    private Input input;
    private Process process;
    protected Title title;
    protected State currentState;
    private Actor player;
    private KeyEvent lastKeyEvent;
    private int gameLoopsWithoutInput;
    protected long gameLoopRedrawTimeStart = 0;
    private Console rootConsole;
    private Render render;

    public Roguelike() {
        System.setProperty("sun.java2d.opengl", "true");

        this.window = new Window(this.windowHeight, this.windowWidth, "AotCG", fontSize);
        rootConsole = this.window.getMainPanel().getRootConsole();
        //this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - gameInformationConsoleWidth;

        this.mapWidth = this.windowWidth - 2 - gameInformationConsoleWidth;
        this.mapHeight = this.windowHeight - 1 - this.messageHeight;

        this.gui = new Gui(this);

        rng = new Random();
        input = new Input(this);
        process = new Process(this);

        refreshIntervalMs = defaultRefreshIntervalMs;
        gameLoopsWithoutInput = 0;

        initGame();
        startGame();

        render = new Render(this);
        if(RENDER_BETWEEN_TURNS) {
            render.start();
        }

        while (true) {
            gameLoop();
        }
    }

    public static void main(String[] args) {
        new Roguelike();
    }

    public int getTurns() {
        return turns;
    }

    private void gameLoop() {
        // Check for key input
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent)) {
            /**
             * Check for game state or victory/defeat console displayed
             */
            checkWinConditions();
            processGameState(currentState);

            // Set lastKeyEvent to this one that we just used, so we do not enter loop again
            this.lastKeyEvent = this.window.getLastKeyEvent();

        } else {
            // No key input, increase the draw refresh time to reduce cpu usage when idle
        }
        if(!RENDER_BETWEEN_TURNS) {
            if(redrawGame) {
                render.renderSingleFrame();
            }
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
                    }
                    break;
                default:
                    shouldRedraw = false;
                    break;
            }
            redrawGame = shouldRedraw;
        }
        else if(currentState == State.PLAYING) {
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
                        initGame();
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
                        initGame();
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

                KeyEvent key = this.window.getLastKeyEvent();

                // Obtain the command related to the keypress determined by game state
                Input.Command command = input.processKey(key);

                // Check command and act upon it
                int prevTurns = turns;
                if (command != null) {
                    switch (command) {

                        case MOVEMENT:
                            Input.Direction direction = input.getPlayerMovementDirection(key);
                            if (process.moveActor(player, direction)) {
                                recalculateFOV = true;
                            }
                            redrawGame = true;
                            turns++;
                            break;

                        case DEBUG:
                            input.processDebugCommand(key, this);
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
                // Recalculate field of vision if it was set to true
                // This allows us to know what we're seeing next, since NPC turn is after player
                // NPC need to know if they now see the player
                if (recalculateFOV) {
                    //
                    process.calculateVision(player);
                    redrawGame = true;
                }
                // Was there a turn just there?
                if (prevTurns != turns) {
                    // Then we need to do the NPC moves
                    // if we do it in MOVEMENT, then we don't get a path immediately.
                    process.processNpcActors();

                    // Following all the NPC movement, we need to recalculate player view to see any new NPC
                    process.calculateVision(player);
                    redrawGame = true;
                }


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
        if (giantSlain && victoryConsole == null) {
            gui.initVictoryConsole();
            showVictoryConsole = true;
        }

        if (!player.isAlive()) {
            gui.initDefeatConsole();
            showDefeatConsole = true;
        }
    }

    /**
     * refreshTile
     * <p/>
     * calls mapConsole setChar and setColor at the tile's Y X location, using the tile's symbol and color
     *
     * @param tile
     */
    private void refreshTile(Tile tile) {
        /*
        Shimmer water code

        Sets the backgroundColor of tile to a varied color based on the standard WATER_BG
         */

        if (tile.getType() == Tile.Type.WATER) {
            if (rng.nextInt(100) < 50) {
                tile.setBackgroundColor(ColorTools.varyColor(Colors.WATER_BG, 0.7, 1.0, ColorTools.BaseColor.RGB));

            }
            if (rng.nextInt(100) < 50) {
                tile.setColor(ColorTools.varyColor(Colors.WATER, 0.7, 1.0, ColorTools.BaseColor.RGB));
                if (tile.getSymbol() == Symbol.ALMOST_EQUAL_TO) {
                    tile.setSymbol('=');
                } else {
                    tile.setSymbol(Symbol.ALMOST_EQUAL_TO);
                }
            }
        }

        /*

         */
        char glyph;
        Color color;
        Color bgColor;
        if (tile.isVisible()) {
            if (tile.hasActor()) {
                Actor actor = tile.getActor();
                glyph = actor.getSymbol();
                color = actor.getColor();
            } else if(tile.hasItem()) {
                Item item = tile.getItem();
                glyph = item.getSymbol();
                color = item.getColor();
            } else {
                glyph = tile.getSymbol();
                color = tile.getColor().brighter().brighter();
            }
            bgColor = tile.getBackgroundColor().brighter().brighter();
        }
        else if (tile.isExplored()) {
            glyph = tile.getSymbol();
            color = tile.getColor().darker().darker();
            bgColor = tile.getBackgroundColor().darker().darker();
        }
        else {
            glyph = ' ';
            color = Color.black;
            bgColor = Color.black;
        }


        mapConsole.setColor(tile.getY(), tile.getX(), color);
        mapConsole.setBgColor(tile.getY(), tile.getX(), bgColor);
        mapConsole.setChar(tile.getY(), tile.getX(), glyph);
    }

    protected void showActorPaths() {
        /**
         * Debug
         *
         * Show paths
         */
        ArrayList<Actor> actors = map.getCurrentLevel().getActors();

        for(Actor a : actors) {
            int tRed, tGreen, tBlue;
            Color pathColor;
            for(Tile t : a.getCurrentPath()) {
                tRed = t.getBackgroundColor().getRed();
                tGreen = t.getBackgroundColor().getGreen();
                tBlue = t.getBackgroundColor().getBlue();

                int shimmer = Roguelike.rng.nextInt(20) + 100;
                pathColor = new Color(tRed + shimmer, tGreen, tBlue).brighter();
                mapConsole.setBgColor(t.getY(), t.getX(), pathColor);
            }
        }
    }



    protected void initGame() {
        currentState = State.TITLE;
        giantSlain = false;
        victoryConsole = null;
        showVictoryConsole = false;
        defeatConsole = null;
        showDefeatConsole = false;
        turns = 0;
        lastRenderTime = 0;

        // Set seed
        //rng.setSeed(12345);


        initTitleScreen();

        // Set up map
        this.mapConsole = new Console(this.mapHeight, this.mapWidth);

        // Start with a depth of 1. We can create new levels as needed.
        this.map = new Map(mapHeight, mapWidth);

        initPlayer();

        for(Actor a : map.getCurrentLevel().getActors()) {
            process.calculateVision(a);
        }



        // Init the GUI
        gui.initGui();
    }



    private void initTitleScreen() {
        title = new Title(windowHeight, windowWidth);
    }

    private void initPlayer() {
        // Set up starting tile for player
        Tile startingTile = map.getCurrentLevel().getUpStairs();

        // Create player at that tile and set them up
        player = ActorFactory.createActor(ActorFactory.TYPE.PLAYER, startingTile);
        startingTile.setActor(player);
    }



    private void startGame() {
        // Copy the map to mapConsole(buffer)
        this.copyMapToBuffer();
        // Allow rendering
        this.window.getMainPanel().setRender(true);

        // Set lastKeyEvent so we can reference a change
        this.lastKeyEvent = this.window.getLastKeyEvent();

        fpsTimerStart = System.currentTimeMillis();
        redrawGame = true;
    }

    protected void copyMapToBuffer() {
        for (int y = 0; y < map.getCurrentLevel().getHeight(); ++y) {
            for (int x = 0; x < map.getCurrentLevel().getWidth(); ++x) {
                Tile t = map.getCurrentLevel().getTile(y, x);
                refreshTile(t);
            }
        }

    }

    private Input.Direction getDirectionTowardsActor(Actor source, Actor target) {
        Tile sourceTile = source.getTile();
        Tile targetTile = target.getTile();
        return getDirectionTowardsTile(sourceTile, targetTile);
    }

    private Input.Direction getDirectionTowardsTile(Tile sourceTile, Tile targetTile) {
        int yd = sourceTile.getY() - targetTile.getY();
        int xd = sourceTile.getX() - targetTile.getX();

        if(Math.abs(yd) > Math.abs(xd)) {
            if(yd < 0) {
                return Input.Direction.DOWN;
            } else {
                return Input.Direction.UP;
            }
        }
        else if (Math.abs(yd) < Math.abs(xd)) {
            if(xd < 0) {
                return Input.Direction.RIGHT;
            } else {
                return Input.Direction.LEFT;
            }
        }
        else if(Math.abs(yd) == Math.abs(xd)) {
            if(yd < 0 && xd < 0) {
                return Input.Direction.SE;
            } else if(yd > 0 && xd > 0) {
                return Input.Direction.NW;
            } else if( yd < 0 && xd > 0) {
                return Input.Direction.SW;
            } else if(yd > 0 && xd < 0) {
                return Input.Direction.NE;
            }
        }
        return null;
    }

    public MessageConsole getMessageConsole() {
        return messageConsole;
    }

    public Console getRootConsole() {
        return rootConsole;
    }

    public int getGameInformationConsoleWidth() {
        return gameInformationConsoleWidth;
    }

    public GameInformationConsole getGameInformationConsole() {
        return gameInformationConsole;
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

    public EnhancedConsole getInventorySideConsole() {
        return inventorySideConsole;
    }

    public Console getVictoryConsole() {
        return victoryConsole;
    }

    public Console getDefeatConsole() {
        return defeatConsole;
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


    public enum State {TITLE, PLAYING, VICTORY}
}


