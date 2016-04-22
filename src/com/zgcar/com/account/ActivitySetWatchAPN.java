package com.zgcar.com.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
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
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;

public class ActivitySetWatchAPN extends Activity implements OnClickListener {
	private EditText apnEt, nameEt, pswEt;
	private SharedPreferences sf;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_watch_apn);
		initialize();
	}

	private void initialize() {
		app = (MyApplication) getApplication();
		sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER, Context.MODE_PRIVATE);
		ImageButton back = (ImageButton) findViewById(R.id.set_watch_apn_back);
		back.setOnClickListener(this);
		Button confirm = (Button) findViewById(R.id.set_watch_apn_confirm);
		confirm.setOnClickListener(this);
		sf.getString("ApnName" + app.getImei(), "");
		sf.getString("ApnPsw" + app.getImei(), "");
		apnEt = (EditText) findViewById(R.id.set_watch_apn_apn_et);
		nameEt = (EditText) findViewById(R.id.set_watch_apn_name_et);
		pswEt = (EditText) findViewById(R.id.set_watch_apn_psw_et);
		apnEt.setText(sf.getString("Apn" + app.getImei(), ""));
		nameEt.setText(sf.getString("ApnName" + app.getImei(), ""));
		pswEt.setText(sf.getString("ApnPsw" + app.getImei(), ""));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_watch_apn_back:
			finish();
			break;
		case R.id.set_watch_apn_confirm:
			setApn();
			break;

		default:
			break;
		}
	}

	private void setApn() {
		Editor editor = sf.edit();
		String apnStr = apnEt.getText().toString().trim();
		String apnName = nameEt.getText().toString().trim();
		String apnPsw = pswEt.getText().toString().trim();
		editor.putString("Apn" + app.getImei(), apnStr);
		editor.putString("ApnName" + app.getImei(), apnName);
		editor.putString("ApnPsw" + app.getImei(), apnPsw);
		editor.commit();
		String msg = "860755#APN#" + apnStr + "," + apnName + "," + apnPsw
				+ "#";
		Uri smsToUri = Uri.parse("smsto:"
				+ ListInfosEntity.getTerminalListInfos().get(app.getPosition())
						.getNumber());
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", msg);
		startActivity(intent);
	}

}
