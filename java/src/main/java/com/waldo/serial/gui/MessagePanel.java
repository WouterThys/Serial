package com.waldo.serial.gui;

import com.waldo.serial.classes.Message.SerialMessage;
import com.waldo.serial.classes.SerialManager;
import com.waldo.serial.gui.components.IPICMessageTableModel;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITable;
import com.waldo.utils.icomponents.ITextPane;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public abstract class MessagePanel extends IPanel implements IMessagePanelListener {

    private static final String TABLE_TYPE = "TABLE";
    private static final String PANE_TYPE = "PANE_TYPE";

    private final ITextPane messagePane = new ITextPane();
    private final Style messageStyle = messagePane.addStyle("MessageStyle", messagePane.getLogicalStyle());
    private final StyledDocument messageDoc = messagePane.getStyledDocument();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private IPICMessageTableModel tableModel;
    private ITable<SerialMessage> messageTable;

    // Settings
    private boolean appendWithNewLine = true;
    private Color txColor = new Color(0, 0, 100);
    private Color rxColor = new Color(0,100,0);

    MessagePanel() {
        initializeComponents();
        initializeLayouts();

        updateComponents();
    }

    public boolean isAppendWithNewLine() {
        return appendWithNewLine;
    }

    public void setAppendWithNewLine(boolean appendWithNewLine) {
        this.appendWithNewLine = appendWithNewLine;
    }

    public Color getTxColor() {
        return txColor;
    }

    public void setTxColor(Color txColor) {
        this.txColor = txColor;
    }

    public Color getRxColor() {
        return rxColor;
    }

    public void setRxColor(Color rxColor) {
        this.rxColor = rxColor;
    }

    public void setMessageType(SerialManager.MessageTypes messageType) {
        if (messageType == SerialManager.MessageTypes.Text) {
            cardLayout.show(cardPanel, PANE_TYPE);
        } else {
            cardLayout.show(cardPanel, TABLE_TYPE);
        }
    }

    @Override
    public void clearMessagePane() {
        messagePane.setText("");
        tableModel.clearItemList();
    }

    @Override
    public void addReceivedMessage(SerialMessage message) {
        if (message != null) {
            if (message.getMessageType() == SerialManager.MessageTypes.Text) {
                StyleConstants.setForeground(messageStyle, rxColor);
                try {
                    String m = message.getInput();
                    if (appendWithNewLine && !m.isEmpty() && !m.endsWith("\n")) {
                        m += "\n";
                    }
                    messageDoc.insertString(messageDoc.getLength(), m, messageStyle);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else {
                SerialMessage selected = messageTable.getSelectedItem();
                tableModel.addItem(message);
                if (selected == null) {
                    selected = message;
                }
                messageTable.selectItem(selected);
            }
        }
    }

    @Override
    public void addTransmittedMessage(SerialMessage message) {
        if (message != null) {
            if (message.getMessageType() == SerialManager.MessageTypes.Text) {
                StyleConstants.setForeground(messageStyle, txColor);
                try {
                    String m = message.getInput();
                    if (appendWithNewLine && !m.isEmpty() && !m.endsWith("\n")) {
                        m += "\n";
                    }
                    messageDoc.insertString(messageDoc.getLength(), m, messageStyle);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else {
                SerialMessage selected = messageTable.getSelectedItem();
                tableModel.addItem(message);
                if (selected == null) {
                    selected = message;
                }
                messageTable.selectItem(selected);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        messagePane.setEnabled(enabled);
        messageTable.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    //
    // Gui listener
    //
    @Override
    public void initializeComponents() {
        messagePane.setEditable(false);
        DefaultCaret caret = (DefaultCaret) messagePane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        StyleConstants.setBold(messageStyle, true);

        tableModel = new IPICMessageTableModel() {
            @Override
            public Color getTxColor() {
                return txColor;
            }

            @Override
            public Color getRxColor() {
                return rxColor;
            }
        };
        messageTable = new ITable<>(tableModel);
        messageTable.setFillsViewportHeight(true);
        messageTable.getSelectionModel().addListSelectionListener(e -> selectedMessageChanged(messageTable.getSelectedItem()));

        setEnabled(false);
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(messagePane);
        JScrollPane scrollTable = new JScrollPane(messageTable);

        JPanel panePnl = new JPanel(new BorderLayout());
        JPanel tablePnl = new JPanel(new BorderLayout());
        panePnl.add(scrollPane, BorderLayout.CENTER);
        tablePnl.add(scrollTable, BorderLayout.CENTER);

        cardPanel.add(PANE_TYPE, panePnl);
        cardPanel.add(TABLE_TYPE, tablePnl);

        add(cardPanel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... objects) {
        cardLayout.show(cardPanel, PANE_TYPE);
    }
}
