package com.example.lloyd.multifunctionbed.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {

    /**
     * 单例模式
     */
    static ConvertUtils instance = null;// 句柄
    private static Date now;
    private static SimpleDateFormat dateFormat;

    private ConvertUtils() {
    }

    public static ConvertUtils getInstance() {
        if (instance == null)
            instance = new ConvertUtils();
        return instance;
    }

    /**
     * byte数组转换为十六进制字符串
     *
     * @param b
     * @return
     */
    public static String bytesToHexString(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < b.length; i++) {
            int value = b[i] & 0xFF; // 按位与运算，都是1时结果为1，其它为0，所以与全是1相与，结果还是原来的01串
            String hv = Integer.toHexString(value);
            if (hv.length() < 2) {
                sb.append(0);
            }

            sb.append(hv);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转字节数组
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];

        for(int i = 0; i < len; i += 2) {
            b[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return b;
    }


    public static String getTime(){
        now = new Date();
        dateFormat = new SimpleDateFormat("HH:mm:ss");//可以方便地修改日期格式
        return dateFormat.format(now);
    }

    public static String addZeroInHead(String string) {
        String result = string;
        while (result.length() < 2) {
            result = "0" + result;
        }
        return result;
    }


}
