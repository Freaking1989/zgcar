package com.zgcar.com.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.WindowManager;

public class SetTitleBackground {

	/**
	 * ���õ�ǰ�����״̬����ɫ
	 * 
	 * @param activity
	 * @param color
	 */
	@SuppressLint("InlinedApi")
	public static synchronized void setTitleBg(Activity activity, int color) {
		try {
			if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
				// ͸��״̬��
				activity.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				// ͸��������
				SystemBarTintManager tintManager = new SystemBarTintManager(
						activity);

				tintManager.setStatusBarTintEnabled(true);
				tintManager.setStatusBarTintResource(color);
				activity.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			}
		} catch (Exception e) {
		}
	}
}
