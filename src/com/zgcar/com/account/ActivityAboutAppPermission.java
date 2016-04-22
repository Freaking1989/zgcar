package com.zgcar.com.account;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;

public class ActivityAboutAppPermission extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app_permission);
		ImageButton back = (ImageButton) findViewById(R.id.account_about_app_sub_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
