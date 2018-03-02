package com.waldo.serial.classes;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.waldo.serial.classes.Message.SerialMessage;
import com.waldo.utils.StringUtils;

import javax.swing.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.waldo.serial.classes.SerialManager.MessageTypes.VARIABLE;

public class SerialManager {

    public enum ParityTypes {
        None  (SerialPort.NO_PARITY),
        Odd   (SerialPort.ODD_PARITY),
        Even  (SerialPort.EVEN_PARITY),
        Mark  (SerialPort.MARK_PARITY),
        Space (SerialPort.SPACE_PARITY);


        private final int intValue;
        ParityTypes(int intValue) {
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public static ParityTypes fromInt(int intValue) {
            switch (intValue) {
                default:
                case SerialPort.NO_PARITY: return None;
                case SerialPort.ODD_PARITY: return Odd;
                case SerialPort.EVEN_PARITY: return Even;
                case SerialPort.MARK_PARITY: return Mark;
                case SerialPort.SPACE_PARITY: return Space;
            }
        }
    }

    public enum FlowControlTypes {
        None
    }

    public enum BaudRateValues {
        _1200  ("1200 bps", 1200),
        _2400  ("2400 bps", 2400),
        _4800  ("4800 bps", 4800),
        _9600  ("9600 bps", 9600),
        _14400 ("14400 bps", 14400),
        _19200 ("19200 bps", 19200),
        _28800 ("28800 bps", 28800),
        _38400 ("38400 bps", 38400),
        _57600 ("57600 bps", 57600),
        _Custom("<Custom>", 0);

        private final int baud;
        private final String string;
        BaudRateValues(String string, int baud) {
            this.string = string;
            this.baud = baud;
        }

        @Override
        public String toString() {
            return string;
        }

        public int getBaud() {
            return baud;
        }

        public static BaudRateValues fromBaud(int baud) {
            switch (baud) {
                case 1200: return _1200;
                case 2400: return _2400;
                case 4800: return _4800;
                case 9600: return _9600;
                case 14400: return _14400;
                case 19200: return _19200;
                case 28800: return _28800;
                case 38400: return _38400;
                case 57600: return _57600;
                default:
                case 0: return _Custom;
            }
        }
    }

    public enum DataBitValues {
        _5 ("5 bits", 5),
        _6 ("6 bits", 6),
        _7 ("7 bits", 7),
        _8 ("8 bits", 8);

        private final int bits;
        private final String string;
        DataBitValues(String string, int bits) {
            this.string = string;
            this.bits = bits;
        }

        @Override
        public String toString() {
            return string;
        }

        public int getBits() {
            return bits;
        }

        public static DataBitValues fromBits(int bits) {
            switch (bits) {
                case 5: return _5;
                case 6: return _6;
                case 7: return _7;
                default:
                case 8: return _8;
            }
        }
    }

    public enum StopBitValues {
        _1 ("1 bit", SerialPort.ONE_STOP_BIT),
        _2 ("2 bits", SerialPort.TWO_STOP_BITS);

        private final int stopBits;
        private final String string;
        StopBitValues(String string, int stopBits) {
            this.string = string;
            this.stopBits = stopBits;
        }

        @Override
        public String toString() {
            return string;
        }

        public int getStopBits() {
            return stopBits;
        }

        public static StopBitValues fromStopBits(int stopBits) {
            switch (stopBits) {
                default:
                case 1: return _1;
                case 2: return _2;
            }
        }
    }

    /*
     *                  SINGLETON
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static final SerialManager Instance = new SerialManager();
    public static SerialManager serMgr() {
        return Instance;
    }
    private SerialManager() {


    }

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private List<SerialListener> serialListenerList = new ArrayList<>();
    private SerialPort serialPort;
    private final List<SerialMessage> txSerialMessageList = new ArrayList<>();
    private final List<SerialMessage> rxSerialMessageList = new ArrayList<>();

    // Settings
    private MessageTypes messageType;

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void init(SerialListener serialListener) {
        serialListenerList.add(serialListener);
    }

    public void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public List<SerialPort> getSerialPorts() {
        List<SerialPort> portList = new ArrayList<>();
        Collections.addAll(portList, SerialPort.getCommPorts());
        return portList;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public SerialPort findSerialPortByName(String name) {
        for (SerialPort port : getSerialPorts()) {
            if (port.getSystemPortName().equals(name)) {
                return port;
            }
        }



        return null;
    }

    public MessageTypes getMessageType() {
        if (messageType == null) {
            messageType = MessageTypes.Text;
        }
        return messageType;
    }

    public void setMessageType(MessageTypes messageType) {
        this.messageType = messageType;
    }

    public void clearRxMessages() {
        rxSerialMessageList.clear();
    }

    public void clearTxMessages() {
        txSerialMessageList.clear();
    }

    public void addSerialListener(SerialListener serialListener) {
        if (!serialListenerList.contains(serialListener)) {
            serialListenerList.add(serialListener);
        }
    }

    public void removeSerialListener(SerialListener serialListener) {
        if (serialListenerList.contains(serialListener)) {
            serialListenerList.remove(serialListener);
        }
    }

    public SerialListener getMainSerialListener() {
        if (serialListenerList.size() > 0) {
            return serialListenerList.get(0);
        }
        return null;
    }

    public void close() {
        if (serialPort != null) {
            try {
                serialPort.removeDataListener();
                serialPort.closePort();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean open(SerialPort port) {
        boolean result = false;
        if (port != null) {
            if (serialPort != null) {
                close();
            }
            this.serialPort = port;
            //this.serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            addDataAvailableEvent(this.serialPort);
            if (!this.serialPort.openPort()) {
                onError("Failed to open port: " + port.getDescriptivePortName());
            } else {
                result = true;
            }
        }
        return result;
    }

    public void initSerialPort(SerialPort port, int baud, int bits, int stopBits, int parity) {
        port.setComPortParameters(baud, bits, stopBits, parity);
        open(port);
    }

    public List<SerialMessage> getTxSerialMessageList() {
        return txSerialMessageList;
    }

    public List<SerialMessage> getRxSerialMessageList() {
        return new ArrayList<>(rxSerialMessageList);
    }

    public void write(String data) {
        try {
            if (serialPort != null) {
                if (serialPort.isOpen()) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            serialPort.writeBytes(data.getBytes(), data.length());
                            onTransmitted(SerialMessage.createTx(messageType, data));
                        } catch (Exception e) {
                            onError("Failed to write bytes..", e);
                        }

                    });
                } else {
                    onError("COM port is closed..");
                }
            } else {
                onError("No COM port available..");
            }
        } catch (Exception e) {
            onError(e);
        }
    }

    private void addToMessageList(SerialMessage serialMessage) {
        txSerialMessageList.add(serialMessage);
    }

    private void onError(Throwable throwable) {
        onError(throwable.getMessage(), throwable);
    }

    private void onError(String error) {
        onError(error, null);
    }

    private void onError(String error, Throwable throwable) {
        for (SerialListener listener : serialListenerList) {
            listener.onSerialError(error, throwable);
        }
    }

    private void onReceived(SerialMessage serialMessage) {
        for (SerialListener listener : serialListenerList) {
            listener.onReceived(serialMessage);
        }
    }

    private void onTransmitted(SerialMessage serialMessage) {
        for (SerialListener listener : serialListenerList) {
            listener.onTransmitted(serialMessage);
        }
    }

    private void addDataAvailableEvent(SerialPort serialPort) {
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    SerialPort comPort = event.getSerialPort();
                    byte[] bytes = new byte[comPort.bytesAvailable()];
                    comPort.readBytes(bytes, bytes.length);
                    newDataAvailable(bytes);
                }
            }
        });
    }


    private String inputMessage = "";
    private void newDataAvailable(byte[] newData) {
        String newMessage = new String(newData);
        if (!newMessage.isEmpty()) {
            inputMessage += new String(newData);

            try {
                SerialMessage m = SerialMessage.createRx(messageType, inputMessage);
                if (m != null && m.isConverted()) {
                    rxSerialMessageList.add(m);
                    onReceived(m);
                    inputMessage = "";
                }
            } catch (ParseException e) {
                onError(e);
                e.printStackTrace();
            }
        }
    }


    public enum MessageTypes {
        Text ("Simple text", "", -2, -2, "", "", "", false),
        PICMessageShort("PIC Simple short", "Simple PIC message, short form", 1, 1, "$", "&", ":", false),
        PICMessageLong("PIC Simple long", "Simple PIC message, long form", 2, 2, "$", "&", ":", true),
        PICMessageVariable("PIC Simple variable", "Variable PIC message", 1, -1, "$", "&", ":", true);

        public static final int VARIABLE = -1;
        public static final int NONE = -2;

        private final String name;
        private final String description;

        private final int commandBytes;
        private final int messageBytes;
        private final String startChar;
        private final String stopChar;
        private final String separator;

        private final boolean acknowledge;

        MessageTypes(String name, String description, int commandBytes, int messageBytes, String startChar, String stopChar, String separator, boolean acknowledge) {
            this.name = name;
            this.description = description;
            this.commandBytes = commandBytes;
            this.messageBytes = messageBytes;
            this.startChar = startChar;
            this.stopChar = stopChar;
            this.separator = separator;
            this.acknowledge = acknowledge;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getTemplate() {
            StringBuilder builder = new StringBuilder();

            // Start
            builder.append(startChar);

            // Command
            if (commandBytes != VARIABLE) {
                if (commandBytes == 1) {
                    builder.append("C");
                } else {
                    for (int i = 0; i < commandBytes; i++) {
                        builder.append("C").append(i);
                    }
                }
                builder.append(separator);
                if (messageBytes == VARIABLE) {
                    builder.append("L");
                    builder.append(separator);
                }
            }

            // SerialMessage
            if (messageBytes != VARIABLE) {
                if (messageBytes == 1) {
                    builder.append("M");
                } else {
                    for (int i = 0; i < messageBytes; i++) {
                        builder.append("M").append(i);
                    }
                }
            } else {
                if (commandBytes != VARIABLE) {
                    builder.append("M0M1M2..ML");
                }
            }

            // Acknowledge
            if (acknowledge) {
                builder.append(":");
                builder.append("A");
            }

            // Stop
            builder.append(stopChar);

            return builder.toString();
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getCommandBytes() {
            return commandBytes;
        }

        public int getMessageBytes() {
            return messageBytes;
        }

        public String getStartChar() {
            return startChar;
        }

        public String getStopChar() {
            return stopChar;
        }

        public String getSeparator() {
            return separator;
        }

        public boolean isAcknowledge() {
            return acknowledge;
        }
    }

    public abstract static class FakeMessageTask extends SwingWorker<Void, SerialMessage> {

        private final MessageTypes messageType;
        private boolean keepRunning;
        private StringUtils.RandomString randomString;

        public FakeMessageTask(MessageTypes messageType) {
            this.messageType = messageType;
            this.keepRunning = true;
        }

        public void stop() {
            keepRunning = false;
            this.cancel(true);
        }

        private SerialMessage createTextMessage() {
            SerialMessage textMessage = null;
            int l = ThreadLocalRandom.current().nextInt(2, 10);
            randomString = new StringUtils.RandomString(l);
            try {
                textMessage = SerialMessage.createRx(MessageTypes.Text, "abc");//randomString.nextString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return textMessage;
        }

        private SerialMessage createPICMessage() {
            SerialMessage textMessage = null;
            String command;
            String message;

            randomString = new StringUtils.RandomString(messageType.commandBytes);
            command = "com";//randomString.nextString();

            int messageLength = messageType.messageBytes;
            if (messageType.messageBytes == VARIABLE) {
                messageLength = ThreadLocalRandom.current().nextInt(2, 8);
            } else {
                messageLength = messageType.messageBytes;
            }
            randomString = new StringUtils.RandomString(messageLength);
            message = "mes";//randomString.nextString();
            String m = messageType.startChar + command;
            if (messageType.messageBytes == VARIABLE) {
                m += messageType.separator + messageLength;
            }
            m += messageType.separator + message;
            if (messageType.isAcknowledge()) {
                m += messageType.separator;
                m += ThreadLocalRandom.current().nextInt(0, 255);
            }
            m += messageType.stopChar;

            try {
                textMessage = SerialMessage.createRx(messageType, m);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return textMessage;
        }

        @Override
        protected Void doInBackground() throws Exception {
            try {
                System.out.println("Start faking messages");
                while(keepRunning) {
                    switch (messageType) {
                        case Text:
                            publish(createTextMessage());
                            break;
                        case PICMessageShort:
                        case PICMessageLong:
                        case PICMessageVariable:
                            publish(createPICMessage());
                            break;
                    }

                    int sleep = ThreadLocalRandom.current().nextInt(1000, 6000 + 1);
                    Thread.sleep(sleep);
                }
            } catch (InterruptedException e) {
                System.out.println("Stopped while sleeping");
            }
            return null;
        }

        @Override
        protected void process(List<SerialMessage> chunks) {
            super.process(chunks);

            if (chunks != null && chunks.size() > 0) {
                for (SerialMessage message : chunks) {
                    processMessage(message);
                }
            }
        }

        public abstract void processMessage(SerialMessage serialMessage);
    }
}
