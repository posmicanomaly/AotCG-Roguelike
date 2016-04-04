package posmicanomaly.AotCG.Factory;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.GameColors;
import posmicanomaly.AotCG.Component.Map.Tile;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 9/29/2015.
 */
public class ActorFactory {
    public enum TYPE {
        PLAYER, RAT, BAT, GIANT
    }
    public static Actor createActor(TYPE type, Tile t) {
        String actorName;
        int level;
        int mod;
        double speed;
        char glyph;
        Color color;

        switch(type) {
            case PLAYER:
                level = 1;
                mod = 30;
                speed = 1.1;
                glyph = '@';
                actorName = "you";
                color = GameColors.PLAYER;
                break;
            case RAT:
                level = 1;
                mod = 4;
                speed = 1.2;
                glyph = 'r';
                actorName = "rat";
                color = GameColors.RAT;
                break;
            case BAT:
                level = 1;
                mod = 4;
                speed = 1.4;
                glyph = 'b';
                actorName = "bat";
                color = GameColors.BAT;
                break;
            case GIANT:
                level = 3;
                mod = 4;
                speed = 1.0;
                glyph = 'G';
                actorName = "giant";
                color = GameColors.GIANT;
                break;
            default:
                return null;
        }

        Actor actor = new Actor(glyph, color, t);
        actor.setLevel(level);
        actor.setMod(mod);
        actor.setSpeed(speed);
        actor.setName(actorName);
        actor.initStats();

        return actor;
    }
}
