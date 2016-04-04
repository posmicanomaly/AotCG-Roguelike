package posmicanomaly.AotCG.Game.Render.Animation;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.Map.Tile;
import posmicanomaly.AotCG.Factory.LevelFactory;
import posmicanomaly.AotCG.Game.Render.Render;
import posmicanomaly.AotCG.Game.Roguelike;

import java.awt.*;

/**
 * Created by jessepospisil on 4/4/16.
 */
public class Animation {
    private Render render;
    private Roguelike roguelike;
    public Animation(Render render) {
        this.render = render;
        this.roguelike = render.getRoguelike();
    }

    public void doFlashActor(Actor actor, int duration) {
        long time = roguelike.getWindow().getLastKeyEvent().getWhen();
        Color actorColor = actor.getColor();
        for(int i = 0; i < duration; i++){
            Color newColor = new Color(255 - i * 2, 255 - i * 2, 255 - i * 2);
            actor.setColor(newColor);

            if(roguelike.getWindow().getLastKeyEvent().getWhen() != time) {
                break;
            }
            doRenderWithSleep();
        }
        actor.setColor(actorColor);
        doRenderWithSleep();
    }

    public void doFlashPlayer(int duration) {
       doFlashActor(roguelike.getPlayer(), duration);
    }

    public void doFlashVisibleTiles(int duration) {
        long time = roguelike.getWindow().getLastKeyEvent().getWhen();
        for(int i = 0; i < duration; i++) {
            for (Tile t : roguelike.getPlayer().getVisibleTiles()) {
                if (roguelike.getWindow().getLastKeyEvent().getWhen() != time) {
                    break;
                }
                if(t.getType() != Tile.Type.TOWN && t.getType() != Tile.Type.CAVE_OPENING)
                    doStepFlash(t, i);
            }
            doRenderWithSleep();
        }
        resetPlayerVisibleTiles();
        doRenderWithSleep();
    }

    public void doRenderWithSleep() {
        render.renderSingleFrame(Render.Reason.ANIMATION);
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doJumble(int duration) {
        long time = roguelike.getWindow().getLastKeyEvent().getWhen();
        for(int i = 0; i < duration; i++) {
            for (Tile t : roguelike.getPlayer().getVisibleTiles()) {
                if (roguelike.getWindow().getLastKeyEvent().getWhen() != time) {
                    break;
                }
                doStepJumble(t);
            }
            doRenderWithSleep();
        }
        resetPlayerVisibleTiles();
        doRenderWithSleep();
    }

    private void doStepJumble(Tile t) {
        t.setSymbol((char) Roguelike.rng.nextInt(255));
    }

    private void doStepFlash(Tile t, int iteration) {
        int r = t.getBackgroundColor().getRed() + iteration * 4;
        int g = t.getBackgroundColor().getGreen() + iteration * 2;
        int b = t.getBackgroundColor().getBlue() + iteration * 2;
        if(r > 255) r = 255;
        if(g > 255) g = 255;
        if(b > 255) b = 255;
        t.setBackgroundColor(new Color(r, g, b));
    }

    private void resetPlayerVisibleTiles() {
        for(Tile t : roguelike.getPlayer().getVisibleTiles()) {
            LevelFactory.initTile(t);
        }
    }


}
