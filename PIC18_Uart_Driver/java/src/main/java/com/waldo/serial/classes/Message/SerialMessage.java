package com.waldo.serial.classes.Message;

import com.waldo.serial.classes.SerialManager.MessageTypes;

import java.text.ParseException;

public class SerialMessage {

    public static SerialMessage createRx(MessageTypes type, String input) throws ParseException {
        SerialMessage m = new SerialMessage(type, true);
        createFromString(m, input);
        return m;
    }

    public static SerialMessage createTx(MessageTypes type, String... args) throws ParseException {
        SerialMessage m = new SerialMessage(type, false);
//        switch (type) {
            m.input = args[0];
            m.message = args[0];
//        }

        return m;
    }

    private final MessageTypes messageType;
    private final boolean isRx;
    private boolean converted = false;
    private boolean acknowledged = false;

    private String input;
    private String command;
    private String message;
    private int ackId;

    private SerialMessage(MessageTypes messageType, boolean isRx) {
        this.messageType = messageType;
        this.isRx = isRx;
        this.input = "";
        this.command = "";
        this.message = "";
    }

    @Override
    public String toString() {
        return "SerialMessage{" +
                "messageType=" + getMessageType() +
                ", converted=" + isConverted() +
                ", input='" + getInput() + '\'' +
                ", command='" + getCommand() + '\'' +
                ", message='" + getMessage() + '\'' +
                ", ackId=" + getAckId() +
                '}';
    }

    private static void createFromString(SerialMessage m, String input) throws ParseException {
        MessageTypes t = m.getMessageType();

        if (input != null && !input.isEmpty()) {
            m.input = input;

            if (t.getStopChar().isEmpty() || t.getStartChar().isEmpty()) {
                m.message = input;
                m.setConverted(true);
            } else {
                if (input.contains(t.getStartChar()) && (input.contains(t.getStopChar()))) {
                    int start = input.indexOf(t.getStartChar());
                    int stop = input.indexOf(t.getStopChar());
                    int messageLength = t.getMessageBytes();

                    if (start < stop) {
                        String data = input.substring(start+1, stop);
                        String[] split = data.split(t.getSeparator());

                        int l = 2; // Command and message
                        if (t.isAcknowledge()) {
                            l++; // And ack
                        }
                        if (messageLength == MessageTypes.VARIABLE) {
                            l++; // and L
                            messageLength = Integer.valueOf(split[1]);
                        }

                        if (split.length != l) {
                            throw new ParseException("Not a correct format..", start);
                        }

                        int position = 0;
                        m.command = split[position];
                        position++;
                        if (t.getMessageBytes() == MessageTypes.VARIABLE) {
                            position++;
                        }
                        m.message = split[position];
                        if (m.message.length() != messageLength) {
                            throw new ParseException("Not a correct format..", start);
                        }
                        position++;
                        if (t.isAcknowledge()) {
                            try {
                                m.ackId = Integer.valueOf(split[position]);
                            } catch (Exception e) {
                                throw new ParseException("Invalid acknowledge", position);
                            }
                        }

                        m.setConverted(true);
                    } else {
                        throw new ParseException("Start character before stop character..", start);
                    }
                }
            }
        }
    }



    public MessageTypes getMessageType() {
        return messageType;
    }

    public String getInput() {
        if (input == null) {
            input = "";
        }
        return input;
    }

    public String getCommand() {
        if (command == null) {
            command = "";
        }
        return command;
    }

    public String getMessage() {
        if (message == null) {
            message = "";
        }
        return message;
    }

    public int getAckId() {
        return ackId;
    }

    public boolean isConverted() {
        return converted;
    }

    public void setConverted(boolean converted) {
        this.converted = converted;
    }

    public boolean isRx() {
        return isRx;
    }

    public boolean isTx() {
        return !isRx;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
}
