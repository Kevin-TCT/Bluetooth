package com.kevin.bluetooth.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.kevin.bluetooth.R;
import com.kevin.bluetooth.bluetooth.BluetoothStateBroadcast;
import com.kevin.bluetooth.presenter.MainPresenter;
import com.kevin.bluetooth.recyclerview.BlueDevicesAdapter;
import com.kevin.bluetooth.recyclerview.DividerItemDecoration;
import com.kevin.bluetooth.viewmodel.MainActView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<MainActView, MainPresenter> implements CompoundButton.OnCheckedChangeListener, MainActView, View.OnClickListener, BluetoothStateBroadcast.BlueStateListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int PERMISSION_REQUEST_CODE = 1;

    private Switch bluetoothStatus;
    private Button startScanBtn;
    private Button stopScanBtn;
    private Animation operatingAnim;
    private ImageView loadingImg;
    private RecyclerView recyclerView;
    private BlueDevicesAdapter adapter;
    private ArrayList<BluetoothDevice> data;
    private BluetoothStateBroadcast broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        registerBroadcast();
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    private void initData() {
        data = new ArrayList<>();
    }

    private void initView() {
        bluetoothStatus = (Switch) findViewById(R.id.bluetooth_status);
        bluetoothStatus.setOnCheckedChangeListener(this);
        bluetoothStatus.setChecked(mPresenter.getBluetoothStatus());
        startScanBtn = (Button) findViewById(R.id.btn_start);
        stopScanBtn = (Button) findViewById(R.id.btn_stop);
        startScanBtn.setOnClickListener(this);
        stopScanBtn.setOnClickListener(this);
        loadingImg = (ImageView) findViewById(R.id.img_loading);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlueDevicesAdapter(this, data);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this);
        recyclerView.addItemDecoration(itemDecoration);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelperCallBack());
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void registerBroadcast() {
        broadcastReceiver = new BluetoothStateBroadcast(this);
        IntentFilter intentFilter = broadcastReceiver.makeFilter();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mPresenter.changeBluetoothStatus(isChecked);
    }

    @Override
    public void scannedDevice(BluetoothDevice device) {
        if (!data.contains(device)) {
            data.add(device);
            adapter.notifyItemInserted(data.size() - 1);
        }
    }

    @Override
    public void scanTimeout() {
        stopScanBtn.setVisibility(View.INVISIBLE);
        loadingImg.clearAnimation();
    }

    @Override
    public void scanFailed() {
        stopScanBtn.setVisibility(View.INVISIBLE);
        loadingImg.clearAnimation();
    }

    @Override
    public void bluetoothOn() {
        bluetoothStatus.setChecked(true);
    }

    @Override
    public void bluetoothOff() {
        bluetoothStatus.setChecked(false);
        stopScan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                // TODO check permission in runtime for android6.0 and after
                /*stopScanBtn.setVisibility(View.VISIBLE);
                loadingImg.startAnimation(operatingAnim);
                mPresenter.startScan();*/
                if (mPresenter.getBluetoothStatus()) {
                    if (mPresenter.isSupportBle()) {
                        checkPermission();
                    } else {
                        startScan();
                    }
                } else {
                    Toast.makeText(this, "请先打开蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_stop:
                stopScan();
                break;
        }
    }

    private void startScan() {
        stopScanBtn.setVisibility(View.VISIBLE);
        loadingImg.startAnimation(operatingAnim);
        mPresenter.startScan();
    }

    private void stopScan() {
        stopScanBtn.setVisibility(View.INVISIBLE);
        loadingImg.clearAnimation();
        mPresenter.stopScan();
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
                startScan();
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

    /**
     * RecyclerView拖拽功能
     */
    private class TouchHelperCallBack extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            int swipeFlags = 0;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                // 线性式布局有2个方向
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // 上下拖拽
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; // 左右滑动
            }
            return makeMovementFlags(dragFlags, swipeFlags); // swipeFlags 为0的话item不滑动
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();

            BluetoothDevice moveItem = data.get(from);
            data.remove(from);
            data.add(to, moveItem);//交换数据链表中数据的位置

            adapter.notifyItemMoved(from, to);//更新适配器中item的位置
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            data.remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }

        /**
         * 拖动开始
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.itemView.setBackgroundColor(Color.GRAY);
            }
        }

        /**
         * 当item视图变化时调用
         *
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         * @param dY
         * @param actionState
         * @param isCurrentlyActive
         */
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            //根据item滑动偏移的值修改item透明度。screenwidth是我提前获得的屏幕宽度
            //viewHolder.itemView.setAlpha(1-Math.abs(dX)/screenwidth);
            viewHolder.itemView.setAlpha(1 - Math.abs(dX) / 1080);
        }

        /**
         * 当item拖拽完成时调用
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
