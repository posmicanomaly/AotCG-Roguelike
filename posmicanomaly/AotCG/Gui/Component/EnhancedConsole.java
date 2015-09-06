package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.libjsrte.Console.Console;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public abstract class EnhancedConsole extends Console {
    public EnhancedConsole(int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);
    }

    public abstract void updateConsole();
}
