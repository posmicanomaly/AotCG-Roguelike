package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Item;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/19/2015.
 */
public class InventoryConsole extends EnhancedConsole {
    private Actor player;

    public InventoryConsole(Actor player, int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);
        this.player = player;
    }

    @Override
    public void updateConsole() {
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
}