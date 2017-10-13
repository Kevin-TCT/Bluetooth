package com.kevin.bluetooth.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kevin.bluetooth.R;
import com.kevin.bluetooth.presenter.BasePresenter;

/**
 * Administrator on 2017/10/9.
 */

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    protected T mPresenter;
    protected Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        createDialog();
    }

    protected void createDialog() {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);
    };

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        mPresenter = null;
        super.onDestroy();
    }

    protected abstract T createPresenter();

    private void showLoading() {
        if (null != dialog) {
            dialog.show();
        }
    }

    private void dismissLoading() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public Context getActContext() {
        return getBaseContext();
    }

    public Activity getActActivity() {
        return this;
    }
}
