package com.zgcar.com.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityBindingSim extends Activity implements OnClickListener {

	private EditText phoneNum;
	private String imei, sim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivityBindingSim.this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding_sim);
		init();
	}

	private void init() {
		phoneNum = (EditText) findViewById(R.id.sim_binding_telphone_num);
		Button save = (Button) findViewById(R.id.sim_binding_save);
		save.setOnClickListener(this);
		ImageButton back = (ImageButton) findViewById(R.id.sim_binding_back);
		back.setOnClickListener(this);
		imei = getIntent().getStringExtra("imei");
	}

	@Override
	protected void onDestroy() {
		Log.e("ActivityBindingSim", "我被销毁了");
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sim_binding_save:// 保存并发送指令绑定考拉
			sim = phoneNum.getText().toString().trim();
			Log.e("sim", sim);
			if (sim.equals("")) {
				Util.showToastBottom(ActivityBindingSim.this,
						getString(R.string.sim_can_not_null));
				break;
			}
			newkalaRequest();
			break;
		case R.id.sim_binding_back:
			finish();
			break;
		default:
			break;
		}
	}

	private String getRequestJson() {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", FinalVariableLibrary.ADD_SZBUS_CMA);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("username", ((MyApplication) getApplication()).getUserName());
			b2.put("imei", imei);
			b2.put("number", sim);
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

	private void newkalaRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = getRequestJson();
				boolean flag = SocketUtil.connectService(json);
				// 服务器是否连接成功
				if (flag) {
					MyApplication app = (MyApplication) getApplication();
					app.setImei("");
					app.setPosition(0);
					String country = getResources().getConfiguration().locale
							.getCountry();
					if (country.equals("CN")) {
						startActivity(new Intent(ActivityBindingSim.this,
								ActivityBindWatchSucceed.class));
					} else {
						Intent intent = new Intent(ActivityBindingSim.this,
								ActivityBindingSetAPN.class);
						intent.putExtra("phoneNo", sim);
						intent.putExtra("imei", imei);
						startActivity(intent);
					}

				} else {
					handler.sendMessage(handler.obtainMessage(0));
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
				if (1014 == SocketUtil.errorCode) {

				} else {
					Util.showToastBottom(ActivityBindingSim.this,
							SocketUtil.isFail(ActivityBindingSim.this));
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

}
