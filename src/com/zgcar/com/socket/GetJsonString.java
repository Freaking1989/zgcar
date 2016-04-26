package com.zgcar.com.socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zgcar.com.entity.FinalVariableLibrary;

public class GetJsonString {

	/**
	 * 数据格式如:
	 * {"cmd":"00044","data":[{"imei":"861400000000088","para":0,"user_name"
	 * :0}]}
	 * 
	 * @param cmd
	 *            指令
	 * @param para
	 * 
	 * @param userName
	 *            当前用户账号
	 * 
	 * @return 返回请求json数据
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

	/**
	 * {"cmd":"00111","data":[{"imei":"861400000000088"," map_type ":"1",
	 * " starttime ":"2015-03-21 10:30"," stoptime ":"2015-03-21 10:30"}]}
	 */
	public static String getRequestJson(String cmd, String imei,
			String starttime, String stoptime) {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject object2 = new JSONObject();
			object2.put("imei", imei);
			object2.put("map_type", FinalVariableLibrary.MAP_TYPE);
			object2.put("starttime", starttime);
			object2.put("stoptime", stoptime);
			array.put(object2);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
