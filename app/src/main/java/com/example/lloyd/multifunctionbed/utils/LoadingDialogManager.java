package com.example.lloyd.multifunctionbed.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by lloyd on 2018/1/17.
 */

public class LoadingDialogManager {
    private static volatile LoadingDialogManager loadingDialogManager;
    private ProgressDialog loadingDialog;

    private LoadingDialogManager(){}

    public static synchronized LoadingDialogManager getInstance(){
        if (loadingDialogManager == null){
            loadingDialogManager = new LoadingDialogManager();
        }
        return loadingDialogManager;
    }

    public synchronized void show(Context context, String string){
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage(string);
        loadingDialog.setCancelable(true);
        loadingDialog.show();
    }
    public void setCancelable(boolean flag){
        loadingDialog.setCancelable(flag);
    }
    public void setOnCancelListener(DialogInterface.OnCancelListener listener){
        loadingDialog.setOnCancelListener(listener);
    }
    public void show(Context context){
        show(context,"加载中...");
    }

    public void dismiss(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
