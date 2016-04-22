package com.zgcar.com.util;

import android.content.Context;
import android.util.Log;

import com.zgcar.com.R;

public class ErrorCode {
	/**
	 * 1001 ����У������Ƿ�� 1002 SQL���ݿ���� 1003 �û������������ 1004 �û��������� 9999 Json ��ʽ���� 1005
	 * ������֤����� 1006 ��������� 1007 ���ն��޼�¼ 1008 �������ID 1009 ���ݿ���Ĭ���� 1010 �޴��ն� 1011
	 * Χ�������ظ� 1012 �޴˵���Χ�� 1013 ָ���ʧ��,�ն˲�����
	 * 
	 * @param code
	 * @return
	 */
	public static String getErrorMsg(int code, Context context) {
		Log.e("ErrorCode", code + "");
		switch (code) {
		case 1001:
			return context.getString(R.string.error_code_1001);
		case 1002:
			return context.getString(R.string.error_code_1002);
		case 1003:
			return context.getString(R.string.error_code_1003);
		case 1004:
			return context.getString(R.string.error_code_1004);
		case 1005:
			return context.getString(R.string.error_code_1005);
		case 1006:
			return context.getString(R.string.error_code_1006);
		case 1007:
			return context.getString(R.string.error_code_1007);
		case 1008:
			return context.getString(R.string.error_code_1008);
		case 1009:
			return context.getString(R.string.error_code_1009);
		case 1010:
			return context.getString(R.string.error_code_1010);
		case 1011:
			return context.getString(R.string.error_code_1011);
		case 1012:
			return context.getString(R.string.error_code_1012);
		case 1013:
			return context.getString(R.string.error_code_1013);
		case 1014:
			return context.getString(R.string.error_code_1014);
		case 1015:
			return context.getString(R.string.error_code_1015);
		case 8888:
			return context.getString(R.string.error_code_8888);
		case 9999:
			return context.getString(R.string.error_code_9999);
		default:
			if (Util.isConnected(context)) {
				return context.getString(R.string.error_code_default);
			} else {
				return context.getString(R.string.can_not_connect_server)
						+ code;
			}
		}
	}
}
