package com.zgcar.com.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
 *  ‰»Îµ«¬º√‹¬Î
 * 
 */
public class ActivityLoadingInputPassword extends Activity implements
		OnClickListener {

	private String phoneNo;
	private EditText psw;
	private Dialog dialog;
	private SharedPreferences sf;
	private MyApplication app;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				dialog.dismiss();
				startActivity(new Intent(ActivityLoadingInputPassword.this,
						MainActivity.class));
				Util.showToastBottom(ActivityLoadingInputPassword.this,
						getString(R.string.loading_succeed));
				Quit.recyclingCacheActivity();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		Quit.addCacheActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_input_password);
		initialze();
	}

	private void initialze() {
		app = (MyApplication) getApplication();
		sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER,
				MODE_PRIVATE);
		dialog = new Dialog(ActivityLoadingInputPassword.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		Intent intent = getIntent();
		phoneNo = intent.getStringExtra("phoneNo");
		ImageButton back = (ImageButton) findViewById(R.id.input_psd_back);
		psw = (EditText) findViewById(R.id.input_psd_edittext);
		psw.setText(sf.getString("userPsw", ""));
		Button forgetPsd = (Button) findViewById(R.id.forget_password);
		Button goNext = (Button) findViewById(R.id.loading_input_password);
		forgetPsd.setOnClickListener(this);
		goNext.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	private void sendLodingReqtest(final String password) {
		dialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String string = getRequestData(password);
				boolean falg = SocketUtil.connectService(string);
				if (falg) {
					app.setUserName(phoneNo);
					Editor editor = sf.edit();
					editor.putString("userName", phoneNo);
					editor.putString("userPsw", password);
					editor.putBoolean("autoLoading", true);
					editor.commit();
					FinalVariableLibrary.bitmap = ResolveServiceData
							.getUserBasics(sf,
									ActivityLoadingInputPassword.this, app);
					handler.sendMessage(handler.obtainMessage(0));
					return;
				} else {
					Looper.prepare();
					dialog.dismiss();
					Util.showToastBottom(ActivityLoadingInputPassword.this,
							SocketUtil
									.isFail(ActivityLoadingInputPassword.this));
					Looper.loop();
					return;
				}
			}
		}).start();
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
			b2.put("name", phoneNo);
			b2.put("password", password);
			array.put(b2);
			b.put("data", array);
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_input_password:
			String password = psw.getText().toString().trim();
			if (password.equals("")) {
				Util.showToastBottom(ActivityLoadingInputPassword.this,
						getString(R.string.please_input_password));
				break;
			}
			sendLodingReqtest(password);
			break;

		case R.id.forget_password:
			startActivity(new Intent(ActivityLoadingInputPassword.this,
					ActivityGetPasswordOne.class));
			break;
		case R.id.input_psd_back:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
}
