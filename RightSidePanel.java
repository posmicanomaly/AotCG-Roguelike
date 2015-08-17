import posmicanomaly.libjsrte.Console.Console;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class RightSidePanel extends Console {
    public RightSidePanel(int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);
    }

    public void updateConsole() {
        writeString("Player", 1, 1);
        writeString("HP", 2, 1);
    }
}
