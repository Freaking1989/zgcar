package com.zgcar.com.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * ◊¢≤·’À∫≈
 * 
 */
public class ActivityRegisterInputIMEI extends Activity implements
		OnClickListener {

	private Dialog dialog;
	private String phoneNo;
	private EditText imei;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_input_imei);
		initialize();
	}

	/**
	 * ≥ı ºªØ
	 */
	@SuppressLint("InflateParams")
	private void initialize() {
		imei = (EditText) findViewById(R.id.register_input_imei_et);
		ImageButton scanningImei = (ImageButton) findViewById(R.id.register_input_imei_scanning_ib);
		Intent intent = getIntent();
		ImageButton back = (ImageButton) findViewById(R.id.register_input_imei_back);
		phoneNo = intent.getStringExtra("phoneNo");
		Button goNext = (Button) findViewById(R.id.register_input_imei_next_bt);
		goNext.setOnClickListener(this);
		back.setOnClickListener(this);
		scanningImei.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_input_imei_next_bt:
			String imeiStr = imei.getText().toString().trim();
			if (imeiStr.length() != 15) {
				Util.showToastBottom(ActivityRegisterInputIMEI.this,
						getString(R.string.please_input_imei_desc));
			} else {
				showProgressDialog();
				nextRequest(imeiStr);
			}
			break;
		case R.id.register_input_imei_back:
			finish();
			break;
		case R.id.register_input_imei_scanning_ib:
			startActivityForResult(new Intent(ActivityRegisterInputIMEI.this,
					ActivityRegisterScanning.class), 0);
			break;
		default:
			break;
		}

	}

	private void nextRequest(final String imei) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = getRequestJson(imei);
				boolean flag = SocketUtil.connectService(json);
				if (flag) {
					dismissDialog();
					Intent intent = new Intent(ActivityRegisterInputIMEI.this,
							ActivityNewPassword.class);
					intent.putExtra("code", imei);
					intent.putExtra("phoneNo", phoneNo);
					intent.putExtra("cmd",
							FinalVariableLibrary.SET_REGISTER_PSW_CMD);
					startActivity(intent);
				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(ActivityRegisterInputIMEI.this,
							SocketUtil.isFail(ActivityRegisterInputIMEI.this));
					Looper.loop();
				}
			}
		}).start();
	}

	private String getRequestJson(String imei) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", FinalVariableLibrary.GET_CODE_STATE_CMD);
			JSONArray array = new JSONArray();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("code", imei);
			jsonObject1.put("number", phoneNo);
			array.put(jsonObject1);
			jsonObject.put("data", array);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == 1) {
			String imeiStr = data.getStringExtra("imei");
			if (imeiStr.contains("ZG:")) {
				int start = imeiStr.indexOf("ZG:") + 3;
				String tempTextString = imeiStr.substring(start, start + 15);
				imei.setText(tempTextString);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityRegisterInputIMEI.this, R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

	}
}
