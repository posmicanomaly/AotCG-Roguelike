package posmicanomaly.AotCG.Component.Item;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.Entity;
import posmicanomaly.AotCG.Component.Map.Tile;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 9/14/2015.
 */
public abstract class Item extends Entity implements ItemInteraction {
    public Item(char symbol, Color color, Tile tile, String name) {
        super(symbol, color, tile);
        this.setName(name);
    }

    @Override
    public boolean consume(Actor target) {
        System.out.println("can't consume");
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
