package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.*;
import posmicanomaly.AotCG.Gui.Component.EnhancedConsole;
import posmicanomaly.AotCG.Gui.Component.GameInformationConsole;
import posmicanomaly.AotCG.Gui.Component.InventorySideConsole;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;
import posmicanomaly.AotCG.Gui.Gui;
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

    Window window;
    int fontSize = 24;
    int windowHeight = 40;
    int windowWidth = 135;
    int messageHeight = 6;
    int messageWidth;
    int mapHeight;
    int mapWidth;
    int mapDepth = 10;
    long lastRenderTime;
    long minFrameSpeed = 40;
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
    private Console mapConsole;
    private int turns;
    private Gui gui;
    private MessageConsole messageConsole;
    private GameInformationConsole gameInformationConsole;
    private EnhancedConsole inventorySideConsole;
    private Console menuWindow;
    private Title title;
    private Console victoryConsole;
    private Console defeatConsole;
    private State currentState;
    private Actor player;
    private KeyEvent lastKeyEvent;

    public static Random rng;

    public Roguelike() {

        this.window = new Window(this.windowHeight, this.windowWidth, "AotCG", fontSize);
        //this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - gameInformationConsoleWidth;

        this.mapWidth = this.windowWidth - 2 - gameInformationConsoleWidth;
        this.mapHeight = this.windowHeight - 1 - this.messageHeight;

        this.gui = new Gui();

        rng = new Random();

        initGame();

        startGame();

        while (true) {
           gameLoop();
        }
    }

    public static void main(String[] args) {
        new Roguelike();
    }

    private void gameLoop() {
        long startTime = System.currentTimeMillis();

        // Check for key input
        if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent)) {

            /**
             * Check for game state or victory/defeat console displayed
             */

            // Title Screen
            // .
            // .

            if (currentState == State.TITLE) {
                switch (this.window.getLastKeyEvent().getKeyCode()) {
                    case KeyEvent.VK_UP:
                        title.scrollUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        title.scrollDown();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (title.getSelectedItem().equals("New Game")) {
                            currentState = State.PLAYING;
                        }
                }
            }
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
                Input.Command command = Input.processKey(key);

                // Check command and act upon it
                if (command != null) {
                    switch (command) {

                        case MOVEMENT:
                            Direction direction = getPlayerMovementDirection(key);
                            if (moveActor(player, direction)) {
                                recalculateFOV = true;
                            }
                            turns++;

                            /**
                             Check win condition
                             if giant was killed
                             and victoryConsole is null(not initialized yet)

                             If it is not null, then we've seen it and likely hit ESCAPE to keep playing
                             */
                            if (giantSlain && victoryConsole == null) {
                                initVictoryConsole();
                                showVictoryConsole = true;
                            }

                            if (!player.isAlive()) {
                                initDefeatConsole();
                                showDefeatConsole = true;
                            }

                            processNpcActors();
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

                // Recalculate field of vision if it was set to true
                if (recalculateFOV) {
                    //
                    calculateVision(player);
                }
            }

            // Set lastKeyEent to this one that we just used, so we do not enter loop again
            this.lastKeyEvent = this.window.getLastKeyEvent();
        }

        // Draw the game
        // TODO: draw only if we need to, to improve CPU usage
        this.drawGame(this.window.getMainPanel().getRootConsole());

        // Determine remaining time in frame based on when we started loop, to after we've drawn the game
        long remainingTime = minFrameSpeed - (System.currentTimeMillis() - startTime);

        // During initialization, this time may go negative. Set to 0 if this happens to prevent exception.
        if (remainingTime < 0) {
            remainingTime = 0;
        }

        // Sleep for whatever time we have remaining to maintain the desired FPS
        try {
            Thread.sleep(remainingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Increment our current frames
        currentFrames++;

        // Determine FPS
        // .
        // .

        // fpsTimerStart is initialized in startGame()
        // if over 1 second or more has passed, set the currentFrames to lastFramesPerSecond
        if (System.currentTimeMillis() - fpsTimerStart >= 1000) {
            lastFramesPerSecond = currentFrames;

            // Reset currentFrames
            currentFrames = 0;
            // Reset fpsTimerStart
            fpsTimerStart = System.currentTimeMillis();
        }
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
            case KeyEvent.VK_EQUALS:
                minFrameSpeed++;
                messageConsole.addMessage("minFrameSpeed: " + minFrameSpeed);
                break;
            case KeyEvent.VK_MINUS:
                if(minFrameSpeed > 0) {
                    minFrameSpeed--;
                    messageConsole.addMessage("minFrameSpeed: " + minFrameSpeed);
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

    private void calculateVision(Actor actor) {
        map.getCurrentLevel().toggleAllTilesVisible(false);
        actor.clearVisibleTiles();
        int y = player.getTile().getY();
        int x = player.getTile().getX();

        ArrayList<Tile> fieldOfVisionTiles = FieldOfVision.calculateRayCastingFOVVisibleTiles(y, x, map
                .getCurrentLevel(), map.getWidth() / 2);

        for (Tile t : fieldOfVisionTiles) {
            t.setVisible(true);
            t.setExplored(true);
            actor.addVisibleTile(t);
        }
    }

    private void processNpcActors() {
        ArrayList<Actor> npcActors = new ArrayList<>();
        Level currentLevel = map.getCurrentLevel();
        Tile[][] tiles = currentLevel.getTileArray();
        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[y].length; x++) {
                Tile t = tiles[y][x];
                if(t.hasActor()) {
                    if(!t.getActor().equals(player)) {
                        npcActors.add(t.getActor());
                    }
                }
            }
        }


        for(Actor a : npcActors) {
            if(actorInPlayerView(a)) {
                //moveActor(a, Direction.getRandomDirection());
                Direction direction = testAStar(a, player);
                if(direction == null) {
                    messageConsole.addMessage("processNpcActors() :: direction is null");
                } else {
                    moveActor(a, direction);
                }
            } else {
                moveActor(a, Direction.getRandomDirection());
            }
        }
    }

    private boolean actorInPlayerView(Actor actor) {
        for(Tile t : player.getVisibleTiles()) {
            if(t.hasActor()) {
                if(t.getActor().equals(actor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processCombat(Actor firstAttacker, Actor secondAttacker) {
        // get power
        int firstAttackerDamage = firstAttacker.getPower();
        int secondAttackerDamage = secondAttacker.getPower();
        String combatMessage = "";


        // firstAttacker attacks
        combatMessage += firstAttacker.getName() + " hit " + secondAttacker.getName() + " for " + firstAttackerDamage;

        secondAttacker.setCurrentHp(secondAttacker.getCurrentHp() - firstAttackerDamage);

        // Check if secondAttacker was killed
        if (secondAttacker.getCurrentHp() <= 0) {
            secondAttacker.setAlive(false);
        }

        // If secondAttacker is still alive
        if (secondAttacker.isAlive()) {
            // secondAttacker attacks
            combatMessage += ", " + secondAttacker.getName() + " hit " + firstAttacker.getName() + " for " + secondAttackerDamage;
            firstAttacker.setCurrentHp(firstAttacker.getCurrentHp() - secondAttackerDamage);

            // Check if firstAttacker was killed
            if (firstAttacker.getCurrentHp() <= 0) {
                firstAttacker.setAlive(false);
            }
        }

        messageConsole.addMessage(combatMessage);
    }

    private boolean moveActor(Actor actor, Direction d) {
        if(d == null) {
            messageConsole.addMessage("moveActor(" + actor.hashCode() + ", " + d + ") error");
            return false;
        }
        // Obtain the current tile the actor is on
        Tile currentTile = actor.getTile();

        // desiredTile is the tile we will try to move the actor to
        Tile desiredTile;

        // by default, the move will be true
        // we will inhibit the move by setting this to false
        boolean move = true;

        // Get coordinates of the currentTile
        int y = currentTile.getY();
        int x = currentTile.getX();

        // Determine coordinates of desired tile based on direction input
        switch (d) {
            case UP:    y--;        break;
            case DOWN:  y++;        break;
            case LEFT:  x--;        break;
            case RIGHT: x++;        break;
            case NW:    x--; y--;   break;
            case NE:    x++; y--;   break;
            case SW:    x--; y++;   break;
            case SE:    x++; y++;   break;
            default:
                move = false;
                break;
        }

        // If the input was good, move will still be true
        if (move) {
            // set desiredTile to the tile located at the y, x coordinates
            desiredTile = map.getCurrentLevel().getTile(y, x);

            // If the tile is null, it is likely out of range
            if (desiredTile == null) {
                messageConsole.addMessage("Tile is null");
                return false;
            }

            // If the tile is blocked(terrain), we can't go there, likely a wall at this point
            else if (desiredTile.isBlocked()) {
                if(actor.equals(player))
                    messageConsole.addMessage("You bumped into a wall");
                return false;
            }

            // If the tile already has an actor, and the actor is alive, we need to do some combat
            else if (desiredTile.hasActor() && desiredTile.getActor().isAlive()) {
                /**
                 * Player only combat hack
                 */
                if (actor.equals(player) || desiredTile.getActor().equals(player)) {
                    // Combat
                    // .
                    // .

                    Actor nextTileActor = desiredTile.getActor();


                    // Determine who goes first

                    Actor firstAttacker = null;
                    Actor secondAttacker = null;

                    if (actor.getSpeed() > nextTileActor.getSpeed()) {
                        firstAttacker = actor;
                        secondAttacker = nextTileActor;
                    } else if (actor.getSpeed() < nextTileActor.getSpeed()) {
                        firstAttacker = nextTileActor;
                        secondAttacker = actor;
                    } else if (actor.getSpeed() == nextTileActor.getSpeed()) {
                        firstAttacker = actor;
                        secondAttacker = nextTileActor;
                    }

                    processCombat(firstAttacker, secondAttacker);


                    // If nextTileActor was killed
                    // .
                    // .

                    if (!nextTileActor.isAlive()) {
                        nextTileActor.setAlive(false);
                        nextTileActor.setTile(null);
                        desiredTile.setActor(null);

                        Item corpse = new Item('%', Color.gray, desiredTile);
                        corpse.setName(nextTileActor.getCorpseName());
                        desiredTile.setItem(corpse);


                        int baseExp = 10;
                        int expVariance = 3;
                        int randomExp = rng.nextInt((baseExp + expVariance) - (baseExp - expVariance) + 1) + (baseExp
                                - expVariance);

                        actor.addExperience(randomExp);
                        messageConsole.addMessage(actor.getName() + " killed " + nextTileActor.getName() + " (" + randomExp + " exp)");
                        if (nextTileActor.getName().equals("Giant")) {
                            messageConsole.addMessage("Main quest complete: Slay Giant (350 exp)");
                            actor.addExperience(350);
                            giantSlain = true;
                        }
                        int prevLevel = actor.getLevel();
                        actor.evaulateLevel();
                        if (prevLevel < actor.getLevel()) {
                            messageConsole.addMessage(actor.getName() + " leveled up: " + actor.getLevel());
                        }
                        return false;
                    }
                }
            }
            // The tile is valid, not blocked, and has no "living" actor
            // We can move the actor there
            else {
                /*
                I don't like this
                 */
                if(actor.equals(player)) {
                    if (desiredTile.getType() == Tile.Type.STAIRS_DOWN) {
                        boolean levelChanged = map.goDeeper();
                        if (levelChanged) {
                            desiredTile = map.getCurrentLevel().getUpStairs();
                        }
                    } else if (desiredTile.getType() == Tile.Type.STAIRS_UP) {
                        boolean levelChanged = map.goHigher();
                        if (levelChanged) {
                            desiredTile = map.getCurrentLevel().getDownStairs();
                        }
                    }
                }
                Tile previousTile = currentTile;

                // set actor's tile
                actor.setTile(desiredTile);
                // set tile's actor
                desiredTile.setActor(actor);

                // Secret wall?
                // .
                // .

                if (desiredTile.getType() == Tile.Type.WALL_SECRET) {
                    // Set type to DOOR
                    desiredTile.setType(Tile.Type.DOOR);
                    LevelFactory.initTile(desiredTile);
                }

                // remove actor from old tile

                previousTile.setActor(null);

                // Return true, a move was made
                return true;

            }

        }
        // Return false, no move was made
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


            this.messageConsole.copyBufferTo(rootConsole, this.mapHeight, gameInformationConsoleWidth);
            gameInformationConsole.setTurns(turns);
            gameInformationConsole.setCurrentFrames(currentFrames);
            gameInformationConsole.setFps(lastFramesPerSecond);
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

            if(showDefeatConsole) {
                defeatConsole.update();
                defeatConsole.copyBufferTo(rootConsole, this.windowHeight / 2 - defeatConsole.getyBufferHeight() / 2, this.windowWidth / 2 - defeatConsole.getxBufferWidth() / 2);
            }
            this.window.refresh();
        } else if (currentState == State.TITLE) {
            title.update();
            this.title.getTitleConsole().copyBufferTo(rootConsole, 0, 0);
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
        victoryConsole = null;
        showVictoryConsole = false;
        defeatConsole = null;
        showDefeatConsole = false;
        turns = 0;
        lastRenderTime = 0;

        // Set seed
        rng.setSeed(12345);


        initTitleScreen();

        // Set up map
        this.mapConsole = new Console(this.mapHeight, this.mapWidth);

        // Start with a depth of 1. We can create new levels as needed.
        this.map = new Map(mapHeight, mapWidth);

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
        victoryMessages.add("Press R to restart");
        for(String s : victoryMessages) {
            victoryConsole.writeCenteredString(s, row);
            row++;
        }
    }

    private void initDefeatConsole() {
        defeatConsole = new Console(windowHeight / 2, windowWidth / 2);
        defeatConsole.setBorder(true);
        int row = 1;
        ArrayList<String> defeatMessages = new ArrayList<String>();
        defeatMessages.add("You Died!");
        defeatMessages.add("");
        defeatMessages.add("You were killed and now the Giant will destroy everyone!");
        defeatMessages.add("");
        defeatMessages.add("Level Reached: " + player.getLevel());
        defeatMessages.add("Turns taken: " + turns);
        defeatMessages.add("Maximum HP: " + player.getMaxHp());
        defeatMessages.add("");
        defeatMessages.add("Press R to restart");
        for(String s : defeatMessages) {
            defeatConsole.writeCenteredString(s, row);
            row++;
        }
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
        calculateVision(player);
    }

    private void initGui() {
        initMessageConsole();
        gameInformationConsole = new GameInformationConsole(gameInformationConsoleHeight,
                gameInformationConsoleWidth, player, map);
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

        fpsTimerStart = System.currentTimeMillis();
    }

    private void copyMapToBuffer() {
        for (int y = 0; y < map.getCurrentLevel().getHeight(); ++y) {
            for (int x = 0; x < map.getCurrentLevel().getWidth(); ++x) {
                Tile t = map.getCurrentLevel().getTile(y, x);
                refreshTile(t);
            }
        }

    }

    private Direction testAStar(Actor source, Actor target) {
        AStar astar = new AStar(map.getCurrentLevel());

        Tile sourceTile = source.getTile();
        Tile targetTile = target.getTile();

        ArrayList<Tile> path = astar.getShortestPath(sourceTile, targetTile);

        if(path == null) {
            messageConsole.addMessage("testAStar() :: Path is null");
        } else {
            return getDirectionTowardsTile(sourceTile, path.get(0));
        }
        return null;
    }

    private Direction getDirectionTowardsTile(Tile sourceTile, Tile targetTile) {
        int yd = sourceTile.getY() - targetTile.getY();
        int xd = sourceTile.getX() - targetTile.getX();

        if(Math.abs(yd) > Math.abs(xd)) {
            if(yd < 0) {
                return Direction.DOWN;
            } else {
                return Direction.UP;
            }
        }
        else if (Math.abs(yd) < Math.abs(xd)) {
            if(xd < 0) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        }
        else if(Math.abs(yd) == Math.abs(xd)) {
            if(yd < 0 && xd < 0) {
                return Direction.SE;
            } else if(yd > 0 && xd > 0) {
                return Direction.NW;
            } else if( yd < 0 && xd > 0) {
                return Direction.SW;
            } else if(yd > 0 && xd < 0) {
                return Direction.NE;
            }
        }
        return null;
    }

    private Direction getDirectionTowardsActor(Actor source, Actor target) {
        Tile sourceTile = source.getTile();
        Tile targetTile = target.getTile();
        return getDirectionTowardsTile(sourceTile, targetTile);
    }

    public enum Direction {UP, DOWN, LEFT, RIGHT, NW, NE, SW, SE;
        public static Direction getRandomDirection() {
            return values()[rng.nextInt(values().length)];
        }
    }

    public enum State {TITLE, PLAYING, VICTORY}
}


