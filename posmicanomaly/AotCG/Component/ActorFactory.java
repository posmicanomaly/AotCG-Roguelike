package posmicanomaly.AotCG.Component;

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
                color = Color.PINK;
                break;
            case RAT:
                level = 1;
                mod = 4;
                speed = 1.2;
                glyph = 'r';
                actorName = "rat";
                color = Color.yellow;
                break;
            case BAT:
                level = 1;
                mod = 4;
                speed = 1.4;
                glyph = 'b';
                actorName = "bat";
                color = Color.green;
                break;
            case GIANT:
                level = 3;
                mod = 4;
                speed = 1.0;
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
