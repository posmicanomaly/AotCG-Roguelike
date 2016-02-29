package posmicanomaly.AotCG.Game;

import posmicanomaly.AotCG.Component.Actor;
import posmicanomaly.AotCG.Component.Map;
import posmicanomaly.AotCG.Gui.Component.GameInformationConsole;
import posmicanomaly.AotCG.Gui.Component.InventoryConsole;
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
    private InventoryConsole inventoryConsole;
    private MessageConsole messageConsole;
    private Console victoryConsole;
    private Console defeatConsole;

    public Gui(Roguelike roguelike) {
        this.roguelike = roguelike;
    }

    protected void initGui() {
        Actor player = null;
        Map map = null;

        initMessageConsole();
        gameInformationConsole = new GameInformationConsole(roguelike.getGameInformationConsoleHeight(),
                roguelike.getGameInformationConsoleWidth(), player, map, this.roguelike);
        gameInformationConsole.setBorder(true);
        gameInformationConsole.setBorderColor(Color.gray);
        gameInformationConsole.setBorderStyle(Console.BorderStyle.DOUBLE);

        //initInventoryConsole();

        roguelike.menuWindow = new Console(25, 25);
        roguelike.menuWindow.setBorder(true);
        roguelike.menuWindow.setBorderStyle(Console.BorderStyle.DOUBLE);
        roguelike.menuWindow.fillBgColor(new Color(0, 0, 0, 0.3f));
        roguelike.showMenu = false;
        roguelike.showInventory = false;

        victoryConsole = null;
        defeatConsole = null;
    }

    public void initInventoryConsole() {
        inventoryConsole = new InventoryConsole(roguelike.getPlayer(), 2 + roguelike.getPlayer().getInventory().size(), 25);
        inventoryConsole.setBorder(true);
        inventoryConsole.setBorderColor(Color.gray);
        inventoryConsole.setBorderStyle(Console.BorderStyle.DOUBLE);
    }

    public void connectPlayer() {
        gameInformationConsole.setPlayer(roguelike.getPlayer());
       // inventoryConsole.setPlayer(roguelike.getPlayer());
    }
    public void connectMap() {
        gameInformationConsole.setMap(roguelike.getMap());
    }

    private void initMessageConsole() {
        messageConsole = new MessageConsole(roguelike.messageHeight, roguelike.messageWidth);
        messageConsole.setBorder(true);
        messageConsole.setBorderColor(Color.gray);
        messageConsole.setBorderStyle(Console.BorderStyle.DOUBLE);
    }

    public void drawGUI() {
        messageConsole.copyBufferTo(roguelike.getRootConsole(), roguelike.mapHeight, roguelike
                .getGameInformationConsoleWidth());
        gameInformationConsole.setTurns(roguelike.getTurns());
        gameInformationConsole.updateConsole();
        gameInformationConsole.copyBufferTo(roguelike.getRootConsole(), 0, 0);

        if (roguelike.showMenu) {
            roguelike.getMenuWindow().copyBufferTo(roguelike.getRootConsole(), roguelike.window.HEIGHT / 2 - 12, roguelike.window.WIDTH / 2 - 12);
        }

        if (roguelike.showInventory) {
          //  initInventoryConsole();
            inventoryConsole.updateConsole();
            inventoryConsole.copyBufferTo(roguelike.getRootConsole(), 1, gameInformationConsole.getxBufferWidth() + 1);
        }

        if(roguelike.showVictoryConsole) {
            victoryConsole.update();
            victoryConsole.copyBufferTo(roguelike.getRootConsole(), roguelike.windowHeight / 2 - victoryConsole
                    .getyBufferHeight() / 2, roguelike.windowWidth / 2 - victoryConsole.getxBufferWidth() / 2);
        }

        if(roguelike.showDefeatConsole) {
            defeatConsole.update();
            defeatConsole.copyBufferTo(roguelike.getRootConsole(), roguelike.windowHeight / 2 - defeatConsole
                    .getyBufferHeight() / 2, roguelike.windowWidth / 2 - defeatConsole.getxBufferWidth() / 2);
        }
    }

    protected void initVictoryConsole() {
        victoryConsole = new Console(roguelike.windowHeight / 2, roguelike.windowWidth / 2);
        victoryConsole.setBorder(true);
        victoryConsole.setBorderStyle(Console.BorderStyle.DOUBLE);
        victoryConsole.setBorderColor(Color.BLUE);
        int row = 1;

        victoryConsole.writeCenteredString("You Win!", row);
        row+=2;
        drawGameStatistics(victoryConsole, row);

        row+=2;
        victoryConsole.writeCenteredString("Press ESCAPE to keep playing", row);
        row++;
        victoryConsole.writeCenteredString("Press R to restart", row);
    }

    private void drawGameStatistics(Console target, int startRow) {
        ArrayList<String> gameStatistics = new ArrayList<>();
        gameStatistics.add("Maximum HP: " + roguelike.getPlayer().getMaxHp());
        for(String s : gameStatistics) {
            target.writeCenteredString(s, startRow);
            startRow++;
        }
    }

    protected void initDefeatConsole() {
        defeatConsole = new Console(roguelike.windowHeight / 2, roguelike.windowWidth / 2);
        defeatConsole.setBorder(true);
        defeatConsole.setBorderStyle(Console.BorderStyle.DOUBLE);
        defeatConsole.setBorderColor(Color.RED);
        int row = 1;
        defeatConsole.writeCenteredString("You Died!", row);
        row+=2;
        drawGameStatistics(defeatConsole, row);

        row+=2;
        defeatConsole.writeCenteredString("Press R to restart", row);
    }

    public MessageConsole getMessageConsole() {
        return messageConsole;
    }

    public Console getVictoryConsole() {
        return victoryConsole;
    }


    public InventoryConsole getInventoryConsole() {
        return inventoryConsole;
    }
}
