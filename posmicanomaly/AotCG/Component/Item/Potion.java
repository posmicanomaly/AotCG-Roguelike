package posmicanomaly.AotCG.Component.Item;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.Map.Tile;

import java.awt.*;

/**
 * Created by jessepospisil on 3/10/16.
 */
public class Potion extends Item {
    int hp;
    public Potion(char symbol, Color color, Tile tile, String name, int hp) {
        super(symbol, color, tile, name);
        this.hp = hp;
    }

    @Override
    public boolean consume(Actor target) {
        target.setMaxHp(target.getMaxHp() + hp);
        target.setCurrentHp(target.getMaxHp());
        target.removeInventoryItem(this);
        return true;
    }
}
