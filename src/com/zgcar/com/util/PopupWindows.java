package com.zgcar.com.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.util.KCalendar.OnCalendarClickListener;
import com.zgcar.com.util.KCalendar.OnCalendarDateChangedListener;

public class PopupWindows extends PopupWindow {

	private String date;
	private Context mContext;
	private View parent;
	private TextView yearAndMonthTv;
	private PopupWindowCallback callback;

	public PopupWindows(Context mContext, View parent, String date,
			PopupWindowCallback callback) {
		this.date = date;
		this.mContext = mContext;
		this.parent = parent;
		this.callback = callback;
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		View view = View.inflate(mContext, R.layout.clock, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_in));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in_1));
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAsDropDown(parent);
		update();
		yearAndMonthTv = (TextView) view
				.findViewById(R.id.popupwindow_calendar_date);
		final KCalendar calendar = (KCalendar) view
				.findViewById(R.id.popupwindow_calendar);
		yearAndMonthTv.setText(calendar.getCalendarYear() + "-"
				+ calendar.getCalendarMonth());
		if (null != date) {
			int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
			int month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
					date.lastIndexOf("-")));
			yearAndMonthTv.setText(years + "-" + month);
			calendar.showCalendar(years, month);
			calendar.setCalendarDayBgColor(date,
					R.drawable.calendar_date_focused);
		}
		List<String> list = new ArrayList<String>(); // 设置标记列表
		list.add("2014-04-01");
		list.add("2014-04-02");
		calendar.addMarks(list, 0);
		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {
			public void onCalendarClick(int row, int col, String dateFormat) {
				int month = Integer.parseInt(dateFormat.substring(
						dateFormat.indexOf("-") + 1,
						dateFormat.lastIndexOf("-")));
				if (calendar.getCalendarMonth() - month == 1// 跨年跳转
						|| calendar.getCalendarMonth() - month == -11) {
					calendar.lastMonth();
				} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
						|| month - calendar.getCalendarMonth() == -11) {
					calendar.nextMonth();
				} else {
					calendar.removeAllBgColor();
					calendar.setCalendarDayBgColor(dateFormat,
							R.drawable.calendar_date_focused);
					date = dateFormat;// 最后返回给全局 date
					dismiss();
					yearAndMonthTv.setText(date);
					if (callback != null) {
						callback.getDataHositoryInfos(date);
					}

				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				yearAndMonthTv.setText(year + "-" + month);
			}
		});

	}

	public interface PopupWindowCallback {
		public void getDataHositoryInfos(String date);

	}

}