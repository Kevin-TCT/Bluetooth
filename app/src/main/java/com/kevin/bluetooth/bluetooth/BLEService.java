package com.kevin.bluetooth.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Administrator on 2017/10/13 0013.
 */

@SuppressLint("NewApi")
public class BLEService extends Service {

    private static final String TAG = BLEService.class.getSimpleName();

    private static final int WHAT_DISCOVER_SERVICE = 1;
    private static final int WHAT_DISCOVER_SERVICE_TIMEOUT = 2;

    public BluetoothBinder mBinder = new BluetoothBinder();
    private BluetoothGatt bluetoothGatt;
    private BaseBluetooth.BlueHandleCallback callback;
    private BaseBluetooth.BlueStateListener blueStateListener;
    private int blueState = BluetoothProfile.STATE_DISCONNECTED;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class BluetoothBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    public void setCallback(BaseBluetooth.BlueHandleCallback callback) {
        this.callback = callback;
    }

    public void setBlueStateListener(BaseBluetooth.BlueStateListener blueStateListener) {
        this.blueStateListener = blueStateListener;
    }

    /**
     * 更新Bluetooth类中蓝牙的状态
     *
     * @param state
     */
    private void updateBlueState(int state) {
        if (null != blueStateListener) {
            blueStateListener.updateBlueState(state);
        }
    }

    public void connectDevice(final BluetoothDevice device) {
        new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                bluetoothGatt = device.connectGatt(BLEService.this, false, gattCallback);
            }
        });
    }

    public void disConnectedDevice() {
        if (bluetoothGatt != null && bluetoothGatt.connect()) {
            new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    bluetoothGatt.disconnect();
                }
            });
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (null == bluetoothGatt) {
            return null;
        }
        return bluetoothGatt.getServices();
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable, byte[] descriptorValue, String uuidStr) {
        if (null != bluetoothGatt) {
            bluetoothGatt.setCharacteristicNotification(characteristic, enable);
            // TODO 验证下这里是否可以使用Characteristic中的UUID
            android.util.Log.v(TAG, "characteristic.getUuid(): " + characteristic.getUuid());
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(uuidStr));
            descriptor.setValue(descriptorValue);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (null != bluetoothGatt) {
            bluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            android.util.Log.v(TAG, "onConnectionStateChange()  gatt: " + gatt + "  status:" + status + "  newState:" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) { // 成功连接
                    blueState = newState;
                    updateBlueState(BaseBluetooth.STATE_CONNECTED);
                    if (null != callback) {
                        new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onConnectSuccess();
                            }
                        });
                    }
                    // 开始搜索相应的Service，以触发BluetoothGattCallBack的onServicesDiscovered()方法
                    scanServiceHandler.sendEmptyMessageDelayed(WHAT_DISCOVER_SERVICE, 500);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断开连接
                    blueState = newState;
                    if (blueState == BluetoothProfile.STATE_CONNECTED) { // 只有在连接状态中才处理断开连接状态，避免发出连接请求就收到断开连接状态的消息
                        updateBlueState(BaseBluetooth.STATE_DISCONNECTED);
                        if (null != callback) {
                            new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDisConnected();
                                }
                            });
                        }
                        scanServiceHandler.removeMessages(WHAT_DISCOVER_SERVICE);
                    }
                }
            } else { // 操作失败
                if (null != callback) {
                    new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onConnectFail();
                        }
                    });
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            android.util.Log.v(TAG, "onServicesDiscovered()  gatt: " + gatt + "  status:" + status);
            scanServiceTimeoutHandler.removeMessages(WHAT_DISCOVER_SERVICE_TIMEOUT);
            if (null != callback) {
                new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onServicesDiscovered();
                    }
                });
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            android.util.Log.v(TAG, "onCharacteristicRead()  gatt: " + gatt + "  characteristic:" + characteristic + "  status:" + status);
            handleCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            android.util.Log.v(TAG, "onCharacteristicWrite()  gatt: " + gatt + "  characteristic:" + characteristic + "  status:" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            android.util.Log.v(TAG, "onCharacteristicChanged()  gatt: " + gatt + "  characteristic:" + characteristic);
            handleCharacteristic(characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            android.util.Log.v(TAG, "onDescriptorRead()  gatt: " + gatt + "  descriptor:" + descriptor + "  status:" + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            android.util.Log.v(TAG, "onDescriptorWrite()  gatt: " + gatt + "  descriptor:" + descriptor + "  status:" + status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            android.util.Log.v(TAG, "onReliableWriteCompleted()  gatt: " + gatt + "  status:" + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            android.util.Log.v(TAG, "onReadRemoteRssi()  gatt: " + gatt + "  rssi:" + rssi + "  status:" + status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            // TODO 改变ATT数据传输的长度
            // bluetoothGatt.requestMtu()
        }

        private void handleCharacteristic(BluetoothGattCharacteristic characteristic) {
            if (null != callback) {
                final byte[] data = characteristic.getValue();
                if (null != data && data.length > 0) {
                    new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCharacteristicChange(data);
                        }
                    });
                }
            }
        }
    };

    private Handler scanServiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            android.util.Log.d(TAG, "--ScanServiceHandler-- (null != bluetoothGatt)： " + (null != bluetoothGatt));
            if (null != bluetoothGatt) {
                bluetoothGatt.discoverServices();
                scanServiceTimeoutHandler.sendEmptyMessageDelayed(WHAT_DISCOVER_SERVICE_TIMEOUT, 20000);
            } else {
                if (null != callback) {
                    new Handler(BLEService.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onConnectFail();
                        }
                    });
                }
            }
        }
    };

    private Handler scanServiceTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            android.util.Log.d(TAG, "----scanServiceTimeoutHandler----");
            disConnectedDevice();
        }
    };
}
