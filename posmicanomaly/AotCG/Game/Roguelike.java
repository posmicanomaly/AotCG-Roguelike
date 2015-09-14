package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.*;
import posmicanomaly.AotCG.Gui.Component.EnhancedConsole;
import posmicanomaly.AotCG.Gui.Component.GameInformationConsole;
import posmicanomaly.AotCG.Gui.Component.InventorySideConsole;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;
import posmicanomaly.AotCG.Gui.Gui;
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

    Window window;
    private Console mapConsole;

    private int turns;
    private Gui gui;

    private MessageConsole messageConsole;
    private GameInformationConsole gameInformationConsole;
    private EnhancedConsole inventorySideConsole;
    private Console menuWindow;
    private Console titleConsole;
    private Console victoryConsole;

    public enum Direction {UP, DOWN, LEFT, RIGHT, NW, NE, SW, SE}

    public enum State {TITLE, PLAYING, VICTORY}

    private State currentState;

    int fontSize = 24;

    int windowHeight = 40;
    int windowWidth = 135;
    int messageHeight = 6;
    int messageWidth;
    int mapHeight;
    int mapWidth;
    int mapDepth = 10;

    int gameInformationConsoleHeight = windowHeight;
    int gameInformationConsoleWidth = 20;
    boolean showMenu;
    boolean showInventory;
    boolean giantSlain;
    boolean showVictoryConsole;
    Map map;

    private Actor player;
    private KeyEvent lastKeyEvent;

    public Roguelike() {

        this.window = new Window(this.windowHeight, this.windowWidth, "AotCG", fontSize);
        //this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - gameInformationConsoleWidth;

        this.mapWidth = this.windowWidth - 2 - gameInformationConsoleWidth;
        this.mapHeight = this.windowHeight - 1 - this.messageHeight;

        this.gui = new Gui();


        initGame();

        startGame();

        while (true) {
           gameLoop();
        }
    }

    private void gameLoop() {
        /*
        Limit CPU
         */
        try {
            Thread.sleep((long) Window.THREAD_SLEEP);
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        }

        // Check for key input
        if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent)) {
                /*
                Skip title screen, start PLAYING
                 */
            if (currentState == State.TITLE) {
                currentState = State.PLAYING;
            }

            if (showVictoryConsole) {
                switch (this.window.getLastKeyEvent().getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        showVictoryConsole = false;
                }
            }
                /*
                Game Loop
                 */
            else {
                boolean recalculateFOV = false;
                KeyEvent key = this.window.getLastKeyEvent();
                Input.Command command = Input.processKey(key);
                if(command != null) {
                    switch (command) {
                        case MOVEMENT:
                            Direction direction = getPlayerMovementDirection(key);
                            if (moveActor(player, direction)) {
                                recalculateFOV = true;
                            }
                            turns++;
                            /*
                            Check win condition
                            if giant was killed
                            and victoryConsole is null(not initialized yet)

                            If it is not null, then we've seen it and likely hit ESCAPE to keep playing
                             */
                            if(giantSlain && victoryConsole == null) {
                                initVictoryConsole();
                                showVictoryConsole = true;
                            }
                            break;
                        case DEBUG:
                            processDebugCommand(key);
                            break;
                        case MENU:
                            processMenuCommand(key);
                            break;
                        default:
                            break;
                    }
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

    private void processMenuCommand(KeyEvent key) {
        switch (key.getKeyCode()) {

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
    }

    private void processDebugCommand(KeyEvent key) {
        switch (key.getKeyCode()) {

                    /*
                    DEBUG Input
                     */
            case KeyEvent.VK_F:
                LevelFactory.DEBUG_FLOOD_FILL(map.getCurrentLevel().getTileArray());
                LevelFactory.DEBUG_PROCESS_MAP(map.getCurrentLevel().getTileArray());
                messageConsole.addMessage("Level flood filled");
                break;
            case KeyEvent.VK_R:
                initGame();
                break;
            case KeyEvent.VK_V:
                map.getCurrentLevel().toggleAllTilesVisible(true);
                messageConsole.addMessage("All tiles visible");
                break;
            case KeyEvent.VK_B:
                if (window.getMainPanel().isDrawBackgroundGlyphs()) {
                    messageConsole.addMessage("Background glyphs off");
                    window.getMainPanel().setDrawBackgroundGlyphs(false);
                } else {
                    messageConsole.addMessage("Background glyphs on");
                    window.getMainPanel().setDrawBackgroundGlyphs(true);
                }
                break;
        }
    }

    private Direction getPlayerMovementDirection(KeyEvent key) {
        Direction direction = null;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                direction = Direction.UP;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                direction = Direction.DOWN;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                direction = Direction.LEFT;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                direction = Direction.RIGHT;
                break;
            case KeyEvent.VK_Q:
                direction = Direction.NW;
                break;
            case KeyEvent.VK_E:
                direction = Direction.NE;
                break;
            case KeyEvent.VK_Z:
                direction = Direction.SW;
                break;
            case KeyEvent.VK_C:
                direction = Direction.SE;
                break;
        }
        return direction;
    }

    private void calculateVision() {
        map.getCurrentLevel().toggleAllTilesVisible(false);
        int y = player.getTile().getY();
        int x = player.getTile().getX();

        ArrayList<Tile> fieldOfVisionTiles = FieldOfVision.calculateRayCastingFOVVisibleTiles(y, x, map
                .getCurrentLevel(), 12);

        for (Tile t : fieldOfVisionTiles) {
            t.setVisible(true);
            t.setExplored(true);
        }
    }

    private boolean moveActor(Actor actor, Direction d) {
        Tile prevTile = actor.getTile();
        Tile nextTile;
        boolean move = true;
        int y = prevTile.getY();
        int x = prevTile.getX();
        switch (d) {
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
            case NW:
                x--;
                y--;
                break;
            case NE:
                x++;
                y--;
                break;
            case SW:
                x--;
                y++;
                break;
            case SE:
                x++;
                y++;
                break;
            default:
                move = false;
                break;
        }
        if (move) {
            nextTile = map.getCurrentLevel().getTile(y, x);
            if (nextTile == null) {
                messageConsole.addMessage("Tile is null");
                return false;
            } else if (nextTile.isBlocked()) {
                messageConsole.addMessage("You bumped into a wall");
                return false;
            } else if (nextTile.hasActor() && nextTile.getActor().isAlive()) {
                Actor nextTileActor = nextTile.getActor();

                nextTileActor.setAlive(false);
                nextTileActor.setTile(null);
                nextTile.setActor(null);
                Item corpse = new Item('%', Color.gray, nextTile);
                corpse.setName(nextTileActor.getCorpseName());
                nextTile.setItem(corpse);
                player.setCurrentHp(player.getCurrentHp() - 1);
                Random rng = new Random();
                int baseExp = 10;
                int expVariance = 3;
                int randomExp = rng.nextInt((baseExp + expVariance) - (baseExp - expVariance) + 1) + (baseExp - expVariance);
                player.addExperience(randomExp);
                messageConsole.addMessage("Killed " + nextTileActor.getName() + " (" + randomExp + " exp)");
                if(nextTileActor.getName().equals("Giant")) {
                    messageConsole.addMessage("Main quest complete: Slay Giant (350 exp)");
                    player.addExperience(350);
                    giantSlain = true;
                }
                int prevLevel = player.getLevel();
                player.evaulateLevel();
                if(prevLevel < player.getLevel()) {
                    messageConsole.addMessage("You leveled up: " + player.getLevel());
                }
                return false;
            } else {
                /*
                I don't like this
                 */
                actor.setTile(nextTile);
                nextTile.setActor(actor);
                prevTile.setActor(null);
                return true;
            }

        }
        return false;
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
        Random rng = new Random();
        if (tile.getType() == Tile.Type.WATER) {
            if (rng.nextInt(100) < 1) {
                tile.setBackgroundColor(ColorTools.varyColor(Colors.WATER_BG, 0.7, 1.0, ColorTools.BaseColor.RGB));

            }
            if (rng.nextInt(100) < 1) {
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
                color = tile.getColor().brighter();
            }
            bgColor = tile.getBackgroundColor().brighter();
        } else if (tile.isExplored()) {
            glyph = tile.getSymbol();
            color = tile.getColor().darker().darker();
            bgColor = tile.getBackgroundColor().darker().darker();
        } else {
            glyph = ' ';
            color = Color.black;
            bgColor = Color.black;
        }
        mapConsole.setColor(tile.getY(), tile.getX(), color);
        mapConsole.setBgColor(tile.getY(), tile.getX(), bgColor);
        mapConsole.setChar(tile.getY(), tile.getX(), glyph);
    }

    private void drawGame(Console rootConsole) {
        rootConsole.clear();
        if (currentState == State.PLAYING) {

            // Refresh the map buffer
            copyMapToBuffer();

            this.mapConsole.copyBufferTo(rootConsole, 0, gameInformationConsoleWidth);


            this.messageConsole.copyBufferTo(rootConsole, this.mapHeight + 1, gameInformationConsoleWidth);
            gameInformationConsole.setTurns(turns);
            gameInformationConsole.updateConsole();
            gameInformationConsole.copyBufferTo(rootConsole, 0, 0);

            if (this.showMenu) {
                this.menuWindow.copyBufferTo(rootConsole, this.window.HEIGHT / 2 - 12, this.window.WIDTH / 2 - 12);
            }

            if (showInventory) {
                inventorySideConsole.updateConsole();
                inventorySideConsole.copyBufferTo(rootConsole, 0, rootConsole.getxBufferWidth() - inventorySideConsole.getxBufferWidth());
            }

            if(showVictoryConsole) {
                victoryConsole.update();
                victoryConsole.copyBufferTo(rootConsole, this.windowHeight / 2 - victoryConsole.getyBufferHeight() / 2, this.windowWidth / 2 - victoryConsole.getxBufferWidth() / 2);
            }
            this.window.refresh();
        } else if (currentState == State.TITLE) {
            this.titleConsole.copyBufferTo(rootConsole, 0, 0);
            this.window.refresh();
        }
    }

    private void initMessageConsole() {
        this.messageConsole = new MessageConsole(this.messageHeight, this.messageWidth);
        //this.messageConsole.setBorder(true);
        this.messageConsole.addMessage("Welcome");
    }

    private void initGame() {
        currentState = State.TITLE;
        giantSlain = false;
        showVictoryConsole = false;
        turns = 0;

        initTitleScreen();

        // Set up map
        this.mapConsole = new Console(this.mapHeight, this.mapWidth);
        this.map = new Map(mapHeight, mapWidth, mapDepth);

        initPlayer();



        // Init the GUI
        initGui();
    }

    private void initVictoryConsole() {
        victoryConsole = new Console(windowHeight / 2, windowWidth / 2);
        victoryConsole.setBorder(true);
        int row = 1;
        ArrayList<String> victoryMessages = new ArrayList<String>();
        victoryMessages.add("You Win!");
        victoryMessages.add("");
        victoryMessages.add("You have slain the Giant and saved everyone!");
        victoryMessages.add("");
        victoryMessages.add("Level Reached: " + player.getLevel());
        victoryMessages.add("Turns taken: " + turns);
        victoryMessages.add("Maximum HP: " + player.getMaxHp());
        victoryMessages.add("");
        victoryMessages.add("Press ESCAPE to keep playing");
        for(String s : victoryMessages) {
            victoryConsole.writeCenteredString(s, row);
            row++;
        }
    }

    private void initTitleScreen() {
        //title console
        titleConsole = new Console(windowHeight, windowWidth);
        //titleConsole.setBorder(true);
        String title1 = "AotCG";
        String title2 = "A Roguelike game written in Java using Swing with libjsrte";
        String title3 = "Press any key to start game";
        titleConsole.writeString(title1, 2, titleConsole.getxBufferWidth() / 2 - title1.length() / 2);
        titleConsole.writeString(title2, 4, titleConsole.getxBufferWidth() / 2 - title2.length() / 2);
        titleConsole.writeString(title3, 6, titleConsole.getxBufferWidth() / 2 - title3.length() / 2);
    }

    private void initPlayer() {
        // Set up starting tile for player
        Tile startingTile = map.getCurrentLevel().getRandomTile(Tile.Type.FLOOR);

        // Create player at that tile and set them up
        player = new Actor('@', ColorTools.getRandomColor(), startingTile);
        player.setName("Player");
        startingTile.setActor(player);
        calculateVision();
    }

    private void initGui() {
        initMessageConsole();
        gameInformationConsole = new GameInformationConsole(gameInformationConsoleHeight,
                gameInformationConsoleWidth, player);
        //gameInformationConsole.setBorder(true);

        inventorySideConsole = new InventorySideConsole(mapHeight, 20);
        //inventorySideConsole.setBorder(true);

        this.menuWindow = new Console(25, 25);
        this.menuWindow.setBorder(true);
        this.menuWindow.fillBgColor(new Color(0, 0, 0, 0.3f));
        this.showMenu = false;
        showInventory = false;
    }

    private void startGame() {
        // Copy the map to mapConsole(buffer)
        this.copyMapToBuffer();
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


