package com.kevin.bluetooth.presenter;

import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import com.kevin.bluetooth.bluetooth.ResultListener;
import com.kevin.bluetooth.bluetooth.SelfBluetoothManager;
import com.kevin.bluetooth.viewmodel.MainActView;

/**
 * Administrator on 2017/10/9.
 */

public class MainPresenter extends BasePresenter<MainActView> implements ResultListener {

    private MainActView viewModel;
    private SelfBluetoothManager selfBluetoothManager;

    public MainPresenter(MainActView view) {
        this.viewModel = view;
        init();
    }

    @Override
    protected void init() {
        selfBluetoothManager = new SelfBluetoothManager(viewModel.getActContext(), this);
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

    }

    public void disConnectedDevice(BluetoothDevice device) {

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
}
