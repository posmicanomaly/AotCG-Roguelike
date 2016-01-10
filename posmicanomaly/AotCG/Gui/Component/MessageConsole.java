package posmicanomaly.AotCG.Gui.Component;

import posmicanomaly.libjsrte.Console.Console;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 8/17/2015.
 */
public class MessageConsole extends Console {
    private class Message {
        String message;
        Color foregroundColor;
        Color backgroundColor;

        private Message(String message, Color foregroundColor, Color backgroundColor) {
            this.message = message;
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
        }

        public String getMessage() {
            return message;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public Color getForegroundColor() {
            return foregroundColor;
        }
    }
    private ArrayList<Message> messageList;
    private int linesToDisplay;

    public MessageConsole(int height, int width) {
        super(height, width);
        messageList = new ArrayList<>();
    }

    public void addMessage(String message) {
        messageList.add(new Message(message, Color.white, Color.black));
        updateMessages();
    }

    public void addMessage(String message, Color foregroundColor) {
        messageList.add(new Message(message, foregroundColor, Color.black));
        updateMessages();
    }

    public void addMessage(String message, Color foregroundColor, Color backgroundColor) {
        messageList.add(new Message(message, foregroundColor, backgroundColor));
        updateMessages();
    }

    private void updateMessages() {
        this.clear();
        int y = 0;
        int x = 0;
        linesToDisplay = this.getyBufferHeight();

        if (hasBorder()) {
            y++;
            x++;
            linesToDisplay -= 2;
        }
        y = linesToDisplay;
        for (int i = messageList.size() - 1; i > messageList.size() - 1 - linesToDisplay; i--) {
            if (i >= 0) {
                Message message = messageList.get(i);
                writeColoredString(message.getMessage(), y, x, message.getForegroundColor(), message
                        .getBackgroundColor());
                y--;
            }
        }
    }
}
