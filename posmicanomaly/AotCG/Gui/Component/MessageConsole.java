package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.libjsrte.Console.Console;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class MessageConsole extends Console {
    private ArrayList<String> messageList;
    private int linesToDisplay;

    public MessageConsole(int height, int width) {
        super(height, width);
        messageList = new ArrayList<String>();
    }

    public void addMessage(String message) {
        messageList.add(message);
        updateMessages();
    }

    private void updateMessages() {
        this.clear();
        int y = 0;
        int x = 0;
        linesToDisplay = this.getyBufferHeight();
        if(hasBorder()) {
            y++;
            x++;
            linesToDisplay -= 2;
        }
        for (int i = messageList.size() - 1; i > messageList.size() - 1 - linesToDisplay; i--) {
            if (i >= 0) {
                writeString(messageList.get(i), y, x);
                y++;
            }
        }
    }
}
