package com.waldo.serial.gui.dialogs.serialsettingsdialog;

import com.waldo.serial.classes.SerialManager.MessageTypes;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

class ManagerSettingsPanel extends IPanel {
    
    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final DefaultComboBoxModel<MessageTypes> messageTypeCbModel = new DefaultComboBoxModel<>(MessageTypes.values());
    private final IComboBox<MessageTypes> messageTypeCb = new IComboBox<>(messageTypeCbModel);

    private final ITextArea descriptionTa = new ITextArea(false);
    private final ITextField templateTf = new ITextField(false);

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private MessageTypes selectedMessageType;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ManagerSettingsPanel() {
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    MessageTypes getSelectedMessageType() {
        return selectedMessageType;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        descriptionTa.setBorder(templateTf.getBorder());
        descriptionTa.setEnabled(false);
        descriptionTa.setLineWrap(true);
        descriptionTa.setWrapStyleWord(true);

        messageTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedMessageType = (MessageTypes) messageTypeCb.getSelectedItem();
                if (selectedMessageType != null) {
                    descriptionTa.setText(selectedMessageType.getDescription());
                    templateTf.setText(selectedMessageType.getTemplate());
                } else {
                    descriptionTa.setText("");
                    templateTf.setText("");
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {
        final JPanel panel1 = new JPanel(new BorderLayout());
        final JPanel panel2 = new JPanel(new BorderLayout());
        final JPanel panel3 = new JPanel(new BorderLayout());
        final JPanel panel4 = new JPanel();

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel4, 10);
        gbc.addLine("", new ILabel("C = Command"));
        gbc.addLine("", new ILabel("M = SerialMessage"));
        gbc.addLine("", new ILabel("L = Length"));
        gbc.addLine("", new ILabel("A = Acknowledge"));

        panel1.add(messageTypeCb, BorderLayout.NORTH);
        panel1.add(descriptionTa, BorderLayout.CENTER);

        panel2.add(panel1, BorderLayout.CENTER);
        panel2.add(templateTf, BorderLayout.SOUTH);

        panel3.add(panel2, BorderLayout.CENTER);
        panel3.add(panel4, BorderLayout.SOUTH);

        add(panel3);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            selectedMessageType = (MessageTypes) args[0];
            messageTypeCb.setSelectedItem(selectedMessageType);
        }
    }
}
