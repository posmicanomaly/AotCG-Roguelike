package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Colors;
import posmicanomaly.AotCG.Component.Tile;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class GameInformationConsole extends EnhancedConsole {
    Actor player;

    public GameInformationConsole(int yBufferWidth, int xBufferWidth, Actor player) {
        super(yBufferWidth, xBufferWidth);
        this.player = player;
    }

    @Override
    public void updateConsole() {
        clear();

        ArrayList<String> placeHolder = new ArrayList<String>();
        int row = 0;
        drawBar(row, 0, this.getxBufferWidth(), Colors.HEALTH_DEFICIT, "");
        drawBar(row, 0, (int) (this.getxBufferWidth() * .75), Colors.HEALTH_REMAINING, "75 / 100");

        row++;

        placeHolder.add(player.getName());
        placeHolder.add("HP: " + player.getCurrentHp() + "/" + player.getMaxHp());
        placeHolder.add("Well");
        placeHolder.add("Turn: 23993");
        placeHolder.add("Here:");
        if(player.getTile().hasItem()) {
            placeHolder.add(player.getTile().getItem().getName());
        }
        for (String s : placeHolder) {
            writeString(s, row, 0);
            row++;
        }
    }

    private void drawBar(int y, int x, int width, Color color, String text) {
        writeString(text, y, x);
        for(int xLoc = x; xLoc < x + width; xLoc++) {
            setBgColor(y, xLoc, color);
        }
    }
}
