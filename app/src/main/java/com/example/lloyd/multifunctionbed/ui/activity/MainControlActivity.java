package com.example.lloyd.multifunctionbed.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lloyd.multifunctionbed.R;
import com.example.lloyd.multifunctionbed.app.Constant;
import com.example.lloyd.multifunctionbed.entity.EntityDevice;
import com.example.lloyd.multifunctionbed.ui.widget.PressValueBar;
import com.example.lloyd.multifunctionbed.utils.BluetoothController;
import com.example.lloyd.multifunctionbed.utils.ConvertUtils;

import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainControlActivity extends AppCompatActivity {

    private PressValueBar pvbLeftInnerGallbladder;
    @BindView(R.id.pvb_one)
    PressValueBar pvb_one;
    @BindView(R.id.pvb_two)
    PressValueBar pvb_two;
    @BindView(R.id.pvb_third)
    PressValueBar pvb_third;
    @BindView(R.id.pvb_four)
    PressValueBar pvb_four;
    @BindView(R.id.pvb_five)
    PressValueBar pvb_five;
    @BindView(R.id.iv_bar_one_add)
    ImageView iv_bar_one_add;
    @BindView(R.id.iv_bar_one_reduce)
    ImageView iv_bar_one_reduce;
    @BindView(R.id.iv_bar_two_add)
    ImageView iv_bar_two_add;
    @BindView(R.id.iv_bar_two_reduce)
    ImageView iv_bar_two_reduce;
    @BindView(R.id.iv_bar_third_add)
    ImageView iv_bar_third_add;
    @BindView(R.id.iv_bar_third_reduce)
    ImageView iv_bar_third_reduce;
    @BindView(R.id.iv_bar_four_add)
    ImageView iv_bar_four_add;
    @BindView(R.id.iv_bar_four_reduce)
    ImageView iv_bar_four_reduce;
    @BindView(R.id.iv_bar_five_add)
    ImageView iv_bar_five_add;
    @BindView(R.id.iv_bar_five_reduce)
    ImageView iv_bar_five_reduce;
    @BindView(R.id.iv_tem_left_add)
    ImageView iv_tem_left_add;
    @BindView(R.id.iv_tem_left_reduce)
    ImageView iv_tem_left_reduce;
    @BindView(R.id.iv_tem_right_add)
    ImageView iv_tem_right_add;
    @BindView(R.id.iv_tem_right_reduce)
    ImageView iv_tem_right_reduce;
    @BindView(R.id.tv_tem_left)
    TextView tv_tem_left;
    @BindView(R.id.tv_tem_right)
    TextView tv_tem_right;
    @BindView(R.id.ll_auto)
    LinearLayout ll_auto;
    @BindView(R.id.ll_stop)
    LinearLayout ll_stop;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_en)
    TextView tvTitleEn;
    private int barOneValue;
    private int barTwoValue;
    private int barThirdValue;
    private int barFourValue;
    private int barFiveValue;
    private int leftTemValue;
    private int rightTemValue;
    private EntityDevice connectDevice;
    private MsgReceiver receiver;

    public static String CMD_AUTO = "FA4200A5";
    public static String CMD_STOP = "FA4100A5";
    private String receiveText;
    private boolean isSendingPress;
    private Handler mainHanlder;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        registerReceiver();
        connectDevice = (EntityDevice) getIntent().getSerializableExtra("connectDevice");
        initPressViewValue();
        mainHanlder = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initPressViewValue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(100);
                        BluetoothController.getInstance(MainControlActivity.this)
                                .write(ConvertUtils.hexStringToByteArray("FA3100A5"));
                        wait(100);
                        BluetoothController.getInstance(MainControlActivity.this)
                                .write(ConvertUtils.hexStringToByteArray("FA3200A5"));
                        wait(100);
                        BluetoothController.getInstance(MainControlActivity.this)
                                .write(ConvertUtils.hexStringToByteArray("FA3300A5"));
                        wait(100);
                        BluetoothController.getInstance(MainControlActivity.this)
                                .write(ConvertUtils.hexStringToByteArray("FA3400A5"));
                        wait(100);
                        BluetoothController.getInstance(MainControlActivity.this)
                                .write(ConvertUtils.hexStringToByteArray("FA3500A5"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }
        }).start();
    }

    private void registerReceiver() {
        receiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_UPDATE_DEVICE_LIST);
        intentFilter.addAction(Constant.ACTION_CONNECTED_ONE_DEVICE);
        intentFilter.addAction(Constant.ACTION_RECEIVE_MESSAGE_FROM_DEVICE);
        intentFilter.addAction(Constant.ACTION_STOP_CONNECT);
        intentFilter.addAction(Constant.ACTION_STOP_SCAN);
        intentFilter.addAction(Constant.ACTION_NEED_SCAN);
        intentFilter.addAction(Constant.ACTION_SET_NOTIFY_SUCCESS);
        intentFilter.addAction(Constant.ACTION_SET_NOTIFY_FAILED);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 广播接收器
     */
    public class MsgReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("###", "receive broadcast " + intent.getAction());
            if (intent.getAction().equalsIgnoreCase(
                    Constant.ACTION_UPDATE_DEVICE_LIST)) {

            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_CONNECTED_ONE_DEVICE)) {
                initPressViewValue();
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_STOP_CONNECT)) {
                BluetoothController.getInstance(MainControlActivity.this).connect(connectDevice);
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_RECEIVE_MESSAGE_FROM_DEVICE)) {
                receiveText = intent.getStringExtra("message");
                handleReceiveMessage(receiveText);
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_STOP_SCAN)) {

            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_NEED_SCAN)) {

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void handleReceiveMessage(String string) {
        if (string == null || string.length() < 8) {
            return;
        }
        int pressValue = 0;
        try {
            pressValue = Integer.parseInt(string.substring(5, 6));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(MainControlActivity.this, "数据解析输错：" + string, Toast.LENGTH_SHORT).show();
        }
        switch (string.substring(2, 4)) {
            case "31":
            case "21":
                setIsSendingPress(false);
                mainHanlder.removeCallbacks(runnable);
                barOneValue = pressValue;
                pvb_one.setValue(pressValue);
                break;
            case "32":
            case "22":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                barTwoValue = pressValue;
                pvb_two.setValue(pressValue);
                break;
            case "33":
            case "23":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                barThirdValue = pressValue;
                pvb_third.setValue(pressValue);
                break;
            case "34":
            case "24":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                barFourValue = pressValue;
                pvb_four.setValue(pressValue);
                break;
            case "35":
            case "25":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                barFiveValue = pressValue;
                pvb_five.setValue(pressValue);
                break;
            case "26":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                leftTemValue = pressValue;
                tv_tem_left.setText(leftTemValue + "℃");
                break;
            case "27":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                rightTemValue = pressValue;
                tv_tem_right.setText(rightTemValue + "℃");
                break;
            case "41":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                Toast.makeText(MainControlActivity.this, "停止指令下发成功！", Toast.LENGTH_SHORT).show();
                initPressViewValue();
                break;
            case "42":
                mainHanlder.removeCallbacks(runnable);
                setIsSendingPress(false);
                Toast.makeText(MainControlActivity.this, "自动指令下发成功！", Toast.LENGTH_SHORT).show();
                setAutoCmdSuccessView();
                break;

        }
    }

    public void setAutoCmdSuccessView(){
        barOneValue = 6;
        barTwoValue = 6;
        barThirdValue = 6;
        barFourValue = 6;
        barFiveValue = 6;
        pvb_one.setValue(6);
        pvb_two.setValue(6);
        pvb_third.setValue(6);
        pvb_four.setValue(6);
        pvb_five.setValue(6);
    }

    public void setIsSendingPress(boolean flag) {
        this.isSendingPress = flag;
    }

    public void handlePressSendFailed() {
        mainHanlder.postDelayed(runnable, 500);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isSendingPress) {
                Toast.makeText(MainControlActivity.this, "发送指令失败!", Toast.LENGTH_SHORT).show();
                isSendingPress = false;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void handleBarAdd(PressValueBar pressValueBar, int index) {
        if (isSendingPress) {
            return;
        }
        switch (index) {
            case 1:
                if (barOneValue == 9) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barOneValue + 1) + "A5"));
                handlePressSendFailed();
                break;
            case 2:
                if (barTwoValue == 9) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barTwoValue + 1) + "A5"));
                handlePressSendFailed();
                break;
            case 3:
                if (barThirdValue == 9) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barThirdValue + 1) + "A5"));
                handlePressSendFailed();
                break;
            case 4:
                if (barFourValue == 9) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barFourValue + 1) + "A5"));
                handlePressSendFailed();
                break;
            case 5:
                if (barFiveValue == 9) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barFiveValue + 1) + "A5"));
                handlePressSendFailed();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void handleBarReduce(PressValueBar pressValueBar, int index) {
        if (isSendingPress) {
            return;
        }
        switch (index) {
            case 1:
                if (barOneValue == 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barOneValue - 1) + "A5"));
                handlePressSendFailed();
                break;
            case 2:
                if (barTwoValue == 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barTwoValue - 1) + "A5"));
                handlePressSendFailed();
                break;
            case 3:
                if (barThirdValue == 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barThirdValue - 1) + "A5"));
                handlePressSendFailed();
                break;
            case 4:
                if (barFourValue == 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barFourValue - 1) + "A5"));
                handlePressSendFailed();
                break;
            case 5:
                if (barFiveValue == 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIsSendingPress(true);
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray("FA2" + index + "0" + (barFiveValue - 1) + "A5"));
                handlePressSendFailed();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @OnClick({R.id.iv_bar_one_add, R.id.iv_bar_one_reduce,
            R.id.iv_bar_two_add, R.id.iv_bar_two_reduce,
            R.id.iv_bar_third_add, R.id.iv_bar_third_reduce,
            R.id.iv_bar_four_add, R.id.iv_bar_four_reduce,
            R.id.iv_bar_five_add, R.id.iv_bar_five_reduce,
            R.id.iv_tem_left_add, R.id.iv_tem_left_reduce,
            R.id.iv_tem_right_add, R.id.iv_tem_right_reduce,
            R.id.ll_auto, R.id.ll_stop
    })
    public void menuClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_one_add:
                handleBarAdd(pvb_one, 1);
                break;
            case R.id.iv_bar_one_reduce:
                handleBarReduce(pvb_one, 1);
                break;
            case R.id.iv_bar_two_add:
                handleBarAdd(pvb_two, 2);
                break;
            case R.id.iv_bar_two_reduce:
                handleBarReduce(pvb_two, 2);
                break;
            case R.id.iv_bar_third_add:
                handleBarAdd(pvb_third, 3);
                break;
            case R.id.iv_bar_third_reduce:
                handleBarReduce(pvb_third, 3);
                break;
            case R.id.iv_bar_four_add:
                handleBarAdd(pvb_four, 4);
                break;
            case R.id.iv_bar_four_reduce:
                handleBarReduce(pvb_four, 4);
                break;
            case R.id.iv_bar_five_add:
                handleBarAdd(pvb_five, 5);
                break;
            case R.id.iv_bar_five_reduce:
                handleBarReduce(pvb_five, 5);
                break;
            case R.id.iv_tem_left_add:
                if (leftTemValue >= 255) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                } else {
                    BluetoothController.getInstance(MainControlActivity.this)
                            .write(ConvertUtils.hexStringToByteArray("FA26"
                                    + ConvertUtils.addZeroInHead(Integer.toHexString(leftTemValue + 1)) + "A5"));
                    setIsSendingPress(true);
                    handlePressSendFailed();
                }
                break;
            case R.id.iv_tem_left_reduce:
                if (leftTemValue <= 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                } else {
                    leftTemValue--;
                    BluetoothController.getInstance(MainControlActivity.this)
                            .write(ConvertUtils.hexStringToByteArray("FA26"
                                    + ConvertUtils.addZeroInHead(Integer.toHexString(leftTemValue - 1)) + "A5"));
                    setIsSendingPress(true);
                    handlePressSendFailed();
                }
                break;
            case R.id.iv_tem_right_add:
                if (rightTemValue >= 255) {
                    Toast.makeText(this, "已达到最大值!", Toast.LENGTH_SHORT).show();
                } else {
                    rightTemValue++;
                    BluetoothController.getInstance(MainControlActivity.this)
                            .write(ConvertUtils.hexStringToByteArray("FA27"
                                    + ConvertUtils.addZeroInHead(Integer.toHexString(rightTemValue + 1)) + "A5"));
                    setIsSendingPress(true);
                    handlePressSendFailed();
                }
                break;
            case R.id.iv_tem_right_reduce:
                if (rightTemValue <= 0) {
                    Toast.makeText(this, "已达到最小值!", Toast.LENGTH_SHORT).show();
                } else {
                    rightTemValue--;
                    BluetoothController.getInstance(MainControlActivity.this)
                            .write(ConvertUtils.hexStringToByteArray("FA27" +
                                    ConvertUtils.addZeroInHead(Integer.toHexString(rightTemValue - 1)) + "A5"));
                    setIsSendingPress(true);
                    handlePressSendFailed();
                }
                break;
            case R.id.ll_auto:
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray(CMD_AUTO));
                setIsSendingPress(true);
                handlePressSendFailed();
                break;
            case R.id.ll_stop:
                BluetoothController.getInstance(MainControlActivity.this)
                        .write(ConvertUtils.hexStringToByteArray(CMD_STOP));
                setIsSendingPress(true);
                handlePressSendFailed();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        BluetoothController.getInstance(MainControlActivity.this).disconnect();
        super.onDestroy();
    }
}
