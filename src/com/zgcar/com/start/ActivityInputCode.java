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
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

//测试

/**
 * 
 * 输入注册用的手机收到的验证码
 * 
 */
public class ActivityInputCode extends Activity implements OnClickListener {
	private String phoneNo;
	private TextView phoneNoTv;
	private Button timer;
	private Dialog dialog;
	private int timeString;
	private EditText inputCode;
	private boolean isEn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		Quit.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_code);

		init();

	}

	private void init() {
		inputCode = (EditText) findViewById(R.id.input_code_edittext);
		ImageButton back = (ImageButton) findViewById(R.id.input_code_back);
		phoneNo = getIntent().getStringExtra("phoneNo");
		timer = (Button) findViewById(R.id.input_code_time_tv);
		phoneNoTv = (TextView) findViewById(R.id.input_code_textview);
		Button goNext = (Button) findViewById(R.id.input_code_bt_next);
		goNext.setOnClickListener(this);
		timer.setOnClickListener(this);
		back.setOnClickListener(this);
		String country = getResources().getConfiguration().locale.getCountry();
		TextView title = (TextView) findViewById(R.id.input_code_title);
		ImageButton scanImeiIb = (ImageButton) findViewById(R.id.input_code_time_ib);
		scanImeiIb.setOnClickListener(this);
		if ("CN".equals(country)) {
			isEn = false;
			initCnView(title, scanImeiIb);
		} else {
			isEn = true;
			initEnView(title, scanImeiIb);
		}

	}

	private void initCnView(TextView title, ImageButton scanImeiIb) {
		scanImeiIb.setVisibility(View.GONE);
		timer.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.get_code));
		timeString = 120;
		phoneNoTv.setText(getString(R.string.get_code_desc).replace("0000",
				phoneNo));
		handler.postDelayed(timeTask, 1000);
	}

	private void initEnView(TextView title, ImageButton scanImeiIb) {
		inputCode.setHint(getString(R.string.input_watch_imei));
		scanImeiIb.setVisibility(View.VISIBLE);
		timer.setVisibility(View.GONE);
		title.setText(getString(R.string.input_watch_imei));

	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityInputCode.this, R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private Runnable timeTask = new Runnable() {
		public void run() {
			handler.sendMessage(handler.obtainMessage(0));
			handler.postDelayed(timeTask, 1000);
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (timeString > 0) {
					timeString--;
					timer.setEnabled(false);
					timer.setText(timeString + "s");
				} else {
					timeString = 120;
					handler.removeCallbacks(timeTask);
					timer.setEnabled(true);
					timer.setText(getString(R.string.get_code_again));
				}
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.input_code_bt_next:
			String code = inputCode.getText().toString().trim();
			if (codeIsCanEffective(code)) {
				nextRequest(code);
			}
			break;

		case R.id.input_code_time_tv:
			getPhoneCode();
			break;
		case R.id.input_code_back:
			finish();
			break;
		case R.id.input_code_time_ib:
			startActivityForResult(new Intent(ActivityInputCode.this,
					ActivityRegisterScanning.class), 0);
			break;
		default:
			break;
		}

	}

	private boolean codeIsCanEffective(String code) {
		if ("".equals(code)) {
			Util.showToastBottom(ActivityInputCode.this,
					getString(R.string.input_code_has_false));
			return false;
		}
		if (isEn) {
			if (code.length() != 15) {
				Util.showToastBottom(ActivityInputCode.this,
						getString(R.string.input_code_has_false));
				return false;
			}
		} else {
			if (code.length() != 5) {
				Util.showToastBottom(ActivityInputCode.this,
						getString(R.string.input_code_has_false));
				return false;
			}
		}
		return true;
	}

	// {"cmd":"20003,"data":[{"code":"12345",”number”:”13717033460”}]}

	private void nextRequest(final String code) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = getRequestJson(code);
				boolean flag = SocketUtil.connectService(json);
				if (flag) {
					Intent intent = new Intent(ActivityInputCode.this,
							ActivityNewPassword.class);
					intent.putExtra("phoneNo", phoneNo);
					intent.putExtra("code", code);
					intent.putExtra("cmd",
							FinalVariableLibrary.CHANGE_USER_PSW_LOADING);
					startActivity(intent);
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivityInputCode.this,
							SocketUtil.isFail(ActivityInputCode.this));
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

	/**
	 * 获取手机验证码
	 */
	private void getPhoneCode() {
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = getReqtestJson();
				boolean flag = SocketUtil.connectService(jsonString);
				if (flag) {
					handler.postDelayed(timeTask, 1000);
					dismissDialog();
				} else {
					Looper.prepare();
					dismissDialog();
					Util.showToastBottom(ActivityInputCode.this,
							SocketUtil.isFail(ActivityInputCode.this));
					Looper.loop();
				}
			}
		}).start();
	}

	private String getReqtestJson() {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", FinalVariableLibrary.GET_BACK_PSW_CMD);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("number", phoneNo);
			array.put(b2);
			b.put("data", array);
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == 1) {
			String imeiStr = data.getStringExtra("imei");
			if (imeiStr.contains("ZG:")) {
				int start = imeiStr.indexOf("ZG:") + 3;
				String tempTextString = imeiStr.substring(start, start + 15);
				inputCode.setText(tempTextString);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
