import java.awt.*;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class Tile {
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

    public Tile(int y, int x, char symbol, Color color) {
        this.y = y;
        this.x = x;
        this.symbol = symbol;
        this.color = color;
    }
}
