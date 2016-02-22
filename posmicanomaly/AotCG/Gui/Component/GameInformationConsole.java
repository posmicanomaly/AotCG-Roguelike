package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Colors;
import posmicanomaly.AotCG.Component.Map;
import posmicanomaly.AotCG.Component.Tile;
import posmicanomaly.AotCG.Game.Roguelike;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class GameInformationConsole extends EnhancedConsole {
    Roguelike roguelike;
    Actor player;
    private int turns;
    private Map map;
    private int barWidth;

    public GameInformationConsole(int height, int width, Actor player, Map map, Roguelike roguelike) {
        super(height, width);
        this.player = player;
        this.roguelike = roguelike;
        turns = 0;
        this.map = map;
    }

    public void setPlayer(Actor player) {
        this.player = player;
    }
    @Override
    public void updateConsole() {
        clear();
        barWidth = getxBufferWidth();


        if(hasBorder()) {
            barWidth -= 2;
        }
        if(player == null) {
            return;
        }

        String powerString = "PWR: " + player.getPower();


        int row = 0;
        int col = 0;
        if(this.hasBorder()) {
            row++;
            col++;
        }
        writeString("Z:" + map.getCurrentDepth(), row, col);
        row++;
        writeString("WC: x:" + roguelike.getPlayerMapX() + " y:" + roguelike.getPlayerMapY(), row, col);
        row++;
        drawHealthBar(row, col, player);

        row++;
        drawExpBar(row, col);
        row++;
        writeString(powerString, row, col);
        row++;
        writeColoredString("S: Well", row, col, Colors.HEALTH_REMAINING);
        row++;
        row++;
        Tile playerTile = player.getTile();
        Color tileTypeForeground = playerTile.getColor().brighter().brighter().brighter().brighter().brighter();
        Color tileTypeBackground = playerTile.getBackgroundColor().brighter().brighter().brighter();
        if(playerTile.getType() == Tile.Type.WATER) {
            tileTypeForeground = Color.white;
        }
        writeColoredString(player.getTile().getTypeString(), row, col, tileTypeForeground, tileTypeBackground);
        row++;
        row++;

        writeString("On Ground:", row, col);
        row++;
        if (player.getTile().hasItem()) {
            writeString(player.getTile().getItem().getName(), row, col);
        }
        row++;
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
       // writeString("CF: " + currentFrames, row, col);
        row--;
       // writeString("FPS: " + fps, row, col);
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

    public void setMap(Map map) {
        this.map = map;
    }
}
