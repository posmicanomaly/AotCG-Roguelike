package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Level;
import posmicanomaly.AotCG.Component.Tile;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/31/2015.
 */
public abstract class FieldOfVision {
    /**
     * Simple returns the surrounding tiles nearest to the y, x, location on level
     *
     * @param y
     * @param x
     * @param level
     * @return
     */
    public static ArrayList<Tile> calculateSimpleFOVVisibleTiles(int y, int x, Level level) {
        ArrayList<Tile> visibleTiles = level.getNearbyTiles(y, x);
        visibleTiles.add(level.getTile(y, x));

        return visibleTiles;
    }

    /**
     * Uses raycasting to get visible tiles from y,x location on level
     * <p/>
     * Based on Eligloscode on roguebasin
     *
     * @param y
     * @param x
     * @param level
     * @return
     */
    public static ArrayList<Tile> calculateRayCastingFOVVisibleTiles(int y, int x, Level level, int radius) {
        ArrayList<Tile> visibleTiles = new ArrayList<Tile>();
        visibleTiles.add(level.getTile(y, x));
        int VIEW_RADIUS = radius;
        for (int i = 0; i < 360; i++) {
            double xd = Math.cos((double) i * 0.01745f);
            double yd = Math.sin((double) i * 0.01745f);

            double oy = (double) y + 0.5f;
            double ox = (double) x + 0.5f;
            for (int r = 0; r < VIEW_RADIUS; r++) {
                Tile t = level.getTile((int) oy, (int) ox);
                visibleTiles.add(t);
                if (!t.isTransparent()) {
                    break;
                }
                ox += xd;
                oy += yd;
            }
        }

        return visibleTiles;
    }
}
