package posmicanomaly.AotCG.Game;

import java.awt.event.KeyEvent;

/**
 * Created by Jesse Pospisil on 9/14/2015.
 */
public abstract class Input {
    public static Command processKey(KeyEvent key) {
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

    public enum Command {MOVEMENT, DEBUG, MENU}
}
