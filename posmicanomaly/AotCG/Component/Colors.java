package posmicanomaly.AotCG.Component;

import java.awt.*;

/**
 * Created by jessepospisil on 9/6/15.
 */
public abstract class Colors {

    public static final Color FOREST = new Color(0.11372549f, 0.32941177f, 0.10980392f);
    public static Color WALL = new Color(0.0f, 0.0f, 0.0f);
    public static Color WALL_BG = new Color(0.12941177f, 0.13725491f, 0.3137255f, 1.0f);
    public static Color WATER = new Color(0.043137256f, 0.043137256f, 0.26666668f);
    public static Color WATER_BG = new Color(0.0f, 0.05490196f, 0.49803922f);
    public static Color FLOOR = new Color(0.57254905f, 0.57254905f, 0.57254905f, 1.0f);
    public static Color FLOOR_BG = new Color(0.039215688f, 0.039215688f, 0.05882353f, 1.0f);
    public static Color CAVE_GRASS = new Color(0, 84, 0);
    public static Color CAVE_GRASS_BG = FLOOR_BG;
    public static Color DOOR = new Color(156, 116, 107);
    public static Color DOOR_BG = new Color(24, 1, 0);

    public static Color HEALTH_REMAINING = new Color(0, 142, 0);
    public static Color HEALTH_DEFICIT = new Color(102, 0, 0);
    public static Color EXPERIENCE = new Color(174, 91, 0);
}
