package com.kevin.bluetooth.presenter;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Administrator on 2017/10/9.
 */

public abstract class BasePresenter<T> {

    protected Reference<T> mViewRef; //View接口类型的弱引用

    public void attachView(T view) {
        mViewRef = new WeakReference<T>(view); //建立关联
    }

    public void detachView() {
        mViewRef = null;
    }

    protected abstract void init();

    protected T getView() {
        if (mViewRef != null) {
            return mViewRef.get(); //获取View
        }
        return null;
    }
}
