package com.kevin.bluetooth.bluetooth;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Administrator on 2017/10/13 0013.
 */

public class ScanTimeoutHandler extends Handler {

    private static final int OUT_TIME = 1500;

    private WeakReference<BaseBluetooth> reference;

    public ScanTimeoutHandler(BaseBluetooth bluetooth) {
        reference = new WeakReference<BaseBluetooth>(bluetooth);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (null != reference && null != reference.get()) {
            reference.get().scanTimeout();
        }
    }
}
