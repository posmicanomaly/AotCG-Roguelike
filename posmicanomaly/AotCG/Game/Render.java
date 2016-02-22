package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Colors;
import posmicanomaly.AotCG.Component.Level;
import posmicanomaly.AotCG.Component.Tile;
import posmicanomaly.libjsrte.Console.Console;
import posmicanomaly.libjsrte.Console.Symbol;
import posmicanomaly.libjsrte.Util.ColorTools;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/16/2015.
 */
public class Render implements Runnable {
    private Thread thread;
    private Roguelike roguelike;
    private boolean run;
    private ArrayList<DebugTile> highlightedDebugTiles;
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
    }

    // Rendering reasons
    public enum Reason {
        MOUSE_MOVED
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


    protected void drawGame(Console rootConsole, Reason reason) {
        //rootConsole.clear();
        if (roguelike.currentState == Roguelike.State.TITLE) {
            roguelike.title.update();
            roguelike.title.getTitleConsole().copyBufferTo(rootConsole, 0, 0);
            roguelike.window.refresh();
        }
        else if (roguelike.currentState == Roguelike.State.PLAYING || roguelike.SHOW_MAP_CREATION) {

            // Refresh the map buffer
            if(roguelike.getMap() == null) {
                return;
            }
            roguelike.copyMapToBuffer();

            // Lighting test
            if(roguelike.getMap().getCurrentDepth() > 0) {
               // applyLightingToMap();
            }

            // water shimmer
            if(reason != Reason.MOUSE_MOVED) {
                shimmerWater();
            }
            // Debug
            showActorPaths();
            showHighlightedDebugTiles();

            roguelike.getMapConsole().copyBufferTo(rootConsole, 0, roguelike.getGameInformationConsoleWidth());


            roguelike.gui.drawGUI();
            // Mouse testing
            drawMouseToolTips(rootConsole);

            roguelike.window.refresh();
        }
    }
    public void drawGame(Console rootConsole) {
       drawGame(rootConsole, null);
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
            rootConsole.setBgColor(roguelike.getLastMy(), roguelike.getLastMx(), Color.RED);
            int transX = roguelike.getLastMx() - roguelike.getGameInformationConsoleWidth() - 1;
            int transY = roguelike.getLastMy() - 1;
            Tile t = roguelike.map.getCurrentLevel().getTile(transY, transX);
            int y = roguelike.getLastMy() - 1;
            int x = roguelike.getLastMx();
            if(t == null) {
                System.out.println("Mouse on map, but tile is null. Check the math!");
            } else {
                Color foreground = Color.gray;
                Color background = new Color(0, 0, 0, 0.8f);
                String tip = "?";
                if(t.isVisible()) {
                    if (t.hasActor()) {
                        tip = t.getActor().getName();
                        foreground = Color.red;
                    } else {
                        tip = t.getTypeString();
                        foreground = Color.green;
                    }
                } else if(t.isExplored()) {
                    tip = t.getTypeString() + "?";
                }
                rootConsole.writeColoredString(tip, y, x, foreground, background);
            }
        }
    }

    public void renderSingleFrame() {
            // Draw the game
            // TODO: draw only if we need to, to improve CPU usage
            drawGame(roguelike.getRootConsole());
            roguelike.lastFrameDrawTime = System.currentTimeMillis();
            roguelike.redrawGame = false;
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
                } else {
                    tRed += shimmer;
                    if(tRed > 255) {
                        tRed = 255;
                    }
                    pathColor = new Color(tRed, tGreen, tBlue).brighter();
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
            if(System.currentTimeMillis() - roguelike.lastFrameDrawTime > roguelike.refreshIntervalMs) {
                roguelike.redrawGame = true;
            }
            /**
             * Check if we need to force a redraw for next loop. Idle
             */

            if (roguelike.redrawGame) {
                long startTime = System.currentTimeMillis();
                // Draw the game
                // TODO: draw only if we need to, to improve CPU usage
                drawGame(roguelike.getRootConsole());
                roguelike.lastFrameDrawTime = System.currentTimeMillis();
                roguelike.redrawGame = false;
                //roguelike.gameLoopRedrawTimeStart = System.currentTimeMillis();
                roguelike.currentFrames++;
                long remainingTime = roguelike.minFrameSpeed - (System.currentTimeMillis() - startTime);

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
                if (System.currentTimeMillis() - roguelike.fpsTimerStart >= 1000) {
                    roguelike.lastFramesPerSecond = roguelike.currentFrames;

                    // Reset currentFrames
                    roguelike.currentFrames = 0;
                    // Reset fpsTimerStart
                    roguelike.fpsTimerStart = System.currentTimeMillis();
                }

                // Sleep for whatever time we have remaining to maintain the desired FPS
                try {
                    Thread.sleep(remainingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(roguelike.minFrameSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
