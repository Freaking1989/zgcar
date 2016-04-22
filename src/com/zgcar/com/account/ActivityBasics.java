package com.zgcar.com.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.TerminalInfos;
import com.zgcar.com.db.DbManager;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MainActivity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityBasics extends Activity implements OnClickListener {

	private CheckBox silenceBt, temperatureRemind, bodyAnswer;
	private Button removeWatch;
	private CircleImageView imageIcon;
	private Dialog dialog;
	private MyApplication app;
	private TextView titleName, name, joinTimeTv, terminalVer;
	/**
	 * 用来判断静音以及温度提醒的标记0关，1开
	 */
	private int flag;
	private TerminalInfos info;
	private Bitmap bitmap;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initView();
				break;
			case 1:// 解绑成功后操作
				DbManager dbManager = new DbManager(ActivityBasics.this,
						app.getImei(), app.getUserName());
				dbManager.deletTable(app.getImei());
				Intent intent = new Intent(ActivityBasics.this,
						MainActivity.class);
				ListInfosEntity.getTerminalListInfos()
						.remove(app.getPosition());
				ListInfosEntity.getPathList().remove(app.getPosition());
				startActivity(intent);
				closeDialog();
				app.setPosition(0);
				Quit.recycling(-1);
				Util.showToastBottom(ActivityBasics.this,
						getString(R.string.operate_succeed));
				finish();
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
		super.onCreate(savedInstanceState);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		setContentView(R.layout.activity_basics);
		app = (MyApplication) getApplication();
		showProgressDialog();
		initialize();
	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityBasics.this, R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
		dialog.setCancelable(false);
	}

	private void initialize() {
		removeWatch = (Button) findViewById(R.id.basics_remove_watch_bt);
		terminalVer = (TextView) findViewById(R.id.basics_terminal_ver);
		joinTimeTv = (TextView) findViewById(R.id.basics_join_time);
		titleName = (TextView) findViewById(R.id.basics_title_textview_name);
		name = (TextView) findViewById(R.id.basics_textview_name);
		imageIcon = (CircleImageView) findViewById(R.id.basics_image_icon);
		ImageButton back = (ImageButton) findViewById(R.id.basics_back);
		temperatureRemind = (CheckBox) findViewById(R.id.basics_togglebt_temperature_remind);
		silenceBt = (CheckBox) findViewById(R.id.basics_togglebt_silence);
		bodyAnswer = (CheckBox) findViewById(R.id.basics_box_body_answer);
		LinearLayout editBasic = (LinearLayout) findViewById(R.id.basics_edit);
		LinearLayout qrCode = (LinearLayout) findViewById(R.id.basics_qr_code);
		LinearLayout wearingHabits = (LinearLayout) findViewById(R.id.basics_wearing_habits);
		LinearLayout locationModel = (LinearLayout) findViewById(R.id.basics_location_model);
		editBasic.setOnClickListener(this);
		back.setOnClickListener(this);
		qrCode.setOnClickListener(this);
		wearingHabits.setOnClickListener(this);
		locationModel.setOnClickListener(this);
		removeWatch.setOnClickListener(this);
		lisener();
	}

	@Override
	protected void onResume() {
		sendRequest(FinalVariableLibrary.GET_TERMINAL_INFOS);
		super.onResume();
	}

	private void initView() {
		if (ListInfosEntity.getPathList() != null) {
			int width = (int) Util.dip2px(ActivityBasics.this, 50);
			bitmap = Util.decodeSampledBitmapFromResource(ListInfosEntity
					.getPathList().get(app.getPosition()), width, width);
			if (bitmap != null) {
				imageIcon.setImageBitmap(bitmap);
			} else {
				imageIcon.setImageResource(R.drawable.icon);
			}
		}

		titleName.setText(ListInfosEntity.getTerminalListInfos()
				.get(app.getPosition()).getName());
		String joinTime = info.getJoinTime().split(" ")[0];
		joinTimeTv.setText(ListInfosEntity.getTerminalListInfos()
				.get(app.getPosition()).getName()
				+ " " + getString(R.string.bind_in) + " " + joinTime);
		temperatureRemind.setChecked(info.getTemperature_alarm() == 1 ? true
				: false);
		bodyAnswer.setChecked(info.getBodyAnswer() == 1 ? true : false);
		silenceBt.setChecked(info.getMute() == 1 ? true : false);
		terminalVer.setText(info.getTerminal_ver());
		name.setText(ListInfosEntity.getTerminalListInfos()
				.get(app.getPosition()).getName());
	}

	private void lisener() {
		temperatureRemind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (temperatureRemind.isChecked()) {
					flag = 1;
				} else {
					flag = 0;
				}
				sendRequest(FinalVariableLibrary.TEMPERATUTE_AlARM_CMD, 0);
			}
		});

		silenceBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (silenceBt.isChecked()) {
					flag = 1;
				} else {
					flag = 0;
				}
				sendRequest(FinalVariableLibrary.SIlENCE_CMD, 1);
			}
		});

		bodyAnswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (bodyAnswer.isChecked()) {
					flag = 1;
				} else {
					flag = 0;
				}
				sendRequest(FinalVariableLibrary.SET_BODY_ANSWER, 2);
			}
		});
	}

	/**
	 * 
	 * 设置静音以及
	 * 
	 * @param cmd
	 *            指令
	 * @param item
	 *            用于标示哪个被点击
	 * 
	 */
	private void sendRequest(final String cmd, final int item) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = GetJsonString.getRequestJson(cmd,
						app.getImei(), flag, app.getUserName());
				Log.e("sendRequest", jsonStr);
				boolean flag2 = SocketUtil.connectService(jsonStr);
				if (flag2) {
					if (item == 0) {
						info.setTemperature_alarm(flag);
					} else if (item == 1) {
						info.setMute(flag);
					} else if (item == 2) {
						info.setBodyAnswer(flag);
					}
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivityBasics.this,
							SocketUtil.isFail(ActivityBasics.this));
					Looper.loop();
				}

			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.basics_edit:
			startActivity(new Intent(ActivityBasics.this,
					ActivityEditBasics.class));
			break;
		case R.id.basics_qr_code:
			startActivity(new Intent(ActivityBasics.this, ActivityQrCode.class));
			break;

		case R.id.basics_wearing_habits:
			intent.setClass(ActivityBasics.this, ActivityWearingHabits.class);
			intent.putExtra("TerminalInfos", info);
			startActivity(intent);
			break;
		case R.id.basics_location_model:
			intent.setClass(ActivityBasics.this, ActivityLocationModel.class);
			intent.putExtra("TerminalInfos", info);
			startActivity(intent);
			break;
		case R.id.basics_remove_watch_bt:
			if (ListInfosEntity.getTerminalListInfos().get(app.getPosition())
					.isAdmin()) {
				dialogChangeAdmin();
				break;
			}
			closeDialog();
			removeAction();
			break;

		case R.id.basics_back:
			recycle();
			finish();
			break;
		case R.id.view_dialog_change_admin_no:
			closeDialog();
			break;
		case R.id.view_dialog_change_admin_unwrap_alone:
			closeDialog();
			intent.setClass(ActivityBasics.this, ActivitySelectAdmin.class);
			intent.putExtra("IsleaveFimily", true);
			intent.putExtra("position", app.getPosition());
			startActivity(intent);
			break;
		case R.id.view_dialog_change_admin_unwrap_all:
			closeDialog();
			showUnbindWearningDialog();
			break;
		case R.id.view_remove_watch_negative:
			closeDialog();
			break;
		case R.id.view_remove_watch_positive:
			closeDialog();
			showProgressDialog();
			sendRequest(FinalVariableLibrary.LEAVE_ALONE_CMD);
			break;

		case R.id.view_unbind_warning_negative:
			closeDialog();
			break;
		case R.id.view_unbind_warning_positive:
			closeDialog();
			showProgressDialog();
			sendRequest(FinalVariableLibrary.LEAVE_ALONE_CMD);
			break;

		default:
			break;
		}
	}

	/**
	 * 解绑管理员时弹出警告信息
	 */
	private void showUnbindWearningDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityBasics.this, R.style.dialog);
		}
		View view = View.inflate(ActivityBasics.this,
				R.layout.view_unbind_all_warning, null);
		Button yes = (Button) view
				.findViewById(R.id.view_unbind_warning_positive);
		Button no = (Button) view
				.findViewById(R.id.view_unbind_warning_negative);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();

	}

	private void closeDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 解绑管理员
	 */
	private void dialogChangeAdmin() {
		if (dialog == null) {
			dialog = new Dialog(ActivityBasics.this, R.style.dialog);
		}
		View dialogAdminView = View.inflate(ActivityBasics.this,
				R.layout.view_dialog_change_admin, null);
		Button unwrapAlone = (Button) dialogAdminView
				.findViewById(R.id.view_dialog_change_admin_unwrap_alone);
		Button unwrapAll = (Button) dialogAdminView
				.findViewById(R.id.view_dialog_change_admin_unwrap_all);
		Button no = (Button) dialogAdminView
				.findViewById(R.id.view_dialog_change_admin_no);
		no.setOnClickListener(this);
		unwrapAll.setOnClickListener(this);
		unwrapAlone.setOnClickListener(this);
		dialog.setContentView(dialogAdminView);
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * 解除考拉
	 */
	private void removeAction() {
		if (dialog == null) {
			dialog = new Dialog(ActivityBasics.this, R.style.dialog);
		}
		View dialogNotAdminView = View.inflate(ActivityBasics.this,
				R.layout.view_remove_koala, null);
		TextView title = (TextView) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_tv);
		TextView title1 = (TextView) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_tv1);
		TextView title2 = (TextView) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_tv2);
		title1.setVisibility(View.INVISIBLE);
		title2.setVisibility(View.INVISIBLE);
		title.setVisibility(View.VISIBLE);
		Button positive = (Button) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_positive);
		positive.setOnClickListener(this);
		Button negative = (Button) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_negative);
		negative.setOnClickListener(this);
		title.setText(getString(R.string.sure_to_set)
				+ ListInfosEntity.getTerminalListInfos().get(app.getPosition())
						.getName() + getString(R.string.remove_from_bindind)
				+ "?");
		dialog.setContentView(dialogNotAdminView);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void recycle() {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		recycle();
		super.onDestroy();
	}

	/**
	 * 获取终端基本信息
	 * 
	 */
	private void sendRequest(final String cmd) {
		Log.e("ActivityBasics", cmd + "");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = GetJsonString.getRequestJson(cmd,
						app.getImei(), -1, app.getUserName());
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					closeDialog();
					if (FinalVariableLibrary.GET_TERMINAL_INFOS.equals(cmd)) {
						info = ResolveServiceData.setInfos(app.getImei());
						Log.e("info", info.toString());
						handler.sendMessage(handler.obtainMessage(0));
					} else if (FinalVariableLibrary.LEAVE_ALONE_CMD.equals(cmd)) {
						handler.sendMessage(handler.obtainMessage(1));
					}
				} else {
					closeDialog();
					Looper.prepare();
					Util.showToastBottom(ActivityBasics.this,
							SocketUtil.isFail(ActivityBasics.this));
					Looper.loop();
				}

			}
		}).start();
	}

}
