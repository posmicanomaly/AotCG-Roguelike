package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.*;
import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.Item.Corpse;
import posmicanomaly.AotCG.Component.Item.Item;
import posmicanomaly.AotCG.Component.Item.Potion;
import posmicanomaly.AotCG.Component.Map.Map;
import posmicanomaly.AotCG.Component.Map.Tile;
import posmicanomaly.AotCG.Factory.LevelFactory;
import posmicanomaly.AotCG.Game.Input.Input;
import posmicanomaly.AotCG.Game.Gui.Component.MessageConsole;
import posmicanomaly.AotCG.Game.Render.Animation.Animation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jesse Pospisil on 12/8/2015.
 */
public class Process {
    private final Roguelike roguelike;

    public Process(Roguelike roguelike) {
        this.roguelike = roguelike;
    }

    protected boolean processNpcActor(Actor npcActor) {
        Map map = roguelike.getMap();
        Actor player = roguelike.getPlayer();

        ArrayList<Actor> npcActors = map.getCurrentLevel().getActors();
        npcActors.remove(player);

        Actor a = npcActor;

        // Check if player is dead
        if(!player.isAlive()) {
            return false;
        }
        if(!a.canTakeTurn()) {
            return false;
        }
        // No zombies yet
        if(!a.isAlive()) {
            return false;
        }
        // Player in view
        // (check both views, so we minimize "walking shadows")
        // .
        // .
        if (actorCanSeeActor(a, player) && actorCanSeeActor(player, a)) {
            // get a new AStar path to the player
            a.setCurrentPath(map.getCurrentLevel().getAstar().getShortestPath(a.getTile(), player.getTile()));
            if (a.getCurrentPath() != null) {
                switch(moveActor(a, a.getCurrentPath().get(0))) {
                    case MOVED:
                        a.getCurrentPath().remove(0);
                        if(a.getCurrentPath().size() == 0) {
                            a.setCurrentPath(null);
                        }
                        break;
                }
            }
        }
        // Player's not in npc's view, but has a current path to follow
        // .
        // .
        else if(a.getCurrentPath() != null) {

            switch(moveActor(a, a.getCurrentPath().get(0))) {
                case MOVED:
                    a.getCurrentPath().remove(0);
                    if(a.getCurrentPath().size() == 0) {
                        a.setCurrentPath(null);
                    }
                    break;
            }
        }
        // Not in player view
        else {
            moveActor(a, Input.Direction.getRandomDirection());
        }
        if(a.isAlive()) {
            calculateVision(a);
        }

        return true;
    }
    protected void processNpcActors() {
        Map map = roguelike.getMap();
        Actor player = roguelike.getPlayer();

        ArrayList<Actor> npcActors = map.getCurrentLevel().getActors();
        npcActors.remove(player);

        for (Actor a : npcActors) {
           processNpcActor(a);
        }
    }

    public enum MoveResult {DID_NOT_MOVE, MOVED, COMBAT, ERROR, BUMPED}
    protected MoveResult moveActor(Actor actor, Tile t) {
        MessageConsole messageConsole = roguelike.getGui().getMessageConsole();
        Actor player = roguelike.getPlayer();
        Random rng = Roguelike.rng;
        Map map = roguelike.getMap();


        Tile currentTile = actor.getTile();
        Tile desiredTile = t;
        // If the tile is null, it is likely out of range
        if (desiredTile == null) {
            messageConsole.addMessage("Tile is null", GameColors.ERROR);
            return MoveResult.ERROR;
        }

        // If the tile is blocked(terrain), we can't go there, likely a wall at this point
        else if (desiredTile.isBlocked()) {
            if (actor.equals(player))
                messageConsole.addMessage("You bumped into a wall", GameColors.NOISE);
            actor.depleteEnergy((int) (1000 / actor.getSpeed()));
            return MoveResult.BUMPED;
        }

        // If the tile already has an actor, and the actor is alive, we need to do some combat
        else if (desiredTile.hasActor() && desiredTile.getActor().isAlive()) {
            processCombat(actor, desiredTile.getActor());

            Actor winner = null;
            Actor loser = null;
            boolean bothDied = false;

            if(actor.isAlive() && desiredTile.getActor().isAlive()) {
                //messageConsole.addMessage("moveActor() :: both actors still alive");
                return MoveResult.COMBAT;
            }

            if(actor.isAlive() && !desiredTile.getActor().isAlive()) {
                winner = actor;
                loser = desiredTile.getActor();
                //messageConsole.addMessage("moveActor() :: W: " + winner.getName() + " L: " + loser.getName());
            }
            else if(!actor.isAlive() && desiredTile.getActor().isAlive()) {
                winner = desiredTile.getActor();
                loser = actor;
                // messageConsole.addMessage("moveActor() :: W: " + winner.getName() + " L: " + loser.getName());
            }
            else if(!actor.isAlive() && !desiredTile.getActor().isAlive()) {
                bothDied = true;
                //messageConsole.addMessage("moveActor() :: Both died");
            }

            if(bothDied) {
                //messageConsole.addMessage("Both died");
                return MoveResult.COMBAT;
            }

            // Loser gets a corpse
            Tile loserTile = loser.getTile();
            loser.setAlive(false);
            if(!loser.equals(player)) {
                loser.setTile(null);
            }
            Item corpse = new Corpse('%', GameColors.ITEM, loserTile, loser.getCorpseName());
            loserTile.setItem(corpse);

            // random item?
            if(rng.nextInt(100) - 10 > 0) {
                Item randomitem = new Potion(')', GameColors.ITEM, loserTile, "Health Potion", 10);
                loserTile.setItem(randomitem);
            }

            if(!loser.equals(player)) {
                loserTile.setActor(null);
            }

            // Winner gets reward, if player
            if(winner.equals(player)) {
                messageConsole.addMessage(winner.getName() + " killed " + loser.getName(), GameColors.EXPERIENCE);
                return MoveResult.COMBAT;
            }

        }
        // The tile is valid, not blocked, and has no "living" actor
        // We can move the actor there
        else {
            // First check if we're changing levels
            /**
             * About level changing:
             *
             * The "world map" is modal, and each move updates roguelike.playerMapY and X respectively.
             *
             * The "levels" are stored in map.level3dArray[][][].
             * Moving "deeper" increases the depth by 1, and "higher" decreases it.
             * Levels are vertically stacked, and at this time do not horizontally connect.
             * We utilize the playerMapY and X coordinates to determine where the next or previous level is.
             *
             * todo:
             * Levels can be horizontally connected as long as the playerMapY and X get updated when that occurs.
             * Abstract much of this to the map to figure out.
             * There are root coordinate variables in the levels, work to utilize those coordinates in the future if needed.
             */

            Tile previousTile = currentTile;

            // set actor's tile
            actor.setTile(desiredTile);
            // set tile's actor
            desiredTile.setActor(actor);

            // Secret wall?
            // .
            // .

            if (desiredTile.getType() == Tile.Type.WALL_SECRET) {
                // Set type to DOOR
                desiredTile.setType(Tile.Type.DOOR);
                LevelFactory.initTile(desiredTile);
            }

            // remove actor from old tile

            previousTile.setActor(null);

            // More hacks for map x,y
            if(actor.equals(player) && map.getCurrentDepth() == 0) {
                roguelike.setPlayerMapY(actor.getTile().getY());
                roguelike.setPlayerMapX(actor.getTile().getX());
            }
            // Return true, a move was made
            actor.depleteEnergy((int) (1000 / actor.getSpeed()));
            return MoveResult.MOVED;

        }
        // Return false, no move was made
        return MoveResult.DID_NOT_MOVE;
    }

    protected MoveResult moveActor(Actor actor, Input.Direction d) {
        MessageConsole messageConsole = roguelike.getGui().getMessageConsole();
        Map map = roguelike.getMap();

        if(d == null) {
            messageConsole.addMessage("moveActor(" + actor.hashCode() + ", " + d + ") error");
            return MoveResult.ERROR;
        }
        // Obtain the current tile the actor is on
        Tile currentTile = actor.getTile();

        // desiredTile is the tile we will try to move the actor to
        Tile desiredTile;

        // by default, the move will be true
        // we will inhibit the move by setting this to false
        boolean move = true;

        // Get coordinates of the currentTile
        int y = currentTile.getY();
        int x = currentTile.getX();

        // Determine coordinates of desired tile based on direction input
        switch (d) {
            case UP:    y--;        break;
            case DOWN:  y++;        break;
            case LEFT:  x--;        break;
            case RIGHT: x++;        break;
            case NW:    x--; y--;   break;
            case NE:    x++; y--;   break;
            case SW:    x--; y++;   break;
            case SE:    x++; y++;   break;
            default:
                move = false;
                break;
        }

        // If the input was good, move will still be true
        if (move) {
            // set desiredTile to the tile located at the y, x coordinates
            desiredTile = map.getCurrentLevel().getTile(y, x);
            return moveActor(actor, desiredTile);
        }
        // Move was false
        return MoveResult.DID_NOT_MOVE;
    }

    private void processCombat(Actor actor1, Actor actor2) {
        MessageConsole messageConsole = roguelike.getGui().getMessageConsole();
        Actor player = roguelike.getPlayer();

        // Determine who goes first

        Actor firstAttacker = null;
        Actor secondAttacker = null;

        if (actor1.getSpeed() > actor2.getSpeed()) {
            firstAttacker = actor1;
            secondAttacker = actor2;
        } else if (actor1.getSpeed() < actor2.getSpeed()) {
            firstAttacker = actor2;
            secondAttacker = actor1;
        } else if (actor1.getSpeed() == actor2.getSpeed()) {
            firstAttacker = actor1;
            secondAttacker = actor2;
        }

        // get power
        int firstAttackerDamage = firstAttacker.getPower();
        int secondAttackerDamage = secondAttacker.getPower();
        String combatMessage = "";


        // firstAttacker attacks, if they can
        if(firstAttacker.canTakeTurn()) {
            combatMessage += firstAttacker.getName() + " hit " + secondAttacker.getName() + " for " + firstAttackerDamage;
            secondAttacker.setCurrentHp(secondAttacker.getCurrentHp() - firstAttackerDamage);
            firstAttacker.depleteEnergy((int) (1000 / firstAttacker.getSpeed()));
            roguelike.getRender().getAnimation().doFlashActor(secondAttacker, Animation.DURATION_SHORT);
        }

        // Check if secondAttacker was killed
        if (secondAttacker.getCurrentHp() <= 0) {
            ExplosionHandler explosionHandler = new ExplosionHandler(roguelike);
            explosionHandler.explodeTiles(secondAttacker.getTile(), 10);
            //roguelike.getRender().getAnimation().explodeTilesTest(secondAttacker, Animation.DURATION_MED);
            secondAttacker.setAlive(false);
        }

        // If secondAttacker is still alive, and can attack
        if(secondAttacker.canTakeTurn()) {
            if (secondAttacker.isAlive()) {
                // secondAttacker attacks
                combatMessage += ", " + secondAttacker.getName() + " hit " + firstAttacker.getName() + " for " + secondAttackerDamage;
                firstAttacker.setCurrentHp(firstAttacker.getCurrentHp() - secondAttackerDamage);
                secondAttacker.depleteEnergy((int) (1000 / secondAttacker.getSpeed()));
                roguelike.getRender().getAnimation().doFlashActor(firstAttacker, Animation.DURATION_SHORT);

                // Check if firstAttacker was killed
                if (firstAttacker.getCurrentHp() <= 0) {
                    ExplosionHandler explosionHandler = new ExplosionHandler(roguelike);
                    explosionHandler.explodeTiles(firstAttacker.getTile(), 10);
                    //roguelike.getRender().getAnimation().explodeTilesTest(firstAttacker, Animation.DURATION_MED);
                    firstAttacker.setAlive(false);
                }
            }
        }

        // If the player is watching, or is involved, show the message
        // Otherwise, show some "noise" message
        if(!combatMessage.equals("")) {
            if (actor1.equals(player) || actor2.equals(player)) {
                messageConsole.addMessage(combatMessage, GameColors.COMBAT);
            } else if (actorInPlayerView(actor1) || actorInPlayerView(actor2)) {
                messageConsole.addMessage(combatMessage, GameColors.COMBAT);
            } else {
                messageConsole.addMessage("You hear noise", GameColors.NOISE);
            }
        }
    }

    protected boolean actorCanSeeActor(Actor source, Actor target) {
        for(Tile t : source.getVisibleTiles()) {
            if(t.hasActor()) {
                if(t.getActor().equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean actorInPlayerView(Actor actor) {
        Actor player = roguelike.getPlayer();

        return actorCanSeeActor(player, actor);
    }

    protected void calculateVision(Actor actor) {
        Actor player = roguelike.getPlayer();

        Map map = roguelike.getMap();

        if(actor.equals(player)) {
            // If on the world map, skip FOV, all visible
            // todo: visible by height, realism
            if(roguelike.map.getCurrentDepth() == 0) {
                map.getCurrentLevel().toggleAllTilesVisible(false);
            } else {
                map.getCurrentLevel().toggleAllTilesVisible(false);
            }
        }
        actor.clearVisibleTiles();
        int y = actor.getTile().getY();
        int x = actor.getTile().getX();

        ArrayList<Tile> fieldOfVisionTiles = null;

        int radius = map.getWidth();
        if(map.getCurrentDepth() == 0) {
            radius = 20;
        }
        fieldOfVisionTiles = FieldOfVision.calculateRayCastingFOVVisibleTiles(y, x, map
                    .getCurrentLevel(), radius);

        for (Tile t : fieldOfVisionTiles) {
            if(actor.equals(player)) {
                t.setVisible(true);
                t.setExplored(true);
            }
            actor.addVisibleTile(t);
        }
    }

    public boolean actuateTile(Actor actor) {
        Tile desiredTile = actor.getTile();
        if(desiredTile.hasItem()) {
            Item item = desiredTile.getItem();
            actor.addInventoryItem(item);
            desiredTile.setItem(null);
            roguelike.getGui().getMessageConsole().addMessage("Picked up " + item.getName(), GameColors.INVENTORY);
            return true;
        }
        boolean levelChanged = false;
        switch(desiredTile.getType()) {
            case CAVE_OPENING:
            case STAIRS_DOWN:
                if(!actor.equals(roguelike.getPlayer())) {
                    System.out.println(actor.getName() + " tried to actuate stairs_down, only players are allowed");
                    return false;
                }
                // Save the map coordinates if we're on the world map
                if (roguelike.map.getCurrentDepth() == 0) {
                    roguelike.setPlayerMapY(desiredTile.getY());
                    roguelike.setPlayerMapX(desiredTile.getX());
                }

                // Get the result of our attempt to change levels
                levelChanged = roguelike.map.goDeeper(roguelike.getPlayerMapY(), roguelike.getPlayerMapX());
                if (levelChanged) {
                    desiredTile = roguelike.map.getCurrentLevel().getEntryTile();
                }
                break;

            case STAIRS_UP:
                if(!actor.equals(roguelike.getPlayer())) {
                    System.out.println(actor.getName() + " tried to actuate stairs_up, only players are allowed");
                    return false;
                }
                // Change only if we're not on a path to another tile
                levelChanged = roguelike.map.goHigher(roguelike.getPlayerMapY(), roguelike.getPlayerMapX());
                if (levelChanged) {
                    // If we are going to the world map, then we place the player back at the last known world map coordinates.

                    if (roguelike.map.getCurrentDepth() == 0) {
                        desiredTile = roguelike.map.getCurrentLevel().getTile(roguelike.getPlayerMapY(), roguelike.getPlayerMapX());
                    }
                    // Otherwise, we place them on the down-stairs for the previous level, like they
                    // climbed them.
                    else {
                        desiredTile = roguelike.map.getCurrentLevel().getDownStairs();
                    }
                }
                break;

            default:
                break;
        }

        // If the level was changed, check and perform catch up if needed based on when we last left it.
        if (levelChanged) {
            System.out.println("turnsExit: " + roguelike.map.getCurrentLevel().getTurnExited());
            if(roguelike.map.getCurrentLevel().getTurnExited() != -1) {
                int catchup = 0;
                for (int i = roguelike.map.getCurrentLevel().getTurnExited(); i < Roguelike.turns; i++) {
                    processNpcActors();
                    catchup++;
                }
                System.out.println("level caught up, processNpcActors() " + catchup + " times");
            }
            Tile previousTile = actor.getTile();

            // set actor's tile
            actor.setTile(desiredTile);
            // set tile's actor
            desiredTile.setActor(actor);

            // Secret wall?
            // .
            // .

            if (desiredTile.getType() == Tile.Type.WALL_SECRET) {
                // Set type to DOOR
                desiredTile.setType(Tile.Type.DOOR);
                LevelFactory.initTile(desiredTile);
            }

            // remove actor from old tile

            previousTile.setActor(null);

            // More hacks for map x,y
            if(roguelike.map.getCurrentDepth() == 0) {
                roguelike.setPlayerMapY(actor.getTile().getY());
                roguelike.setPlayerMapX(actor.getTile().getX());
            }
            System.out.println(desiredTile.getTypeString());
            return true;
        }
        return false;
    }
}
