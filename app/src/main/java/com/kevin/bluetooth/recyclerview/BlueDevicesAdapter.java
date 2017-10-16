package com.kevin.bluetooth.recyclerview;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevin.bluetooth.R;

import java.util.List;

/**
 * Administrator on 2017/10/16 0016.
 */

public class BlueDevicesAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<BluetoothDevice> devices;
    private LayoutInflater layoutInflater;

    public BlueDevicesAdapter(Context context, List<BluetoothDevice> devices) {
        this.mContext = context;
        this.devices = devices;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new DeviceViewHolder(layoutInflater.inflate(R.layout.item_scan_result, parent, false), itemClick);
            default:
                return new DeviceViewHolder(layoutInflater.inflate(R.layout.item_scan_result, parent, false), itemClick);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HolderEntity holderEntity = new HolderEntity();
        holderEntity.setPosition(position);
        if (holder instanceof DeviceViewHolder) {
            ((DeviceViewHolder) holder).refreshView(devices.get(position), holderEntity);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO 返回不同的viewType在onCreateViewHolder()可以用来区分加载不同的ViewHolder
        return super.getItemViewType(position);
    }

    private View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
