package com.waldo.serial.gui.dialogs.serialsettingsdialog;

import com.fazecast.jSerialComm.SerialPort;
import com.waldo.serial.classes.SerialManager;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.waldo.serial.classes.SerialManager.serMgr;

class PortSettingsPanel extends IPanel {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Port config
    private final DefaultComboBoxModel<String> comPortCbModel = new DefaultComboBoxModel<>();
    private final IComboBox<String> comPortCb = new IComboBox<>(comPortCbModel);

    private final DefaultComboBoxModel<SerialManager.BaudRateValues> baudCbModel = new DefaultComboBoxModel<>(SerialManager.BaudRateValues.values());
    private final IComboBox<SerialManager.BaudRateValues> baudCb = new IComboBox<>(baudCbModel);

    private final DefaultComboBoxModel<SerialManager.DataBitValues> dataBitsCbModel = new DefaultComboBoxModel<>(SerialManager.DataBitValues.values());
    private final IComboBox<SerialManager.DataBitValues> dataBitsCb = new IComboBox<>(dataBitsCbModel);

    private final DefaultComboBoxModel<SerialManager.StopBitValues> stopBitsCbModel = new DefaultComboBoxModel<>(SerialManager.StopBitValues.values());
    private final IComboBox<SerialManager.StopBitValues> stopBitsCb = new IComboBox<>(stopBitsCbModel);

    private final DefaultComboBoxModel<SerialManager.ParityTypes> parityCbModel = new DefaultComboBoxModel<>(SerialManager.ParityTypes.values());
    private final IComboBox<SerialManager.ParityTypes> parityCb = new IComboBox<>(parityCbModel);

    private final DefaultComboBoxModel<SerialManager.FlowControlTypes> flowControlCbModel = new DefaultComboBoxModel<>(SerialManager.FlowControlTypes.values());
    private final IComboBox<SerialManager.FlowControlTypes> flowControlTypesCb = new IComboBox<>(flowControlCbModel);

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private final IDialog parent;
    private SerialPort selectedPort;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    SerialPort getSelectedPort() {
        return selectedPort;
    }

    PortSettingsPanel(final IDialog parent) {
        this.parent = parent;
        initializeComponents();
        initializeLayouts();
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void updateComPortCb() {
        if (selectedPort == null) {
            serMgr().getSerialPorts();
        }
        if (selectedPort != null) {
            comPortCb.selectItem(selectedPort.getSystemPortName());
        }

    }

    private void updateBaudCb() {
        if (selectedPort != null) {
            baudCb.setSelectedItem(SerialManager.BaudRateValues.fromBaud(selectedPort.getBaudRate()));
        }
    }

    SerialManager.BaudRateValues getSelectedBaudRate() {
        return (SerialManager.BaudRateValues) baudCb.getSelectedItem();
    }

    private void updateDataBitsCb() {
        if (selectedPort != null) {
            dataBitsCb.setSelectedItem(SerialManager.DataBitValues.fromBits(selectedPort.getNumDataBits()));
        }
    }

    SerialManager.DataBitValues getSelectedDataBitValue() {
        return (SerialManager.DataBitValues) dataBitsCb.getSelectedItem();
    }

    private void updateStopBitsCb() {
        if (selectedPort != null) {
            stopBitsCb.setSelectedItem(SerialManager.StopBitValues.fromStopBits(selectedPort.getNumStopBits()));
        }
    }

    SerialManager.StopBitValues getSelectedStopBitsValue() {
        return (SerialManager.StopBitValues) stopBitsCb.getSelectedItem();
    }

    private void updateParityCb() {
        if (selectedPort != null) {
            parityCb.setSelectedItem(SerialManager.ParityTypes.fromInt(selectedPort.getParity()));
        }
    }

    SerialManager.ParityTypes getSelectedParityType() {
        return (SerialManager.ParityTypes) parityCb.getSelectedItem();
    }

    private void updateFlowControlCb() {
        if (selectedPort != null) {

        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        List<SerialPort> portList = serMgr().getSerialPorts();
        if (portList != null && portList.size() > 0) {
            for (SerialPort port : portList) {
                comPortCbModel.addElement(port.getSystemPortName());
            }
            selectedPort = portList.get(0);
        }
        comPortCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !parent.isUpdating()) {
                String port = (String) e.getItem();
                updateComponents(serMgr().findSerialPortByName(port));
            }
        });
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(panel);
        gbc.addLine("Port: ", comPortCb);
        gbc.addLine("Baud: ", baudCb);
        gbc.addLine("Data bits: ", dataBitsCb);
        gbc.addLine("Stop bits: ", stopBitsCb);
        gbc.addLine("Parity: ", parityCb);
        gbc.addLine("Flow control: ", flowControlTypesCb);

        add(panel);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length > 0 && args[0] != null) {
            selectedPort = (SerialPort) args[0];
        }
        if (!parent.isUpdating()) {
            parent.beginWait();
            try {
                updateComPortCb();
                updateBaudCb();
                updateDataBitsCb();
                updateStopBitsCb();
                updateParityCb();
                updateFlowControlCb();
            } finally {
                parent.endWait();
            }
        }
    }
}
