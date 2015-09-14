package posmicanomaly.AotCG.Gui.Component;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class InventorySideConsole extends EnhancedConsole {
    public InventorySideConsole(int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);
    }

    @Override
    public void updateConsole() {
        ArrayList<String> placeHolder = new ArrayList<String>();
        placeHolder.add("Player");
        placeHolder.add("HP: 10/10");
        placeHolder.add("Well");
        placeHolder.add("Turn: 23993");
        int row = 0;
        for (String s : placeHolder) {
            writeString(s, row, 0);
            row++;
        }
        for (int y = 0; y < this.getyBufferHeight(); y++) {
            for (int x = 0; x < this.getxBufferWidth(); x++) {
                setColor(y, x, Color.WHITE);
            }
        }
    }
}
