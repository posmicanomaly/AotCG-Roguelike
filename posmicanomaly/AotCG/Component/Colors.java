package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by jessepospisil on 9/6/15.
 */
public abstract class Colors {

    public static final Color FOREST = new Color(0.13333334f, 0.54509807f, 0.13333334f);
    public static final Color MOUNTAIN = new Color(0.6627451f, 0.6627451f, 0.6627451f);
    public static final Color SAND = new Color(1.0f, 0.8901961f, 0.77254903f);
    public static final Color CAVE_OPENING = new Color(0.84705883f, 0.84313726f, 0.84313726f);
    public static final Color JUNGLE = new Color(0.13725491f, 0.654902f, 0.13725491f);
    public static Color WALL = new Color(0.0f, 0.0f, 0.0f);
    public static Color WALL_BG = new Color(0.78039217f, 0.6392157f, 0.54509807f);
    public static Color WATER = new Color(0.09803922f, 0.09803922f, 0.8980392f);
    public static Color WATER_BG = new Color(0.0f, 0.0627451f, 0.90588236f);
    public static Color FLOOR = new Color(0.78039217f, 0.78039217f, 0.78039217f);
    public static Color FLOOR_BG = new Color(0.12156863f, 0.12156863f, 0.21176471f);
    public static Color CAVE_GRASS = new Color(0, 196, 0);
    public static Color WORLD_GRASS = new Color(119, 196, 1);
    public static Color PLAINS = new Color(183, 199, 0);
    public static Color BRUSH = new Color(103, 173, 54);
    public static Color CAVE_GRASS_BG = FLOOR_BG;
    public static Color DOOR = new Color(188, 134, 125);
    public static Color TOWN = new Color(205, 194, 116);
    public static Color DOOR_BG = new Color(67, 1, 0);

    public static Color HILL = new Color(119, 59, 19);

    public static Color HEALTH_REMAINING = new Color(0, 142, 0);
    public static Color HEALTH_DEFICIT = new Color(102, 0, 0);
    public static Color EXPERIENCE = new Color(174, 91, 0);

    public static Color SHROUD = new Color(10, 35, 67);

    public static Color blueify(Color color) {
        int r = (int) (color.getRed() * 0.25f);
        int g = (int) (color.getGreen() * 0.25f);
        int b = (int) (color.getBlue());

        return new Color(r, g, b);
    }

    public static Color shroud(Color color) {
        return blueify(color).darker();
    }
}
