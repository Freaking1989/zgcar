package com.zgcar.com.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.receiver.MyPushService;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class MainActivity extends Activity implements OnClickListener,
		LocationCallBack {
	private RadioGroup radioGroup;
	private FragmentManager fm;
	private Fragment currentFragment;
	private MyApplication app;
	private Dialog dialog;
	private Dialog prgressDialog;
	private List<Fragment> list;
	private TextView tvHasVoiceInfo;
	private int tempId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.color_4);
		SetTitleBackground.setTitleBg(MainActivity.this, R.color.color_4);
		Quit.addActivity(this);
		super.onCreate(savedInstanceState);
		isTheFirst();
		setContentView(R.layout.activity_main);
		initialize();
		IntentFilter filter = new IntentFilter(
				FinalVariableLibrary.VoiceNotifyMessageAction);
		registerReceiver(receiver, filter);
		/**
		 * 时时定位改成点击定位 startService(new Intent(MainActivity.this,
		 * ServiceStartLocation.class));
		 * */

	}

	@Override
	public void onResume() {
		if (app.getMessageNum() > 0) {
			tvHasVoiceInfo.setVisibility(View.VISIBLE);
			if (app.getMessageNum() > 99) {
				tvHasVoiceInfo.setText("99+");
			} else {
				tvHasVoiceInfo.setText(app.getMessageNum() + "");
			}
		}
		super.onResume();
	}

	private void isTheFirst() {
		app = (MyApplication) getApplication();
		if (app.isFirst()) {
			JPushInterface.init(MainActivity.this);
			JPushInterface.setDebugMode(false);
			SharedPreferences sf = getSharedPreferences(
					FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
			boolean flag = sf.getBoolean("isReceiveNotify", true);
			if (flag) {
				JPushInterface.resumePush(MainActivity.this);
				JPushInterface.setAlias(MainActivity.this, app.getUserName(),
						app.mTagsCallback);
			}
			app.setFirst(false);
		}
	}

	private void fragentFactory(int checkedId) {
		switch (checkedId) {
		case R.id.radio_button1:
			tempId = R.id.radio_button1;
			String language = getResources().getConfiguration().locale
					.getCountry();
			if (language.equals("CN") || language.equals("TW")) {
				FinalVariableLibrary.MAP_TYPE = "2";
				changeFragment(0);
			} else {
				FinalVariableLibrary.MAP_TYPE = "1";
				changeFragment(1);
			}
			break;
		case R.id.radio_button2:
			tvHasVoiceInfo.setVisibility(View.INVISIBLE);
			app.setMessageNum(0);
			dismissDialog();
			changeFragment(2);
			break;
		case R.id.radio_button3:
			dismissDialog();
			changeFragment(3);
			break;
		case R.id.radio_button4:
			tempId = R.id.radio_button4;
			dismissDialog();
			changeFragment(4);
			break;
		default:
			break;
		}
	}

	private void initialize() {
		setRadioButtonDrawableTop();
		tvHasVoiceInfo = (TextView) findViewById(R.id.main_has_voice_info);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) tvHasVoiceInfo
				.getLayoutParams();
		params.leftMargin = (int) (FinalVariableLibrary.ScreenWidth / 2 - Util
				.dip2px(MainActivity.this, 35));
		tvHasVoiceInfo.setLayoutParams(params);
		tvHasVoiceInfo.setVisibility(View.INVISIBLE);
		list = new ArrayList<Fragment>();
		radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		fm = getFragmentManager();
		initView();
	}

	private void initView() {
		int pushType = getIntent().getIntExtra("PUSHTYPE", -1);
		if (pushType == 1) {
			fragentFactory(R.id.radio_button2);
			radioGroup.check(R.id.radio_button2);
		} else if (pushType == 2) {
			fragentFactory(R.id.radio_button3);
			radioGroup.check(R.id.radio_button3);
		} else {
			showDialog();
			String language = getResources().getConfiguration().locale
					.getCountry();
			if (language.equals("CN") || language.equals("TW")) {
				changeFragment(0);
				FinalVariableLibrary.MAP_TYPE = "2";
			} else {
				changeFragment(1);
				FinalVariableLibrary.MAP_TYPE = "1";
			}
			tempId = R.id.radio_button1;
			radioGroup.check(R.id.radio_button1);
		}
		initRadioGroupLisener();
	}

	/**
	 * radiogroup监听事件
	 **/
	private void initRadioGroupLisener() {
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				showDialog();
				if (!app.imeiIsEmpty(MainActivity.this, true)) {
					fragentFactory(checkedId);
					return;
				}
				if (R.id.radio_button4 == checkedId
						|| R.id.radio_button1 == checkedId) {
					if (tempId != checkedId) {
						fragentFactory(checkedId);
					} else {
						dismissDialog();
					}
				} else {
					radioGroup.check(tempId);
				}

			}
		});
	}

	private void quitDialog() {
		dialog = new Dialog(MainActivity.this, R.style.dialog);
		View view = View.inflate(MainActivity.this,
				R.layout.view_dialog_yes_or_not, null);
		TextView title = (TextView) view.findViewById(R.id.view_dialog_title);
		title.setText(R.string.exit_reminder_desc);
		Button yes = (Button) view.findViewById(R.id.view_dialog_yes);
		yes.setOnClickListener(this);
		Button no = (Button) view.findViewById(R.id.view_dialog_no);
		no.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quitDialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		if (!app.isVoicePush()) {
			app.setFirst(true);
			JPushInterface.stopPush(MainActivity.this);
			stopService(new Intent(MainActivity.this, MyPushService.class));
			stopService(new Intent(MainActivity.this,
					ServiceStartLocation.class));
		} else {
			app.setVoicePush(false);
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.view_dialog_yes:
			dialog.dismiss();
			Quit.quit();
			Quit.recycling(-1);
			break;
		case R.id.view_dialog_no:
			dialog.dismiss();
			break;
		default:
			break;
		}
	}

	public synchronized void changeFragment(int flag) {
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = fm.findFragmentByTag(flag + "");
		boolean isShowOrAdd = true;// true表示show，false表示add
		if (fragment == null) {
			isShowOrAdd = false;
			switch (flag) {
			case 0:
				fragment = new FragmentLocation1();
				((FragmentLocation1) fragment).setLocationCallBack(this);
				list.add(fragment);
				break;
			case 1:
				fragment = new FragmentLocation2();
				((FragmentLocation2) fragment).setLocationCallBack(this);
				list.add(fragment);
				break;
			case 2:
				fragment = new FragmentStatistics();
				list.add(fragment);
				break;
			case 3:
				fragment = new FragmentHistory();
				list.add(fragment);
				break;
			case 4:
				fragment = new FragmentAccount();
				list.add(fragment);
				break;
			default:
				break;
			}
		}
		if (currentFragment != null) {
			ft.hide(currentFragment);
		}
		if (isShowOrAdd) {
			ft.show(fragment);
			fragment.onResume();
		} else {
			ft.add(R.id.content, fragment, flag + "");
		}
		currentFragment = fragment;
		ft.commit();
	}

	@Override
	public void onPause() {
		removeFragment();
		super.onPause();
	}

	/**
	 * 按home键时收回当前不使用的fragment以防返回该界面加载数据时浪费内存。
	 */
	private void removeFragment() {
		for (int i = 0; i < list.size(); i++) {
			if (currentFragment instanceof FragmentLocation1) {
				if (!(list.get(i) instanceof FragmentLocation1)) {
					FragmentTransaction ft = fm.beginTransaction();
					ft.remove(list.get(i));
					ft.commit();
				}
			} else if (currentFragment instanceof FragmentLocation2) {
				if (!(list.get(i) instanceof FragmentLocation2)) {
					FragmentTransaction ft = fm.beginTransaction();
					ft.remove(list.get(i));
					ft.commit();
				}
			} else if (currentFragment instanceof FragmentStatistics) {
				if (!(list.get(i) instanceof FragmentStatistics)) {
					FragmentTransaction ft = fm.beginTransaction();
					ft.remove(list.get(i));
					ft.commit();
				}
			} else if (currentFragment instanceof FragmentHistory) {
				if (!(list.get(i) instanceof FragmentHistory)) {
					FragmentTransaction ft = fm.beginTransaction();
					ft.remove(list.get(i));
					ft.commit();
				}
			} else if (currentFragment instanceof FragmentAccount) {
				if (!(list.get(i) instanceof FragmentAccount)) {
					FragmentTransaction ft = fm.beginTransaction();
					ft.remove(list.get(i));
					ft.commit();
				}
			}
		}
		int length = list.size();
		for (int i = 0; i < length; i++) {
			list.remove(0);
		}
		list.add(currentFragment);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (currentFragment instanceof FragmentStatistics) {
				app.setMessageNum(0);
				tvHasVoiceInfo.setVisibility(View.GONE);
				return;
			}
			if (app.getMessageNum() > 0) {
				tvHasVoiceInfo.setVisibility(View.VISIBLE);
				if (app.getMessageNum() > 99) {
					tvHasVoiceInfo.setText("99+");
				} else {
					tvHasVoiceInfo.setText(app.getMessageNum() + "");
				}
			}
		}
	};

	private void setRadioButtonDrawableTop() {
		int width = (int) Util.dip2px(MainActivity.this, 35);
		RadioButton radioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		RadioButton radioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		RadioButton radioButton3 = (RadioButton) findViewById(R.id.radio_button3);
		RadioButton radioButton4 = (RadioButton) findViewById(R.id.radio_button4);
		setDrawableTop(radioButton1, R.drawable.radio_button1_top_bg, width);
		setDrawableTop(radioButton2, R.drawable.radio_button2_top_bg, width);
		setDrawableTop(radioButton3, R.drawable.radio_button3_top_bg, width);
		setDrawableTop(radioButton4, R.drawable.radio_button4_top_bg, width);

	}

	private void setDrawableTop(RadioButton radioButton, int dawableId,
			int width) {
		Drawable topDawable = getResources().getDrawable(dawableId);
		topDawable.setBounds(0, 0, width, width);
		radioButton.setCompoundDrawables(null, topDawable, null, null);
	}

	// ------------------------------location回调----------------------------
	@Override
	public void showDialog() {
		if (prgressDialog == null) {
			prgressDialog = new Dialog(MainActivity.this, R.style.dialog);
			prgressDialog.setContentView(R.layout.view_progress_dialog);
			prgressDialog.setCancelable(false);
		}
		prgressDialog.show();
	}

	@Override
	public void dismissDialog() {
		if (prgressDialog != null && prgressDialog.isShowing()) {
			prgressDialog.dismiss();
		}
	}

}
