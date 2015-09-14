package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.libjsrte.Console.Console;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class MessageConsole extends Console {
    private ArrayList<String> messageList;
    private final int messageHeight = 10;

    public MessageConsole(int yBufferWidth, int xBufferWidth) {
        super(yBufferWidth, xBufferWidth);

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
        for (int i = messageList.size() - 1; i > messageList.size() - 1 - messageHeight; i--) {
            if (i >= 0) {
                writeString(messageList.get(i), y, x);
                y++;
            }
        }
    }
}
