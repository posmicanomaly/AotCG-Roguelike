package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Colors;
import posmicanomaly.AotCG.Component.Map;
import posmicanomaly.AotCG.Component.Tile;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class GameInformationConsole extends EnhancedConsole {
    Actor player;
    private int turns;
    private int currentFrames;
    private int fps;
    private Map map;

    public GameInformationConsole(int height, int width, Actor player, Map map) {
        super(height, width);
        this.player = player;
        turns = 0;
        currentFrames = 0;
        fps = 0;
        this.map = map;
    }

    @Override
    public void updateConsole() {
        clear();
        int barWidth = getxBufferWidth();
        if(barWidth > player.getMaxHp()) {
            barWidth = player.getMaxHp();
        }

        int healthPerChar = player.getMaxHp() / barWidth;
        int expPerChar = player.getExperienceCap(player.getLevel()) / barWidth;

        String healthString = "HP: " + player.getCurrentHp() + "/" + player.getMaxHp();
        String expString = "EXP: " + player.getExperience() + "/" + player.getExperienceCap(player.getLevel());
        String powerString = "PWR: " + player.getPower();

        ArrayList<String> placeHolder = new ArrayList<String>();
        int row = 0;
        writeString("@: " + player.getLevel() + " (Depth " + map.getCurrentDepth() + ")", row, 0);
        row++;
        drawBar(row, 0, barWidth, Colors.HEALTH_DEFICIT, "");
        drawBar(row, 0, barWidth - ((player.getMaxHp() - player.getCurrentHp()) / healthPerChar), Colors.HEALTH_REMAINING, healthString);

        row++;
        drawBar(row, 0, barWidth - ((player.getExperienceCap(player.getLevel()) - player.getExperience()) / expPerChar), Colors.EXPERIENCE, expString);
        row++;
        writeString(powerString, row, 0);
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

        row++;
        writeCenteredString("Visible", row);
        row++;

        int maxTargets = 10;
        int targets = 0;
        for(Tile t : player.getVisibleTiles()) {
            if(t.hasActor() && t.getActor() != player) {
                if(targets == maxTargets) {
                    continue;
                }
                targets++;
                barWidth = getxBufferWidth();
                if(barWidth > t.getActor().getMaxHp()) {
                    barWidth = t.getActor().getMaxHp();
                }

                healthPerChar = t.getActor().getMaxHp() / barWidth;
                drawBar(row, 0, barWidth, Colors.HEALTH_DEFICIT, "");
                drawBar(row, 0, barWidth - ((t.getActor().getMaxHp() - t.getActor().getCurrentHp()) / healthPerChar), Colors.HEALTH_REMAINING, t.getActor().getName());
                row++;
                row++;
            }
        }

        row = getyBufferHeight() - 1;
        writeString("T: " + turns, row, 0);
        row--;
        writeString("CF: " + currentFrames, row, 0);
        row--;
        writeString("FPS: " + fps, row, 0);
        row--;
    }

    private void drawBar(int y, int x, int width, Color color, String text) {
        if(y > getyBufferHeight() - 1) {
            return;
        }
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

    public void setCurrentFrames(int currentFrames) {
        this.currentFrames = currentFrames;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }
}
