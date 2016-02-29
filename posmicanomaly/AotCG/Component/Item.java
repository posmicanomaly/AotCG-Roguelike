package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 9/14/2015.
 */
public class Item extends Entity implements ItemInteraction{
    public Item(char symbol, Color color, Tile tile, String name) {
        super(symbol, color, tile);
        this.setName(name);
    }

    @Override
    public boolean consume(Actor target) {
        if (this.name.equals("Health Potion")) {
            target.setMaxHp(target.getMaxHp() + 10);
            target.setCurrentHp(target.getMaxHp());
            target.removeInventoryItem(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean drop(Actor actor) {
        if(actor.getTile().getItem() == null) {
            actor.getTile().setItem(this);
            this.setTile(actor.getTile());
            actor.removeInventoryItem(this);
            return true;
        }
        return false;
    }
}
