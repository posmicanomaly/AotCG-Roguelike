package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Map;
import posmicanomaly.AotCG.Gui.Component.GameInformationConsole;
import posmicanomaly.AotCG.Gui.Component.InventorySideConsole;
import posmicanomaly.AotCG.Gui.Component.MessageConsole;
import posmicanomaly.libjsrte.Console.Console;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by jessepospisil on 9/6/15.
 */
public class Gui {
    private final Roguelike roguelike;

    public Gui(Roguelike roguelike) {
        this.roguelike = roguelike;
    }

    protected void initGui() {
        Actor player = roguelike.getPlayer();
        Map map = roguelike.getMap();

        initMessageConsole();
        roguelike.gameInformationConsole = new GameInformationConsole(roguelike.getGameInformationConsoleHeight(),
                roguelike.getGameInformationConsoleWidth(), player, map);
        //gameInformationConsole.setBorder(true);

        roguelike.inventorySideConsole = new InventorySideConsole(roguelike.mapHeight, 20);
        //inventorySideConsole.setBorder(true);

        roguelike.menuWindow = new Console(25, 25);
        roguelike.menuWindow.setBorder(true);
        roguelike.menuWindow.fillBgColor(new Color(0, 0, 0, 0.3f));
        roguelike.showMenu = false;
        roguelike.showInventory = false;
    }

    private void initMessageConsole() {
        roguelike.messageConsole = new MessageConsole(roguelike.messageHeight, roguelike.messageWidth);
        //this.messageConsole.setBorder(true);
        roguelike.messageConsole.addMessage("Welcome");
    }

    public void drawGUI() {
        roguelike.getMessageConsole().copyBufferTo(roguelike.getRootConsole(), roguelike.mapHeight, roguelike.getGameInformationConsoleWidth());
        roguelike.getGameInformationConsole().setTurns(roguelike.getTurns());
        roguelike.getGameInformationConsole().setCurrentFrames(roguelike.getCurrentFrames());
        roguelike.getGameInformationConsole().setFps(roguelike.getLastFramesPerSecond());
        roguelike.getGameInformationConsole().updateConsole();
        roguelike.getGameInformationConsole().copyBufferTo(roguelike.getRootConsole(), 0, 0);

        if (roguelike.showMenu) {
            roguelike.getMenuWindow().copyBufferTo(roguelike.getRootConsole(), roguelike.window.HEIGHT / 2 - 12, roguelike.window.WIDTH / 2 - 12);
        }

        if (roguelike.showInventory) {
            roguelike.getInventorySideConsole().updateConsole();
            roguelike.getInventorySideConsole().copyBufferTo(roguelike.getRootConsole(), 0, roguelike.getRootConsole().getxBufferWidth() -
                    roguelike.getInventorySideConsole().getxBufferWidth());
        }

        if(roguelike.showVictoryConsole) {
            roguelike.getVictoryConsole().update();
            roguelike.getVictoryConsole().copyBufferTo(roguelike.getRootConsole(), roguelike.windowHeight / 2 - roguelike.getVictoryConsole().getyBufferHeight() / 2, roguelike.windowWidth / 2 - roguelike.getVictoryConsole().getxBufferWidth() / 2);
        }

        if(roguelike.showDefeatConsole) {
            roguelike.getDefeatConsole().update();
            roguelike.getDefeatConsole().copyBufferTo(roguelike.getRootConsole(), roguelike.windowHeight / 2 - roguelike.getDefeatConsole().getyBufferHeight() / 2, roguelike.windowWidth / 2 - roguelike.getDefeatConsole().getxBufferWidth() / 2);
        }
    }

    protected void initVictoryConsole() {
        roguelike.victoryConsole = new Console(roguelike.windowHeight / 2, roguelike.windowWidth / 2);
        roguelike.getVictoryConsole().setBorder(true);
        int row = 1;
        ArrayList<String> victoryMessages = new ArrayList<String>();
        victoryMessages.add("You Win!");
        victoryMessages.add("");
        victoryMessages.add("You have slain the Giant and saved everyone!");
        victoryMessages.add("");
        victoryMessages.add("Level Reached: " + roguelike.getPlayer().getLevel());
        victoryMessages.add("Turns taken: " + roguelike.getTurns());
        victoryMessages.add("Maximum HP: " + roguelike.getPlayer().getMaxHp());
        victoryMessages.add("");
        victoryMessages.add("Press ESCAPE to keep playing");
        victoryMessages.add("Press R to restart");
        for(String s : victoryMessages) {
            roguelike.getVictoryConsole().writeCenteredString(s, row);
            row++;
        }
    }

    protected void initDefeatConsole() {
        roguelike.defeatConsole = new Console(roguelike.windowHeight / 2, roguelike.windowWidth / 2);
        roguelike.getDefeatConsole().setBorder(true);
        int row = 1;
        ArrayList<String> defeatMessages = new ArrayList<String>();
        defeatMessages.add("You Died!");
        defeatMessages.add("");
        defeatMessages.add("You were killed and now the Giant will destroy everyone!");
        defeatMessages.add("");
        defeatMessages.add("Level Reached: " + roguelike.getPlayer().getLevel());
        defeatMessages.add("Turns taken: " + roguelike.getTurns());
        defeatMessages.add("Maximum HP: " + roguelike.getPlayer().getMaxHp());
        defeatMessages.add("");
        defeatMessages.add("Press R to restart");
        for(String s : defeatMessages) {
            roguelike.getDefeatConsole().writeCenteredString(s, row);
            row++;
        }
    }
}
