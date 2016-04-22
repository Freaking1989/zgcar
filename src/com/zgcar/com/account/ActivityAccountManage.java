package com.zgcar.com.account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.start.ActivityGetPasswordOne;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * ’Àªßπ‹¿Ì
 * 
 */
public class ActivityAccountManage extends Activity implements OnClickListener {
	private EditText oldPsw, newPsw1, newPsw2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_manage);
		initialize();
	}

	private void initialize() {
		Button finish = (Button) findViewById(R.id.account_manage_finish);
		ImageButton back = (ImageButton) findViewById(R.id.account_manage_back);
		oldPsw = (EditText) findViewById(R.id.account_manage_old_psw);
		newPsw1 = (EditText) findViewById(R.id.account_manage_new_psw1);
		newPsw2 = (EditText) findViewById(R.id.account_manage_new_psw2);
		Button forgetPsw = (Button) findViewById(R.id.account_manage_get_psw);
		back.setOnClickListener(this);
		forgetPsw.setOnClickListener(this);
		finish.setOnClickListener(this);
	}

	private boolean isCanConnection() {
		String oldPswStr = oldPsw.getText().toString().trim();
		String newPswStr1 = newPsw1.getText().toString().trim();
		String newPswStr2 = newPsw2.getText().toString().trim();
		if (oldPswStr.equals("")) {
			Util.showToastBottom(ActivityAccountManage.this,
					getString(R.string.input_old_password));
			return false;
		}
		if (newPswStr1.equals("")) {
			Util.showToastBottom(ActivityAccountManage.this,
					getString(R.string.input_new_password));
			return false;
		}
		if (newPswStr2.equals("")) {
			Util.showToastBottom(ActivityAccountManage.this,
					getString(R.string.please_input_confirm_password));
			return false;
		}

		if (newPswStr1.length() < 6 || newPswStr1.length() > 16
				|| newPswStr2.length() < 6 || newPswStr2.length() > 16) {
			Util.showToastBottom(ActivityAccountManage.this,
					getString(R.string.password_length_error));
			return false;
		}
		if (!newPswStr1.equals(newPswStr2)) {
			Util.showToastBottom(ActivityAccountManage.this,
					getString(R.string.new_password_not_consistent));
			return false;
		}

		return true;
	}

	private String getRequestJsonStr() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.CHANGE_USER_PSW);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			SharedPreferences sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
			object1.put("name", sf.getString("userName", ""));
			object1.put("password", oldPsw.getText().toString().trim());
			object1.put("new_password", newPsw1.getText().toString().trim());
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void connectService() {

		if (isCanConnection()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String jsonStr = getRequestJsonStr();
					if (SocketUtil.connectService(jsonStr)) {
						Looper.prepare();
						Util.showToastBottom(ActivityAccountManage.this,
								getString(R.string.change_succeed));
						finish();
						Looper.loop();
					} else {
						Looper.prepare();
						Util.showToastBottom(ActivityAccountManage.this,
								SocketUtil
										.isFail(ActivityAccountManage.this));
						Looper.loop();
					}
				}
			}).start();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.account_manage_get_psw:
			startActivity(new Intent(ActivityAccountManage.this,
					ActivityGetPasswordOne.class));
			break;
		case R.id.account_manage_back:
			finish();
			break;
		case R.id.account_manage_finish:
			connectService();
			break;
		default:
			break;
		}

	}
}
