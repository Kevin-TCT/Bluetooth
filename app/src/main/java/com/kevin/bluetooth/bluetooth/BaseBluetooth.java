package com.kevin.bluetooth.bluetooth;


public abstract class BaseBluetooth {

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_SCANNING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_SERVICES_DISCOVERED = 4;

    public int status = STATE_DISCONNECTED;

    public abstract void startScan();

    public abstract void stopScan();

    public abstract void scanTimeout();
}
