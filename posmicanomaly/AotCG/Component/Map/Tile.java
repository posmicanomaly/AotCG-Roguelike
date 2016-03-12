package posmicanomaly.AotCG.Component.Map;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.Item.Item;

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
    private String typeString;

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

    public String getTypeString() {
        String s;
        switch (type) {
            // Interior
            case DEFAULT: s = "DEFAULT"; break;
            case WALL: s = "Wall"; break;
            case FLOOR: s = "Floor"; break;
            case WATER: s = "Water"; break;
            case PATH: s = "Path"; break;
            case BUILD_FLOOD: s = "BUILD_FLOOR"; break;
            case DOOR: s = "Door"; break;
            case WALL_SECRET: s = "Secret Wall"; break;
            case STAIRS_UP: s = "Stairs Up"; break;
            case STAIRS_DOWN: s = "Stairs Down"; break;
            case CAVE_GRASS: s = "Cave Grass"; break;
            case LOW_GRASS: s = "Low Grass"; break;
                // Exterior
            case WORLD_GRASS: s = "World Grass"; break;
            case FOREST: s = "Forest"; break;
            case MOUNTAIN: s = "Mountain"; break;
            case SAND: s = "Sand"; break;
            case CAVE_OPENING: s = "Cave Opening"; break;
            case JUNGLE: s = "Jungle"; break;
            case PLAINS: s = "Plains"; break;
            case BRUSH: s = "Brush"; break;
            case HILL: s = "Hill"; break;
            case TOWN: s = "Town"; break;
            default: s = "error"; break;
        }
        return s;
    }

    public static enum Type {
        // Interior
        DEFAULT, WALL, FLOOR, WATER, PATH, BUILD_FLOOD, DOOR, WALL_SECRET, STAIRS_UP, STAIRS_DOWN, CAVE_GRASS, LOW_GRASS,
        // Exterior
        WORLD_GRASS, FOREST, MOUNTAIN, SAND, CAVE_OPENING, JUNGLE, PLAINS, BRUSH, HILL, TOWN
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
