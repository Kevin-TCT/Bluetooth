package com.kevin.bluetooth.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Kevin-Tu on 2017/10/16 0016.
 */

public interface ResultListener {

    void scannedDevice(BluetoothDevice device);

    void scanTimeout();

    void scanFailed();
}
