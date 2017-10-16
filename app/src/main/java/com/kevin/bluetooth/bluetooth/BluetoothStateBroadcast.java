package com.kevin.bluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Kevin-Tu on 2017/10/16 0016.
 */

public class BluetoothStateBroadcast extends BroadcastReceiver {

    private static final String TAG = BluetoothStateBroadcast.class.getSimpleName();

    private BlueStateListener listener;

    public BluetoothStateBroadcast(BlueStateListener listener) {
        this.listener = listener;
    }

    public IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        android.util.Log.e(TAG, "onReceive---------STATE_TURNING_ON");
                        listener.bluetoothOn();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        android.util.Log.e(TAG, "onReceive---------STATE_ON");
                        listener.bluetoothOn();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        android.util.Log.e(TAG, "onReceive---------STATE_TURNING_OFF");
                        listener.bluetoothOff();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        android.util.Log.e(TAG, "onReceive---------STATE_OFF");
                        listener.bluetoothOff();
                        break;
                }
                break;
        }
    }

    public interface BlueStateListener {

        void bluetoothOn();

        void bluetoothOff();
    }
}
