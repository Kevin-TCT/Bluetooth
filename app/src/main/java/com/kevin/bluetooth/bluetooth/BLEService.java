package com.kevin.bluetooth.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Administrator on 2017/10/13 0013.
 */

public class BLEService extends Service {

    public BluetoothBinder mBinder = new BluetoothBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class BluetoothBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }
}
