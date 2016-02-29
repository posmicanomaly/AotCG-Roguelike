package posmicanomaly.AotCG.Component;

/**
 * Created by jessepospisil on 2/29/16.
 */
public interface ItemInteraction {
    boolean consume(Actor target);
    boolean drop(Actor actor);
}
