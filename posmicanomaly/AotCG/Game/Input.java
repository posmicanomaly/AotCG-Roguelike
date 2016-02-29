package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Item;
import posmicanomaly.AotCG.Component.LevelFactory;
import posmicanomaly.AotCG.Gui.Component.InventoryConsole;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Jesse Pospisil on 9/14/2015.
 */
public class Input {

    private final Roguelike roguelike;
    private MessageConsole messageConsole;

    public Input(Roguelike roguelike) {
        this.roguelike = roguelike;
    }

    public void connectToGui(Gui gui) {
        messageConsole = gui.getMessageConsole();
    }

    public Command processKey(KeyEvent key) {
        if(key.isControlDown()) {
            switch(key.getKeyCode()) {
                  /*
                    Menu Toggle Input
                     */
                case KeyEvent.VK_M:
                case KeyEvent.VK_D:
                case KeyEvent.VK_Q:
                    return Command.MENU;
                default:
                    return null;
            }
        }
        else if(roguelike.showInventory && !key.isControlDown()) {
            return Command.INVENTORY;
        }
        else {
            switch (key.getKeyCode()) {
                    /*
                    Player Movement Input
                     */
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_Q:
                case KeyEvent.VK_E:
                case KeyEvent.VK_Z:
                case KeyEvent.VK_C:
                    return Command.MOVEMENT;

                case KeyEvent.VK_PERIOD:
                    return Command.ACTUATE;

                    /*
                    DEBUG Input
                     */
                case KeyEvent.VK_F:
                case KeyEvent.VK_R:
                case KeyEvent.VK_V:
                case KeyEvent.VK_B:
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_EQUALS:
                    return Command.DEBUG;

                default:
                    return null;
            }
        }
    }

    private boolean isInventorySelectionValid(KeyEvent key, Roguelike roguelike) {
        // Check if the input is a -> z, as in selecting an item
        int selection = key.getKeyChar() - 'a';
        if(selection < 0) {
            System.out.println("Inventory selection not a letter: " + selection);
        } else if(selection > roguelike.getPlayer().getInventory().size() - 1) {
            System.out.println("Inventory selection input larger than inventory size: " + selection);
        }
        return true;
    }

    public enum ItemUse {CONSUME, DROP}

    public void processConsumeInventoryCommand(KeyEvent key, Roguelike roguelike) {
        if (isInventorySelectionValid(key, roguelike)) {
            Item item = roguelike.getPlayer().getInventory().get(key.getKeyChar() - 'a');
            roguelike.getPlayer().useItem(item, ItemUse.CONSUME);
        }
    }

    public void processDropInventoryCommand(KeyEvent key, Roguelike roguelike) {
        if(isInventorySelectionValid(key, roguelike)) {
            Item item = roguelike.getPlayer().getInventory().get(key.getKeyChar() - 'a');
            roguelike.getPlayer().useItem(item, ItemUse.DROP);
        }
    }

    public void processMenuCommand(KeyEvent key, Roguelike roguelike) {
        switch (key.getKeyCode()) {
            /*
            Menu Toggle Input
             */
            case KeyEvent.VK_M:
                if (roguelike.showMenu) {
                    roguelike.showMenu = false;
                } else {
                    roguelike.showMenu = true;
                }
                break;
            case KeyEvent.VK_Q:

                if(roguelike.showInventory) {
                    roguelike.showInventory = false;
                } else {
                    roguelike.showInventory = true;
                    roguelike.getGui().getInventoryConsole().setDisplayMode(InventoryConsole.Display.CONSUME);
                }

                break;
            case KeyEvent.VK_D:

                if (roguelike.showInventory) {
                    roguelike.showInventory = false;
                } else {
                    roguelike.showInventory = true;
                    roguelike.getGui().getInventoryConsole().setDisplayMode(InventoryConsole.Display.DROP);
                }

                break;
            default:
                break;
        }
    }

    public void processDebugCommand(KeyEvent key, Roguelike roguelike) {

        switch (key.getKeyCode()) {

            /*
            DEBUG Input
             */
            case KeyEvent.VK_F:
                LevelFactory.DEBUG_FLOOD_FILL(roguelike.map.getCurrentLevel().getTileArray());
                LevelFactory.DEBUG_PROCESS_MAP(roguelike.map.getCurrentLevel().getTileArray());
                messageConsole.addMessage("Level flood filled", Color.yellow);
                break;
            case KeyEvent.VK_R:
                roguelike.initializeGameEnvironment();
                break;
            case KeyEvent.VK_V:
                roguelike.map.getCurrentLevel().toggleAllTilesVisible(true);
                messageConsole.addMessage("All tiles visible", Color.yellow);
                break;
            case KeyEvent.VK_B:
                if(roguelike.runPlayerBot) {
                    roguelike.runPlayerBot = false;
                } else {
                    roguelike.runPlayerBot = true;
                }
                break;
            case KeyEvent.VK_EQUALS:
                roguelike.getWindow().increaseDisplayScale();
                break;
            case KeyEvent.VK_MINUS:
                roguelike.getWindow().decreaseDisplayScale();
                break;
        }
    }

    protected Direction getPlayerMovementDirection(KeyEvent key) {
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

    public enum Direction {UP, DOWN, LEFT, RIGHT, NW, NE, SW, SE;

        public static Direction getRandomDirection() {
            return values()[Roguelike.rng.nextInt(values().length)];
        }

        public static Direction getRandomCardinalDirection() {
            Direction direction = null;
            boolean valid = false;
            do {
                direction = getRandomDirection();
                switch (direction) {
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        valid = true;
                        break;
                }
            } while(!valid);
            return direction;
        }
    }

    public enum Command {MOVEMENT, DEBUG, MENU, ACTUATE, INVENTORY}
}
