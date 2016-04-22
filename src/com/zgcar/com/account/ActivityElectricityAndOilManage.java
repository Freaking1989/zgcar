package com.zgcar.com.account;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * –°ªÔ∞È
 * 
 */
public class ActivityElectricityAndOilManage extends Activity implements
		OnClickListener {

	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_electricity_and_oil_manage);
		showInputPswDialog();
		init();
	}

	private void init() {
		ImageButton back = (ImageButton) findViewById(R.id.electricity_oil_manage_back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.electricity_oil_manage_back:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// private void getGuysInfosRequest() {
	// app = (MyApplication) getApplication();
	// sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER,
	// MODE_PRIVATE);
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// String jsonStr = GetJsonString.getRequestJson(
	// FinalVariableLibrary.GUYS_INFOS_CMD, app.getImei(), -1,
	// app.getUserName());
	// boolean flag1 = SocketUtil.connectService(jsonStr);
	// if (flag1) {
	// guysListInfos = ResolveServiceData.getGuysListInfos(sf,
	// app, ActivityElectricityAndOilManage.this);
	// handler.sendMessage(handler.obtainMessage(0));
	// } else {
	// Looper.prepare();
	// dialog.dismiss();
	// Util.showToastBottom(
	// ActivityElectricityAndOilManage.this,
	// SocketUtil
	// .isFail(ActivityElectricityAndOilManage.this));
	// Looper.loop();
	// return;
	// }
	// }
	// }).start();
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// -----------------------------dialog--------------------------

	private void showInputPswDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityElectricityAndOilManage.this,
				R.layout.view_input_info, null);
		TextView titleTv = (TextView) view
				.findViewById(R.id.view_input_info_title_tv);
		titleTv.setText(" ‰»Î√‹¬Î");
		final EditText content = (EditText) view
				.findViewById(R.id.view_input_info_content_et);
		content.setHint("«Î ‰»Î√‹¬Î");
		Button no = (Button) view
				.findViewById(R.id.view_input_info_negative_button);
		Button yes = (Button) view
				.findViewById(R.id.view_input_info_positive_button);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPswRight(content.getText().toString().trim());
			}
		});
		dialog.setCancelable(false);
		dialog.setContentView(view);
		dialog.show();
	}

	private void isPswRight(String psw) {
		String userPsw = getSharedPreferences(
				FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE).getString(
				"userPsw", "");
		if (userPsw.equals(psw)) {
			dialogDismiss();
		} else {
			Util.showToastCenter(ActivityElectricityAndOilManage.this,
					"«Î ‰»Î’˝»∑’ ªß√‹¬Î.");
		}

	}

	public Dialog getDialog() {
		return dialog == null ? new Dialog(
				ActivityElectricityAndOilManage.this, R.style.dialog) : dialog;
	}

	private void dialogDismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
