package com.waldo.serial.gui.components;

import com.waldo.serial.classes.Message.SerialMessage;
import com.waldo.utils.icomponents.IAbstractTableModel;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ITableIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public abstract class IPICMessageTableModel extends IAbstractTableModel<SerialMessage> {

    private static final String[] COLUMN_NAMES = {""};
    private static final Class[] COLUMN_CLASSES = {JLabel.class};

    public IPICMessageTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
    }

    public abstract Color getTxColor();
    public abstract Color getRxColor();

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SerialMessage message = getItemAt(rowIndex);
        if (message != null) {
            switch (columnIndex) {
                case -1:
                    return message;
                case 0: // Input
                    return message;
            }
        }
        return null;
    }

    @Override
    public boolean hasTableCellRenderer() {
        return true;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public DefaultTableCellRenderer getTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof SerialMessage) {

                    SerialMessage message = (SerialMessage) value;
                    ILabel lbl = new ITableIcon(c.getBackground(), row, isSelected, message.getInput());
                    lbl.setFont(Font.BOLD);

                    if (message.isRx()) {
                        if (isSelected) {
                            lbl.setForeground(Color.WHITE);
                        } else {
                            lbl.setForeground(getRxColor());
                        }
                    } else {
                        if (isSelected) {
                            lbl.setForeground(Color.WHITE);
                        } else {
                            lbl.setForeground(getTxColor());
                        }
                    }

                    return lbl;
                }
                return c;
            }
        };
    }
}
