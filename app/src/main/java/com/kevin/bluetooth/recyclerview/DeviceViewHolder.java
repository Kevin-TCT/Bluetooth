package com.kevin.bluetooth.recyclerview;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kevin.bluetooth.R;

/**
 * Kevin-Tu on 2017/10/16 0016.
 */

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    private RelativeLayout relativeLayout;
    private TextView deviceNameTv;
    private TextView deviceMacTv;
    private TextView rssiTv;

    public DeviceViewHolder(View itemView, View.OnClickListener itemClick) {
        super(itemView);
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
        deviceNameTv = (TextView) itemView.findViewById(R.id.txt_name);
        deviceMacTv = (TextView) itemView.findViewById(R.id.txt_mac);
        rssiTv = (TextView) itemView.findViewById(R.id.txt_rssi);

        relativeLayout.setOnClickListener(itemClick);
    }

    public void refreshView(BluetoothDevice device, HolderEntity holderEntity) {
        relativeLayout.setTag(holderEntity);
        deviceNameTv.setText(device.getName());
        deviceMacTv.setText(device.getAddress());
        //rssiTv.setText(device.);
    }
}
