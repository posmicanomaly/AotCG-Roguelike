package posmicanomaly.AotCG.Component;

import java.awt.*;

import posmicanomaly.libjsrte.Console.Colors.Db32;

/**
 * Created by jessepospisil on 9/6/15.
 */
public abstract class GameColors {
    public static Color BACKGROUND = Db32.BLACK;
    public static Color GREEN_PATH = Db32.GREEN;
    public static Color MOUSEBG = Db32.RED;
    public static Color MOUSEBG2 = Db32.GRAY;
    public static Color RED_PATH = Db32.RED;
    //public static final Color FOREST = new Color(0.13333334f, 0.54509807f, 0.13333334f);
    public static Color FOREST = Db32.GREEN;
    //public static final Color MOUNTAIN = new Color(0.6627451f, 0.6627451f, 0.6627451f);
    public static Color MOUNTAIN = Db32.SLATE;

    //public static final Color SAND = new Color(1.0f, 0.8901961f, 0.77254903f);
    public static Color SAND = Db32.LIGHTTAN;
    //public static final Color CAVE_OPENING = new Color(0.84705883f, 0.84313726f, 0.84313726f);
    public static Color CAVE_OPENING = Db32.LIGHTTAN;
    //public static final Color JUNGLE = new Color(0.13725491f, 0.654902f, 0.13725491f);
    public static Color JUNGLE = Db32.GREEN;
    //public static Color WALL = new Color(0.0f, 0.0f, 0.0f);
    public static Color WALL_DARK = Db32.BLACK;
    public static Color WALL = Db32.GRAY;
    //public static Color WALL_BG = new Color(0.78039217f, 0.6392157f, 0.54509807f);
    public static Color WALL_BG_DARK = Db32.GRAY;
    public static Color WALL_BG = Db32.LIGHTTAN;
    //public static Color WATER = new Color(0.09803922f, 0.09803922f, 0.8980392f);
    public static Color WATER = Db32.BLUE;
    //public static Color WATER_BG = new Color(0.0f, 0.0627451f, 0.90588236f);
    public static Color WATER_BG = Db32.DARKBLUE;
    //public static Color FLOOR = new Color(0.78039217f, 0.78039217f, 0.78039217f);
    public static Color FLOOR = Db32.WHITE;
    //public static Color FLOOR_BG = new Color(0.12156863f, 0.12156863f, 0.21176471f);
    public static Color FLOOR_BG = BACKGROUND;
    //public static Color CAVE_GRASS = new Color(0, 196, 0);
    public static Color CAVE_GRASS = Db32.GREEN;
    public static Color CAVE_GRASS_BG = Db32.DARKGREEN;
    public static Color LOW_GRASS = Db32.GREEN;
    public static Color LOW_GRASS_BG = BACKGROUND;
    //public static Color WORLD_GRASS = new Color(119, 196, 1);
    public static Color WORLD_GRASS = Db32.GREEN;
    //public static Color PLAINS = new Color(183, 199, 0);
    public static Color PLAINS = Db32.LIGHTGREEN;
    //public static Color BRUSH = new Color(103, 173, 54);
    public static Color BRUSH = Db32.GREEN;
    //public static Color CAVE_GRASS_BG = FLOOR_BG;
    //public static Color DOOR = new Color(188, 134, 125);
    public static Color DOOR = Db32.YELLOW;
    //public static Color TOWN = new Color(205, 194, 116);
    public static Color TOWN = Db32.YELLOW;
    //public static Color DOOR_BG = new Color(67, 1, 0);
    public static Color DOOR_BG = Db32.BROWN;

    //public static Color HILL = new Color(119, 59, 19);
    public static Color HILL = Db32.LIGHTBROWN;

    //public static Color HEALTH_REMAINING = new Color(0, 142, 0);
    public static Color HEALTH_REMAINING = Db32.GREEN;
    //public static Color HEALTH_DEFICIT = new Color(102, 0, 0);
    public static Color HEALTH_DEFICIT = Db32.RED;
    //public static Color EXPERIENCE = new Color(174, 91, 0);
    public static Color EXPERIENCE = Db32.ORANGE;

    public static Color SHROUD = new Color(10, 35, 67);

    public static Color PLAYER = Db32.MAGENTA;
    public static Color RAT = Db32.LIGHTRED;
    public static Color BAT = Db32.LIGHTRED;
    public static Color GIANT = Db32.ORANGE;


    public static Color INVENTORY = Db32.YELLOW;
    public static Color NOISE = Db32.GRAY;
    public static Color COMBAT = Db32.CYAN;
    public static Color ITEM = Db32.YELLOW;
    public static Color ERROR = Db32.LIGHTRED;
    public static Color blueify(Color color) {
        int r = (int) (color.getRed() * 0.25f);
        int g = (int) (color.getGreen() * 0.25f);
        int b = (int) (color.getBlue());

        //return new Color(r, g, b);
        return color;
    }

    public static Color shroud(Color color) {
        return blueify(color).darker();
    }

    // Luminance
    public static Color grayScale(Color color) {
        int alpha = color.getAlpha();
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);

        return new Color(red, red, red, alpha);
    }
}
