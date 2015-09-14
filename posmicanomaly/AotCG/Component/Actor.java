package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Actor extends Entity {
    private int maxHp;
    private int currentHp;
    private String corpseName;
    private boolean alive;

    public Actor(char symbol, Color color, Tile tile) {
        super(symbol, color, tile);

        setMaxHp(15);
        setCurrentHp(getMaxHp());
        setAlive(true);

        setName("Default Actor");
        setCorpseName(getName() + "'s corpse");
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setCorpseName(this.getName() +"'s corpse");
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public String getCorpseName() {
        return corpseName;
    }

    public void setCorpseName(String corpseName) {
        this.corpseName = corpseName;
    }

}
