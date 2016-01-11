package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Tile;
import posmicanomaly.libjsrte.Console.Console;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 12/16/2015.
 */
public class Render implements Runnable {
    private Thread thread;
    private Roguelike roguelike;
    private boolean run;

    public Render(Roguelike roguelike) {
        this.roguelike = roguelike;
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

    protected void drawGame(Console rootConsole) {
        //rootConsole.clear();
        if (roguelike.currentState == Roguelike.State.PLAYING) {

            // Refresh the map buffer
            roguelike.copyMapToBuffer();

            // Debug
            showActorPaths();


            roguelike.getMapConsole().copyBufferTo(rootConsole, 0, roguelike.getGameInformationConsoleWidth());


            roguelike.gui.drawGUI();
            // Mouse testing
            drawMouseToolTips(rootConsole);

            roguelike.window.refresh();
        } else if (roguelike.currentState == Roguelike.State.TITLE) {
            roguelike.title.update();
            roguelike.title.getTitleConsole().copyBufferTo(rootConsole, 0, 0);
            roguelike.window.refresh();
        }
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
            int tRed, tGreen, tBlue;
            Color pathColor;
            for (Tile t : a.getCurrentPath()) {
                tRed = t.getBackgroundColor().getRed();
                tGreen = t.getBackgroundColor().getGreen();
                tBlue = t.getBackgroundColor().getBlue();

                int shimmer = Roguelike.rng.nextInt(20) + 100;
                pathColor = new Color(tRed + shimmer, tGreen, tBlue).brighter();

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
