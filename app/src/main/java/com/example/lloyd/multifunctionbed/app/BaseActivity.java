package com.example.lloyd.multifunctionbed.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;



public class BaseActivity extends AppCompatActivity {

    protected boolean isConnect;
    private Handler handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        handle = new Handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    protected void tipTextChange(String text) {
    }

    protected void receiveTextChange(String text) {
    }
}
