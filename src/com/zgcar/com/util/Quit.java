package com.zgcar.com.util;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import com.zgcar.com.account.model.ListInfosAccount;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.model.ListInfosEntity;

public class Quit {

	private static List<Activity> list;
	private static List<Activity> cacheActivityList;

	public static void addCacheActivity(Activity activity) {
		if (cacheActivityList == null) {
			cacheActivityList = new ArrayList<Activity>();
		}
		cacheActivityList.add(activity);
	}

	public static void recyclingCacheActivity() {
		if (cacheActivityList != null) {
			for (Activity activity : cacheActivityList) {
				if (!activity.isFinishing()) {
					activity.finish();
				}
			}
			cacheActivityList = null;
		}
	}

	public static void addActivity(Activity activity) {
		if (list == null) {
			list = new ArrayList<Activity>();
		}
		list.add(activity);
	}

	public static void quit() {
		if (list != null) {
			for (Activity activity : list) {
				if (!activity.isFinishing()) {
					activity.finish();
				}
			}
			list = null;
		}
	}

	/**
	 * 收回资源
	 */
	public static void recycling(int flag) {
		if (flag == 0) {
			ListInfosEntity.setTerminalListInfos(null);
			if (FinalVariableLibrary.bitmap!=null) {
				FinalVariableLibrary.bitmap.recycle();
				FinalVariableLibrary.bitmap = null;
			}
		}
		ListInfosEntity.setZoneSafetyData(null);
		ListInfosAccount.setFamilyList(null);
	}

}
