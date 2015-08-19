package posmicanomaly.LibjsrteRoguelikeExample.Game;

import posmicanomaly.LibjsrteRoguelikeExample.Component.Actor;
import posmicanomaly.LibjsrteRoguelikeExample.Component.Map;
import posmicanomaly.LibjsrteRoguelikeExample.Component.Tile;
import posmicanomaly.LibjsrteRoguelikeExample.Gui.MessageConsole;
import posmicanomaly.LibjsrteRoguelikeExample.Gui.RightSidePanel;
import posmicanomaly.libjsrte.Console.Console;
import posmicanomaly.libjsrte.Util.ColorTools;
import posmicanomaly.libjsrte.Window;

import java.awt.event.KeyEvent;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Roguelike {
    Window window;
    private Console mapConsole;
    private MessageConsole messageConsole;
    private RightSidePanel rightSidePanel;
    private Console menuWindow;

    public enum Direction {UP, DOWN, LEFT, RIGHT};

    int windowHeight = 9 * 6;
    int windowWidth = 16 * 7;
    int messageHeight = 10;
    int messageWidth;
    int mapHeight;
    int mapWidth;
    int mapDepth = 10;

    int rightSidePanelHeight = windowHeight;
    int rightSidePanelWidth = 16;
    boolean showMenu;
    Map map;

    private Actor player;
    private KeyEvent lastKeyEvent;

    public Roguelike() {

        this.window = new Window(this.windowHeight, this.windowWidth);
        this.window.getMainPanel().getRootConsole().setBorder(true);

        this.messageWidth = this.windowWidth - rightSidePanelWidth;

        this.mapWidth = this.windowWidth - 2 - rightSidePanelWidth;
        this.mapHeight = this.windowHeight - 1 - this.messageHeight;



        this.mapConsole = new Console(this.mapHeight, this.mapWidth);

        initMessageConsole();
        rightSidePanel = new RightSidePanel(rightSidePanelHeight, rightSidePanelWidth);
        rightSidePanel.setBorder(true);

        this.menuWindow = new Console(25, 25);
        this.menuWindow.setBorder(true);
        this.showMenu = false;
        this.map = new Map(mapHeight, mapWidth, mapDepth);

        this.copyMapToBuffer(this.mapConsole);

        Tile startingTile = map.getCurrentLevel().getTile(map.getCurrentLevel().getHeight() / 2, map.getCurrentLevel().getWidth() / 2);
        player = new Actor('@', ColorTools.getRandomColor(), startingTile);

        this.window.getMainPanel().setRender(true);
        this.lastKeyEvent = this.window.getLastKeyEvent();

        while (true) {
            try {
                Thread.sleep((long) Window.THREAD_SLEEP);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            // Randomize the player's color just for fun
            player.setColor(ColorTools.getRandomColor());
            if (!this.window.getLastKeyEvent().equals(this.lastKeyEvent)) {
                switch (this.window.getLastKeyEvent().getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        moveActor(Direction.LEFT);
                        break;
                    case KeyEvent.VK_UP:
                        moveActor(Direction.UP);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveActor(Direction.RIGHT);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveActor(Direction.DOWN);
                        break;
                    case KeyEvent.VK_M:
                        if (this.showMenu) {
                            this.showMenu = false;
                        } else {
                            this.showMenu = true;
                        }
                }

                this.lastKeyEvent = this.window.getLastKeyEvent();
            }
            this.drawGame(this.window.getMainPanel().getRootConsole());
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
            } else {
                player.setTile(nextTile);
                messageConsole.addMessage("Moved");
                refreshTile(playerTile);
                messageConsole.addMessage("Tile refreshed");
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
        mapConsole.setChar(tile.getY(), tile.getX(), tile.getSymbol());
        mapConsole.setColor(tile.getY(), tile.getX(), tile.getColor());
    }

    private void drawGame(Console rootConsole) {
        rootConsole.clear();

        // Copy player to the map
        Tile playerTile = player.getTile();
        mapConsole.setChar(playerTile.getY(), playerTile.getX(), player.getSymbol());
        mapConsole.setColor(playerTile.getY(), playerTile.getX(), player.getColor());
        this.mapConsole.copyBufferTo(rootConsole, 1, 1);


        this.messageConsole.copyBufferTo(rootConsole, this.mapHeight + 1, 0);
        rightSidePanel.updateConsole();
        rightSidePanel.copyBufferTo(rootConsole, 0, mapWidth + 2);

        if (this.showMenu) {
            this.menuWindow.copyBufferTo(rootConsole, this.window.HEIGHT / 2 - 12, this.window.WIDTH / 2 - 12);
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

    private void copyMapToBuffer(Console target) {
        for (int y = 0; y < map.getHeight(); ++y) {
            for (int x = 0; x < map.getWidth(); ++x) {
                target.setChar(y, x, map.getCurrentLevel().getSymbol(y, x));
                target.setColor(y, x, map.getCurrentLevel().getColor(y, x));
            }
        }

    }

    public static void main(String[] args) {
        new Roguelike();
    }
}


