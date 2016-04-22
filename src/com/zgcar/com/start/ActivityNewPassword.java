package com.zgcar.com.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
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

/**
 * 注册设置密码或者账户重置密码
 * 
 */
public class ActivityNewPassword extends Activity implements OnClickListener {

	private Button finish;
	private String phoneNo;
	private String code;
	private EditText pswText1, pswText2;
	private Dialog dialog;
	private String cmd;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		initialize();
	}

	private void initialize() {
		ImageButton back = (ImageButton) findViewById(R.id.change_psw_back);
		pswText1 = (EditText) findViewById(R.id.change_psw_text1);
		pswText2 = (EditText) findViewById(R.id.change_psw_text2);
		Intent intent = getIntent();
		phoneNo = intent.getStringExtra("phoneNo");
		code = intent.getStringExtra("code");
		cmd = intent.getStringExtra("cmd");
		title = (TextView) findViewById(R.id.changepsd_title_tv);
		if (cmd.equals(FinalVariableLibrary.SET_REGISTER_PSW_CMD)) {
			title.setText(getString(R.string.set_password));
		} else if (cmd.equals(FinalVariableLibrary.CHANGE_USER_PSW_LOADING)) {
			title.setText(getString(R.string.change_password));
		}
		finish = (Button) findViewById(R.id.new_password_finish);
		finish.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	private void sendRegisterRequrst() {
		String psw1 = pswText1.getText().toString().trim();
		final String psw2 = pswText2.getText().toString().trim();
		if ("".equals(psw1)) {
			Util.showToastBottom(ActivityNewPassword.this,
					getString(R.string.please_input_password));
			return;
		}
		if ("".equals(psw2)) {
			Util.showToastBottom(ActivityNewPassword.this,
					getString(R.string.please_input_password_again));
			return;
		}
		if (psw1.length() < 6 || psw1.length() > 16 || psw2.length() < 6
				|| psw2.length() > 16) {
			Util.showToastBottom(ActivityNewPassword.this,
					getString(R.string.password_length_error));
			return;
		}
		if (psw1.equals(psw2)) {
			showProgressDialog();
			new Thread(new Runnable() {
				@Override
				public void run() {
					String json = getJsonData(psw2);
					boolean flag = SocketUtil.connectService(json);
					if (flag) {
						dismissDialog();
						Intent intent1 = new Intent(ActivityNewPassword.this,
								ActivityLoadingInputPhoneNo.class);
						Editor editor = getSharedPreferences(
								FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE)
								.edit();
						editor.putBoolean("autoLoading", false).commit();
						intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent1);
						Quit.quit();
						finish();
					} else {
						dismissDialog();
						Looper.prepare();
						Util.showToastBottom(ActivityNewPassword.this,
								SocketUtil.isFail(ActivityNewPassword.this));
						Looper.loop();
					}
				}
			}).start();
		} else {
			Util.showToastBottom(ActivityNewPassword.this,
					getString(R.string.new_password_not_consistent));
		}
	}

	/**
	 * 构造请求的json数据
	 */
	private String getJsonData(String psw) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject b = new JSONObject();
			b.put("name", "" + phoneNo);
			b.put("password", psw);
			b.put("code", code + "");
			array.put(b);
			jsonObject.put("data", array);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityNewPassword.this, R.style.dialog);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_password_finish:
			sendRegisterRequrst();
			break;
		case R.id.change_psw_back:
			finish();
			break;
		default:
			break;
		}

	}

}
