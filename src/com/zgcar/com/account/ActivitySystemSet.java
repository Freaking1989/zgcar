package com.zgcar.com.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.start.ActivityLoadingInputPhoneNo;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;

public class ActivitySystemSet extends Activity implements OnClickListener {

	private MyApplication app;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_set);
		init();
	}

	private void init() {
		app = (MyApplication) getApplication();
		LinearLayout setMessageWearning = (LinearLayout) findViewById(R.id.system_set_message_remind);
		LinearLayout changePsw = (LinearLayout) findViewById(R.id.system_set_change_psw);
		LinearLayout map = (LinearLayout) findViewById(R.id.system_set_map);
		LinearLayout suggest = (LinearLayout) findViewById(R.id.system_set_suggest);
		LinearLayout about = (LinearLayout) findViewById(R.id.system_set_about);
		ImageButton back = (ImageButton) findViewById(R.id.system_set_back);
		Button quit = (Button) findViewById(R.id.system_set_sign_out);
		String language = getResources().getConfiguration().locale.getCountry();
		if ("CN".equals(language)) {
			map.setVisibility(View.VISIBLE);
		} else {
			map.setVisibility(View.GONE);
		}
		quit.setOnClickListener(this);
		back.setOnClickListener(this);
		setMessageWearning.setOnClickListener(this);
		changePsw.setOnClickListener(this);
		map.setOnClickListener(this);
		suggest.setOnClickListener(this);
		about.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.system_set_back:
			finish();
			break;
		case R.id.system_set_message_remind:
			startActivity(new Intent(ActivitySystemSet.this,
					ActivityMessageWarn.class));
			break;

		case R.id.system_set_change_psw:
			startActivity(new Intent(ActivitySystemSet.this,
					ActivityAccountManage.class));
			break;

		case R.id.system_set_map:
			startActivity(new Intent(ActivitySystemSet.this,
					ActivityOfflineProvince.class));
			break;

		case R.id.system_set_suggest:
			if (!app.imeiIsEmpty(ActivitySystemSet.this, true)) {
				startActivity(new Intent(ActivitySystemSet.this,
						ActivityFeedBack.class));
			}
			break;
		case R.id.system_set_about:
			startActivity(new Intent(ActivitySystemSet.this,
					ActivityAboutApp.class));
			break;
		case R.id.system_set_sign_out:
			showQuitViewDialog();
			break;
		case R.id.view_dialog_yes:
			dismissDialog();
			JPushInterface.stopPush(ActivitySystemSet.this);
			app.setPosition(0);
			app.setImei("");
			app.setFirst(true);
			Editor editor = getSharedPreferences(
					FinalVariableLibrary.CACHE_FOLDER, Context.MODE_PRIVATE)
					.edit();
			editor.putBoolean("autoLoading", false);
			editor.putString("userPsw", "");
			editor.commit();
			startActivity(new Intent(ActivitySystemSet.this,
					ActivityLoadingInputPhoneNo.class));
			Quit.recycling(0);
			Quit.quit();
			break;
		case R.id.view_dialog_no:
			dismissDialog();
			break;
		default:
			break;
		}
	}

	private void showQuitViewDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivitySystemSet.this, R.style.dialog);
		}
		View view1 = View.inflate(ActivitySystemSet.this,
				R.layout.view_dialog_yes_or_not, null);
		TextView title = (TextView) view1.findViewById(R.id.view_dialog_title);
		Button yes = (Button) view1.findViewById(R.id.view_dialog_no);
		Button no = (Button) view1.findViewById(R.id.view_dialog_yes);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		title.setText(getString(R.string.quit_and_reloading));
		dialog.setContentView(view1);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
