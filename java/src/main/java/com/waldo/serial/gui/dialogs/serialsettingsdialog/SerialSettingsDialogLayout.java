package com.waldo.serial.gui.dialogs.serialsettingsdialog;

import com.fazecast.jSerialComm.SerialPort;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;

import static com.waldo.serial.classes.SerialManager.serMgr;


abstract class SerialSettingsDialogLayout extends IDialog {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    PortSettingsPanel portSettingsPanel;
    ManagerSettingsPanel managerSettingsPanel;
    GuiSettingsPanel guiSettingsPanel;

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SerialSettingsDialogLayout(Window parent, String title) {
        super(parent, title);
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        portSettingsPanel = new PortSettingsPanel(this);
        managerSettingsPanel = new ManagerSettingsPanel();
        guiSettingsPanel = new GuiSettingsPanel();
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.X_AXIS));

        portSettingsPanel.setBorder(GuiUtils.createTitleBorder("Port config"));
        managerSettingsPanel.setBorder(GuiUtils.createTitleBorder("Manager config"));
        guiSettingsPanel.setBorder(GuiUtils.createTitleBorder("Gui settings"));

        getContentPanel().add(portSettingsPanel);
        getContentPanel().add(managerSettingsPanel);
        getContentPanel().add(guiSettingsPanel);

        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        SerialPort serialPort = null;
        if (args.length > 0 && args[0] != null) {
            serialPort = (SerialPort) args[0];
        }

        portSettingsPanel.updateComponents(serialPort);
        managerSettingsPanel.updateComponents(serMgr().getMessageType());
        if (args.length >= 4) {
            guiSettingsPanel.updateComponents(args[1], args[2], args[3]);
        }
    }
}