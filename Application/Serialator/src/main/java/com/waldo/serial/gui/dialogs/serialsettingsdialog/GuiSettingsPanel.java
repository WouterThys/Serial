package com.waldo.serial.gui.dialogs.serialsettingsdialog;

import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.ICheckBox;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;

public class GuiSettingsPanel extends IPanel {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField sessionNameTf;
    private ICheckBox appendWithNewLineCb;
    private JButton txColorBtn;
    private JButton rxColorBtn;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private Color txColor;
    private Color rxColor;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    GuiSettingsPanel() {
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public boolean isAppendWithNewLine() {
        return appendWithNewLineCb.isSelected();
    }

    public Color getRxColor() {
        return rxColor;
    }

    public Color getTxColor() {
        return txColor;
    }

    public String getSessionName() {
        return sessionNameTf.getText();
    }

    private void setTxColor(Color txColor) {
        if (txColor != null) {
            this.txColor = txColor;
            txColorBtn.setBackground(txColor);
        }
    }

    private void setRxColor(Color rxColor) {
        if (rxColor != null) {
            this.rxColor = rxColor;
            rxColorBtn.setBackground(rxColor);
        }
    }

    /*
             *                  LISTENERS
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        sessionNameTf = new ITextField();

        appendWithNewLineCb = new ICheckBox();
        txColorBtn = new JButton();
        rxColorBtn = new JButton();

        txColorBtn.addActionListener(e -> {
            Color tx = JColorChooser.showDialog(GuiSettingsPanel.this, "TX color", txColor);
            setTxColor(tx);
        });

        rxColorBtn.addActionListener(e -> {
            Color rx = JColorChooser.showDialog(GuiSettingsPanel.this, "RX color", rxColor);
            setRxColor(rx);
        });
    }

    @Override
    public void initializeLayouts() {

        JPanel sessionPanel = new JPanel();
        JPanel panel = new JPanel();
        GuiUtils.GridBagHelper gbc;

        gbc = new GuiUtils.GridBagHelper(sessionPanel);
        gbc.addLineVertical("Session name", sessionNameTf);

        gbc = new GuiUtils.GridBagHelper(panel, 160);
        gbc.addLine("Append new line: ", appendWithNewLineCb);
        gbc.addLine("TX msg color: ", txColorBtn);
        gbc.addLine("RX mmsg color: ", rxColorBtn);

        setLayout(new BorderLayout());

        add(sessionPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0) {
            appendWithNewLineCb.setSelected((Boolean) args[0]);
            setTxColor((Color) args[1]);
            setRxColor((Color) args[2]);
        }
    }
}
