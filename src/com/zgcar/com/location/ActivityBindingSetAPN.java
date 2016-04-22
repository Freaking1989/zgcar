package com.zgcar.com.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.util.SetTitleBackground;

public class ActivityBindingSetAPN extends Activity implements OnClickListener {
	private EditText apnEt, nameEt, pswEt;
	private String phoneNo, imei;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_apn_when_add_new);
		initialize();
	}

	private void initialize() {
		phoneNo = getIntent().getStringExtra("phoneNo");
		imei = getIntent().getStringExtra("imei");
		Button confirm = (Button) findViewById(R.id.set_apn_when_add_new_confirm);
		confirm.setOnClickListener(this);
		apnEt = (EditText) findViewById(R.id.set_apn_when_add_new_apn_et);
		nameEt = (EditText) findViewById(R.id.set_apn_when_add_new_name_et);
		pswEt = (EditText) findViewById(R.id.set_apn_when_add_new_psw_et);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_apn_when_add_new_confirm:
			setApn();
			break;

		default:
			break;
		}
	}

	private void setApn() {
		SharedPreferences sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER,
				Context.MODE_PRIVATE);
		Editor editor = sf.edit();
		String apnStr = apnEt.getText().toString().trim();
		String apnName = nameEt.getText().toString().trim();
		String apnPsw = pswEt.getText().toString().trim();
		editor.putString("Apn" + imei, apnStr);
		editor.putString("ApnName" + imei, apnName);
		editor.putString("ApnPsw" + imei, apnPsw);
		editor.commit();
		String msg = "860755#APN#" + apnStr + "," + apnName + "," + apnPsw+"#";
		Uri smsToUri = Uri.parse("smsto:" + phoneNo);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", msg);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			startActivity(new Intent(ActivityBindingSetAPN.this,
					ActivityBindWatchSucceed.class));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
