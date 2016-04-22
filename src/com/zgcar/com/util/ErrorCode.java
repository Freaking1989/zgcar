package com.zgcar.com.util;

import android.content.Context;
import android.util.Log;

import com.zgcar.com.R;

public class ErrorCode {
	/**
	 * 1001 短信校验网关欠费 1002 SQL数据库出错 1003 用户名或密码错误 1004 用户名不可用 9999 Json 格式错误 1005
	 * 短信验证码错误 1006 创建组出错 1007 此终端无记录 1008 错误的组ID 1009 数据库无默认组 1010 无此终端 1011
	 * 围栏名称重复 1012 无此电子围栏 1013 指令发送失败,终端不在线
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
