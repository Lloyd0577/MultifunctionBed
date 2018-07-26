package com.example.lloyd.multifunctionbed.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lloyd.multifunctionbed.R;
import com.example.lloyd.multifunctionbed.app.BaseActivity;
import com.example.lloyd.multifunctionbed.app.Constant;
import com.example.lloyd.multifunctionbed.entity.EntityDevice;
import com.example.lloyd.multifunctionbed.service.BLEService;
import com.example.lloyd.multifunctionbed.ui.adapter.DeviceAdapter;
import com.example.lloyd.multifunctionbed.utils.BluetoothController;
import com.example.lloyd.multifunctionbed.utils.LoadingDialogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeviceListActivity extends BaseActivity {

    private ListView listview;
    private Intent intentService;
    private TextView tv_tip;
    private TextView tv_title;
    private ImageView iv_back;
    private ImageView iv_icon;
    private GetDataTask task;
    private View line;
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
    private static final int BLUETOOTH_DISCOVERABLE_DURATION = 120;
    private boolean isSupportBlueTooth;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private MsgReceiver receiver;
    private AtomicBoolean hasFound = new AtomicBoolean(false);
    private ArrayList<EntityDevice> list = new ArrayList<>();
    private DeviceAdapter adapter;
    private RotateAnimation animation;
    private EntityDevice currentConnectDevice;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        Log.d("####", "activity onCreate");
        initView();
        registerReceiver();
        initService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    showToast("自Android 6.0开始需要打开位置权限才可以搜索到Ble设备");
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
    }

    private void startAnimation() {
        animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        animation.setRepeatCount(1000);
        iv_icon.startAnimation(animation);
    }


    public void stopAnimation() {
        animation.cancel();
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
                synchronized (this) {
                    String name = intent.getStringExtra("name");
                    String address = intent.getStringExtra("address");
                    int rssi = intent.getIntExtra("rssi", 0);
                    EntityDevice device = new EntityDevice();
                    device.setName(name);
                    device.setAddress(address);
                    device.setRssi(rssi);

                    if (name == null
                            || name.length() < 4) {
                        return;
                    }
                    hasFound.set(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getAddress()
                                .equals(address)) {
                            list.set(i, device);
                            hasFound.set(true);
                            break;
                        }
                    }
                    if (!hasFound.get()) {
                        list.add(device);
                    }
                    Collections.sort(list, new Comparator<EntityDevice>() {
                        @Override
                        public int compare(EntityDevice scanResult, EntityDevice t1) {
                            if (scanResult.getRssi() > t1.getRssi()) {
                                return -1;
                            }
                            if (scanResult.getRssi() == t1.getRssi()) {
                                return 0;
                            }
                            return 1;
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_CONNECTED_ONE_DEVICE)) {

            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_STOP_CONNECT)) {
                Toast.makeText(DeviceListActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_RECEIVE_MESSAGE_FROM_DEVICE)) {

            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_STOP_SCAN)) {
                stopAnimation();
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_NEED_SCAN)) {
                Log.d("####", "broad receive");
                initBlueTooth();
            } else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_SET_NOTIFY_SUCCESS)) {
                LoadingDialogManager.getInstance().dismiss();
                Intent intent1 = new Intent(DeviceListActivity.this, MainControlActivity.class);
                intent1.putExtra("connectDevice", currentConnectDevice);
                startActivity(intent1);
            }else if (intent.getAction().equalsIgnoreCase(Constant.ACTION_SET_NOTIFY_FAILED)){

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        Log.d("####", "activity onResume");
        super.onResume();

    }

    /**
     * 初始化蓝牙
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initBlueTooth() {
        isSupportBlueTooth = BluetoothController.getInstance(this).initBLE();
        if (isSupportBlueTooth) {
            if (BluetoothController.getInstance(this).isBleOpen()) {
                startScan();
            } else {
                turnOnBlueTooth();
            }
        } else {
            Toast.makeText(this, "设备不支持蓝牙功能！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 扫描蓝牙设备
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScan() {
        startAnimation();
        BluetoothController.getInstance(this).disconnect();
        list.clear();
        adapter.notifyDataSetChanged();
        BluetoothController.getInstance(this).startScanBLE();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //permission was granted, yay! Do the contacts-related task you need to do.
                //这里进行授权被允许的处理
                initBlueTooth();
            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                //这里进行权限被拒绝的处理
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.list_devices);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_icon.setVisibility(View.VISIBLE);
        tv_title.setText("设备列表");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new DeviceAdapter(this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                currentConnectDevice = list.get(index);
                LoadingDialogManager.getInstance().show(DeviceListActivity.this, "正在连接设备...");
                BluetoothController.getInstance(DeviceListActivity.this).connect(currentConnectDevice);
            }
        });
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                initBlueTooth();

            }
        });
    }

    /**
     * 打开蓝牙
     */
    public void turnOnBlueTooth() {
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BLUETOOTH_ON) {
            switch (resultCode) {
                // 点击确认按钮
                case Activity.RESULT_OK: {
                    startScan();
                }
                break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED: {
                    tv_tip.setText("蓝牙未开启！");
                }
                break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void tipTextChange(String text) {
        tv_tip.setText(text);
        if ("扫描结束".equals(text)) {
            tv_tip.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            tv_tip.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
        if (isConnect) {
//            startActivity(new Intent(DeviceListActivity.this, BlueDebugActivity.class));
        }

    }

    @Override
    protected void receiveTextChange(String text) {
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        protected String[] doInBackground(Void... params) {
            if (BluetoothController.getInstance(DeviceListActivity.this).isBleOpen()) {
                BluetoothController.getInstance(DeviceListActivity.this).startScanBLE();
            }
            ;// 开始扫描
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
        }
    }

    /**
     * 开始服务
     */
    private void initService() {
        //开始服务
        intentService = new Intent(DeviceListActivity.this, BLEService.class);
        startService(intentService);
    }

    @Override
    protected void onDestroy() {
        stopService(intentService);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        Log.d("####", "activity onStart");
        super.onStart();
    }
}
