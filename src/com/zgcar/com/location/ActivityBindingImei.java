package com.zgcar.com.location;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityBindingImei extends Activity implements OnClickListener {

	private Button imeibinding_next_imeibt;
	private EditText imeibinding_edit_ed;
	private String imeiStr;
	private Dialog dialog;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground
				.setTitleBg(ActivityBindingImei.this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_imei);
		init();
	}

	@Override
	protected void onDestroy() {
		Log.e("ActivityBindingImei", "我被销毁了");
		super.onDestroy();
	}

	private void init() {
		// 绑定imei按钮
		app = (MyApplication) getApplication();
		imeibinding_next_imeibt = (Button) findViewById(R.id.imeibinding_next_imeibt);
		imeibinding_next_imeibt.setOnClickListener(this);
		// 输入绑定IMEI文本框
		imeibinding_edit_ed = (EditText) findViewById(R.id.imeibinding_edit_ed);
		imeibinding_edit_ed.setOnClickListener(this);
		// 返回
		ImageButton kala = (ImageButton) findViewById(R.id.input_imei_back);
		kala.setOnClickListener(this);
		String text = getIntent().getStringExtra("imei");
		if (text.contains("ZG:")) {
			int start = text.indexOf("ZG:") + 3;
			String tempTextString = text.substring(start, start + 15);
			imeibinding_edit_ed.setText(tempTextString);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imeibinding_next_imeibt:
			imeiStr = imeibinding_edit_ed.getText().toString().trim();
			if (imeiStr.length() == 0) {
				Util.showToastBottom(ActivityBindingImei.this,
						getString(R.string.imei_can_not_null));
			} else {
				if (imeiIsRepeatBinding()) {
					Util.showToastBottom(ActivityBindingImei.this,
							getString(R.string.error_code_1015));
				} else {
					showProgressDialog();
					checkImeiRequest(getCheckImeiJson(), true);
				}
			}
			break;
		case R.id.input_imei_back:
			finish();
			break;
		default:
			break;
		}

	}

	private boolean imeiIsRepeatBinding() {
		List<TerminalListInfos> list = ListInfosEntity.getTerminalListInfos();
		for (TerminalListInfos terminalListInfos : list) {
			if (terminalListInfos.getImei().equals(imeiStr)) {
				return true;
			}
		}
		return false;
	}

	private void checkImeiRequest(final String json, final boolean isCheckImei) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (SocketUtil.connectService(json)) {
					dismissDialog();
					if (isCheckImei) {
						Intent in = new Intent();
						in.setClass(ActivityBindingImei.this,
								ActivityBindingSim.class);
						in.putExtra("imei", imeiStr);
						startActivity(in);
					} else {
						app.setImei("");
						app.setPosition(0);
						startActivity(new Intent(ActivityBindingImei.this,
								ActivityBindWatchSucceed.class));
					}
				} else {
					if (isCheckImei) {
						if (1 == SocketUtil.errorCode) {
							checkImeiRequest(getRequestJson(), false);
							/**
							 * 发送绑定请求
							 * **/
							return;
						}
					}
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(ActivityBindingImei.this,
							SocketUtil.isFail(ActivityBindingImei.this));
					Looper.loop();

				}
			}
		}).start();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityBindingImei.this, R.style.dialog);
			dialog.setContentView(R.layout.view_progress_dialog);
			dialog.setCancelable(false);
		}
		dialog.show();
	}

	private String getRequestJson() {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", FinalVariableLibrary.ADD_SZBUS_CMA);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("username", app.getUserName());
			b2.put("imei", imeiStr);
			b2.put("number", "");
			b2.put("name", "爱车");
			b2.put("age", 0);
			b2.put("height", 0);
			b2.put("weight", 0);
			b2.put("sex", "1");
			b2.put("birthday", Util.getTodayDate());
			b2.put("image_flag", 0);
			b2.put("u_name", "");// 称谓
			array.put(b2);
			b.put("data", array);
			Log.e("json", b.toString());
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String getCheckImeiJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", FinalVariableLibrary.CHECK_IMEI_CMD);
			JSONArray array = new JSONArray();
			JSONObject json1 = new JSONObject();
			json1.put("username",
					((MyApplication) getApplication()).getUserName());
			json1.put("imei", imeiStr);
			array.put(json1);
			json.put("data", array);
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}
}
