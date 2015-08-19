package posmicanomaly.LibjsrteRoguelikeExample.Gui;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class GameInformationConsole extends EnhancedConsole {
    public GameInformationConsole(int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);
    }

    @Override
    public void updateConsole() {
        ArrayList<String> placeHolder = new ArrayList<String>();
        placeHolder.add("Player");
        placeHolder.add("HP: 10/10");
        placeHolder.add("Well");
        placeHolder.add("Turn: 23993");
        int row = 1;
        for(String s : placeHolder) {
            writeString(s, row, 1);
            row++;
        }
    }
}
