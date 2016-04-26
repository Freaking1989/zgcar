package com.zgcar.com.statisty;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;
import com.zgcar.com.wheelview.OnWheelScrollListener;
import com.zgcar.com.wheelview.WheelView;
import com.zgcar.com.wheelview.adapters.ArrayWheelAdapter;

/**
 * @author Freaking 超速统计
 */
public class ActivityOverSpeedStatisty extends Activity implements
		OnClickListener, OnCheckedChangeListener {
	private TextView timeTv;
	private Dialog dialog;
	private int tempCheckId;
	private Button endTimeBt, startTimeBt;
	private String[] yearArray, monthArray, dayArray, hourArray, minArray;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.color_4);
		SetTitleBackground.setTitleBg(ActivityOverSpeedStatisty.this,
				R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overspeed_statisty);
		init();
		getInfosRequest();
	}

	private void init() {
		app = (MyApplication) getApplication();
		tempCheckId = 0;
		yearArray = Util.getYearArray();
		dayArray = Util.getDayArray(null, null);
		monthArray = Util.getMonthArray();
		hourArray = getResources().getStringArray(R.array.hour);
		minArray = getResources().getStringArray(R.array.mins);
		ImageButton back = (ImageButton) findViewById(R.id.overspeed_statistics_back);
		back.setOnClickListener(this);
		Button selectTime = (Button) findViewById(R.id.overspeed_statistics_select_time);
		selectTime.setOnClickListener(this);
		timeTv = (TextView) findViewById(R.id.overspeed_statistics_time_tv);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.overspeed_statistics_back:
			finish();
			break;
		case R.id.overspeed_statistics_select_time:
			showSelectTimeView();
			break;
		case R.id.view_statisty_time_selector_radio0:
			dialogDismiss();
			break;
		case R.id.view_statisty_time_selector_radio1:
			dialogDismiss();
			break;
		case R.id.view_statisty_time_selector_radio2:
			dialogDismiss();
			break;
		case R.id.view_statisty_time_selector_radio3:
			dialogDismiss();
			tempCheckId = R.id.view_statisty_time_selector_radio3;
			showCostumTimeDialog();
			break;

		case R.id.view_statisty_time_selector_sub1_start:
			showGetTimeView(true);
			break;
		case R.id.view_statisty_time_selector_sub1_end:
			showGetTimeView(false);
			break;
		case R.id.view_statisty_time_selector_sub1_no:
			dialogDismiss();
			break;
		case R.id.view_statisty_time_selector_sub1_yes:
			dialogDismiss();
			break;
		case R.id.view_date_picker_no:
			dialogDismiss();
			break;
		default:
			break;
		}

	}

	private void getInfosRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = GetJsonString.getRequestJson(
						FinalVariableLibrary.SPEEDING_SRATISTICS,
						app.getImei(), "", "");
				boolean flag1 = SocketUtil.connectService(jsonStr);
				if (flag1) {
					/**
					 * 
					 * 
					 * **/
				} else {
					Looper.prepare();
					dialogDismiss();
					Util.showToastBottom(ActivityOverSpeedStatisty.this,
							SocketUtil.isFail(ActivityOverSpeedStatisty.this));
					Looper.loop();
					return;
				}
			}
		}).start();
	}

	private void showGetTimeView(final boolean isStart) {
		dialog = getDialog();
		dialog.setCancelable(true);
		View view = View.inflate(ActivityOverSpeedStatisty.this,
				R.layout.view_date_picker, null);
		final WheelView year = (WheelView) view
				.findViewById(R.id.view_date_picker_year);
		initWheelview(year);
		year.setViewAdapter(new ArrayWheelAdapter<String>(
				ActivityOverSpeedStatisty.this, yearArray));
		final WheelView day = (WheelView) view
				.findViewById(R.id.view_date_picker_day);
		initWheelview(day);
		day.setViewAdapter(new ArrayWheelAdapter<String>(
				ActivityOverSpeedStatisty.this, dayArray));
		final WheelView month = (WheelView) view
				.findViewById(R.id.view_date_picker_month);
		initWheelview(month);
		month.setViewAdapter(new ArrayWheelAdapter<String>(
				ActivityOverSpeedStatisty.this, monthArray));

		year.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				dayArray = Util.getDayArray(yearArray[year.getCurrentItem()],
						monthArray[month.getCurrentItem()]);
				day.setViewAdapter(new ArrayWheelAdapter<String>(
						ActivityOverSpeedStatisty.this, dayArray));
			}
		});

		month.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				dayArray = Util.getDayArray(yearArray[year.getCurrentItem()],
						monthArray[month.getCurrentItem()]);
				day.setViewAdapter(new ArrayWheelAdapter<String>(
						ActivityOverSpeedStatisty.this, dayArray));
			}
		});

		WheelView wheelView = (WheelView) view
				.findViewById(R.id.view_date_picker_wheel);
		wheelView.setTextSize(20);
		wheelView.setEnabled(false);
		wheelView.setViewAdapter(new ArrayWheelAdapter<String>(
				ActivityOverSpeedStatisty.this, new String[] { "-" }));
		final WheelView hour = (WheelView) view
				.findViewById(R.id.view_date_picker_hour);
		initWheelview(hour);
		hour.setViewAdapter(new ArrayWheelAdapter<String>(
				ActivityOverSpeedStatisty.this, hourArray));
		final WheelView min = (WheelView) view
				.findViewById(R.id.view_date_picker_min);
		initWheelview(min);
		min.setViewAdapter(new ArrayWheelAdapter<String>(
				ActivityOverSpeedStatisty.this, minArray));

		Button no = (Button) view.findViewById(R.id.view_date_picker_no);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDismiss();
			}
		});
		Button yes = (Button) view.findViewById(R.id.view_date_picker_yes);
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isStart) {
					startTimeBt.setText(yearArray[year.getCurrentItem()] + "-"
							+ monthArray[month.getCurrentItem()] + "-"
							+ dayArray[day.getCurrentItem()] + " "
							+ hourArray[hour.getCurrentItem()] + ":"
							+ minArray[min.getCurrentItem()]);
				} else {
					endTimeBt.setText(yearArray[year.getCurrentItem()] + "-"
							+ monthArray[month.getCurrentItem()] + "-"
							+ dayArray[day.getCurrentItem()] + " "
							+ hourArray[hour.getCurrentItem()] + ":"
							+ minArray[min.getCurrentItem()]);
				}
				dialogDismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	private void initWheelview(WheelView wView) {
		wView.setCyclic(true);
		wView.setTextSize(20);
	}

	private void showSelectTimeView() {
		dialog = getDialog();
		dialog.setCancelable(true);
		View view = View.inflate(ActivityOverSpeedStatisty.this,
				R.layout.view_statisty_time_selector, null);
		RadioButton rButton0 = (RadioButton) view
				.findViewById(R.id.view_statisty_time_selector_radio0);
		rButton0.setOnClickListener(this);
		RadioButton rButton1 = (RadioButton) view
				.findViewById(R.id.view_statisty_time_selector_radio1);
		rButton1.setOnClickListener(this);
		RadioButton rButton2 = (RadioButton) view
				.findViewById(R.id.view_statisty_time_selector_radio2);
		rButton2.setOnClickListener(this);
		RadioButton rButton3 = (RadioButton) view
				.findViewById(R.id.view_statisty_time_selector_radio3);
		rButton3.setOnClickListener(this);
		RadioGroup radioGroup = (RadioGroup) view
				.findViewById(R.id.view_statisty_time_selector_radioGroup);
		if (tempCheckId != 0) {
			radioGroup.check(tempCheckId);
		}
		radioGroup.setOnCheckedChangeListener(this);
		dialog.setContentView(view);
		dialog.show();

	}

	private void dialogDismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		tempCheckId = checkedId;
		Log.e("onCheckedChanged", "onCheckedChanged:" + checkedId);
	}

	/**
	 * 显示自定义时间段view
	 */
	private void showCostumTimeDialog() {
		dialog = getDialog();
		dialog.setCancelable(true);
		View view = View.inflate(ActivityOverSpeedStatisty.this,
				R.layout.view_statisty_time_selector_sub1, null);
		startTimeBt = (Button) view
				.findViewById(R.id.view_statisty_time_selector_sub1_start);
		startTimeBt.setText(Util.getCurrentTime().substring(0, 16));
		startTimeBt.setOnClickListener(this);

		endTimeBt = (Button) view
				.findViewById(R.id.view_statisty_time_selector_sub1_end);
		endTimeBt.setOnClickListener(this);
		endTimeBt.setText(Util.getCurrentTime().substring(0, 16));
		Button no = (Button) view
				.findViewById(R.id.view_statisty_time_selector_sub1_no);
		no.setOnClickListener(this);
		Button yes = (Button) view
				.findViewById(R.id.view_statisty_time_selector_sub1_yes);
		yes.setOnClickListener(this);

		dialog.setContentView(view);
		dialog.show();

	}

	private void showProgressDialog() {
		dialog = getDialog();
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
	}

	public Dialog getDialog() {
		return dialog == null ? new Dialog(ActivityOverSpeedStatisty.this,
				R.style.dialog) : dialog;
	}
}
