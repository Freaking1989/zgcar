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
 * 定位模式
 * 
 * 完成
 */
public class ActivityLocationModel extends Activity implements OnClickListener {

	private CheckBox normal, lowBattery;
	private int flag;
	private Dialog dialog;
	private TerminalInfos info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_model);
		initialize();
	}

	private void initialize() {
		info = (TerminalInfos) getIntent()
				.getSerializableExtra("TerminalInfos");
		dialog = new Dialog(ActivityLocationModel.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		ImageButton back = (ImageButton) findViewById(R.id.location_model_back);
		normal = (CheckBox) findViewById(R.id.location_model_normal);
		lowBattery = (CheckBox) findViewById(R.id.location_model_low_battery);
		checkBoxLisenner();
		back.setOnClickListener(this);
	}

	private void checkBoxLisenner() {

		int item = info.getLocal_mode();
		if (item == 0) {
			flag = 0;
			normal.setChecked(true);
			lowBattery.setChecked(false);
		} else if (item == 1) {
			flag = 1;
			normal.setChecked(false);
			lowBattery.setChecked(true);
		}

		normal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					lowBattery.setChecked(false);
					flag = 0;
				}
			}
		});
		lowBattery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					normal.setChecked(false);
					flag = 1;
				}
			}
		});

	}

	private boolean isSelected() {
		if (!normal.isChecked() && !lowBattery.isChecked()) {
			Util.showToastBottom(ActivityLocationModel.this,
					getString(R.string.please_select_model));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断是否编辑并发送指令
	 */
	private void connectService() {
		if (flag == info.getLocal_mode()) {
			finish();
			return;
		}
		if (isSelected()) {
			MyApplication app = (MyApplication) getApplication();
			dialog.show();
			final String jsonStr = GetJsonString.getRequestJson(
					FinalVariableLibrary.SET_LOCATION_MODEL_CMD, app.getImei(),
					flag, app.getUserName());
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (SocketUtil.connectService(jsonStr)) {
						dialog.dismiss();
						finish();
						Looper.prepare();
						Util.showToastBottom(ActivityLocationModel.this,
								getString(R.string.set_succeed));
						Looper.loop();
					} else {
						Looper.prepare();
						Util.showToastBottom(ActivityLocationModel.this,
								SocketUtil.isFail(ActivityLocationModel.this));
						dialog.dismiss();
						Looper.loop();
					}

				}
			}).start();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			connectService();
			return false;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.location_model_back:
			connectService();
			break;

		default:
			break;
		}

	}
}
