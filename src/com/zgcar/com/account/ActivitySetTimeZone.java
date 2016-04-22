package com.zgcar.com.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.wheelview.OnWheelChangedListener;
import com.zgcar.com.wheelview.WheelView;
import com.zgcar.com.wheelview.adapters.ArrayWheelAdapter;

public class ActivitySetTimeZone extends Activity implements OnClickListener {

	private String timeZoneStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time_zone);
		init();
		initWheelView();
	}

	private void initWheelView() {
		WheelView wheelView1 = (WheelView) findViewById(R.id.set_time_zone_wheelView1);
		final WheelView hourWheelView = (WheelView) findViewById(R.id.set_time_zone_wheelView2);
		WheelView wheelView3 = (WheelView) findViewById(R.id.set_time_zone_wheelView3);
		final WheelView minWheelView = (WheelView) findViewById(R.id.set_time_zone_wheelView4);
		ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
				ActivitySetTimeZone.this, new String[] { "UTC" });
		ArrayWheelAdapter<String> adapter3 = new ArrayWheelAdapter<String>(
				ActivitySetTimeZone.this, new String[] { ":" });
		wheelView1.setViewAdapter(adapter1);
		wheelView3.setViewAdapter(adapter3);
		wheelView1.setTextSize(23);
		wheelView3.setTextSize(23);
		wheelView1.setEnabled(false);
		wheelView3.setEnabled(false);
		final String[] str1 = { "-12", "-11", "-10", "-9", "-8", "-7", "-6",
				"-5", "-4", "-3", "-2", "-1", "0", "+1", "+2", "+3", "+4",
				"+5", "+6", "+7", "+8", "+9", "+10", "+11", "+12", "+13", "+14" };
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
				ActivitySetTimeZone.this, str1);
		final String[] str = { "00", "15", "30", "45" };
		ArrayWheelAdapter<String> adapter2 = new ArrayWheelAdapter<String>(
				ActivitySetTimeZone.this, str);
		hourWheelView.setTextSize(23);
		minWheelView.setTextSize(23);
		hourWheelView.setViewAdapter(adapter);
		hourWheelView.setCyclic(true);
		minWheelView.setViewAdapter(adapter2);
		hourWheelView.setCurrentItem(20);
		minWheelView.setCurrentItem(0);

		// UTC+14:00
		hourWheelView.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				timeZoneStr = "860755#" + "ZONE#" + str1[newValue] + ":"
						+ str[minWheelView.getCurrentItem()] + "#";
			}
		});
		// 860755#ZONE#-12:30#
		minWheelView.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				timeZoneStr = "860755#" + "ZONE#"
						+ str1[hourWheelView.getCurrentItem()] + ":"
						+ str[newValue] + "#";
			}
		});
	}

	private void init() {
		timeZoneStr = "860755#" + "ZONE#+8:00#";
		Button sure = (Button) findViewById(R.id.set_time_zone_confirm);
		sure.setOnClickListener(this);
		ImageButton back = (ImageButton) findViewById(R.id.set_time_zone_back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_time_zone_confirm:
			setZone();
			break;
		case R.id.set_time_zone_back:
			finish();
			break;
		default:
			break;
		}

	}

	private void setZone() {
		MyApplication app = (MyApplication) getApplication();
		Uri smsToUri = Uri.parse("smsto:"
				+ ListInfosEntity.getTerminalListInfos().get(app.getPosition())
						.getNumber());
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", timeZoneStr);
		startActivity(intent);
	}
}
