package com.kevin.bluetooth.presenter;

import com.kevin.bluetooth.bluetooth.SelfBluetoothManager;
import com.kevin.bluetooth.viewmodel.MainActView;

/**
 * Administrator on 2017/10/9.
 */

public class MainPresenter extends BasePresenter<MainActView> {

    private MainActView viewModel;
    private SelfBluetoothManager selfBluetoothManager;

    public MainPresenter(MainActView view) {
        this.viewModel = view;
        init();
    }

    @Override
    protected void init() {
        selfBluetoothManager = new SelfBluetoothManager(viewModel.getActContext());
    }

    public boolean getBluetoothStatus() {
        return selfBluetoothManager.getBluetoothStatus();
    }

    public void changeBluetoothStatus(boolean enable) {
        selfBluetoothManager.changeBluetoothStatus(enable);
    }

    public void startScan() {
        selfBluetoothManager.startScan();
    }

    public void stopScan() {
        selfBluetoothManager.stopScan();
    }
}
