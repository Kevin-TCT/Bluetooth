package com.kevin.bluetooth.viewmodel;

import android.bluetooth.BluetoothDevice;

/**
 * Administrator on 2017/10/9.
 */

public interface MainActView extends BaseView {

    void scannedDevice(BluetoothDevice device);

    void stopScan();

    void scanTimeout();

    void scanFailed();

    void onConnectFail();

    void onConnectSuccess();

    void onDisConnected();
}
