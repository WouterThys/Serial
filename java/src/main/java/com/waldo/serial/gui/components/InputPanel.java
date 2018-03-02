package com.waldo.serial.gui.components;

import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IPanel;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.serial.classes.SerialManager.serMgr;

public class InputPanel extends IPanel {

    private final ITextField inputTf = new ITextField("Send stuff");
    private AbstractAction sendAction;


    public InputPanel() {
        super();
        initializeComponents();
        initializeLayouts();
    }


    public void clearInput() {
        inputTf.setText("");
    }

    @Override
    public void initializeComponents() {
        sendAction = new AbstractAction("Send") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String data = inputTf.getText();
                if (data != null && !data.isEmpty()) {
                    serMgr().write(data);
                }
                inputTf.requestFocus();
            }
        };

        inputTf.addActionListener(sendAction);
    }

    @Override
    public void initializeLayouts() {
        JPanel sendPnl = GuiUtils.createComponentWithActions(inputTf, sendAction);
        add(sendPnl);
    }

    @Override
    public void updateComponents(Object... objects) {

    }
}
