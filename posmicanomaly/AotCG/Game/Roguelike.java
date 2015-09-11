package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.LevelFactory;
import posmicanomaly.AotCG.Component.Map;
import posmicanomaly.AotCG.Component.Tile;
import posmicanomaly.AotCG.Gui.Component.EnhancedConsole;
import posmicanomaly.AotCG.Gui.Component.GameInformationConsole;
import posmicanomaly.AotCG.Gui.Component.InventorySideConsole;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;
import posmicanomaly.AotCG.Gui.Gui;
import posmicanomaly.libjsrte.Console.Console;
import posmicanomaly.libjsrte.Util.ColorTools;
import posmicanomaly.libjsrte.Window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Roguelike {
    Window window;
    private Console mapConsole;

    private Gui gui;

    private MessageConsole messageConsole;
    private EnhancedConsole gameInformationConsole;
    private EnhancedConsole inventorySideConsole;
    private Console menuWindow;
    private Console titleConsole;

    public enum Direction {UP, DOWN, LEFT, RIGHT}
    public enum State {TITLE, PLAYING}

    private State currentState;

    int fontSize = 16;

    int windowHeight = 9 * 6;
    int windowWidth = 16 * 7;
    int messageHeight = 10;
    int messageWidth;
    int mapHeight;
    int mapWidth;
    int mapDepth = 10;

    int gameInformationConsoleHeight = windowHeight;
    int gameInformationConsoleWidth = 16;
    boolean showMenu;
    boolean showInventory;
    Map map;

    private Actor player;
    private KeyEvent lastKeyEvent;

    public Roguelike() {

        this.window = new Window(this.windowHeight, this.windowWidth, "AotCG", fontSize);
        this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - gameInformationConsoleWidth;

        this.mapWidth = this.windowWidth - 2 - gameInformationConsoleWidth;
        this.mapHeight = this.windowHeight - 1 - this.messageHeight;

        this.gui = new Gui();


        initGame();

        startGame();

        while (true) {
            try {
                Thread.sleep((long) Window.THREAD_SLEEP);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent)) {
                if (currentState == State.TITLE) {
                    currentState = State.PLAYING;
                } else {
                    boolean recalculateFOV = false;
                    switch (this.window.getLastKeyEvent().getKeyCode()) {
                    /*
                    Player Movement Input
                     */
                        case KeyEvent.VK_LEFT:
                            moveActor(Direction.LEFT);
                            recalculateFOV = true;
                            break;
                        case KeyEvent.VK_UP:
                            moveActor(Direction.UP);
                            recalculateFOV = true;
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveActor(Direction.RIGHT);
                            recalculateFOV = true;
                            break;
                        case KeyEvent.VK_DOWN:
                            moveActor(Direction.DOWN);
                            recalculateFOV = true;
                            break;

                    /*
                    DEBUG Input
                     */
                        case KeyEvent.VK_F:
                            LevelFactory.DEBUG_FLOOD_FILL(map.getCurrentLevel().getTileArray());
                            //copyMapToBuffer();
                            break;
                        case KeyEvent.VK_P:
                            LevelFactory.DEBUG_PROCESS_MAP(map.getCurrentLevel().getTileArray());
                            //copyMapToBuffer();
                            break;
                        case KeyEvent.VK_R:
                            initGame();
                            break;
                        case KeyEvent.VK_V:
                            map.getCurrentLevel().toggleAllTilesVisible(true);
                            //copyMapToBuffer();
                            break;
                        case KeyEvent.VK_B:
                            if (window.getMainPanel().isDrawBackgroundGlyphs()) {
                                window.getMainPanel().setDrawBackgroundGlyphs(false);
                            } else {
                                window.getMainPanel().setDrawBackgroundGlyphs(true);
                            }
                            break;

                    /*
                    Menu Toggle Input
                     */
                        case KeyEvent.VK_M:
                            if (this.showMenu) {
                                this.showMenu = false;
                            } else {
                                this.showMenu = true;
                            }
                            break;
                        case KeyEvent.VK_I:
                            if (showInventory) {
                                showInventory = false;
                            } else {
                                showInventory = true;
                            }
                            break;
                        default:
                            break;
                    }
                    if (recalculateFOV) {
                        //
                        calculateVision();
                    }
                }
                this.lastKeyEvent = this.window.getLastKeyEvent();
            }
            this.drawGame(this.window.getMainPanel().getRootConsole());
        }
    }

    private void calculateVision() {
        map.getCurrentLevel().toggleAllTilesVisible(false);
        int y = player.getTile().getY();
        int x = player.getTile().getX();

        ArrayList<Tile> fieldOfVisionTiles = FieldOfVision.calculateRayCastingFOVVisibleTiles(y, x, map.getCurrentLevel());

        for(Tile t : fieldOfVisionTiles) {
            t.setVisible(true);
            t.setExplored(true);
        }
    }

    private void moveActor(Direction d) {
        Tile playerTile = player.getTile();
        Tile nextTile = null;
        boolean move = true;
        int y = playerTile.getY();
        int x = playerTile.getX();
        switch(d) {
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
            default:
                move = false;
                break;
        }
        if(move) {
            nextTile = map.getCurrentLevel().getTile(y, x);
            if(nextTile == null) {
                messageConsole.addMessage("Tile is null");
            } else if(nextTile.isBlocked()) {
                messageConsole.addMessage("You bumped into a wall");
            } else {
                player.setTile(nextTile);
                nextTile.setVisible(true);
                // This removed the player from the previous tile
                refreshTile(playerTile);
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
        if(tile.isVisible()) {
            mapConsole.setChar(tile.getY(), tile.getX(), tile.getSymbol());
            mapConsole.setColor(tile.getY(), tile.getX(), tile.getColor());
            mapConsole.setBgColor(tile.getY(), tile.getX(), tile.getBackgroundColor());
        } else if(tile.isExplored()) {
            mapConsole.setChar(tile.getY(), tile.getX(), tile.getSymbol());
            mapConsole.setColor(tile.getY(), tile.getX(), tile.getColor().darker().darker());
            mapConsole.setBgColor(tile.getY(), tile.getX(), tile.getBackgroundColor().darker().darker());
        } else {
            mapConsole.setChar(tile.getY(), tile.getX(), ' ');
            mapConsole.setColor(tile.getY(), tile.getX(), Color.black);
            mapConsole.setBgColor(tile.getY(), tile.getX(), Color.black);
        }
    }

    private void drawGame(Console rootConsole) {
        rootConsole.clear();
        if(currentState == State.PLAYING) {

            // Refresh the map buffer
            copyMapToBuffer();
            // Copy player to the map
            Tile playerTile = player.getTile();
            mapConsole.setChar(playerTile.getY(), playerTile.getX(), player.getSymbol());
            mapConsole.setColor(playerTile.getY(), playerTile.getX(), player.getColor());
            this.mapConsole.copyBufferTo(rootConsole, 1, 1);


            this.messageConsole.copyBufferTo(rootConsole, this.mapHeight + 1, 0);
            gameInformationConsole.updateConsole();
            gameInformationConsole.copyBufferTo(rootConsole, 0, mapWidth + 2);

            if (this.showMenu) {
                this.menuWindow.copyBufferTo(rootConsole, this.window.HEIGHT / 2 - 12, this.window.WIDTH / 2 - 12);
            }

            if (showInventory) {
                inventorySideConsole.updateConsole();
                inventorySideConsole.copyBufferTo(rootConsole, 1, 1);
            }

            this.window.refresh();
        } else if(currentState == State.TITLE) {
            this.titleConsole.copyBufferTo(rootConsole, 0, 0);
            this.window.refresh();
        }
    }

    private void initMessageConsole() {
        this.messageConsole = new MessageConsole(this.messageHeight, this.messageWidth);
        this.messageConsole.setBorder(true);
        this.messageConsole.addMessage("Welcome");

//        this.messageConsole.writeString("Hello World", 1, 1);
//        this.messageConsole.writeString("KeyEvent: " + this.lastKeyEvent.getKeyCode(), 2, 1);
    }

    private void initGame() {
        currentState = State.TITLE;

        //title console
        titleConsole = new Console(windowHeight, windowWidth);
        titleConsole.setBorder(true);
        String title1 = "AotCG";
        String title2 = "A Roguelike game written in Java using Swing with libjsrte";
        String title3 = "Press any key to start game";
        titleConsole.writeString(title1, 2, titleConsole.getxBufferWidth() / 2 - title1.length() / 2);
        titleConsole.writeString(title2, 4, titleConsole.getxBufferWidth() / 2 - title2.length() / 2);
        titleConsole.writeString(title3, 6, titleConsole.getxBufferWidth() / 2 - title3.length() / 2);
        // Set up map
        this.mapConsole = new Console(this.mapHeight, this.mapWidth);
        this.map = new Map(mapHeight, mapWidth, mapDepth);

        // Set up starting tile for player
        Tile startingTile = map.getCurrentLevel().getTile(map.getCurrentLevel().getHeight() / 2, map.getCurrentLevel().getWidth() / 2);

        // Create player at that tile and set them up
        player = new Actor('@', ColorTools.getRandomColor(), startingTile);
        calculateVision();

        // Copy the map to mapConsole(buffer)
        this.copyMapToBuffer();

        // Init the GUI
        initMessageConsole();
        gameInformationConsole = new GameInformationConsole(gameInformationConsoleHeight, gameInformationConsoleWidth, player);
        gameInformationConsole.setBorder(true);

        inventorySideConsole = new InventorySideConsole(mapHeight, 20);
        inventorySideConsole.setBorder(true);

        this.menuWindow = new Console(25, 25);
        this.menuWindow.setBorder(true);
        this.showMenu = false;
        showInventory = false;
    }

    private void startGame() {
        // Allow rendering
        this.window.getMainPanel().setRender(true);

        // Set lastKeyEvent so we can reference a change
        this.lastKeyEvent = this.window.getLastKeyEvent();
    }

    private void copyMapToBuffer() {
        for (int y = 0; y < map.getCurrentLevel().getHeight(); ++y) {
            for (int x = 0; x < map.getCurrentLevel().getWidth(); ++x) {
                Tile t = map.getCurrentLevel().getTile(y, x);
                refreshTile(t);
            }
        }

    }

    public static void main(String[] args) {
        new Roguelike();
    }
}


