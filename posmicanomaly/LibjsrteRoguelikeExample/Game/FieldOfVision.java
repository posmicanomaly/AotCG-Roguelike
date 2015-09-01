package posmicanomaly.LibjsrteRoguelikeExample.Game;

import posmicanomaly.LibjsrteRoguelikeExample.Component.Level;
import posmicanomaly.LibjsrteRoguelikeExample.Component.Tile;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/31/2015.
 */
public abstract class FieldOfVision {
    public static ArrayList<Tile> calculateSimpleFOVVisibleTiles(int y, int x, Level level) {
        ArrayList<Tile> visibleTiles = level.getNearbyTiles(y, x);
        visibleTiles.add(level.getTile(y, x));

        return visibleTiles;
    }
}
