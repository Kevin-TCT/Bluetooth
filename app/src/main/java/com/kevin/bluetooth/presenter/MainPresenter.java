package com.kevin.bluetooth.presenter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.kevin.bluetooth.bluetooth.BaseBluetooth;
import com.kevin.bluetooth.bluetooth.BluetoothUtils;
import com.kevin.bluetooth.bluetooth.ResultListener;
import com.kevin.bluetooth.bluetooth.SelfBluetoothManager;
import com.kevin.bluetooth.viewmodel.MainActView;

import java.util.ArrayList;
import java.util.List;

/**
 * Administrator on 2017/10/9.
 */

@SuppressLint("NewApi")
public class MainPresenter extends BasePresenter<MainActView> implements ResultListener, BaseBluetooth.BlueHandleCallback {

    private MainActView viewModel;
    private SelfBluetoothManager selfBluetoothManager;
    private BluetoothGattCharacteristic mWrightCharacteristic; // 写的特征
    private int mDevType = 0;

    public MainPresenter(MainActView view) {
        this.viewModel = view;
        init();
    }

    @Override
    protected void init() {
        selfBluetoothManager = new SelfBluetoothManager(viewModel.getActContext(), this, this);
    }

    public boolean getBluetoothStatus() {
        return selfBluetoothManager.getBluetoothStatus();
    }

    public void changeBluetoothStatus(boolean enable) {
        selfBluetoothManager.changeBluetoothStatus(enable);
    }

    public boolean isSupportBle() {
        return selfBluetoothManager.isSupportBle();
    }

    public void startScan() {
        selfBluetoothManager.startScan();
    }

    public void stopScan() {
        selfBluetoothManager.stopScan();
    }

    public void connectDevice(BluetoothDevice device) {
        selfBluetoothManager.connectDevice(device);
        if (device.getName().equals("Dual-SPP")) {
            mDevType = 1;
        } else {
            mDevType = 2;
        }
    }

    public void disConnectedDevice(BluetoothDevice device) {
        selfBluetoothManager.disConnectedDevice(device);
    }

    @Override
    public void scannedDevice(BluetoothDevice device) {
        viewModel.scannedDevice(device);
    }

    @Override
    public void stopScanLis() {
        viewModel.stopScan();
    }

    @Override
    public void scanTimeout() {
        viewModel.scanTimeout();
    }

    @Override
    public void scanFailed() {
        viewModel.scanFailed();
    }

    @Override
    public void onConnectFail() {
        viewModel.onConnectFail();
    }

    @Override
    public void onConnectSuccess() {
        viewModel.onConnectSuccess();
    }

    @Override
    public void onDisConnected() {
        viewModel.onDisConnected();
    }

    @Override
    public void onServicesDiscovered() {
        boolean isHaveGatt = false; // 是否有自己需要关注的服务
        List<BluetoothGattService> gattServices = selfBluetoothManager.getSupportedGattServices();
        // TODO 只关注想关注的属性
        if (null != gattServices && gattServices.size() > 0) {
            for (BluetoothGattService service : gattServices) {
                if (!service.getUuid().toString().startsWith("0000fff0")) {
                    continue;
                }

                List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    final int charaProp = gattCharacteristic.getProperties();
                    if (gattCharacteristic.getUuid().toString().startsWith("0000fff2")) {
                        mWrightCharacteristic = gattCharacteristic;
                    }
                    if (((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) || ((charaProp | BluetoothGattCharacteristic
                            .PROPERTY_INDICATE) > 0)) {
                        if (gattCharacteristic.getUuid().toString().startsWith("0000fff1")) {
                            selfBluetoothManager.setCharacteristicNotification(gattCharacteristic, true, mDevType == 1 ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, "00002902-0000-1000-8000-00805f9b34fb");
                        }
                    }
                }
            }
        }
        if(!isHaveGatt) { // 连接上了，但是没有想要的服务，关闭Bluetooth，在重新连接
            viewModel.onDisConnected();
            selfBluetoothManager.stop();
        }
    }

    /**
     * 处理接收的蓝牙数据
     * @param data
     */
    @Override
    public void onCharacteristicChange(byte[] data) {
        if (data == null || data.length > 0 || mWrightCharacteristic == null) {
            return;
        }
        String dataStr = BluetoothUtils.bytesToHexString(data, data.length);

        switch (data[0]) {
            case 0x30:
                // 给蓝牙设备反馈
                byte[] byteW = new byte[] { (byte) 0xF1, 0x03, 0x36 };
                mWrightCharacteristic.setValue(byteW);
                selfBluetoothManager.writeCharacteristic(mWrightCharacteristic);
                break;
        }
    }
}
