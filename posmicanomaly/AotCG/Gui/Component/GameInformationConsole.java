package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.AotCG.Component.Actor;

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
        ArrayList<String> placeHolder = new ArrayList<String>();
        placeHolder.add(player.getName());
        placeHolder.add("HP: " + player.getCurrentHp() + "/" + player.getMaxHp());
        placeHolder.add("Well");
        placeHolder.add("Turn: 23993");
        int row = 0;
        for(String s : placeHolder) {
            writeString(s, row, 0);
            row++;
        }
    }
}
