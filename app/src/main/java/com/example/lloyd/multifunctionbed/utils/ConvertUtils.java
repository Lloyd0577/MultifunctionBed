package com.example.lloyd.multifunctionbed.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {

    /**
     * ����ģʽ
     */
    static ConvertUtils instance = null;// ���
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
     * byte����ת��Ϊʮ�������ַ���
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
            int value = b[i] & 0xFF; // ��λ�����㣬����1ʱ���Ϊ1������Ϊ0��������ȫ��1���룬�������ԭ����01��
            String hv = Integer.toHexString(value);
            if (hv.length() < 2) {
                sb.append(0);
            }

            sb.append(hv);
        }
        return sb.toString();
    }

    /**
     * ʮ�������ַ���ת�ֽ�����
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
        dateFormat = new SimpleDateFormat("HH:mm:ss");//���Է�����޸����ڸ�ʽ
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
