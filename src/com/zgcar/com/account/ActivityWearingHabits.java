package com.zgcar.com.account;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.account.model.TerminalInfos;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * Åå´÷Ï°¹ß
 * 
 * 
 * Íê³É
 */
public class ActivityWearingHabits extends Activity {
	private CheckBox leftHand, rightHand;
	private ImageButton back;
	private int newHand;
	private Dialog dialog;
	private TerminalInfos info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wearing_habits);
		initialize();
	}

	private void initialize() {
		info = (TerminalInfos) getIntent()
				.getSerializableExtra("TerminalInfos");
		dialog = new Dialog(ActivityWearingHabits.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		leftHand = (CheckBox) findViewById(R.id.wearing_habits_left);
		rightHand = (CheckBox) findViewById(R.id.wearing_habits_right);
		back = (ImageButton) findViewById(R.id.wearing_habits_back);
		clickLisner();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			remindMessage();
			return false;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean isSelected() {
		if (!rightHand.isChecked() && !leftHand.isChecked()) {
			Util.showToastBottom(ActivityWearingHabits.this,
					getString(R.string.please_select_wear_model));
			return false;
		} else {
			return true;
		}

	}

	private void remindMessage() {
		if (newHand == info.getWear()) {
			finish();
			return;
		}
		if (isSelected()) {
			dialog.show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					MyApplication app = (MyApplication) getApplication();
					String jsonStr = GetJsonString.getRequestJson(
							FinalVariableLibrary.SET_WEARING_HABITS_CMD,
							app.getImei(), newHand, app.getUserName());
					boolean flag = SocketUtil.connectService(jsonStr);
					if (flag) {
						dialog.dismiss();
						finish();
						Looper.prepare();
						Util.showToastBottom(ActivityWearingHabits.this,
								getString(R.string.set_succeed));
						Looper.loop();
					} else {
						Looper.prepare();
						Util.showToastBottom(ActivityWearingHabits.this,
								SocketUtil.isFail(ActivityWearingHabits.this));
						dialog.dismiss();
						Looper.loop();
					}
				}
			}).start();
		}

	}

	private void clickLisner() {
		int item = info.getWear();
		if (item == 0) {
			newHand = 0;
			leftHand.setChecked(true);
			rightHand.setChecked(false);
		} else if (item == 1) {
			newHand = 1;
			leftHand.setChecked(false);
			rightHand.setChecked(true);
		}

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				remindMessage();
			}
		});
		rightHand.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					leftHand.setChecked(false);
					newHand = 1;
				}
			}
		});
		leftHand.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					rightHand.setChecked(false);
					newHand = 0;
				}
			}
		});

	}
}
