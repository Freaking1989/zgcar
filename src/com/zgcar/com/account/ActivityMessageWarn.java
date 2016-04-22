package com.zgcar.com.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.util.MyTextView;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;
import com.zgcar.com.wheelview.WheelView;
import com.zgcar.com.wheelview.adapters.ArrayWheelAdapter;

public class ActivityMessageWarn extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private CheckBox isReceiveNotify;
	private CheckBox notifyRing;
	private CheckBox notifyShake;
	private CheckBox notifyNight;
	private SharedPreferences sf;
	private boolean flag1, flag2, flag3, flag4;
	private Editor editor;
	private LinearLayout selectTime;
	private WheelView wheelView1, wheelView3;
	private String timePeriod;
	private MyTextView timePeriodTv;
	private Dialog dialog;
	private String[] timeArray;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				getRingMode();
				break;
			case 1:// 静音无振动
				JPushInterface.setSilenceTime(ActivityMessageWarn.this, 0, 0,
						23, 59);
				break;
			case 2:// 铃声，震动

				JPushInterface.setSilenceTime(ActivityMessageWarn.this, 0, 0,
						0, 0);
				break;
			case 3:// 铃声，不震动

				JPushInterface.setSilenceTime(ActivityMessageWarn.this, 0, 0,
						0, 0);
				break;
			case 4:// 无铃声，震动

				JPushInterface.setSilenceTime(ActivityMessageWarn.this, 0, 0,
						23, 59);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_warn);
		initialize();
	}

	private void initialize() {
		sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
		editor = sf.edit();
		ImageButton back = (ImageButton) findViewById(R.id.message_warn_back);
		isReceiveNotify = (CheckBox) findViewById(R.id.message_warn_is_get_notify);
		notifyRing = (CheckBox) findViewById(R.id.message_warn_ring);
		notifyShake = (CheckBox) findViewById(R.id.message_warn_shake);
		notifyNight = (CheckBox) findViewById(R.id.message_warn_night);
		isReceiveNotify.setOnClickListener(this);
		notifyRing.setOnClickListener(this);
		notifyShake.setOnClickListener(this);
		notifyNight.setOnClickListener(this);
		isReceiveNotify.setOnCheckedChangeListener(this);
		notifyRing.setOnCheckedChangeListener(this);
		notifyShake.setOnCheckedChangeListener(this);
		notifyNight.setOnCheckedChangeListener(this);

		selectTime = (LinearLayout) findViewById(R.id.message_warn_select_time);
		timePeriodTv = (MyTextView) findViewById(R.id.message_warn_time);
		selectTime.setOnClickListener(this);
		back.setOnClickListener(this);
		initView();

	}

	private void initView() {
		flag1 = sf.getBoolean("isReceiveNotify", true);
		flag2 = sf.getBoolean("notifyRing", true);
		flag3 = sf.getBoolean("notifyShake", true);
		flag4 = sf.getBoolean("notifyNight", false);
		timePeriod = sf.getString("timePeriod", "20:00-8:00");
		timePeriodTv.setText(timePeriod);
		isReceiveNotify.setChecked(flag1);
		if (flag1) {
			notifyNight.setChecked(flag4);
			if (flag4) {
				selectTime.setVisibility(View.VISIBLE);
				notifyRing.setChecked(false);
				notifyShake.setChecked(false);
				notifyRing.setClickable(false);
				notifyShake.setClickable(false);
				return;
			}
			selectTime.setVisibility(View.GONE);
			notifyRing.setChecked(flag2);
			if (flag2) {
				notifyShake.setChecked(true);
			} else {
				notifyShake.setChecked(flag3);
			}

		} else {
			selectTime.setVisibility(View.GONE);
			notifyRing.setChecked(false);
			notifyShake.setChecked(false);
			notifyNight.setChecked(false);
			notifyShake.setClickable(false);
			notifyRing.setClickable(false);
			notifyNight.setClickable(false);
			return;
		}
	}

	private void getRingMode() {
		if (notifyNight.isChecked()) {
			handler.sendMessage(handler.obtainMessage(1));
		} else {
			if (notifyRing.isChecked() && notifyShake.isChecked()) {
				handler.sendMessage(handler.obtainMessage(2));
			} else if (!notifyRing.isChecked() && !notifyShake.isChecked()) {
				handler.sendMessage(handler.obtainMessage(1));
			} else if (notifyRing.isChecked() && !notifyShake.isChecked()) {
				handler.sendMessage(handler.obtainMessage(3));
			} else if (!notifyRing.isChecked() && notifyShake.isChecked()) {
				handler.sendMessage(handler.obtainMessage(4));
			}
		}
	}

	private void setSilence() {
		Log.e("setSilence", "时间：" + timePeriod);
		String[] time = timePeriod.split("-");
		JPushInterface.setSilenceTime(getApplicationContext(),
				Integer.parseInt(time[0].split(":")[0]),
				Integer.parseInt(time[0].split(":")[1]),
				Integer.parseInt(time[1].split(":")[0]),
				Integer.parseInt(time[1].split(":")[1]));
	}

	private void showSelectSilenceTimeDialog() {
		if (dialog == null) {
			timeArray = getResources().getStringArray(
					R.array.silence_time_array);
			dialog = new Dialog(ActivityMessageWarn.this, R.style.dialog);
			View view = View.inflate(ActivityMessageWarn.this,
					R.layout.view_select_silence_time, null);
			wheelView1 = (WheelView) view
					.findViewById(R.id.select_solence_time_wheelView1);
			WheelView wheelView2 = (WheelView) view
					.findViewById(R.id.select_solence_time_wheelView2);
			wheelView3 = (WheelView) view
					.findViewById(R.id.select_solence_time_wheelView3);
			Button yes = (Button) view
					.findViewById(R.id.select_solence_time_yes);
			yes.setOnClickListener(this);
			Button no = (Button) view.findViewById(R.id.select_solence_time_no);
			no.setOnClickListener(this);
			String[] str2 = { "-" };
			ArrayWheelAdapter<String> adapter0 = new ArrayWheelAdapter<String>(
					ActivityMessageWarn.this, timeArray);
			ArrayWheelAdapter<String> adapter2 = new ArrayWheelAdapter<String>(
					ActivityMessageWarn.this, str2);
			wheelView2.setViewAdapter(adapter2);
			wheelView2.setEnabled(false);
			wheelView2.setTextSize(23);
			wheelView1.setViewAdapter(adapter0);
			wheelView3.setViewAdapter(adapter0);
			wheelView1.setTextSize(23);
			wheelView1.setVisibleItems(5);
			wheelView1.setCyclic(true);
			wheelView3.setCyclic(true);
			wheelView3.setTextSize(23);
			wheelView3.setVisibleItems(5);
			dialog.setContentView(view);
		}
		dialog.show();
	}

	private void getNotify() {
		flag1 = isReceiveNotify.isChecked();
		if (!flag1) {
			notifyRing.setChecked(false);
			notifyRing.setClickable(false);
			notifyShake.setChecked(false);
			notifyShake.setClickable(false);
			notifyNight.setChecked(false);
			notifyNight.setClickable(false);
			JPushInterface.stopPush(ActivityMessageWarn.this);
		} else {

			notifyRing.setChecked(true);
			notifyShake.setChecked(true);
			notifyNight.setChecked(false);

			notifyNight.setClickable(true);
			notifyRing.setClickable(true);
			notifyShake.setClickable(true);

			handler.sendMessage(handler.obtainMessage(0));
			JPushInterface.resumePush(ActivityMessageWarn.this);
			JPushInterface.setAlias(ActivityMessageWarn.this,
					((MyApplication) getApplication()).getUserName(), null);
		}
	}

	private void setRing() {
		flag2 = notifyRing.isChecked();
		if (flag2) {
			flag3 = true;
			notifyShake.setChecked(true);
		}
		handler.sendMessage(handler.obtainMessage(0));
	}

	private void setNight() {
		flag4 = notifyNight.isChecked();
		if (flag4) {
			// 设置推送静音
			selectTime.setVisibility(View.VISIBLE);
			notifyRing.setChecked(false);
			notifyRing.setClickable(false);
			notifyShake.setChecked(false);
			notifyShake.setClickable(false);
			setSilence();
		} else {
			selectTime.setVisibility(View.GONE);
			notifyRing.setClickable(true);
			notifyRing.setChecked(true);
			notifyShake.setChecked(true);
			notifyShake.setClickable(true);
		}
		handler.sendMessage(handler.obtainMessage(0));
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.message_warn_is_get_notify:
			getNotify();
			break;
		case R.id.message_warn_shake:
			if (flag2) {
				notifyShake.setChecked(true);
				Util.showToastBottom(ActivityMessageWarn.this,
						getString(R.string.ring_model_can_not_change_desc));
			} else {
				handler.sendMessage(handler.obtainMessage(0));
			}
			break;
		case R.id.message_warn_ring:
			setRing();
			break;
		case R.id.message_warn_night:
			setNight();
			break;
		case R.id.message_warn_back:
			finish();
			break;
		case R.id.message_warn_select_time:
			showSelectSilenceTimeDialog();
			break;
		case R.id.select_solence_time_yes:
			setSilencePeriod();
			break;
		case R.id.select_solence_time_no:
			dialog.dismiss();
			break;
		default:
			break;
		}
	}

	private void setSilencePeriod() {
		timePeriod = timeArray[wheelView1.getCurrentItem()] + "-"
				+ timeArray[wheelView3.getCurrentItem()];
		Log.e("asd", timePeriod);
		editor.putString("timePeriod", timePeriod);
		editor.commit();
		setSilence();
		timePeriodTv.setText(timePeriod);
		dialog.dismiss();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.message_warn_is_get_notify:
			flag1 = isChecked;
			editor.putBoolean("isReceiveNotify", isChecked);
			editor.commit();
			break;
		case R.id.message_warn_ring:
			flag2 = isChecked;
			editor.putBoolean("notifyRing", isChecked);
			editor.commit();
			break;
		case R.id.message_warn_shake:
			flag3 = isChecked;
			editor.putBoolean("notifyShake", isChecked);
			editor.commit();
			break;
		case R.id.message_warn_night:
			flag4 = isChecked;
			editor.putBoolean("notifyNight", isChecked);
			editor.commit();
			break;

		default:
			break;
		}

	}
}
