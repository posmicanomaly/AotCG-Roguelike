package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.*;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;

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

    protected void processNpcActors() {
        Map map = roguelike.getMap();
        Actor player = roguelike.getPlayer();

        ArrayList<Actor> npcActors = map.getCurrentLevel().getActors();
        npcActors.remove(player);

        for (Actor a : npcActors) {
            // Check if player is dead
            if(!player.isAlive()) {
                continue;
            }
            // No zombies yet
            if(!a.isAlive()) {
                continue;
            }
            // Player in view
            // (check both views, so we minimize "walking shadows")
            // .
            // .
            if (actorCanSeeActor(a, player) && actorCanSeeActor(player, a)) {
                // get a new AStar path to the player
                a.setCurrentPath(map.getCurrentLevel().getAstar().getShortestPath(a.getTile(), player.getTile()));
                if (a.getCurrentPath() != null) {
                    if(moveActor(a, a.getCurrentPath().get(0))) {
                        a.getCurrentPath().remove(0);
                    }
                }
            }
            // Player's not in npc's view, but has a current path to follow
            // .
            // .
            else if(a.getCurrentPath().size() > 0) {
                if(moveActor(a, a.getCurrentPath().get(0))) {
                    a.getCurrentPath().remove(0);
                }
            }
            // Not in player view
            else {
                moveActor(a, Input.Direction.getRandomDirection());
            }
            if(a.isAlive()) {
                calculateVision(a);
            }
        }
    }

    private boolean moveActor(Actor actor, Tile t) {
        MessageConsole messageConsole = roguelike.getMessageConsole();
        Actor player = roguelike.getPlayer();
        Random rng = Roguelike.rng;
        Map map = roguelike.getMap();


        Tile currentTile = actor.getTile();
        Tile desiredTile = t;
        // If the tile is null, it is likely out of range
        if (desiredTile == null) {
            messageConsole.addMessage("Tile is null");
            return false;
        }

        // If the tile is blocked(terrain), we can't go there, likely a wall at this point
        else if (desiredTile.isBlocked()) {
            if (actor.equals(player))
                messageConsole.addMessage("You bumped into a wall");
            return false;
        }

        // If the tile already has an actor, and the actor is alive, we need to do some combat
        else if (desiredTile.hasActor() && desiredTile.getActor().isAlive()) {
            processCombat(actor, desiredTile.getActor());

            Actor winner = null;
            Actor loser = null;
            boolean bothDied = false;

            if(actor.isAlive() && desiredTile.getActor().isAlive()) {
                //messageConsole.addMessage("moveActor() :: both actors still alive");
                return false;
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
                return false;
            }

            // Loser gets a corpse
            Tile loserTile = loser.getTile();
            loser.setAlive(false);
            if(!loser.equals(player)) {
                loser.setTile(null);
            }
            Item corpse = new Item ('%', Color.gray, loserTile);
            corpse.setName(loser.getCorpseName());
            loserTile.setItem(corpse);
            if(!loser.equals(player)) {
                loserTile.setActor(null);
            }

            // Winner gets reward, if player
            if(winner.equals(player)) {
                //messageConsole.addMessage("moveActor() :: W: " + winner.getName() + " (should be player)");
                // Award exp
                int baseExp = 10;
                int expVariance = 3;
                int randomExp = rng.nextInt((baseExp + expVariance) - (baseExp - expVariance) + 1) + (baseExp
                        - expVariance);

                winner.addExperience(randomExp);
                messageConsole.addMessage(winner.getName() + " killed " + loser.getName() + " (" + randomExp +
                        " exp)");


                if (loser.getName().equals("Giant")) {
                    messageConsole.addMessage("Main quest complete: Slay Giant (350 exp)");
                    winner.addExperience(350);
                    roguelike.giantSlain = true;
                }


                int prevLevel = winner.getLevel();
                winner.evaulateLevel();
                if (prevLevel < winner.getLevel()) {
                    messageConsole.addMessage(winner.getName() + " leveled up: " + winner.getLevel());
                }
                return false;
            }

        }
        // The tile is valid, not blocked, and has no "living" actor
        // We can move the actor there
        else {
                /*
                I don't like this
                 */
            if (actor.equals(player)) {
                if (desiredTile.getType() == Tile.Type.STAIRS_DOWN) {
                    boolean levelChanged = map.goDeeper();
                    if (levelChanged) {
                        System.out.println("turnsExit: " + map.getCurrentLevel().getTurnExited());
                        if(map.getCurrentLevel().getTurnExited() != -1) {
                            int catchup = 0;
                            for (int i = map.getCurrentLevel().getTurnExited(); i < Roguelike.turns; i++) {
                                processNpcActors();
                                catchup++;
                            }
                            System.out.println("level caught up, processNpcActors() " + catchup + " times");
                        }
                        desiredTile = map.getCurrentLevel().getUpStairs();
                    }
                } else if (desiredTile.getType() == Tile.Type.STAIRS_UP) {
                    boolean levelChanged = map.goHigher();
                    if (levelChanged) {
                        System.out.println("turnsExit: " + map.getCurrentLevel().getTurnExited());
                        if(map.getCurrentLevel().getTurnExited() != -1) {
                            int catchup = 0;
                            for (int i = map.getCurrentLevel().getTurnExited(); i < Roguelike.turns; i++) {
                                processNpcActors();
                                catchup++;
                            }
                            System.out.println("level caught up, processNpcActors() " + catchup + " times");
                        }
                        desiredTile = map.getCurrentLevel().getDownStairs();
                    }
                }
            }
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

            // Return true, a move was made
            return true;

        }
        // Return false, no move was made
        return false;
    }

    protected boolean moveActor(Actor actor, Input.Direction d) {
        MessageConsole messageConsole = roguelike.getMessageConsole();
        Map map = roguelike.getMap();

        if(d == null) {
            messageConsole.addMessage("moveActor(" + actor.hashCode() + ", " + d + ") error");
            return false;
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
        return false;
    }

    private void processCombat(Actor actor1, Actor actor2) {
        MessageConsole messageConsole = roguelike.getMessageConsole();
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


        // firstAttacker attacks
        combatMessage += firstAttacker.getName() + " hit " + secondAttacker.getName() + " for " + firstAttackerDamage;

        secondAttacker.setCurrentHp(secondAttacker.getCurrentHp() - firstAttackerDamage);

        // Check if secondAttacker was killed
        if (secondAttacker.getCurrentHp() <= 0) {
            secondAttacker.setAlive(false);
        }

        // If secondAttacker is still alive
        if (secondAttacker.isAlive()) {
            // secondAttacker attacks
            combatMessage += ", " + secondAttacker.getName() + " hit " + firstAttacker.getName() + " for " + secondAttackerDamage;
            firstAttacker.setCurrentHp(firstAttacker.getCurrentHp() - secondAttackerDamage);

            // Check if firstAttacker was killed
            if (firstAttacker.getCurrentHp() <= 0) {
                firstAttacker.setAlive(false);
            }
        }

        // If the player is watching, or is involved, show the message
        // Otherwise, show some "noise" message
        if(actor1.equals(player) || actor2.equals(player)) {
            messageConsole.addMessage(combatMessage);
        } else if(actorInPlayerView(actor1) || actorInPlayerView(actor2)) {
            messageConsole.addMessage(combatMessage);
        } else {
            messageConsole.addMessage("You hear noise");
        }
    }

    private boolean actorCanSeeActor(Actor source, Actor target) {
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
            map.getCurrentLevel().toggleAllTilesVisible(false);
        }
        actor.clearVisibleTiles();
        int y = actor.getTile().getY();
        int x = actor.getTile().getX();

        ArrayList<Tile> fieldOfVisionTiles = FieldOfVision.calculateRayCastingFOVVisibleTiles(y, x, map
                .getCurrentLevel(), map.getWidth() / 2);


        for (Tile t : fieldOfVisionTiles) {
            if(actor.equals(player)) {
                t.setVisible(true);
                t.setExplored(true);
            }
            actor.addVisibleTile(t);
        }
    }
}
