package com.zgcar.com.account;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * ∂œ”Õ∂œµÁ
 * 
 */
public class ActivityDisarmFortification extends Activity implements
		OnClickListener {

	private Dialog dialog;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disarm_fortification);
		init();
		showProgressDialog();
		getInfosRequest();
	}

	private void init() {
		app = (MyApplication) getApplication();
		ImageButton back = (ImageButton) findViewById(R.id.disarm_fortification_back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.disarm_fortification_back:
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

	/**
	 * {"cmd":"00100","data":[{"imei":"861400000000088"}]}
	 */
	private void getInfosRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = GetJsonString.getRequestJson(
						FinalVariableLibrary.FORTIFICATION_DISARM_STATE,
						app.getImei(), -1, app.getUserName());
				boolean flag1 = SocketUtil.connectService(jsonStr);
				if (flag1) {
					/**
					 * 
					 * 
					 * **/
				} else {
					Looper.prepare();
					dialog.dismiss();
					Util.showToastBottom(ActivityDisarmFortification.this,
							SocketUtil.isFail(ActivityDisarmFortification.this));
					Looper.loop();
					return;
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// -----------------------------dialog--------------------------

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void showInputPswDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityDisarmFortification.this,
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
			showProgressDialog();
			getInfosRequest();
			dialogDismiss();
		} else {
			Util.showToastCenter(ActivityDisarmFortification.this, "«Î ‰»Î’˝»∑’ ªß√‹¬Î.");
		}

	}

	private void showProgressDialog() {
		dialog = getDialog();
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
	}

	public Dialog getDialog() {
		return dialog == null ? new Dialog(ActivityDisarmFortification.this,
				R.style.dialog) : dialog;
	}

	private void dialogDismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
