package com.waldo.serial.gui;

import com.waldo.serial.classes.Message.SerialMessage;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextArea;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;

import java.awt.*;

import static com.waldo.serial.gui.Application.resMgr;

public class PicMessageInfoPanel extends IPanel {

    public static final ImageIcon greenBall = resMgr.readImage("Ball.green");
    public static final ImageIcon yellowBall = resMgr.readImage("Ball.yellow");
    public static final ImageIcon redBall = resMgr.readImage("Ball.red");

    public static final ImageIcon txIcon = resMgr.readImage("Message.Tx");
    public static final ImageIcon rxIcon = resMgr.readImage("Message.Rx");

    // State icons
    private ILabel isRxLbl;
    private ILabel isConvertedLbl;
    private ILabel isAckLbl;

    // Info
    private ITextField inputTf;
    private ITextField commandTf;
    private ITextField messageTf;
    private ITextField ackIdTf;

    // Errors
    private ITextArea errorTa;


    public PicMessageInfoPanel() {
        initializeComponents();
        initializeLayouts();
    }

    private void updateStatus(SerialMessage message) {
        if (message != null) {
            isRxLbl.setIcon(message.isRx() ? rxIcon : txIcon);
            isConvertedLbl.setIcon(message.isConverted() ? greenBall : redBall);
            isAckLbl.setIcon(message.isAcknowledged() ? greenBall : redBall);
        } else {
            isRxLbl.setIcon(null);
            isConvertedLbl.setIcon(redBall);
            isAckLbl.setIcon(redBall);
        }
    }

    private void updateInfo(SerialMessage message) {
        if (message != null) {
            inputTf.setText(message.getInput());
            commandTf.setText(message.getCommand());
            messageTf.setText(message.getMessage());
            ackIdTf.setText(String.valueOf(message.getAckId()));
            errorTa.setText("");
        } else {
            inputTf.setText("");
            commandTf.setText("");
            messageTf.setText("");
            ackIdTf.setText("");
            errorTa.setText("");
        }
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        //panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.weightx = 1;

        gbc.gridx = 0;
        panel.add(isRxLbl, gbc);
        gbc.gridx = 1;
        panel.add(isConvertedLbl, gbc);
        gbc.gridx = 2;
        panel.add(isAckLbl, gbc);

        panel.setOpaque(true);
        panel.setBackground(Color.gray);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel dataPnl = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(dataPnl);
        gbc.addLine("Input: ", inputTf);
        gbc.addLine("Command: ", commandTf);
        gbc.addLine("Message: ", messageTf);
        gbc.addLine("Ack id: ", ackIdTf);

        panel.add(dataPnl, BorderLayout.NORTH);
        panel.add(new JScrollPane(errorTa), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel();

        return panel;
    }

    //
    // Gui listener
    //
    @Override
    public void initializeComponents() {
        isRxLbl = new ILabel(redBall);
        isConvertedLbl = new ILabel(redBall);
        isAckLbl = new ILabel(redBall);

        isRxLbl.setToolTipText("Rx or Tx");
        isConvertedLbl.setToolTipText("Converted");
        isAckLbl.setToolTipText("Acknowledged");

        inputTf = new ITextField(false);
        commandTf = new ITextField(false);
        messageTf = new ITextField(false);
        ackIdTf = new ITextField(false);

        errorTa = new ITextArea(false);
        errorTa.setWrapStyleWord(true);
    }

    @Override
    public void initializeLayouts() {
        JPanel statusPanel = createStatusPanel();
        JPanel contentPanel = createContentPanel();
        JPanel toolbarPanel = createToolbarPanel();

        setLayout(new BorderLayout());
        add(statusPanel, BorderLayout.PAGE_START);
        add(contentPanel, BorderLayout.CENTER);
        add(toolbarPanel, BorderLayout.PAGE_END);
    }

    @Override
    public void updateComponents(Object... objects) {
        SerialMessage message = null;
        if (objects.length > 0 && objects[0] != null) {
            message = (SerialMessage) objects[0];
            if (!isVisible()) {
                setVisible(true);
            }
        }
        updateStatus(message);
        updateInfo(message);
    }
}