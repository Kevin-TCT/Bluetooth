package com.kevin.bluetooth.bluetooth;

/**
 * Bluetooth Low Energy 低功耗蓝牙
 */

public class BLEBluetooth extends BaseBluetooth {

    private ScanTimeoutHandler timeoutHandler;

    public BLEBluetooth() {
        timeoutHandler = new ScanTimeoutHandler(this);
    }

    @Override
    public void startScan() {

    }

    @Override
    public void stopScan() {

    }

    @Override
    public void scanTimeout() {

    }
}
