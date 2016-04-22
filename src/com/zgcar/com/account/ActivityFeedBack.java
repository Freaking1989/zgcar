package com.zgcar.com.account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
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
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityFeedBack extends Activity implements OnClickListener {
	private EditText content;
	private Button submitBt;
	private String jsonStr, contentStr;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);
		initialize();
	}

	private void initialize() {
		dialog = new Dialog(ActivityFeedBack.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		ImageButton back = (ImageButton) findViewById(R.id.feed_back_ib);
		content = (EditText) findViewById(R.id.feed_back_content);
		submitBt = (Button) findViewById(R.id.feed_back_submit);
		back.setOnClickListener(this);
		submitBt.setOnClickListener(this);
	}

	private void connectService() {
		if (!isEmpty()) {
			MyApplication app = (MyApplication) getApplication();
			if (!app.imeiIsEmpty(ActivityFeedBack.this, true)) {
				dialog.show();
				getRequestJsonStr(app.getImei());
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (SocketUtil.connectService(jsonStr)) {
							Looper.prepare();
							Util.showToastBottom(
									ActivityFeedBack.this,
									getString(R.string.information_has_hand_in));
							dialog.dismiss();
							finish();
							Looper.loop();
						} else {
							Looper.prepare();
							dialog.dismiss();
							Util.showToastBottom(ActivityFeedBack.this,
									SocketUtil.isFail(ActivityFeedBack.this));
							Looper.loop();
						}

					}
				}).start();
			}
		} else {
			Util.showToastBottom(ActivityFeedBack.this,
					getString(R.string.please_input_suggestion));
		}
	}

	private void getRequestJsonStr(String imei) {
		try {

			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.FEED_BACK_CMD);
			JSONArray array = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("imei", imei);
			SharedPreferences sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
			jsonObject.put("user_name", sf.getString("userName", ""));
			jsonObject.put("text", contentStr);
			array.put(jsonObject);
			object.put("data", array);
			jsonStr = object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private boolean isEmpty() {
		contentStr = content.getText().toString().trim();
		return contentStr.equals("") ? true : false;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.feed_back_ib:
			finish();
			break;
		case R.id.feed_back_submit:
			connectService();
			break;
		default:
			break;
		}

	}
}
