package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Tile {
    private Type type;
    private boolean transparent;
    private boolean explored;
    private boolean visible;
    private int y, x;
    private char symbol;
    private Color color;
    private Color backgroundColor;
    private boolean blocked;

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public boolean hasActor() {
        return this.actor != null;
    }

    private Actor actor;
    private Item item;

    public Tile(int y, int x) {
        this.y = y;
        this.x = x;
        this.symbol = '?';
        this.color = Color.GREEN;
        this.backgroundColor = Color.black;
        this.blocked = false;
        this.type = Type.DEFAULT;

        // All tiles are assumed to be transparent unless explicitly set as not
        this.transparent = true;
        this.explored = false;
        this.visible = false;

        this.actor = null;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean b) {
        this.transparent = b;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean b) {
        this.explored = b;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean b) {
        this.visible = b;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean hasItem() {
        return item != null;
    }

    public static enum Type {
        DEFAULT, WALL, FLOOR, WATER, PATH, BUILD_FLOOD, DOOR, WALL_SECRET, CAVE_GRASS
    }
//    public Tile(int y, int x, char symbol, Color color) {
//        this.y = y;
//        this.x = x;
//        this.symbol = symbol;
//        this.color = color;
//        blocked = false;
//        this.type = Type.DEFAULT;
//    }
}
