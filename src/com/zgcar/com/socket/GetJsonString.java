package com.zgcar.com.socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetJsonString {

	/**
	 * ���ݸ�ʽ��:
	 * {"cmd":"00044","data":[{"imei":"861400000000088","para":0,"user_name"
	 * :0}]}
	 * 
	 * @param cmd
	 *            ָ��
	 * @param para
	 * 
	 * @param userName
	 *            ��ǰ�û��˺�
	 * 
	 * @return ��������json����
	 */
	public static String getRequestJson(String cmd, String imei, int para,
			String userName) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("imei", imei);
			jsonObject1.put("para", para);
			jsonObject1.put("user_name", userName);
			array.put(jsonObject1);
			jsonObject.put("data", array);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
