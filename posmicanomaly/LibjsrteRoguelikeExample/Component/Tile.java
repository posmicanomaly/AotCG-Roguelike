package posmicanomaly.LibjsrteRoguelikeExample.Component;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Tile {
    public static enum Type {
        DEFAULT, WALL, FLOOR, WATER, PATH, BUILD_FLOOD
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private Type type;

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    private boolean transparent;

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    private boolean explored;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private boolean visible;
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

    private int y, x;
    private char symbol;
    private Color color;

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    private boolean blocked;

    public Tile(int y, int x) {
        this.y = y;
        this.x = x;
        this.symbol = '?';
        this.color = Color.GREEN;
        this.blocked = false;
        this.type = Type.DEFAULT;

        // All tiles are assumed to be transparent unless explicitly set as not
        this.transparent = true;
        this.explored = false;
        this.visible = false;
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
