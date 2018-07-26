package com.example.lloyd.multifunctionbed.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.lloyd.multifunctionbed.app.Constant;
import com.example.lloyd.multifunctionbed.app.MyApplication;
import com.example.lloyd.multifunctionbed.entity.EntityDevice;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙控制类
 *
 * @author lloyd
 */
public class BluetoothController {
    private final Context mContext;
    private String deviceAddress;
    private String deviceName;

    private BluetoothAdapter bleAdapter;
    private Handler serviceHandler;// 服务句柄

    static BluetoothGatt bleGatt;// 连接
    static BluetoothGattCharacteristic bleGattCharacteristic;

    /**
     * 单例模式
     */
    private static BluetoothController instance = null;
    private BluetoothGattService localBluetoothGattService;

    private BluetoothController(Context context) {
        this.mContext = context;
    }

    public static BluetoothController getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothController(context);
        }
        return instance;
    }

    /**
     * 初始化蓝牙
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean initBLE() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        // App.app可能会报错，清单文件中不要忘了配置application
        if (!MyApplication.app.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) MyApplication.app
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();
        // 检查设备上是否支持蓝牙
        return bleAdapter != null;
    }


    /**
     * 设置服务事件接收者
     *
     * @return
     */
    public void setServiceHandler(Handler handler) {
        // handler初始化在service中，用于逻辑和界面的沟通
        Log.d("####", "1111111111111111111111111111");
        serviceHandler = handler;
    }

    /**
     * 搜索蓝牙回调
     */
    BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] arg2) {
            Log.d("###", "search device" + rssi + "::" + arg2);
            // device就是搜索到的设备
            String name = device.getName();
            if (name == null) {
                return;
            }
            if (BluetoothController.this.serviceHandler != null
                    && !name.isEmpty()) {
                Message msg = new Message();
                msg.what = Constant.WM_UPDATE_BLE_LIST;
                msg.obj = device;
                msg.arg1 = rssi;
                BluetoothController.this.serviceHandler.sendMessage(msg);
            }
        }
    };

    /**
     * 开始扫描蓝牙
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startScanBLE() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bleAdapter.startLeScan(bleScanCallback);
                Log.d("####", "22222222222222");
                if (serviceHandler != null) {
                    Log.d("###", "service handle not null ");
                    serviceHandler.sendEmptyMessageDelayed(
                            Constant.WM_STOP_SCAN_BLE, 10000);
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopScanBLE();
                        }
                    }, 10000);
                }
            }
        }).start();
    }

    /**
     * 停止扫描蓝牙设备
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopScanBLE() {
        bleAdapter.stopLeScan(bleScanCallback);
    }

    /**
     * 是否蓝牙打开
     *
     * @return
     */
    public boolean isBleOpen() {
        return bleAdapter.isEnabled();
    }

    /**
     * 连接蓝牙设备
     *
     * @param device 待连接的设备
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connect(EntityDevice device) {
        deviceAddress = device.getAddress();
        deviceName = device.getName();
        final BluetoothDevice localBluetoothDevice = bleAdapter
                .getRemoteDevice(device.getAddress());
        disconnect();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bleGatt = localBluetoothDevice.connectGatt(MyApplication.app, false,
                        bleGattCallback);
            }
        }, 250);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnect() {
        if (bleGatt != null) {
            bleGatt.disconnect();
            bleGatt.close();
            bleGatt = null;
        }
    }

    private static final String HEX = "0123456789abcdef";

    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }

    /**
     * 与蓝牙通信回调
     */
    public BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {
        /**
         * 收到消息
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicChanged(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic) {
            Log.d("###", "blue state is onCharacteristicChanged");
            byte[] arrayOfByte = paramAnonymousBluetoothGattCharacteristic
                    .getValue();
            String msgs = ConvertUtils.getInstance().bytesToHexString(arrayOfByte);
            if (BluetoothController.this.serviceHandler != null) {
                Message msg = new Message();
                msg.what = Constant.WM_RECEIVE_MSG_FROM_BLE;
                msg.obj = msgs;
                BluetoothController.this.serviceHandler.sendMessage(msg);
            }
            Log.i("TEST",
                    ConvertUtils.getInstance().bytesToHexString(arrayOfByte));
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicRead(

                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic,
                int paramAnonymousInt) {
            byte[] arrayOfByte = paramAnonymousBluetoothGattCharacteristic
                    .getValue();
            String readmsg = bytes2hex(arrayOfByte);
//            Toast.makeText(mContext, "写特征回调：" + readmsg, Toast.LENGTH_SHORT).show();
            Log.d("###", "blue state is onCharacteristicRead ::" + readmsg);

        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicWrite(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic,
                int paramAnonymousInt) {
            byte[] arrayOfByte = paramAnonymousBluetoothGattCharacteristic
                    .getValue();
            String write = bytes2hex(arrayOfByte);
//            Toast.makeText(mContext, "写特征回调：" + write, Toast.LENGTH_SHORT).show();
            Log.d("###", "blue state is onCharacteristicWrite ::" + write);
            read();
        }

        /**
         * 连接状态改变
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(
                BluetoothGatt paramAnonymousBluetoothGatt, int oldStatus,
                int newStatus) {
            Log.d("###", "blue state is onConnectionStateChange " + newStatus);
            if (newStatus == 2)// 已连接状态，表明连接成功
            {
                Message msg = new Message();
                msg.what = Constant.WM_BLE_CONNECTED_STATE_CHANGE;
                Bundle bundle = new Bundle();
                bundle.putString("address", deviceAddress);
                bundle.putString("name", deviceName);
                msg.obj = bundle;
                serviceHandler.sendMessage(msg);
                paramAnonymousBluetoothGatt.discoverServices();
                // 连接到蓝牙后查找可以读写的服务，蓝牙有很多服务
                return;
            }
            if (newStatus == 0)// 断开连接或未连接成功
            {
                serviceHandler.sendEmptyMessage(Constant.WM_STOP_CONNECT);
                return;
            }
            paramAnonymousBluetoothGatt.disconnect();
            paramAnonymousBluetoothGatt.close();
            return;
        }

        @Override
        public void onDescriptorRead(BluetoothGatt paramAnonymousBluetoothGatt,
                                     BluetoothGattDescriptor paramAnonymousBluetoothGattDescriptor,
                                     int paramAnonymousInt) {
            Log.d("###", "blue state is onDescriptorRead ");

        }

        @Override
        public void onDescriptorWrite(
                BluetoothGatt paramAnonymousBluetoothGatt,
                BluetoothGattDescriptor paramAnonymousBluetoothGattDescriptor,
                int paramAnonymousInt) {
            Log.d("###", "blue state is onDescriptorWrite ");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt paramAnonymousBluetoothGatt,
                                     int paramAnonymousInt1, int paramAnonymousInt2) {
            Log.d("###", "blue state is onReadRemoteRssi ");
        }

        @Override
        public void onReliableWriteCompleted(
                BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt) {
            Log.d("###", "blue state is onReliableWriteCompleted ");
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(
                BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt) {
            Log.d("###", "blue state is onServicesDiscovered ");
            BluetoothController.this.findService(paramAnonymousBluetoothGatt
                    .getServices());

        }

    };

    /**
     * 传输数据
     *
     * @param byteArray
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean write(byte byteArray[]) {
        if (bleGattCharacteristic == null) {
            return false;
        }
        if (bleGatt == null) {
            return false;
        }
        bleGattCharacteristic.setValue(byteArray);
        return bleGatt.writeCharacteristic(bleGattCharacteristic);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean write(String str) {
        if (bleGattCharacteristic == null) {
            return false;
        }
        if (bleGatt == null) {
            return false;
        }
        bleGattCharacteristic.setValue(str);
        return bleGatt.writeCharacteristic(bleGattCharacteristic);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean read() {
        return bleGatt.readCharacteristic(bleGattCharacteristic);
    }

    /**
     * 搜索服务
     *
     * @param paramList
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void findService(List<BluetoothGattService> paramList) {

        Iterator localIterator1 = paramList.iterator();
        while (localIterator1.hasNext()) {
            localBluetoothGattService = (BluetoothGattService) localIterator1
                    .next();
            if (localBluetoothGattService.getUuid().toString()
                    .equalsIgnoreCase(Constant.UUID_SERVER)) {
                List localList = localBluetoothGattService.getCharacteristics();
                Iterator localIterator2 = localList.iterator();
                while (localIterator2.hasNext()) {
                    BluetoothGattCharacteristic localBluetoothGattCharacteristic = (BluetoothGattCharacteristic) localIterator2
                            .next();
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(Constant.UUID_NOTIFY)) {
                        bleGattCharacteristic = localBluetoothGattCharacteristic;
                        break;
                    }
                }
                break;
            }
        }
        Message msg = new Message();
        boolean isSetNotifySuccess;
        boolean result = bleGatt.setCharacteristicNotification(bleGattCharacteristic, true);
        BluetoothGattDescriptor descriptor = bleGattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (result && descriptor == null) {
            isSetNotifySuccess = true;
        } else {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            isSetNotifySuccess = this.bleGatt.writeDescriptor(descriptor);
        }
        if (isSetNotifySuccess) {
            msg.what = Constant.WM_SET_NOTIFY_SUCCESS;
        } else {
            msg.what = Constant.WM_SET_NOTIFY_FAILED;
        }
        if (serviceHandler != null) {
            BluetoothController.this.serviceHandler.sendMessage(msg);
        }
    }
}
