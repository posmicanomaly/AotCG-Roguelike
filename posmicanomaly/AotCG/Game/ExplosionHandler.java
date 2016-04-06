package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Map.Tile;
import posmicanomaly.AotCG.Factory.LevelFactory;
import posmicanomaly.AotCG.Game.Render.Animation.Animation;

import java.util.ArrayList;

/**
 * Created by jessepospisil on 4/6/16.
 */
public class ExplosionHandler {
    private Roguelike roguelike;
    public ExplosionHandler(Roguelike roguelike) {
        this.roguelike = roguelike;
    }
    public void explodeTile(Tile t) {
        roguelike.getRender().getAnimation().doExplodeTile(t, Animation.DURATION_MED);
        resolveTile(t);
    }
    private void explodeTiles(ArrayList<Tile> tiles) {
        roguelike.getRender().getAnimation().doExplodeTiles(tiles, Animation.DURATION_MED);
        for(Tile t : tiles) {
            resolveTile(t);
        }
    }

    private void resolveTile(Tile t) {
        switch(t.getType()) {
            case WALL:
                t.setType(Tile.Type.FLOOR);
                break;
            case CAVE_GRASS:
                t.setType(Tile.Type.LOW_GRASS);
                break;
            case LOW_GRASS:
                t.setType(Tile.Type.FLOOR);
                break;
        }
        LevelFactory.initTile(t);
        if(t.hasActor() && !t.getActor().equals(roguelike.getPlayer())) {
            t.getActor().setCurrentHp(0);
            t.getActor().setAlive(false);
        }
    }

    public void explodeTiles(Tile source, int radius) {
        ArrayList<Tile> tiles = roguelike.getMap().getCurrentLevel().getNearbyTiles(source.getY(), source.getX(), radius);
        explodeTiles(tiles);
    }
}
