package com.kevin.bluetooth.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kevin.bluetooth.presenter.BasePresenter;

/**
 * Administrator on 2017/10/9.
 */

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        mPresenter = null;
        super.onDestroy();
    }

    protected abstract T createPresenter();
}
