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
    private GameInformationConsole gameInformationConsole;
    private InventorySideConsole inventorySideConsole;
    private MessageConsole messageConsole;
    private Console victoryConsole;
    private Console defeatConsole;

    public Gui(Roguelike roguelike) {
        this.roguelike = roguelike;
    }

    protected void initGui() {
        Actor player = roguelike.getPlayer();
        Map map = roguelike.getMap();

        initMessageConsole();
        gameInformationConsole = new GameInformationConsole(roguelike.getGameInformationConsoleHeight(),
                roguelike.getGameInformationConsoleWidth(), player, map);
        gameInformationConsole.setBorder(true);

        inventorySideConsole = new InventorySideConsole(roguelike.mapHeight, 20);
        //inventorySideConsole.setBorder(true);

        roguelike.menuWindow = new Console(25, 25);
        roguelike.menuWindow.setBorder(true);
        roguelike.menuWindow.fillBgColor(new Color(0, 0, 0, 0.3f));
        roguelike.showMenu = false;
        roguelike.showInventory = false;

        victoryConsole = null;
        defeatConsole = null;
    }

    private void initMessageConsole() {
        messageConsole = new MessageConsole(roguelike.messageHeight, roguelike.messageWidth);
        messageConsole.setBorder(true);
        //roguelike.messageConsole.addMessage("");
    }

    public void drawGUI() {
        messageConsole.copyBufferTo(roguelike.getRootConsole(), roguelike.mapHeight, roguelike
                .getGameInformationConsoleWidth());
        gameInformationConsole.setTurns(roguelike.getTurns());
        gameInformationConsole.setCurrentFrames(roguelike.getCurrentFrames());
        gameInformationConsole.setFps(roguelike.getLastFramesPerSecond());
        gameInformationConsole.updateConsole();
        gameInformationConsole.copyBufferTo(roguelike.getRootConsole(), 0, 0);

        if (roguelike.showMenu) {
            roguelike.getMenuWindow().copyBufferTo(roguelike.getRootConsole(), roguelike.window.HEIGHT / 2 - 12, roguelike.window.WIDTH / 2 - 12);
        }

        if (roguelike.showInventory) {
            inventorySideConsole.updateConsole();
            inventorySideConsole.copyBufferTo(roguelike.getRootConsole(), 0, roguelike.getRootConsole()
                    .getxBufferWidth() -
                    inventorySideConsole.getxBufferWidth());
        }

        if(roguelike.showVictoryConsole) {
            victoryConsole.update();
            victoryConsole.copyBufferTo(roguelike.getRootConsole(), roguelike.windowHeight / 2 - victoryConsole.getyBufferHeight() / 2, roguelike.windowWidth / 2 - victoryConsole.getxBufferWidth() / 2);
        }

        if(roguelike.showDefeatConsole) {
            defeatConsole.update();
            defeatConsole.copyBufferTo(roguelike.getRootConsole(), roguelike.windowHeight / 2 - defeatConsole.getyBufferHeight() / 2, roguelike.windowWidth / 2 - defeatConsole.getxBufferWidth() / 2);
        }
    }

    protected void initVictoryConsole() {
        victoryConsole = new Console(roguelike.windowHeight / 2, roguelike.windowWidth / 2);
        victoryConsole.setBorder(true);
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
            victoryConsole.writeCenteredString(s, row);
            row++;
        }
    }

    protected void initDefeatConsole() {
        defeatConsole = new Console(roguelike.windowHeight / 2, roguelike.windowWidth / 2);
        defeatConsole.setBorder(true);
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
            defeatConsole.writeCenteredString(s, row);
            row++;
        }
    }

    public MessageConsole getMessageConsole() {
        return messageConsole;
    }

    public Console getVictoryConsole() {
        return victoryConsole;
    }
}
