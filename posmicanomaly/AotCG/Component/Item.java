package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 9/14/2015.
 */
public class Item extends Entity{
    public Item(char symbol, Color color, Tile tile, String name) {
        super(symbol, color, tile);
        this.setName(name);
    }

    public boolean use(Actor target) {
        if(this.name.equals("Health Potion")) {
            target.setMaxHp(target.getMaxHp() + 10);
            target.setCurrentHp(target.getMaxHp());
            return true;
        }
        return false;
    }
}
