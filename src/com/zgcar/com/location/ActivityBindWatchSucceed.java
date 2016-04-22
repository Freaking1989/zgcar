package com.zgcar.com.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.zgcar.com.main.MainActivity;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.R;

public class ActivityBindWatchSucceed extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivityBindWatchSucceed.this,
				R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_watch_succeed);
		ImageView image = (ImageView) findViewById(R.id.activity_bind_succeed_imageView1);
		String country = getResources().getConfiguration().locale.getCountry();
		if (country.equals("CN") || country.equals("TW")) {
			image.setImageResource(R.drawable.ic_launcher);
		} else {
			image.setImageResource(R.drawable.ic_launcher);

		}
		Button button = (Button) findViewById(R.id.activity_bind_succeed_confirm);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityBindWatchSucceed.this,
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		Log.e("ActivityBindWatchSucceed", "我被销毁了");
		super.onDestroy();
	}

}
