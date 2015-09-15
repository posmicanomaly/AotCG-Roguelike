package posmicanomaly.AotCG.Screen;

import posmicanomaly.libjsrte.Console.Console;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 9/15/2015.
 */
public class Title {
    private Console titleConsole;
    private int currentItemSelected;
    private ArrayList<String> menuItems;

    public Title(int windowHeight, int windowWidth) {
        //title console
        titleConsole = new Console(windowHeight, windowWidth);
        //titleConsole.setBorder(true);
        menuItems = new ArrayList<String>();
        currentItemSelected = 0;
        menuItems.add("New Game");
        menuItems.add("Controls");
        update();
    }

    public void update() {
        titleConsole.clear();
        titleConsole.update();
        int row = titleConsole.getyBufferHeight() / 4;
        titleConsole.writeCenteredString("AotCG", row);
        row += 3;
        for(int i = 0; i < menuItems.size(); i++) {
            String item = menuItems.get(i);
            if(i == currentItemSelected) {
                item = "> " + item + " <";
            }
            titleConsole.writeCenteredString(item, row);
            row++;
        }
    }

    public Console getTitleConsole() {
        return titleConsole;
    }

    public void scrollDown() {
        currentItemSelected++;
        if(currentItemSelected >= menuItems.size()) {
            currentItemSelected = 0;
        }
    }

    public void scrollUp() {
        currentItemSelected--;
        if(currentItemSelected < 0) {
            currentItemSelected = menuItems.size() - 1;
        }
    }

    public String getSelectedItem() {
        return menuItems.get(currentItemSelected);
    }
}
