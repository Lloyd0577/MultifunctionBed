package com.example.lloyd.multifunctionbed.app;

public class Constant {
    //��Ϣ����
    //ֹͣɨ��
    public final static int WM_STOP_SCAN_BLE = 1;
    public final static int WM_UPDATE_BLE_LIST = 2;
    //��������״̬�ı�
    public final static int WM_BLE_CONNECTED_STATE_CHANGE = 3;
    //���ܵ�������������Ϣ
    public final static int WM_RECEIVE_MSG_FROM_BLE = 4;
    //�Ͽ����ӻ�δ���ӳɹ�
    public final static int WM_STOP_CONNECT = 5;
//    Ӧ��������һ��ɨ��
    public final static int WM_NEED_SCAN = 6;
    /*����֪ͨ�ɹ�*/
    public final static int WM_SET_NOTIFY_FAILED = 8;
    /*����֪ͨʧ��*/
    public final static int WM_SET_NOTIFY_SUCCESS = 7;

    //intent��action��
    public final static String ACTION_UPDATE_DEVICE_LIST = "action.update.device.list";//�����豸�б�
    public final static String ACTION_CONNECTED_ONE_DEVICE = "action.connected.one.device";//������ĳ���豸ʱ���Ĺ㲥
    public final static String ACTION_RECEIVE_MESSAGE_FROM_DEVICE = "action.receive.message";
    public final static String ACTION_NEED_SCAN = "action.receive.need.scan";
    public final static String ACTION_STOP_CONNECT = "action.stop.connect";
    public final static String ACTION_STOP_SCAN = "action.stop.scan";
    public final static String ACTION_SET_NOTIFY_SUCCESS = "action.set.notify.success";
    public final static String ACTION_SET_NOTIFY_FAILED = "action.set.notify.failed";


    //UUID
    public final static String UUID_SERVER = "0000fee0-0000-1000-8000-00805f9b34fb";
    public final static String UUID_NOTIFY = "0000fee1-0000-1000-8000-00805f9b34fb";

    public static String DATA_SEND = "7E3132303144303141363045433030303033303330" +
            "333033303330333033303330333033303330333033303330333033303" +
            "0303030303030303331333733343338333233303333333533323331333633" +
            "3833393033353630363032303031323030303033363030303030303030303030" +
            "3030303030303130463030303530303138303030303043303030313030303033333" +
            "339324533313330333832453339333132453332333033383243333233303330333033" +
            "3830303030303130313030303030303030303030303030303030303030303030303030303" +
            "03030303030303030303030303030303030303030303030303030303030303030434638320D";

    public static String NB_IOT_HOST = "120.77.205.98";
    public static String NB_IOT_PORT = "2555";

    public static String[] CMD_NAME_OF_AT = new String[]{
             "������","����", "ֹͣ����", "����","AT", "����ģ��", "Ƶ�β�ѯ", "���ò�ѯ", "���Զ�����", "�ر��Զ�����",
            "��Ƶ���ز�ѯ", "����Ƶ", "����Ƶ", "��ѯ����", "���Ų�ѯ", "�ź�ǿ�Ȳ�ѯ",
            "ע��״̬��ѯ", "����״̬��ѯ", "����״̬��ѯ", "��������ͨ��", "ע������ͨ��",
            "��������", "�����ظ�", "��ѯ�汾", "���뿪", "�����", "PING IP"
    };

    public static String[] CMD_OF_AT = new String[]{
             "clearcahce","save", "stop", "clear", "AT","AT+NRB", "AT+NBAND?", "AT+NCONFIG?", "AT+NCONFIG=AUTOCONNECT,TRUE",
            "AT+NCONFIG=AUTOCONNECT,FALSE", "AT+CFUN?", "AT+CFUN=1", "AT+CFUN=0", "AT+NCCID",
            "AT+CGATT?", "AT+CSQ", "AT+CEREG?", "AT+CSCON?", "AT+NUESTATS",
            "AT+NSOCR=DGRAM,17,1113,1", "AT+NSOCL=0", "AT+NSOST=0," + NB_IOT_HOST + "," + NB_IOT_PORT + ",254," + DATA_SEND,
            "AT+NSORF=0,512", "AT+CGMR", "AT+NCONFIG=CR_0354_0338_SCRAMBLING,TRUE" + "::" + "AT+NCONFIG=CR_0859_SI_AVOID,TRUE",
            "AT+NCONFIG=CR_0354_0338_SCRAMBLING,FALSE" + "::" + "AT+NCONFIG=CR_0859_SI_AVOID,FALSE",
            "AT+NPING=" + NB_IOT_HOST

    };

    public static boolean isReceiveMsg = true;


}
