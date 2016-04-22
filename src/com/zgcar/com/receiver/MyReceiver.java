package com.zgcar.com.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.account.ActivityCheckPhone;
import com.zgcar.com.main.MainActivity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.start.ActivityLoadingInputPhoneNo;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private Bundle bundle;
	private MyApplication app;

	@Override
	public void onReceive(Context context, Intent intent) {
		app = (MyApplication) context.getApplicationContext();
		bundle = intent.getExtras();

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// processCustomMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			app.addPushBundle(bundle);
			Intent intent3 = new Intent(context, MyPushService.class);
			intent3.putExtras(bundle);
			context.startService(intent3);
			Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.e(TAG, "[MyReceiver] 用户点击打开了通知");
			goActivity(context);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.e(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	private void goActivity(Context context) {
		if (bundle != null) {
			try {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				JSONObject object = new JSONObject(
						bundle.getString(JPushInterface.EXTRA_EXTRA));

				if (app.imeiIsEmpty(context, false) || app.userNameIsEmpty()) {
					intent.setClass(context, ActivityLoadingInputPhoneNo.class);
					context.startActivity(intent);
					return;
				}

				Log.e("bundle", object.toString());
				int flag = object.getInt("flag");
				init(object);
				// 打开自定义的Activity
				if (flag == 2 || flag == 12) {
					intent.putExtra("PUSHTYPE", 1);
					intent.setClass(context, MainActivity.class);
					((MyApplication) context.getApplicationContext())
							.setVoicePush(true);
				} else if (flag == 3) {
					intent.setClass(context, ActivityCheckPhone.class);
				} else if (flag == 99) {
					String uriStr = object.getString("link");
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(uriStr));
				} else {
					intent.putExtra("PUSHTYPE", 2);
					intent.setClass(context, MainActivity.class);
				}
				context.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 将全局中保存的当前终端切换为接收到的消息终端
	 * 
	 * @param app
	 * @param object
	 */
	private void init(JSONObject object) {
		try {
			String imei = object.getString("imei");
			if (!"".equals(imei)) {
				app.setImei(imei);
				if (ListInfosEntity.getTerminalListInfos() != null
						&& ListInfosEntity.getTerminalListInfos().size() > 0) {
					for (int i = 0; i < ListInfosEntity.getTerminalListInfos()
							.size(); i++) {
						if (ListInfosEntity.getTerminalListInfos().get(i)
								.getImei().equals(imei)) {
							app.setPosition(i);
							break;
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
