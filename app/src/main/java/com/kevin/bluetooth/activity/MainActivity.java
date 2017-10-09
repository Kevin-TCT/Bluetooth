package com.kevin.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.kevin.bluetooth.R;
import com.kevin.bluetooth.presenter.MainPresenter;
import com.kevin.bluetooth.viewmodel.MainActView;

public class MainActivity extends BaseActivity<MainActView, MainPresenter> implements CompoundButton.OnCheckedChangeListener, MainActView{

	private final static String TAG = MainActivity.class.getSimpleName();

	private Switch bluetoothStatus; // 开关蓝牙
	private BluetoothAdapter mBtAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		init();
	}

	@Override
	protected MainPresenter createPresenter() {
		return new MainPresenter(this);
	}

	private void initView() {
		bluetoothStatus = (Switch) findViewById(R.id.bluetooth_status);
		bluetoothStatus.setOnCheckedChangeListener(this);
	}

	private void init() {
		if (Build.VERSION.SDK_INT >= 18) {
			BluetoothManager manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
			mBtAdapter = manager.getAdapter();
		} else {
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		android.util.Log.v(TAG, "mBtAdapter.isEnabled(): " + mBtAdapter.isEnabled());
		bluetoothStatus.setChecked(mBtAdapter.isEnabled());
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (null != mBtAdapter) {
			if (isChecked) {
				mBtAdapter.enable();
			} else {
				mBtAdapter.disable();
			}
		}
	}
}
