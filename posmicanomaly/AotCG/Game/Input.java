package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.LevelFactory;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;

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

                    /*
                    Menu Toggle Input
                     */
            case KeyEvent.VK_M:
            case KeyEvent.VK_I:
                return Command.MENU;
            default:
                return null;
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
            case KeyEvent.VK_I:
                if (roguelike.showInventory) {
                    roguelike.showInventory = false;
                } else {
                    roguelike.showInventory = true;
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
                messageConsole.addMessage("Level flood filled");
                break;
            case KeyEvent.VK_R:
                roguelike.initGame();
                break;
            case KeyEvent.VK_V:
                roguelike.map.getCurrentLevel().toggleAllTilesVisible(true);
                messageConsole.addMessage("All tiles visible");
                break;
            case KeyEvent.VK_B:
                if (roguelike.window.getMainPanel().isDrawBackgroundGlyphs()) {
                    messageConsole.addMessage("Background glyphs off");
                    roguelike.window.getMainPanel().setDrawBackgroundGlyphs(false);
                } else {
                    messageConsole.addMessage("Background glyphs on");
                    roguelike.window.getMainPanel().setDrawBackgroundGlyphs(true);
                }
                break;
            case KeyEvent.VK_EQUALS:
                roguelike.minFrameSpeed++;
                messageConsole.addMessage("minFrameSpeed: " + roguelike.minFrameSpeed);
                break;
            case KeyEvent.VK_MINUS:
                if (roguelike.minFrameSpeed > 0) {
                    roguelike.minFrameSpeed--;
                    messageConsole.addMessage("minFrameSpeed: " + roguelike.minFrameSpeed);
                }
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
    }

    public enum Command {MOVEMENT, DEBUG, MENU}
}
