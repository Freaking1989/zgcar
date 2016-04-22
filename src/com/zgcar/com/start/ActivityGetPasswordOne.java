package com.zgcar.com.start;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 输入注册用的手机号码
 * 
 */
public class ActivityGetPasswordOne extends Activity implements OnClickListener {

	private Button goNext;
	private EditText phoneNo;
	private String phoneNoStr;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		Quit.addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		initialize();
	}

	private void initialize() {
		ImageButton back = (ImageButton) findViewById(R.id.forget_psw_back);
		dialog = new Dialog(ActivityGetPasswordOne.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		goNext = (Button) findViewById(R.id.forget_next_bt);
		phoneNo = (EditText) findViewById(R.id.forget_psw_edittext);
		goNext.setOnClickListener(this);
		back.setOnClickListener(this);
		ImageButton clear = (ImageButton) findViewById(R.id.forget_psw_clear);
		clear.setOnClickListener(this);
	}

	/**
	 * 判断输入手机号是否有误
	 */
	private boolean isEmpty() {
		phoneNoStr = phoneNo.getText().toString();
		if (phoneNoStr.equals("")) {
			Util.showToastBottom(ActivityGetPasswordOne.this,
					getString(R.string.please_input_num));
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 获取手机验证码
	 */
	private void getPhoneCode() {
		dialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = getReqtestJson();
				if (SocketUtil.connectService(jsonString)) {
					dialog.dismiss();
					Intent intent = new Intent(ActivityGetPasswordOne.this,
							ActivityInputCode.class);
					intent.putExtra("phoneNo", phoneNoStr);
					startActivity(intent);
				} else {
					Looper.prepare();
					dialog.dismiss();
					Util.showToastBottom(ActivityGetPasswordOne.this,
							SocketUtil.isFail(ActivityGetPasswordOne.this));
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
			b2.put("number", phoneNoStr);
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
		case R.id.forget_next_bt:
			String country = getResources().getConfiguration().locale
					.getCountry();
			if (!isEmpty()) {
				if ("CN".equals(country)) {
					getPhoneCode();
				} else {
					Intent intent = new Intent(ActivityGetPasswordOne.this,
							ActivityInputCode.class);
					intent.putExtra("phoneNo", phoneNoStr);
					startActivity(intent);
				}
			}
			break;
		case R.id.forget_psw_back:
			finish();
			break;
		case R.id.forget_psw_clear:
			phoneNo.setText("");
			break;
		default:
			break;
		}

	}
}
