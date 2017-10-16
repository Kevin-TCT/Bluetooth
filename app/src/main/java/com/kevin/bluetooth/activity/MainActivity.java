package com.kevin.bluetooth.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.kevin.bluetooth.R;
import com.kevin.bluetooth.presenter.MainPresenter;
import com.kevin.bluetooth.viewmodel.MainActView;

public class MainActivity extends BaseActivity<MainActView, MainPresenter> implements CompoundButton.OnCheckedChangeListener, MainActView, View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private Button startScanBtn;
    private Button stopScanBtn;
    private Animation operatingAnim;
    private ImageView loadingImg;

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
        startScanBtn = (Button) findViewById(R.id.btn_start);
        stopScanBtn = (Button) findViewById(R.id.btn_stop);
        startScanBtn.setOnClickListener(this);
        stopScanBtn.setOnClickListener(this);
        loadingImg = (ImageView) findViewById(R.id.img_loading);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.changeBluetoothStatus(isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                // TODO check permission in runtime for android6.0 and after
                stopScanBtn.setVisibility(View.VISIBLE);
                loadingImg.startAnimation(operatingAnim);
                mPresenter.startScan();
                break;
            case R.id.btn_stop:
                stopScanBtn.setVisibility(View.INVISIBLE);
                loadingImg.clearAnimation();
                mPresenter.stopScan();
                break;
        }
    }
}
