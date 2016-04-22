package com.zgcar.com.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.zgcar.com.account.model.MessageInfos;
import com.zgcar.com.main.model.NotifyMessageEntity;

public class GetPushMessage {

	/**
	 * ��������Ϣ�����ɲ�ѯ�ֻ�������Ϣ
	 */
	public static MessageInfos getCheckPhoneMessage(JSONObject object) {
		try {
			MessageInfos info = new MessageInfos();
			info.setFlag(1);
			info.setMessage(object.getString("msg"));
			info.setTime(object.getString("time"));
			return info;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ��������Ϣ������ϵͳ��Ϣ
	 */
	public static NotifyMessageEntity getNotifyMessage(JSONObject object) {
		try {
			NotifyMessageEntity pushInfo = new NotifyMessageEntity();
			pushInfo.setAlarm(object.getString("alarm"));
			pushInfo.setGeo(object.getString("geo"));
			pushInfo.setImei(object.getString("imei"));
			pushInfo.setLa(object.getDouble("la"));
			pushInfo.setLitle(object.getString("litle"));
			pushInfo.setLo(object.getDouble("lo"));
			pushInfo.setMsg(object.getString("msg"));
			pushInfo.setTime(object.getString("time"));
			return pushInfo;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

}
