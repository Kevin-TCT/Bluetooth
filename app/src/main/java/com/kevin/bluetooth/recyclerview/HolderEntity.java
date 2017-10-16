package com.kevin.bluetooth.recyclerview;

/**
 * Kevin-Tu on 2017/10/16 0016.
 */

public class HolderEntity {

    private int position;
    // TODO 留待以后扩展时使用
    private int viewType;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
