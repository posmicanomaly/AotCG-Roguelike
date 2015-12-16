package posmicanomaly.AotCG.Game;

import posmicanomaly.libjsrte.Console.Console;

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
        rootConsole.clear();
        if (roguelike.currentState == Roguelike.State.PLAYING) {

            // Refresh the map buffer
            roguelike.copyMapToBuffer();

            // Debug
            roguelike.showActorPaths();

            roguelike.getMapConsole().copyBufferTo(rootConsole, 0, roguelike.getGameInformationConsoleWidth());

            roguelike.gui.drawGUI();

            roguelike.window.refresh();
        } else if (roguelike.currentState == Roguelike.State.TITLE) {
            roguelike.title.update();
            roguelike.title.getTitleConsole().copyBufferTo(rootConsole, 0, 0);
            roguelike.window.refresh();
        }
    }

    public void renderSingleFrame() {
            // Draw the game
            // TODO: draw only if we need to, to improve CPU usage
            drawGame(roguelike.getRootConsole());
            roguelike.lastFrameDrawTime = System.currentTimeMillis();
            roguelike.redrawGame = false;
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
