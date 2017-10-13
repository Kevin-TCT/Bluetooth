package com.kevin.bluetooth.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.kevin.bluetooth.R;
import com.kevin.bluetooth.presenter.MainPresenter;
import com.kevin.bluetooth.viewmodel.MainActView;

public class MainActivity extends BaseActivity<MainActView, MainPresenter> implements CompoundButton.OnCheckedChangeListener, MainActView, View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    private void initView() {
        Switch bluetoothStatus = (Switch) findViewById(R.id.bluetooth_status);
        bluetoothStatus.setOnCheckedChangeListener(this);
        bluetoothStatus.setChecked(mPresenter.getBluetoothStatus());
        findViewById(R.id.scan_btn).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.changeBluetoothStatus(isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_btn:
                mPresenter.startScan();
                break;
        }
    }
}
