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
    private static final int MAP_DEPTH = 20;
    public static boolean RENDER_BETWEEN_TURNS = false;

    public static Random rng;
    public static int turns;
    public static MapSymbols mapSymbols = new MapSymbols("config/symbols.txt");


    protected Console menuWindow;
    protected Gui gui;
    protected Title title;
    protected State currentState;

    Window window;
    int fontSize = 24;
    int windowHeight = 60;
    //int windowWidth = 135;
    int windowWidth = 100;
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
    int gameInformationConsoleWidth = 18;
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

    private int playerMapX;
    private int playerMapY;
    private int playerMapZ;

    private KeyEvent lastKeyEvent;
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

        initGame();
        startGame();

        render = new Render(this);
        if (RENDER_BETWEEN_TURNS) {
            render.start();
        }

        while (true) {
            gameLoop();
        }
    }

    protected void initGame() {
        currentState = State.TITLE;
        giantSlain = false;
        showVictoryConsole = false;
        showDefeatConsole = false;
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

        if(mapConsole.hasBorder()) {
            this.map = new Map(mapHeight - 2, mapWidth - 2, Roguelike.MAP_DEPTH);
        } else {
            this.map = new Map(mapHeight, mapWidth, Roguelike.MAP_DEPTH);
        }

        initPlayer();

        for (Actor a : map.getCurrentLevel().getActors()) {
            process.calculateVision(a);
        }


        // Init the GUI
        gui.initGui();
        input.connectToGui(gui);
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
        if (!RENDER_BETWEEN_TURNS) {
            if (redrawGame) {
                render.renderSingleFrame();
            }
        }
    }

    private void initTitleScreen() {
        title = new Title(windowHeight, windowWidth);
    }

    private void initPlayer() {
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
                    }
                    break;
                default:
                    shouldRedraw = false;
                    break;
            }
            redrawGame = shouldRedraw;
        } else if (currentState == State.PLAYING) {
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

        if(tile.isVisible()) {
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
            } else if (tile.hasItem()) {
                Item item = tile.getItem();
                glyph = item.getSymbol();
                color = item.getColor();
            } else {
                glyph = tile.getSymbol();
                color = tile.getColor().brighter().brighter();
            }
            bgColor = tile.getBackgroundColor().brighter().brighter();
        } else if (tile.isExplored()) {
            glyph = tile.getSymbol();
            color = tile.getColor().darker().darker();
            bgColor = tile.getBackgroundColor().darker().darker();
        } else {
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


    public enum State {TITLE, PLAYING, VICTORY}
}


