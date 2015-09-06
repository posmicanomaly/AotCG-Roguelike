package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Actor {
    private char symbol;
    private Color color;
    private Tile tile;
    private int maxHp;
    private int currentHp;
    private String name;
    private String corpseName;
    private boolean alive;

    public Actor(char symbol, Color color, Tile tile) {
        this.symbol = symbol;
        this.color = color;
        this.tile = tile;

        setMaxHp(15);
        setCurrentHp(getMaxHp());
        setAlive(true);

        setName("Default Actor");
        setCorpseName(getName() + "'s corpse");
    }

    public boolean isAlive() {
        return alive;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorpseName() {
        return corpseName;
    }

    public void setCorpseName(String corpseName) {
        this.corpseName = corpseName;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

}
