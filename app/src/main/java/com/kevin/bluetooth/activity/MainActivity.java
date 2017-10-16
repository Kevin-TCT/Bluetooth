package com.kevin.bluetooth.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<MainActView, MainPresenter> implements CompoundButton.OnCheckedChangeListener, MainActView, View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int PERMISSION_REQUEST_CODE = 1;

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
                /*stopScanBtn.setVisibility(View.VISIBLE);
                loadingImg.startAnimation(operatingAnim);
                mPresenter.startScan();*/

                checkPermission();
                break;
            case R.id.btn_stop:
                stopScanBtn.setVisibility(View.INVISIBLE);
                loadingImg.clearAnimation();
                mPresenter.stopScan();
                break;
        }
    }

    private void checkPermission() {
        // TODO 后面如果正式要用，可以抽出一个工具类
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                stopScanBtn.setVisibility(View.VISIBLE);
                loadingImg.startAnimation(operatingAnim);
                mPresenter.startScan();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }
}
