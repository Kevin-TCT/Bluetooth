package com.kevin.bluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.kevin.bluetooth.bluetooth.ble.BluetoothLE;
import com.kevin.bluetooth.bluetooth.standard.BluetoothStandard;

import java.util.List;

/**
 * Administrator on 2017/10/13 0013.
 */

public class SelfBluetoothManager {

    private BluetoothAdapter mBtAdapter;
    private boolean isSupportBle;
    private BaseBluetooth bluetooth;

    public SelfBluetoothManager(Context context, ResultListener listener, BaseBluetooth.BlueHandleCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBtAdapter = manager.getAdapter();
        } else {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        //mBtAdapter.getBondedDevices()
        // 判断是否支持BLE
        //isSupportBle = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        isSupportBle = false;
        if (isSupportBle) {
            bluetooth = new BluetoothLE(context, mBtAdapter, listener, callback);
        } else {
            bluetooth = new BluetoothStandard(context, mBtAdapter, listener);
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

    public boolean isSupportBle() {
        return isSupportBle;
    }

    public void startScan() {
        if (mBtAdapter.isEnabled()) {
            if (null != bluetooth) {
                bluetooth.startScan();
            }
        }
    }

    public void stopScan() {
        if (mBtAdapter.isEnabled()) {
            if (null != bluetooth) {
                bluetooth.stopScan();
            }
        }
    }

    public void connectDevice(BluetoothDevice device) {
        if (mBtAdapter.isEnabled()) {
            if (null != bluetooth) {
                bluetooth.connectDevice(device);
            }
        }
    }

    public void disConnectedDevice(BluetoothDevice device) {
        if (mBtAdapter.isEnabled()) {
            if (null != bluetooth) {
                bluetooth.disConnectedDevice(device);
            }
        }
    }

    public void stop() {

    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (null != bluetooth && bluetooth instanceof BluetoothLE) {
            return ((BluetoothLE) bluetooth).getSupportedGattServices();
        }
        return null;
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable, byte[] descriptorValue, String uuidStr) {
        if (null != bluetooth && bluetooth instanceof BluetoothLE) {
            ((BluetoothLE) bluetooth).setCharacteristicNotification(characteristic, enable, descriptorValue, uuidStr);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (null != bluetooth && bluetooth instanceof BluetoothLE) {
            ((BluetoothLE) bluetooth).writeCharacteristic(characteristic);
        }
    }
}
