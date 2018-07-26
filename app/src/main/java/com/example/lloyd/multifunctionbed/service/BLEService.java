package com.example.lloyd.multifunctionbed.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.lloyd.multifunctionbed.app.Constant;
import com.example.lloyd.multifunctionbed.utils.BluetoothController;


public class BLEService extends Service {
    BluetoothController bleCtrl;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private Intent mesDevice;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constant.WM_BLE_CONNECTED_STATE_CHANGE:// 连接上某个设备的消息
                    Bundle bundle = (Bundle) msg.obj;
                    String address = bundle.getString("address");
                    String name = bundle.getString("name");
                    // 连接状态改变广播
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("address", address);
                    bundle1.putString("name", name);
                    Intent intentDevice = new Intent(
                            Constant.ACTION_CONNECTED_ONE_DEVICE);
                    intentDevice.putExtras(bundle1);
                    sendBroadcast(intentDevice);
                    break;

                case Constant.WM_STOP_CONNECT:
                    Intent stopConnect = new Intent(
                            Constant.ACTION_STOP_CONNECT);
                    sendBroadcast(stopConnect);
                    break;

                case Constant.WM_STOP_SCAN_BLE:// 搜索5秒后停止搜索
                    Log.d("###", "stop scan...");
                    bleCtrl.stopScanBLE();
                    Intent stopScan = new Intent(
                            Constant.ACTION_STOP_SCAN);
                    sendBroadcast(stopScan);
                    break;
                case Constant.WM_UPDATE_BLE_LIST:// 回调发来的更新列表消息
                    // 更新蓝牙列表广播
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    int rssi = msg.arg1;
                    Intent intent = new Intent(
                            Constant.ACTION_UPDATE_DEVICE_LIST);
                    intent.putExtra("name", device.getName());
                    intent.putExtra("address", device.getAddress());
                    intent.putExtra("rssi", rssi);
                    sendBroadcast(intent);

                    break;

                case Constant.WM_RECEIVE_MSG_FROM_BLE:// 接受到蓝牙发送的消息
                    String mes = (String) msg.obj;
                    mesDevice = new Intent(
                            Constant.ACTION_RECEIVE_MESSAGE_FROM_DEVICE);
                    mesDevice.putExtra("message", mes);
                    sendBroadcast(mesDevice);
                    break;
                case Constant.WM_NEED_SCAN:
                    Log.d("####", "handler receive");
                    Intent intent1 = new Intent(
                            Constant.ACTION_NEED_SCAN);
                    sendBroadcast(intent1);
                    break;
                case Constant.WM_SET_NOTIFY_SUCCESS:
                    Log.d("####", "handler receive");
                    Intent intent2 = new Intent(
                            Constant.ACTION_SET_NOTIFY_SUCCESS);
                    sendBroadcast(intent2);
                    break;
                case Constant.WM_SET_NOTIFY_FAILED:
                    Log.d("####", "handler receive");
                    Intent intent3 = new Intent(
                            Constant.ACTION_SET_NOTIFY_FAILED);
                    sendBroadcast(intent3);
                default:
                    break;

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("####", "server onCreate");

    }

    public void onStart(Intent intent, int startId) {
        Log.d("####", "server on start");
        bleCtrl = BluetoothController.getInstance(getApplicationContext());
        bleCtrl.setServiceHandler(handler);
        Message message = new Message();
        message.what = Constant.WM_NEED_SCAN;
        handler.sendMessage(message);
    }

}
