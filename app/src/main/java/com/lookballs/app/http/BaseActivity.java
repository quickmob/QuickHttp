package com.lookballs.app.http;

import android.app.ProgressDialog;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.lookballs.http.core.listener.OnHttpListener;

import okhttp3.Call;

public class BaseActivity extends FragmentActivity implements OnHttpListener {

    /**
     * 加载对话框
     */
    private ProgressDialog mDialog;
    /**
     * 对话框数量
     */
    private int mDialogTotal;

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        return mDialog != null && mDialog.isShowing();
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("请求中...");
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!isShowDialog()) {
            mDialog.show();
        }
        mDialogTotal++;
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        if (mDialogTotal == 1) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
        if (mDialogTotal > 0) {
            mDialogTotal--;
        }
    }

    @Override
    public void onStart(Call call) {
        showDialog();
    }

    @Override
    public void onSucceed(Object result) {

    }

    @Override
    public void onError(int code, Exception e) {
        ToastUtils.showShort("请求失败：" + e.getMessage());
    }

    @Override
    public void onEnd(Call call) {
        hideDialog();
    }
}
