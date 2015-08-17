import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Actor {
    private char symbol;
    private Color color;

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    private Tile tile;

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

    public Actor(char symbol, Color color, Tile tile) {
        this.symbol = symbol;
        this.color = color;
        this.tile = tile;
    }

}
