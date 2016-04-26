package com.zgcar.com.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MainActivity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 输入手机号进行注册或登录
 * 
 */
public class ActivityLoadingInputPhoneNo extends Activity implements
		OnClickListener {

	private EditText phoneNo;
	private Dialog dialog;
	private String num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		Quit.addCacheActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_phone_num);
		initialize();
		isAutoLoading();

	}

	private void isAutoLoading() {
		SharedPreferences sf = getSharedPreferences(
				FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
		num = sf.getString("userName", "");
		if (!num.equals("")) {
			phoneNo.setText(num);
		}
		boolean autoLoading = sf.getBoolean("autoLoading", false);
		String psw = sf.getString("userPsw", "");
		if (autoLoading) {
			if (!"".equals(num) && !"".equals(psw)) {
				Log.e("LoadingInputPhoneNoActivity", "userName:" + num
						+ ";userPsw:" + psw);
				MyApplication app = (MyApplication) getApplication();
				sendLodingReqtest(psw, sf, app);
			}
		}
	}

	private String getRequestData(String password) {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", FinalVariableLibrary.INPUT_PASSWOR_AND_LOADING);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			String language = getResources().getConfiguration().locale
					.getCountry();
			if (language.equals("CN") || language.equals("TW")) {
				FinalVariableLibrary.MAP_TYPE = 2;
				b2.put("language", "CN");
			} else {
				FinalVariableLibrary.MAP_TYPE = 1;
				b2.put("language", "EN");
			}
			b2.put("name", num);
			b2.put("password", password);
			array.put(b2);
			b.put("data", array);
			Log.e("b", b.toString());
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 自动登陆时，获取账号资料
	 */
	private void sendLodingReqtest(final String password,
			final SharedPreferences sf, final MyApplication app) {
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String string = getRequestData(password);
				boolean falg = SocketUtil.connectService(string);
				if (falg) {
					app.setUserName(num);
					Bitmap bitmap = ResolveServiceData.getUserBasics(sf,
							ActivityLoadingInputPhoneNo.this, app);
					FinalVariableLibrary.bitmap = bitmap;
					handler.sendMessage(handler.obtainMessage(0));
					return;
				} else {
					Looper.prepare();
					dismissDialog();
					Util.showToastBottom(ActivityLoadingInputPhoneNo.this,
							SocketUtil.isFail(ActivityLoadingInputPhoneNo.this));
					Looper.loop();
					return;
				}
			}
		}).start();
	}

	private void initialize() {
		ImageButton clear = (ImageButton) findViewById(R.id.loading_clear_ib);
		Button goNext = (Button) findViewById(R.id.loading_next_bt);
		phoneNo = (EditText) findViewById(R.id.loading_input_telphone_num);
		goNext.setOnClickListener(this);
		clear.setOnClickListener(this);
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityLoadingInputPhoneNo.this,
					R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_next_bt:
			if (editIsEmpty()) {
				Util.showToastBottom(ActivityLoadingInputPhoneNo.this,
						getString(R.string.please_input_num));
				break;
			}
			sendTextPhoneNo();
			break;

		case R.id.loading_clear_ib:
			if (editIsEmpty()) {
				Util.showToastBottom(ActivityLoadingInputPhoneNo.this,
						getString(R.string.please_input_num));
			}
			phoneNo.setText("");
			break;
		default:
			break;
		}
	}

	/**
	 * 检测手机号是否注册
	 */
	private void sendTextPhoneNo() {
		if (!Util.isConnected(ActivityLoadingInputPhoneNo.this)) {
			Util.showToastBottom(ActivityLoadingInputPhoneNo.this,
					getString(R.string.can_not_connect_server));
			return;
		}
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String requestJson = getJsondata(FinalVariableLibrary.TEST_PHONE_NUMBER);
				boolean flag = SocketUtil.connectService(requestJson);
				if (flag) {
					dismissDialog();
					Intent intent = new Intent(
							ActivityLoadingInputPhoneNo.this,
							ActivityLoadingInputPassword.class);
					intent.putExtra("phoneNo", phoneNo.getText().toString());
					startActivity(intent);
				} else {
					handler.sendMessage(handler.obtainMessage(1));
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:

				break;
			case 1:
				String language = getResources().getConfiguration().locale
						.getCountry();
				if (!language.equals("CN") && !language.equals("TW")) {
					Intent intent = new Intent(
							ActivityLoadingInputPhoneNo.this,
							ActivityRegisterInputIMEI.class);
					intent.putExtra("phoneNo", phoneNo.getText().toString());
					dismissDialog();
					startActivity(intent);
					break;
				}
				getPhoneCode();
				break;
			case 0:
				dismissDialog();
				startActivity(new Intent(ActivityLoadingInputPhoneNo.this,
						MainActivity.class));
				Util.showToastBottom(ActivityLoadingInputPhoneNo.this,
						getString(R.string.loading_succeed));
				Quit.recyclingCacheActivity();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 没有注册时，获取注册验证码
	 */
	private void getPhoneCode() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String requestJson = getJsondata(FinalVariableLibrary.GET_PHONE_CODE);
				boolean flag = SocketUtil.connectService(requestJson);
				if (flag) {
					Intent intent = new Intent(
							ActivityLoadingInputPhoneNo.this,
							ActivityRegisterInputCode.class);
					intent.putExtra("phoneNo", phoneNo.getText().toString());
					dismissDialog();
					startActivity(intent);
					return;
				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(ActivityLoadingInputPhoneNo.this,
							SocketUtil.isFail(ActivityLoadingInputPhoneNo.this));
					Looper.loop();
					return;
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private boolean editIsEmpty() {
		num = phoneNo.getText().toString();
		return "".equals(num) ? true : false;
	}

	/**
	 * 获取请求json数据
	 * 
	 * @param cmd
	 *            指令
	 */
	private String getJsondata(String cmd) {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("number", num);
			array.put(b2);
			b.put("data", array);
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
}
