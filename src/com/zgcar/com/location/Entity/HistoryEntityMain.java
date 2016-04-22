package com.zgcar.com.location.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log;

public class HistoryEntityMain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public static List<HistoryEntityMain> getDate2() {
		Log.e("dayDesc", "asd");
		List<HistoryEntityMain> list = new ArrayList<HistoryEntityMain>();
		Calendar time = Calendar.getInstance();

		int day = time.get(Calendar.DATE); // 日
		int month = time.get(Calendar.MONTH) + 1;// 月
		int year = time.get(Calendar.YEAR);

		for (int i = 0; i < 12; i++) {
			Log.e("dayDesc23", getDaysByYearMonth(year, i) + "");
		}

		for (int i = 0; i < 7; i++) {

			HistoryEntityMain info = new HistoryEntityMain();
			String monthDesc = month + "";
			String dayDesc = (day - i) + "";
			String yearDesc = year + "";

			Log.e("dayDesc", dayDesc);

			if ((day - i) <= 0) {
				Log.e("dayDesc", dayDesc);
				monthDesc = (month - 1) + "";
				dayDesc = (getDaysByYearMonth(year, month - 1) + day - i) + "";
				Log.e("dayDesc", getDaysByYearMonth(year, month - 1) + "");
			}
			if (monthDesc.equals("0")) {
				monthDesc = "12";
				yearDesc = (year - 1) + "";
			}
			if (monthDesc.length() < 2) {
				monthDesc = "0" + monthDesc;
			}
			if (dayDesc.length() < 2) {
				dayDesc = "0" + dayDesc;
			}
			String date = yearDesc + "-" + monthDesc + "-" + dayDesc;
			info.setDate(date);
			list.add(info);
		}
		return list;
	}

	/**
	 * 根据年 月 获取对应的月份 天数
	 * */
	public static int getDaysByYearMonth(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

}
