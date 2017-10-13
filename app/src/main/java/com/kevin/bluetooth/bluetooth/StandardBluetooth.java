package com.kevin.bluetooth.bluetooth;

/**
 * 经典蓝牙
 */

public class StandardBluetooth extends BaseBluetooth {

    private ScanTimeoutHandler timeoutHandler;

    public StandardBluetooth() {
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
