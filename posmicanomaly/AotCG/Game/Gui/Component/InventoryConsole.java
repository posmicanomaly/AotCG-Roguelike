package posmicanomaly.AotCG.Game.Gui.Component;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.Item.Item;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class InventoryConsole extends EnhancedConsole {
    private Actor player;
    private Display displayMode;

    public Display getDisplayMode() {
        return displayMode;
    }

    public enum Display {ALL, CONSUME, DROP}

    public InventoryConsole(Actor player, int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);
        this.player = player;
        displayMode = Display.ALL;
    }

    @Override
    public void updateConsole() {
        String newTitle = "Inventory:";
        switch(displayMode) {
            case ALL:
                newTitle += "All";
                break;
            case CONSUME:
                newTitle += "Consume";
                break;
            case DROP:
                newTitle += "Drop";
                break;
        }
        setTitle(newTitle);
        clear();
        ArrayList<String> inventory = new ArrayList<String>();
        char currentItem = 'a';
        for(Item i : player.getInventory()) {
            inventory.add("(" + currentItem + ") " + i.getName());
            currentItem++;
        }
        int row = 0;
        int col = 0;
        if(hasBorder()) {
            row++;
            col++;
        }
        for (String s : inventory) {
            writeString(s, row, col);
            row++;
        }
        for (int y = 0; y < this.getyBufferHeight(); y++) {
            for (int x = 0; x < this.getxBufferWidth(); x++) {
                setColor(y, x, Color.WHITE);
            }
        }
    }

    public void setPlayer(Actor player) {
        this.player = player;
    }
    public void setDisplayMode(Display displayMode) {
        this.displayMode = displayMode;
    }
}
