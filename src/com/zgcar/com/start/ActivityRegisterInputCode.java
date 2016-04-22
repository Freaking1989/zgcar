package com.zgcar.com.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 注册账号
 * 
 */
public class ActivityRegisterInputCode extends Activity implements
		OnClickListener {

	private TextView registerInputNum, getMessageCode;
	private Dialog dialog;
	private String phoneNo;
	private int timeString;
	private EditText code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_input_code);
		initialize();
	}

	/**
	 * 请求发送验证码
	 */
	private Runnable timeTask = new Runnable() {
		@Override
		public void run() {
			handler.sendMessage(handler.obtainMessage(0));
			handler.postDelayed(timeTask, 1000);
		}
	};

	private void sendRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = getRequestJson();
				if (SocketUtil.connectService(json)) {
					handler.postDelayed(timeTask, 1000);
					dialog.dismiss();
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivityRegisterInputCode.this,
							SocketUtil.isFail(ActivityRegisterInputCode.this));
					dialog.dismiss();
					Looper.loop();
				}
			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				if (timeString > 0) {
					getMessageCode.setEnabled(false);
					getMessageCode.setText(timeString + "s");
					timeString--;
				} else {
					getMessageCode.setEnabled(true);
					getMessageCode.setText(getString(R.string.get_code_again));
				}
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * 构造获取发送验证码的请求数据
	 */
	private String getRequestJson() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", FinalVariableLibrary.GET_PHONE_CODE);
			JSONArray array = new JSONArray();
			JSONObject b = new JSONObject();
			b.put("number", phoneNo);
			array.put(b);
			jsonObject.put("data", array);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 初始化
	 */
	@SuppressLint("InflateParams")
	private void initialize() {
		timeString = 120;
		dialog = new Dialog(ActivityRegisterInputCode.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		code = (EditText) findViewById(R.id.register_edittext_code);
		registerInputNum = (TextView) findViewById(R.id.register_input_num);
		Intent intent = getIntent();
		ImageButton back = (ImageButton) findViewById(R.id.register_back);
		getMessageCode = (TextView) findViewById(R.id.register_get_code);
		phoneNo = intent.getStringExtra("phoneNo");
		Button goNext = (Button) findViewById(R.id.register_button_next);
		registerInputNum.setText(getString(R.string.get_code_desc).replace(
				"0000", phoneNo));
		goNext.setOnClickListener(this);
		back.setOnClickListener(this);
		getMessageCode.setOnClickListener(this);
		handler.postDelayed(timeTask, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_button_next:
			String codeStr = code.getText().toString().trim();
			if (!codeStr.equals("") && codeStr.length() == 5) {
				nextRequest(codeStr);
			} else {
				Util.showToastBottom(ActivityRegisterInputCode.this,
						getString(R.string.input_code_has_false));
			}
			break;
		case R.id.register_get_code:
			timeString = 120;
			dialog.show();
			sendRequest();
			break;
		case R.id.register_back:
			finish();
			break;

		default:
			break;
		}

	}

	private void nextRequest(final String code) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = getRequestJson(code);
				Log.e("json", json);
				boolean flag = SocketUtil.connectService(json);
				if (flag) {
					Intent intent = new Intent(ActivityRegisterInputCode.this,
							ActivityNewPassword.class);
					intent.putExtra("code", code);
					intent.putExtra("phoneNo", phoneNo);
					intent.putExtra("cmd",
							FinalVariableLibrary.SET_REGISTER_PSW_CMD);
					startActivity(intent);
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivityRegisterInputCode.this,
							SocketUtil.isFail(ActivityRegisterInputCode.this));
					Looper.loop();
				}
			}
		}).start();
	}

	private String getRequestJson(String code) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", FinalVariableLibrary.GET_CODE_STATE_CMD);
			JSONArray array = new JSONArray();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("code", code);
			jsonObject1.put("number", phoneNo);
			array.put(jsonObject1);
			jsonObject.put("data", array);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}
}
