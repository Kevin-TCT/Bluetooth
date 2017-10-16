package com.kevin.bluetooth.bluetooth;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Administrator on 2017/10/13 0013.
 */

public class ScanTimeoutHandler extends Handler {

    public static final int OUT_TIME_MILLIS = 15000;

    public static final int WHAT_OUT_TIME = 1;

    private WeakReference<BaseBluetooth> reference;

    public ScanTimeoutHandler(BaseBluetooth bluetooth) {
        reference = new WeakReference<BaseBluetooth>(bluetooth);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        android.util.Log.d("Bluetooth", "------ScanTimeoutHandler time out-----");
        if (null != reference && null != reference.get()) {
            reference.get().scanTimeout();
        }
    }
}
