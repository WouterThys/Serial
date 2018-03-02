package com.waldo.serial.classes;

import com.waldo.serial.classes.Message.SerialMessage;

public interface SerialListener {
    void onSerialError(String error, Throwable throwable);
    void onTransmitted(SerialMessage serialMessage);
    void onReceived(SerialMessage serialMessage);
}
