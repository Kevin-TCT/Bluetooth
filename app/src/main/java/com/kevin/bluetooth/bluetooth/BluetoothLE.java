package com.kevin.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.text.format.DateUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Bluetooth Low Energy 低功耗蓝牙
 */

@SuppressLint("NewApi")
public class BluetoothLE extends BaseBluetooth {

    private static final String TAG = BluetoothLE.class.getSimpleName();

    private Context mContext;
    private BluetoothAdapter mBtAdapter;
    private ScanTimeoutHandler timeoutHandler;
    private BLEService bleService;
    private ScanCallback scanCallback;
    private LeScanCallback leScanCallback;

    public BluetoothLE(Context context, BluetoothAdapter btAdapter) {
        this.mContext = context;
        this.mBtAdapter = btAdapter;
        timeoutHandler = new ScanTimeoutHandler(this);
        Intent gattServiceIntent = new Intent(context, BLEService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void startScan() {
        // TODO 需要加上状态判断，避免重复扫描，或已经连接后再扫描，根据业务逻辑来
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 在android5.0及之后，google关于BLE的搜索提供了新的方法
            BluetoothLeScanner scanner = mBtAdapter.getBluetoothLeScanner();
            if (null == scanCallback) {
                scanCallback = new MyScanCallback(this);
            }
            scanner.startScan(scanCallback);
        } else {
            if (null == leScanCallback) {
                leScanCallback = new MyLeScanCallback(this);
            }
            mBtAdapter.startLeScan(leScanCallback);
        }
        status = STATE_SCANNING;
    }

    @Override
    public void stopScan() {
        if (status == STATE_SCANNING) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothLeScanner scanner = mBtAdapter.getBluetoothLeScanner();
                scanner.stopScan(scanCallback);
            } else {
                mBtAdapter.stopLeScan(leScanCallback);
            }
            status = STATE_DISCONNECTED;
        }
    }

    @Override
    public void scanTimeout() {

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            android.util.Log.d(TAG, "-------onServiceConnected()-------");
            bleService = ((BLEService.BluetoothBinder) service).getService();
            android.util.Log.d(TAG, "BLEService = " + bleService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            android.util.Log.d(TAG, "-------onServiceDisconnected()-------");
            bleService = null;
        }
    };

    static class MyScanCallback extends ScanCallback {
        private WeakReference<BluetoothLE> reference;

        public MyScanCallback(BluetoothLE bluetoothLE) {
            reference = new WeakReference<>(bluetoothLE);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            android.util.Log.d(TAG, "-----onScanResult()----ScanResult: " + result);
            if (null == reference || null == reference.get()) {
                return;
            }
            final BluetoothDevice device = result.getDevice();

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }
    }

    static class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        private WeakReference<BluetoothLE> reference;

        public MyLeScanCallback(BluetoothLE bluetoothLE) {
            reference = new WeakReference<>(bluetoothLE);
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            android.util.Log.d("bluetooth", "-----onLeScan()------device: " + device);
            if (null != reference || null != reference.get()) {

            }
        }
    }
}
