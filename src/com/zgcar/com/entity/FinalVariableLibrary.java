package com.zgcar.com.entity;

import android.graphics.Bitmap;

/**
 * ��������
 * 
 */
public class FinalVariableLibrary {

	/**
	 * �����ļ�����
	 */
	public static final String CACHE_FOLDER = "zgcar";

	/**
	 * ������Ϣ�㲥action
	 */
	public static final String ChackBroadcastReceiverAction = "CHECKPHONEMESSAGESZBUS";
	/**
	 * ϵͳ��Ϣ�㲥action
	 */
	public static final String SystemNotifyMessageAction = "NOTIFYMESSAGESZBUS";

	/**
	 * ������Ϣ�㲥action
	 */
	public static final String VoiceNotifyMessageAction = "THEVOICENUMHASCHANGEDSZBUS";
	/**
	 * �������߶���Ϣ�㲥action
	 */
	public static final String BlueToothStateAction = "BLUETOOTHSTATESZBUS";

	public static int ScreenHeight;

	public static int ScreenWidth;
	/**
	 * ����
	 */
	public static final String WEB_OFFICIAL_WEBSITE = "http://www.3galarm.cn";

	/**
	 * �޸ĵ���Χ����ҳ
	 */
	public static final String WEB_HOST_EDIT_SAFETY_AREA = "file:///android_asset/mapmain/gmap_point.html";
	/**
	 * ��������λ����ҳ���༭����Χ����Ϣ�е�������
	 */
	public static final String WEB_HOST_SEARCH_ADDRESS = "file:///android_asset/mapmain/gmap_getArea.html";

	/**
	 * ��ʾ��ʷ�켣��Ϣ��ҳ
	 */
	public static final String WEB_HOST_HISTORY = "file:///android_asset/mapmain/gmap_main.html";
	/**
	 * locationFragment��ҳ��׼��ͼ
	 */
	public static final String WEB_HOST_LOCATION1 = "file:///android_asset/mapmain/gmap_1.html";

	/**
	 * locationFragment��ҳ���ǵ�ͼ
	 */
	public static final String WEB_HOST_LOCATION2 = "file:///android_asset/mapmain/gmap_2.html";

	public static String requestURL = "http://" + FinalVariableLibrary.ip + ":"
			+ FinalVariableLibrary.DSTPORT0 + "/****/image/up?****.jpg";
	/**
	 * ��ͼ����
	 */
	public static String MAP_TYPE;

	/**
	 * ��ǰ�˻�ͷ��
	 */
	public static Bitmap bitmap;

	/**
	 * ���ػ���·��
	 */
	public static String PATHS;
	/**
	 * �����ip��ַ124.232.150.158,192.168.2.107
	 */
	public static final String ip = "124.232.150.158";
	/**
	 * ��Դ�˿ں�
	 */
	public static final int DSTPORT0 = 2015;
	/**
	 * ͨ�ö˿ں�
	 */
	public static final int DSTPORT1 = 9801;
	/**
	 * ����˿ں�
	 */
	public static final int DSTPORT2 = 7500;
	/**
	 * �������û�����
	 */
	public static final String SET_REGISTER_PSW_CMD = "00001";
	/**
	 * �û����������½
	 */
	public static final String INPUT_PASSWOR_AND_LOADING = "00002";

	/**
	 * ����¿���
	 */
	public static final String ADD_SZBUS_CMA = "00003";
	/**
	 * ��ȡ�ֱ�λ��
	 */
	public static final String GET_WATCH_LOCATION = "00004";
	/**
	 * ��ʷ�켣
	 */
	public static final String GET_HISTORY_CMD = "00005";

	/**
	 * ��ȡ��ȫ����Χ��ָ��
	 */
	public static final String GET_SAFETY_AREA_CMD = "00006";

	/**
	 * ��Ӱ�ȫ����
	 */
	public static final String ADD_SAFETY_AREA = "00007";
	/**
	 * �༭��ȫ����
	 */
	public static final String EDIT_SAFETY_AREA = "00008";
	/**
	 * ɾ����ȫ����
	 */
	public static final String DELETE_SAFETY_AREA_CMD = "00009";

	/**
	 * �����ֱ�
	 */
	public static final String FIND_WATCH_CMD = "00016";
	/**
	 * ���ֱ�¼��
	 */

	public static final String RECORD_WATCH_VOICE = "00018";
	/**
	 * ��λ�ֱ�
	 */
	public static final String LOCATE_WATCH_CMD = "00020";
	/**
	 * Զ�̹ػ�
	 */
	public static final String SHUT_DOWN_CMD = "00021";
	/**
	 * Զ�̼��
	 */
	public static final String SEND_MONITOR_CMD = "00022";
	/**
	 * ��������
	 */
	public static final String SET_ALARM_CMD = "00023";
	/**
	 * ��ȡ ����/������Ϣ��¼
	 */
	public static final String GET_NOTIFY_MESSAGE_CMD = "00026";
	/**
	 * ��ȡApp���°汾��Ϣ���绰�ֱ�27��С���29
	 */
	public static final String GET_APP_VERSION_INFO_CMD = "00027";

	/**
	 * ��ȡ�ն˲���
	 */
	public static final String GET_TERMINAL_INFOS = "00040";

	/**
	 * ��������
	 */
	public static final String SET_VOLUME_CMD = "00041";
	/**
	 * ��������
	 */
	public static final String SET_BRIGHTNESS_CMD = "00042";
	/**
	 * ��������
	 */
	public static final String SET_RING_VOICE_CMD = "00043";
	/**
	 * ���ö�λģʽ
	 */
	public static final String SET_LOCATION_MODEL_CMD = "00044";
	/**
	 * �������ϰ��
	 */
	public static final String SET_WEARING_HABITS_CMD = "00045";
	/**
	 * �¶�����ָ��
	 */
	public static final String TEMPERATUTE_AlARM_CMD = "00046";
	/**
	 * ��������ָ��
	 */
	public static final String SIlENCE_CMD = "00047";

	/**
	 * ��ȡ�Ͽ�ģʽ����cmd
	 */
	public static final String STUDY_MODEL = "00048";

	/**
	 * �޸��Ͽ�ģʽcmd
	 */
	public static final String SET_STUDY_MODEL = "00049";
	/**
	 * ����ֻ����Ƿ�ע��
	 */
	public static final String TEST_PHONE_NUMBER = "00050";
	/**
	 * ��ȡС�컨����
	 */
	public static final String GET_RED_FLOWER_NUM_INFOS = "00051";

	/**
	 * ����С�컨
	 */
	public static final String REWARD_BABY_FLOWERS = "00052";

	/**
	 * ����С�컨Ŀ��
	 */
	public static final String SET_RED_FLOWER_GOAL = "00053";
	/**
	 * ��ѯ�ֱ���
	 */
	public static final String QUERY_COST = "00054";

	/**
	 * ��ѯ�ֱ�����
	 */
	public static final String QUERY_TRAFFIC = "00055";

	/**
	 * ��ȡ�ֱ���������Ϣ
	 */
	public static final String GET_PHONE_MESSAGE = "00056";
	/**
	 * ���ģʽ
	 */
	public static final String SET_BODY_ANSWER = "00057";
	/**
	 * ��ע:��ָ���Ӧ�����ط�
	 * 
	 * 1. �ʻ�-��С�ɷ�-�������
	 * 
	 * 2. �ʻ�-����ͥ��Ա-������Ա-���˳���ͥ��-�����������
	 */
	// public static final String REMOVE_szalarm_CMD = "00060";
	/**
	 * ��ȡ��ͥ��Ա�б�
	 */
	public static final String MEMBERS_OF_FIMILY_CMD = "00061";

	/**
	 * ��ȡС����б�
	 */
	public static final String GUYS_INFOS_CMD = "00062";
	/**
	 * ɾ��С���
	 */
	public static final String DELETE_GUYS_CMD = "00063";
	/**
	 * �뿪��ͥ
	 */
	public static final String LEAVE_ALONE_CMD = "00064";
	/**
	 * �޸ļ�ͥ��Ա��Ϣ
	 */
	public static final String CHANGE_MEMBERINFOS_CMD = "00065";

	/**
	 * �������Աָ��
	 */
	public static final String CHANGE_ADMIN_CMD = "00066";
	/**
	 * �������
	 */
	public static final String FEED_BACK_CMD = "00067";
	/**
	 * ������Ϣ�б�
	 */
	public static final String CLOCK_INFOS_CMD = "00068";

	/**
	 * ������Ϣ
	 */
	public static final String RING_MESSAGE_CMD = "00069";
	/**
	 * ����ʱ���ֳ����ӵ�����������cmd
	 */
	public static final String HEART_PACKAGE_CMD = "00070";
	/**
	 * �ͻ��˷����������ն�
	 */
	public static final String CHAT_VOICE_CMD = "00071";
	/**
	 * �޸��û���¼����
	 */
	public static final String CHANGE_USER_PSW = "10001";

	/**
	 * �һ�������������
	 */
	public static final String CHANGE_USER_PSW_LOADING = "10010";
	/**
	 * ���û�ע���ȡ��֤��
	 */
	public static final String GET_PHONE_CODE = "20001";
	/**
	 * �һ�����
	 */
	public static final String GET_BACK_PSW_CMD = "20002";

	/**
	 * �鿴��֤���Ƿ���ȷ
	 */
	public static final String GET_CODE_STATE_CMD = "20003";

	/**
	 * .��ȡ�ն��б�
	 */
	public static final String GET_WATCH_LIST_CMD = "30001";
	/**
	 * �༭�ն�
	 */
	public static final String EDIT_WATCH_BASIC = "30003";
	/**
	 * ��ѯ�󶨵�imei�Ƿ����
	 */
	public static final String CHECK_IMEI_CMD = "80003";

	/**
	 * �Ͽν��ø��Ľӿں�ָ��
	 */
	public static final String STUDY_MODEL_SAVE_CMD = "80049";

	/**
	 * ����Ա����³�Ա
	 */
	public static final String FAMILY_ADD_NEW_MEMBER_CMD = "80065";

	/**
	 * socket��ʱ��
	 */
	public static final int SOCKET_TIME_OUT_SHORT = 1000;
	/**
	 * socket��ʱ�г�
	 */
	public static final int SOCKET_TIME_OUT_LONG = 5000;
	/**
	 * socket��ʱ��
	 */
	public static final int SOCKET_TIME_OUT_MIDDLE = 3000;
	/**
	 * ���ʱ����
	 */
	public static final int SOCKET_TIME_OUT_MAXCOUNT = 3;

}