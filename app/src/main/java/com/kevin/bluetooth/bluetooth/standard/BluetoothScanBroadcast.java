package com.kevin.bluetooth.bluetooth.standard;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Kevin-Tu on 2017/10/30 0030.
 */

public class BluetoothScanBroadcast extends BroadcastReceiver {

    private static final String TAG = BluetoothScanBroadcast.class.getSimpleName();

    private Callback callback;
    private boolean isFindDevice = false;

    public BluetoothScanBroadcast(Callback callback) {
        this.callback = callback;
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        android.util.Log.d(TAG, "onReceive() action: " + action);
        switch (action) {
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                android.util.Log.d(TAG, "BluetoothDevice.ACTION_FOUND   device：" + device);
                if (null != device) {
                    callback.findDevice(device);
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                if (!isFindDevice) {
                    callback.scanOver();
                }
                isFindDevice = false;
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                break;
        }
    }

    public interface Callback {

        /**
         * 扫描结束，但是没有扫描到设备
         */
        void scanOver();

        void findDevice(BluetoothDevice device);
    }
}
