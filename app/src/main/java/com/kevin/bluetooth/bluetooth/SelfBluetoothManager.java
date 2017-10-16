package com.kevin.bluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Administrator on 2017/10/13 0013.
 */

public class SelfBluetoothManager {

    private BluetoothAdapter mBtAdapter;
    private boolean isSupportBle;
    private BaseBluetooth bluetooth;

    public SelfBluetoothManager(Context context) {
        if (Build.VERSION.SDK_INT >= 18) {
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBtAdapter = manager.getAdapter();
        } else {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        // 判断是否支持BLE
        isSupportBle = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (isSupportBle) {
            bluetooth = new BluetoothLE(context, mBtAdapter);
        } else {
            bluetooth = new BluetoothStandard();
        }
    }

    public boolean getBluetoothStatus() {
        return mBtAdapter.isEnabled();
    }

    public void changeBluetoothStatus(boolean enable) {
        if (enable) {
            // 隐式开启
            mBtAdapter.enable();
            /*// 显式开启
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);*/
        } else {
            mBtAdapter.disable();
        }
    }

    public void startScan() {
        if (null != bluetooth) {
            bluetooth.startScan();
        }
    }

    public void stopScan() {
        if (null != bluetooth) {
            bluetooth.stopScan();
        }
    }

    public boolean isSupportBle() {
        return isSupportBle;
    }
}
