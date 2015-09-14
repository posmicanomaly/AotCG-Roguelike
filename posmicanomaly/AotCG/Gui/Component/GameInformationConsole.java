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
    private int turns;

    public GameInformationConsole(int height, int width, Actor player) {
        super(height, width);
        this.player = player;
        turns = 0;
    }

    @Override
    public void updateConsole() {
        clear();
        int barWidth = getxBufferWidth();

        int healthPerChar = player.getMaxHp() / barWidth;
        int expPerChar = player.getExperienceCap(player.getLevel()) / barWidth;

        String healthString = "HP: " + player.getCurrentHp() + "/" + player.getMaxHp();
        String expString = "EXP: " + player.getExperience() + "/" + player.getExperienceCap(player.getLevel());

        ArrayList<String> placeHolder = new ArrayList<String>();
        int row = 0;
        writeString("@: " + player.getLevel(), row, 0);
        row++;
        drawBar(row, 0, barWidth, Colors.HEALTH_DEFICIT, "");
        drawBar(row, 0, barWidth - ((player.getMaxHp() - player.getCurrentHp()) / healthPerChar), Colors.HEALTH_REMAINING, healthString);

        row++;
        drawBar(row, 0, barWidth - ((player.getExperienceCap(player.getLevel()) - player.getExperience()) / expPerChar), Colors.EXPERIENCE, expString);
        row++;
        placeHolder.add("Well");
        placeHolder.add("Here:");
        if(player.getTile().hasItem()) {
            placeHolder.add(player.getTile().getItem().getName());
        }
        for (String s : placeHolder) {
            writeString(s, row, 0);
            row++;
        }

        row = getyBufferHeight() - 1;
        writeString("T: " + turns, row, 0);
        row--;
        writeString("EXP: " + player.getExperience(), row, 0);
        row--;
    }

    private void drawBar(int y, int x, int width, Color color, String text) {
        writeString(text, y, x);
        for(int xLoc = x; xLoc < x + width; xLoc++) {
            setBgColor(y, xLoc, color);
        }
    }

    public void tickTurns() {
        turns++;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }
}
