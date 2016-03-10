package posmicanomaly.AotCG.Component.Item;

import posmicanomaly.AotCG.Component.Actor.Actor;

/**
 * Created by jessepospisil on 2/29/16.
 */
public interface ItemInteraction {
    boolean consume(Actor target);
    boolean drop(Actor actor);
}
