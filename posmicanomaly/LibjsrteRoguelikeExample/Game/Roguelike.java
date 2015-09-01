package posmicanomaly.LibjsrteRoguelikeExample.Game;

import posmicanomaly.LibjsrteRoguelikeExample.Component.Actor;
import posmicanomaly.LibjsrteRoguelikeExample.Component.LevelFactory;
import posmicanomaly.LibjsrteRoguelikeExample.Component.Map;
import posmicanomaly.LibjsrteRoguelikeExample.Component.Tile;
import posmicanomaly.LibjsrteRoguelikeExample.Gui.EnhancedConsole;
import posmicanomaly.LibjsrteRoguelikeExample.Gui.GameInformationConsole;
import posmicanomaly.LibjsrteRoguelikeExample.Gui.InventorySideConsole;
import posmicanomaly.LibjsrteRoguelikeExample.Gui.MessageConsole;
import posmicanomaly.libjsrte.Console.Console;
import posmicanomaly.libjsrte.Util.ColorTools;
import posmicanomaly.libjsrte.Window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Roguelike {
    Window window;
    private Console mapConsole;
    private MessageConsole messageConsole;
    private EnhancedConsole gameInformationConsole;
    private EnhancedConsole inventorySideConsole;
    private Console menuWindow;

    public enum Direction {UP, DOWN, LEFT, RIGHT};

    int windowHeight = 9 * 7;
    int windowWidth = 16 * 9;
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

        this.window = new Window(this.windowHeight, this.windowWidth, "Roguelike Example");
        this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - gameInformationConsoleWidth;

        this.mapWidth = this.windowWidth - 2 - gameInformationConsoleWidth;
        this.mapHeight = this.windowHeight - 1 - this.messageHeight;



        initGame();

        while (true) {
            try {
                Thread.sleep((long) Window.THREAD_SLEEP);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent)) {
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
                        if(showInventory) {
                            showInventory = false;
                        } else {
                            showInventory = true;
                        }
                        break;
                    default:
                        break;
                }
                if(recalculateFOV) {
                    //
                    calculateVision();
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
        } else if(tile.isExplored()) {
            mapConsole.setChar(tile.getY(), tile.getX(), tile.getSymbol());
            mapConsole.setColor(tile.getY(), tile.getX(), tile.getColor().darker());
        } else {
            mapConsole.setChar(tile.getY(), tile.getX(), ' ');
        }
    }

    private void drawGame(Console rootConsole) {
        rootConsole.clear();

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

        if(showInventory) {
            inventorySideConsole.updateConsole();
            inventorySideConsole.copyBufferTo(rootConsole, 1, 1);
        }

        this.window.refresh();
    }

    private void initMessageConsole() {
        this.messageConsole = new MessageConsole(this.messageHeight, this.messageWidth);
        this.messageConsole.setBorder(true);
        this.messageConsole.addMessage("Welcome");

//        this.messageConsole.writeString("Hello World", 1, 1);
//        this.messageConsole.writeString("KeyEvent: " + this.lastKeyEvent.getKeyCode(), 2, 1);
    }

    private void initGame() {
        this.mapConsole = new Console(this.mapHeight, this.mapWidth);

        initMessageConsole();
        gameInformationConsole = new GameInformationConsole(gameInformationConsoleHeight, gameInformationConsoleWidth);
        gameInformationConsole.setBorder(true);

        inventorySideConsole = new InventorySideConsole(mapHeight, 20);
        inventorySideConsole.setBorder(true);

        this.menuWindow = new Console(25, 25);
        this.menuWindow.setBorder(true);
        this.showMenu = false;
        showInventory = false;
        this.map = new Map(mapHeight, mapWidth, mapDepth);



        Tile startingTile = map.getCurrentLevel().getTile(map.getCurrentLevel().getHeight() / 2, map.getCurrentLevel().getWidth() / 2);
        player = new Actor('@', ColorTools.getRandomColor(), startingTile);
        calculateVision();
        this.copyMapToBuffer();

        this.window.getMainPanel().setRender(true);
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


