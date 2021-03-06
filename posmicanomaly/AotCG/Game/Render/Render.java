package posmicanomaly.AotCG.Game.Render;

import posmicanomaly.AotCG.Component.Actor.Actor;
import posmicanomaly.AotCG.Component.GameColors;
import posmicanomaly.AotCG.Component.Map.Tile;
import posmicanomaly.AotCG.Factory.LevelFactory;
import posmicanomaly.AotCG.Game.Render.Animation.Animation;
import posmicanomaly.AotCG.Game.Roguelike;
import posmicanomaly.libjsrte.Console.Console;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/16/2015.
 */
public class Render implements Runnable {
    private Thread thread;
    private Roguelike roguelike;
    private Animation animation;
    private boolean run;
    private ArrayList<DebugTile> highlightedDebugTiles;

    public void doTestAnimation() {
        animation.doFlashPlayer(5);
        animation.doFlashVisibleTiles(5);
        animation.doJumble(5);
    }

    public Roguelike getRoguelike() {
        return roguelike;
    }

    public Animation getAnimation() {
        return animation;
    }


    private class DebugTile {
        Tile t;
        Color c;
        private DebugTile(Tile t, Color c) {
            this.t = t;
            this.c = c;
        }
    }

    public Render(Roguelike roguelike) {
        this.roguelike = roguelike;
        highlightedDebugTiles = new ArrayList<>();
        this.animation = new Animation(this);
    }

    // Rendering reasons
    public enum Reason {
        ANIMATION, NONE, MOUSE_MOVED
    }

    public void start() {
        System.out.println("Starting Render Thread");
        thread = new Thread(this);
        run = true;
        thread.start();
    }

    public void stop() {
        run = false;
    }

    protected void shimmerWater(boolean onlyVisibleWater) {
         /*
        Shimmer water code

        Sets the backgroundColor of tile to a varied color based on the standard WATER_BG
         */
        //Level level = roguelike.getMap().getCurrentLevel();
        ArrayList<Tile> waterTiles;
        if(onlyVisibleWater) {
            if(roguelike.getPlayer() == null) {
                // There's no player
                return;
            }
            waterTiles = roguelike.getPlayer().getVisibleTiles();
        }
        else {
            waterTiles = roguelike.getMap().getCurrentLevel().getWaterTiles();
        }
        roguelike.getMap().getCurrentLevel().shimmerWaterTiles(waterTiles);
    }
    protected void shimmerWater() {
       shimmerWater(true);
    }

    protected void applyLightingToMap() {
        for(Actor a : roguelike.getMap().getCurrentLevel().getActors()) {
            for(Tile t : a.getVisibleTiles()) {
                if(roguelike.getPlayer().getVisibleTiles().contains(t)) {
                    if(t.hasActor()) {
                        continue;
                    }
                    // offset because of map borders
                    int mapConsoleTileY = t.getY() + 1;
                    int mapConsoleTileX = t.getX() + 1;
                    Color bgColor = roguelike.getMapConsole().getBgColor(mapConsoleTileY, mapConsoleTileX);
                    int r = bgColor.getRed();
                    int g = bgColor.getGreen();
                    int b = bgColor.getBlue();

                    int d = Math.abs(a.getTile().getY() - t.getY()) + Math.abs(a.getTile().getX() - t.getX());
                    r += 125 / (d + 2);
                    if(r < 0)
                        r = 0;
                    if(r > 255)
                        r = 255;
                    g += 105 / (d + 2);
                    if(g < 0)
                        g = 0;
                    if(g > 255)
                        g = 255;
                    b += 50 / (d + 2);
                    if(b < 0)
                        b = 0;
                    if(b > 255)
                        b = 255;

                    roguelike.getMapConsole().setBgColor(mapConsoleTileY, mapConsoleTileX, new Color(r, g, b));
                }
            }
        }
    }


    public void drawGame(Console rootConsole, Reason reason) {
        //rootConsole.clear();
        if (roguelike.getCurrentState() == Roguelike.State.TITLE) {
            roguelike.getTitle().update();
            roguelike.getTitle().getTitleConsole().copyBufferTo(rootConsole, 0, 0);
            roguelike.getWindow().refresh();
        }
        else if (roguelike.getCurrentState() == Roguelike.State.PLAYING || roguelike.SHOW_MAP_CREATION) {

            // Refresh the map buffer
            if(roguelike.getMap() == null) {
                return;
            }

            roguelike.copyMapToBuffer();

            // Lighting test
            if(roguelike.getMap().getCurrentDepth() > 0) {
                //applyLightingToMap();
            }

            // water shimmer
            switch(reason) {
                case MOUSE_MOVED:
                case ANIMATION:
                    break;
                default:
                    shimmerWater();
                    break;
            }

            // Debug
            //showActorPaths();
            //showHighlightedDebugTiles();

            roguelike.getMapConsole().copyBufferTo(rootConsole, 0, roguelike.getGameInformationConsoleWidth());


            roguelike.getGui().drawGUI();
            // Mouse testing
            drawMouseToolTips(rootConsole);

            roguelike.getWindow().refresh();
        }
    }
    public void drawGame(Console rootConsole) {
       drawGame(rootConsole, Reason.NONE);
    }

    private void showHighlightedDebugTiles() {
        for(DebugTile t : highlightedDebugTiles) {
            roguelike.getMapConsole().setBgColor(t.t.getY(), t.t.getX(), t.c);
        }
    }

    public boolean addHighlightedDebugTile(Tile t, Color c) {
        for(DebugTile d : highlightedDebugTiles) {
            if(d.t.getY() == t.getY() && d.t.getX() == t.getX()) {
                return false;
            }
        }
        highlightedDebugTiles.add(new DebugTile(t, c));
        return true;
    }

    public void removeHighlightedDebugTile(Tile t) {
        for(DebugTile d : highlightedDebugTiles) {
            if(d.t.getX() == t.getX() && d.t.getY() == t.getY()) {
                highlightedDebugTiles.remove(d);
                return;
            }
        }
    }

    public void clearHighlightedDebugTiles() {
        highlightedDebugTiles = new ArrayList<>();
    }

    private void drawMouseToolTips(Console rootConsole) {
        if(roguelike.isMouseOnMap()) {
            rootConsole.setBgColor(roguelike.getLastMy(), roguelike.getLastMx(), GameColors.MOUSEBG);
            int transX = roguelike.getLastMx() - roguelike.getGameInformationConsoleWidth() - 1;
            int transY = roguelike.getLastMy() - 1;
            Tile t = roguelike.getMap().getCurrentLevel().getTile(transY, transX);
            int y = roguelike.getLastMy() - 1;
            int x = roguelike.getLastMx();
            if(t == null) {
                System.out.println("Mouse on map, but tile is null. Check the math!");
            } else {
                Color foreground = GameColors.MOUSEBG2;
                Color background = new Color(0, 0, 0, 0.8f);
                String tip = "?";
                if(t.isVisible()) {
                    if (t.hasActor()) {
                        tip = t.getActor().getName();
                        foreground = GameColors.RED_PATH;
                    } else {
                        tip = t.getTypeString();
                        foreground = GameColors.GREEN_PATH;
                    }
                } else if(t.isExplored()) {
                    tip = t.getTypeString() + "?";
                }
                rootConsole.writeColoredString(tip, y, x, foreground, background);
            }
        }
    }

    public void renderSingleFrame(Reason reason) {
        drawGame(roguelike.getRootConsole(), reason);
        roguelike.setLastFrameDrawTime(System.currentTimeMillis());
        roguelike.setRedrawGame(false);
    }

    public void renderSingleFrame() {
            // Draw the game
            // TODO: draw only if we need to, to improve CPU usage
            drawGame(roguelike.getRootConsole());
            roguelike.setLastFrameDrawTime(System.currentTimeMillis());
            roguelike.setRedrawGame(false);
    }

    protected void showActorPaths() {
        /**
         * Debug
         *
         * Show paths
         */
        ArrayList<Actor> actors = roguelike.getMap().getCurrentLevel().getActors();

        for (Actor a : actors) {
            // Check for a null path
            if(a.getCurrentPath() == null) {
                continue;
            }
            int tRed, tGreen, tBlue;
            Color pathColor;
            for (Tile t : a.getCurrentPath()) {
                tRed = t.getBackgroundColor().getRed();
                tGreen = t.getBackgroundColor().getGreen();
                tBlue = t.getBackgroundColor().getBlue();

                int shimmer = 100;
                if(a.equals(roguelike.getPlayer())) {
                    tGreen += shimmer;
                    if(tGreen > 255) {
                        tGreen = 255;
                    }
                    pathColor = new Color(tRed, tGreen, tBlue).brighter();
                    pathColor = GameColors.GREEN_PATH;
                } else {
                    tRed += shimmer;
                    if(tRed > 255) {
                        tRed = 255;
                    }
                    pathColor = new Color(tRed, tGreen, tBlue).brighter();
                    pathColor = GameColors.RED_PATH;
                }

                int y = t.getY();
                int x = t.getX();
                // TODO: get rid of these border hacks.
                if(roguelike.getMapConsole().hasBorder()) {
                    y++;
                    x++;
                }
                roguelike.getMapConsole().setBgColor(y, x, pathColor);
            }
        }
    }

    @Override
    public void run() {
        while (run) {
            if(System.currentTimeMillis() - roguelike.getLastFrameDrawTime() > roguelike.getRefreshIntervalMs()) {
                roguelike.setRedrawGame(true);
            }
            /**
             * Check if we need to force a redraw for next loop. Idle
             */

            if (roguelike.getRedrawGame()) {
                long startTime = System.currentTimeMillis();
                // Draw the game
                // TODO: draw only if we need to, to improve CPU usage
                drawGame(roguelike.getRootConsole());
                roguelike.setLastFrameDrawTime(System.currentTimeMillis());
                roguelike.setRedrawGame(false);
                //roguelike.gameLoopRedrawTimeStart = System.currentTimeMillis();
                roguelike.incrementCurrentFrames();
                long remainingTime = roguelike.getMinFrameSpeed() - (System.currentTimeMillis() - startTime);

                // During initialization, this time may go negative. Set to 0 if this happens to prevent exception.
                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                // Determine remaining time in frame based on when we started loop, to after we've drawn the game


                // Increment our current frames


                // Determine FPS
                // .
                // .

                // fpsTimerStart is initialized in startGame()
                // if 1 second or more has passed, set the currentFrames to lastFramesPerSecond
                if (System.currentTimeMillis() - roguelike.getFpsTimerStart() >= 1000) {
                    roguelike.setLastFramesPerSecond(roguelike.getCurrentFrames());

                    // Reset currentFrames
                    roguelike.setCurrentFrames(0);
                    // Reset fpsTimerStart
                    roguelike.setFpsTimerStart(System.currentTimeMillis());
                }

                // Sleep for whatever time we have remaining to maintain the desired FPS
                try {
                    Thread.sleep(remainingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(roguelike.getMinFrameSpeed());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
