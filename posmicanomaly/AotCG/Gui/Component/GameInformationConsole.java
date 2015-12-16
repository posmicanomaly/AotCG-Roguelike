package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Colors;
import posmicanomaly.AotCG.Component.Map;
import posmicanomaly.AotCG.Component.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class GameInformationConsole extends EnhancedConsole {
    Actor player;
    private int turns;
    private int currentFrames;
    private int fps;
    private Map map;
    private int barWidth;

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
        barWidth = getxBufferWidth();


        if(hasBorder()) {
            barWidth -= 2;
        }

        String powerString = "PWR: " + player.getPower();

        ArrayList<String> placeHolder = new ArrayList<String>();
        int row = 0;
        int col = 0;
        if(this.hasBorder()) {
            row++;
            col++;
        }
        writeString("@: " + player.getLevel() + " (Depth " + map.getCurrentDepth() + ")", row, col);
        row++;
        drawHealthBar(row, col, player);

        row++;
        drawExpBar(row, col);
        row++;
        writeString(powerString, row, col);
        row++;
        placeHolder.add("Well");
        placeHolder.add("Here:");

        if (player.getTile().hasItem()) {
            placeHolder.add(player.getTile().getItem().getName());
        }
        for (String s : placeHolder) {
            writeString(s, row, col);
            row++;
        }


        row++;
        writeCenteredString("Visible", row);
        row++;

        int maxTargets = 10;
        int targets = 0;

        try {
            for (Tile t : player.getVisibleTiles()) {
                if (t.hasActor() && t.getActor() != player) {
                    if (targets == maxTargets) {
                        continue;
                    }
                    targets++;

                    drawHealthBar(row, col, t.getActor());

                    row++;
                    row++;
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("List modified");
        }

        row = getyBufferHeight() - 1;
        if(hasBorder()) {
            row--;
        }
        writeString("T: " + turns, row, col);
        row--;
        writeString("CF: " + currentFrames, row, col);
        row--;
        writeString("FPS: " + fps, row, col);
        row--;
    }

    private void drawExpBar(int y, int x) {
        String expString = "EXP: " + player.getExperience() + "/" + player.getExperienceCap(player.getLevel());
        drawProgressBar(y, x, barWidth, player.getExperience(), player.getExperienceCap(player.getLevel()), Colors.EXPERIENCE.darker(), Colors.EXPERIENCE, expString);
    }
    private void drawHealthBar(int y, int x, Actor actor) {
        String healthString;
        if(actor.equals(player)) {
            healthString = "HP: " + actor.getCurrentHp() + "/" + actor.getMaxHp();
        } else {
            healthString = actor.getName();
        }

        drawProgressBar(y, x, barWidth, actor.getCurrentHp(), actor.getMaxHp(), Colors.HEALTH_DEFICIT, Colors.HEALTH_REMAINING, healthString);
    }

    private void drawProgressBar(int y, int x, int maxWidth, int currentValue, int maxValue, Color backgroundColor, Color foregroundColor, String barString) {
        int deficit = maxWidth;
        double remaining = ((double)currentValue / maxValue * maxWidth);
        //System.out.println("deficit: " + deficit + ", remaining: " + remaining);
        drawBar(y, x, deficit, backgroundColor, "");
        drawBar(y, x, (int)remaining, foregroundColor, barString);
    }

    private void drawBar(int y, int x, int width, Color color, String text) {
        if(y > getyBufferHeight() - 1) {
            return;
        }
        if(x + width > this.getxBufferWidth()) {
            System.out.println("Gui.drawBar() error: x + width > bufferWidth");
            System.out.println("\tdrawBar(" + y + ", " + x + ", " + width + ", " + color + ", " + text + ")");
            return;
        }
        writeString(text, y, x);
        for(int xLoc = x; xLoc < x + width; xLoc++) {
            setBgColor(y, xLoc, color);
        }
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
