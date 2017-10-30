package com.kevin.bluetooth.bluetooth.standard;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import com.kevin.bluetooth.bluetooth.BaseBluetooth;
import com.kevin.bluetooth.bluetooth.ResultListener;
import com.kevin.bluetooth.bluetooth.ScanTimeoutHandler;
import com.kevin.bluetooth.util.SharedPreferenceUtils;

import java.util.Set;

/**
 * 经典蓝牙
 */

public class BluetoothStandard extends BaseBluetooth implements BluetoothScanBroadcast.Callback {

    private static final String SP_LAST_CONNECTED = "last_connected";
    private static final String SP_LAST_CONNECTED_DEVICE_NAME = "last_connected_device_name";
    private static final String SP_LAST_CONNECTED_DEVICE_ADDRESS = "last_connected_device_address";

    private BluetoothAdapter mBtAdapter;
    private Context mContext;
    private ScanTimeoutHandler timeoutHandler;
    private BluetoothDevice mConnectedDevice;
    private BluetoothScanBroadcast broadcast;
    private BluetoothConnect bluetoothConnect;
    private ResultListener listener;

    public BluetoothStandard(Context context, BluetoothAdapter bluetoothAdapter, ResultListener listener) {
        this.mContext = context;
        this.mBtAdapter = bluetoothAdapter;
        timeoutHandler = new ScanTimeoutHandler(this);
        bluetoothConnect = new BluetoothConnect();
        this.listener = listener;
    }

    private void registerBroadcast() {
        if (null == broadcast) {
            broadcast = new BluetoothScanBroadcast(this);
            mContext.registerReceiver(broadcast, BluetoothScanBroadcast.getIntentFilter());
        }
    }

    private void unRegisterBroadcast() {
        if (null != broadcast) {
            mContext.unregisterReceiver(broadcast);
            broadcast = null;
        }
    }

    @Override
    public void startScan() {
        registerBroadcast();
        mConnectedDevice = getHistoryDevice();
        if (null != mConnectedDevice) {
            // 连接
            connectDevice(mConnectedDevice);
        } else {
            scan();
        }
    }

    private void scan() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        timeoutHandler.removeMessages(ScanTimeoutHandler.WHAT_OUT_TIME);
        mBtAdapter.startDiscovery();
        timeoutHandler.sendEmptyMessageDelayed(ScanTimeoutHandler.WHAT_OUT_TIME, ScanTimeoutHandler.OUT_TIME_MILLIS);
    }

    @Override
    public void stopScan() {

    }

    @Override
    public void scanTimeout() {
        listener.scanTimeout();
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
    }

    @Override
    public void connectDevice(BluetoothDevice device) {
        bluetoothConnect.connectDevice(device);
    }

    @Override
    public void disConnectedDevice(BluetoothDevice device) {

    }

    @Override
    public void stop() {

    }

    /**
     * 获取历史设备，缓存起来的设备
     *
     * @return
     */
    private BluetoothDevice getHistoryDevice() {// 从手机中取出已经保存的蓝牙
        BluetoothDevice device = null;
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice historyDevice : pairedDevices) {
                if (historyDevice.getName() != null) {// 避免因name=null引起崩溃，而走到此的称名称必然为DUAL-SPP
                    String lastConnectedDevicesName = (String) SharedPreferenceUtils.getValue(mContext, SP_LAST_CONNECTED, SP_LAST_CONNECTED_DEVICE_NAME,
                            String.class);
                    String lastConnectedDevicesAddress = (String) SharedPreferenceUtils.getValue(mContext, SP_LAST_CONNECTED,
                            SP_LAST_CONNECTED_DEVICE_ADDRESS, String.class);
                    if ((historyDevice.getName().equals(lastConnectedDevicesName) && historyDevice.getAddress().equals(lastConnectedDevicesAddress))) {
                        device = mBtAdapter.getRemoteDevice(historyDevice.getAddress());
                        break;
                    }
                }
            }
        }
        return device;
    }

    @Override
    public void scanOver() {
        listener.scanFailed();
    }

    @Override
    public void findDevice(BluetoothDevice device) {
        listener.scannedDevice(device);
    }
}
