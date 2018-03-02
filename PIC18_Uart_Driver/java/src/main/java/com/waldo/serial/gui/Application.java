package com.waldo.serial.gui;

import com.fazecast.jSerialComm.SerialPort;
import com.waldo.serial.classes.Message.SerialMessage;
import com.waldo.serial.classes.SerialListener;
import com.waldo.serial.classes.SerialManager;
import com.waldo.serial.gui.components.InputPanel;
import com.waldo.serial.gui.dialogs.serialsettingsdialog.SerialSettingsDialog;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.ResourceManager;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IFrame;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.waldo.serial.classes.SerialManager.serMgr;

public class Application extends IFrame implements SerialListener {

    public static final ResourceManager resMgr = new ResourceManager("settings/", "Icons.properties");
    public static final ImageIcon greenBall = resMgr.readImage("Ball.green");
    public static final ImageIcon yellowBall = resMgr.readImage("Ball.yellow");
    public static final ImageIcon redBall = resMgr.readImage("Ball.red");


    // Tool bar
    private ILabel statusLbl;
    private ILabel sessionLbl;
    private ILabel infoLbl;
    private AbstractAction settingsActions;
    private AbstractAction clearAction;

    private MessagePanel messagePanel;
    private InputPanel inputPanel;
    private PicMessageInfoPanel picMessageInfoPanel;

    private SerialManager.FakeMessageTask fakeMessageTask;

    public Application(String startUpPath) {
        SerialManager.serMgr().init(this);
    }

    private void updateStatus(SerialPort serialPort) {
        if (serialPort != null && serialPort.isOpen()) {
            statusLbl.setIcon(greenBall);
            infoLbl.setText(serialPort.getSystemPortName() + " " + serialPort.getBaudRate());
        } else {
            statusLbl.setIcon(redBall);
            infoLbl.setText(" - no port selected - ");
        }
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel leftPnl = new JPanel(new BorderLayout());
        JToolBar toolBar = GuiUtils.createNewToolbar(settingsActions, clearAction);

        leftPnl.add(statusLbl, BorderLayout.WEST);
        leftPnl.add(infoLbl, BorderLayout.CENTER);

        panel.add(leftPnl, BorderLayout.WEST);
        panel.add(sessionLbl, BorderLayout.CENTER);
        panel.add(toolBar, BorderLayout.EAST);

        return  panel;
    }

    private void beginWait() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void endWait() {
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void setSessionName(String name) {
        sessionLbl.setText(name);
    }

    public boolean isUpdating() {
        return this.getCursor().getType() == Cursor.WAIT_CURSOR;
    }

    private void startFakeMessages() {
        SwingUtilities.invokeLater(() -> {
            if (fakeMessageTask != null) {
                fakeMessageTask.stop();
            }
            fakeMessageTask = new SerialManager.FakeMessageTask(serMgr().getMessageType()) {
                @Override
                public void processMessage(SerialMessage serialMessage) {
                    onReceived(serialMessage);
                }
            };
            fakeMessageTask.execute();
        });
    }

    private void openSettings() {
        SerialSettingsDialog dialog = new SerialSettingsDialog(
                Application.this, "Settings", serMgr().getSerialPort(), messagePanel);

        if (dialog.showDialog() == IDialog.OK) {
            beginWait();
            try {
                SerialPort port = dialog.getSerialPort();
                if (port != null) {
                    boolean open = serMgr().open(port);
                    if (open) {
                        messagePanel.updateComponents();
                        //startFakeMessages();
                    }
                    messagePanel.setEnabled(open);
                    SerialManager.MessageTypes type = serMgr().getMessageType();
                    if (type == SerialManager.MessageTypes.Text) {
                        picMessageInfoPanel.setVisible(false);
                    }
                    messagePanel.setMessageType(type);
                }
                updateStatus(port);
            } finally {
                endWait();
            }
        }
    }

    //
    // Gui interface
    //
    @Override
    public void initializeComponents() {
        // Icon
        try {
            Image image = resMgr.readImage("Main.Icon").getImage();
            setIconImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stuff
        statusLbl = new ILabel();
        infoLbl = new ILabel();
        sessionLbl = new ILabel();
        sessionLbl.setFont(20, Font.BOLD);
        sessionLbl.setHorizontalAlignment(SwingConstants.CENTER);

        messagePanel = new MessagePanel() {
            @Override
            public void selectedMessageChanged(SerialMessage message) {
                if (message != null && message.getMessageType() != SerialManager.MessageTypes.Text) {
                    picMessageInfoPanel.updateComponents(message);
                }
            }
        };
        inputPanel = new InputPanel();
        picMessageInfoPanel = new PicMessageInfoPanel();
        picMessageInfoPanel.setVisible(false);

        settingsActions = new AbstractAction("Settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettings();
            }
        };

        clearAction = new AbstractAction("Clear") {
            @Override
            public void actionPerformed(ActionEvent e) {
                messagePanel.clearMessagePane();
            }
        };
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        add(createToolbarPanel(), BorderLayout.PAGE_START);
        add(messagePanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(picMessageInfoPanel, BorderLayout.EAST);
    }

    @Override
    public void updateComponents(Object... objects) {
        updateStatus(null);
        SwingUtilities.invokeLater(this::openSettings);
    }

    //
    // Serial listener
    //
    @Override
    public void onSerialError(String error, Throwable throwable) {
        String message = "";
        if (error != null) {
            message = error;
        }
        if (throwable != null) {
            if (!message.isEmpty()) {
                message += "\r\n Exception: \n";
            }
            message += throwable.toString();
        }

        JOptionPane.showMessageDialog(
                Application.this,
                message,
                "Serial error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void onTransmitted(SerialMessage message) {
        if (message != null) {
            messagePanel.addTransmittedMessage(message);
            inputPanel.clearInput();
        }
    }

    @Override
    public void onReceived(SerialMessage message) {
        if (message != null) {
            messagePanel.addReceivedMessage(message);
        }
    }
}
