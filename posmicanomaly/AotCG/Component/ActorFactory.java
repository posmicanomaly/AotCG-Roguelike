package posmicanomaly.AotCG.Component;

import posmicanomaly.libjsrte.Util.ColorTools;

import java.awt.*;

/**
 * Created by Jesse Pospisil on 9/29/2015.
 */
public class ActorFactory {
    public enum TYPE {
        PLAYER, RAT, GIANT
    }
    public static Actor createActor(TYPE type, Tile t) {
        String actorName;
        int level;
        int mod;
        int speed;
        char glyph;
        Color color;

        switch(type) {
            case PLAYER:
                level = 1;
                mod = 10;
                speed = 1;
                glyph = '@';
                actorName = "you";
                color = ColorTools.getRandomColor();
                break;
            case RAT:
                level = 1;
                mod = 2;
                speed = 1;
                glyph = 'r';
                actorName = "rat";
                color = Color.yellow;
                break;
            case GIANT:
                level = 7;
                mod = 4;
                speed = 3;
                glyph = 'G';
                actorName = "giant";
                color = Color.pink;
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
