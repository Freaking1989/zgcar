package com.zgcar.com.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.zgcar.com.entity.FinalVariableLibrary;

public class GetScreenShotImage {
	private Activity activity;

	public GetScreenShotImage(Activity activity) {
		this.activity = activity;
	}

	public String getShareImagePath() {
		String path = FinalVariableLibrary.PATHS;
		if (path.equals("") || path == null) {
			return "";
		}
		File file = new File(path + "image");
		if (!file.exists()) {
			file.mkdirs();
		}
		File file1 = new File(file.getPath(), System.currentTimeMillis()
				+ ".png");
		boolean flag = savePic(takeScreenShot(), file1.getPath());
		if (!flag) {
			return "";
		}
		return file1.getPath();
	}

	/**
	 * 将图片保存到本地进行分享
	 * 
	 * bitmap资源
	 * 
	 * @param strFileName
	 *            保存的名
	 */
	private boolean savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			b.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.flush();
			fos.close();
			b.recycle();
			b = null;
			return true;
		} catch (FileNotFoundException e) {
			b.recycle();
			b = null;
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			b.recycle();
			b = null;
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * 截屏进行分享
	 * 
	 * @return 返回bitmap
	 */
	private Bitmap takeScreenShot() {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		Log.i("TAG", "" + statusBarHeight);
		// 获取屏幕长和高
		int width = activity.getResources().getDisplayMetrics().widthPixels;
		int height = activity.getResources().getDisplayMetrics().heightPixels;
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		b1.recycle();
		b1 = null;
		return b;
	}
}
