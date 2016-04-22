package com.zgcar.com.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class Util {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static float dip2px(Context context, float dpValue) {
		try {
			final float scale = context.getResources().getDisplayMetrics().density;
			context = null;
			return (dpValue * scale + 0.5f);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static float px2dip(Context context, float pxValue) {
		try {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (pxValue / scale + 0.5f);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 
	 * 设备是否联网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		try {
			ConnectivityManager conn = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = conn.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根据计算的inSampleSize，得到压缩后图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {
		File file = new File(pathName);
		if (file.exists()) {
			// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, options);
			// 调用上面定义的方法计算inSampleSize值
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			// 使用获取到的inSampleSize值再次解析图片
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
			return bitmap;
		} else {
			return null;
		}
	}

	public static boolean saveBitmapToSDCard(Bitmap bitmap, String imagePath) {
		try {
			FileOutputStream fos = new FileOutputStream(imagePath);
			boolean flag = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
			return flag;
		} catch (FileNotFoundException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return 2015-02-06
	 */
	public static String getTodayDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.CHINA);
		Date d = new Date(System.currentTimeMillis());
		String date = format.format(d);
		return date;
	}

	/**
	 * 
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sDateFormat.format(new java.util.Date());
	}

	/**
	 * milliseconds并进行格式化yyyy-MM-dd-HHmmss
	 */
	public static String getTime(String milliseconds) {
		Date date1 = new Date();
		date1.setTime(Long.parseLong(milliseconds));
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		return timeFormat.format(date1);
	}

	/**
	 * 
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getTime2(String milliseconds) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date1 = new Date();
		date1.setTime(Long.parseLong(milliseconds));
		return sDateFormat.format(date1);
	}

	public static void showToastBottom(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

	public static void showToastCenter(Context context, String info) {
		Toast toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 
	 * @return 有sd卡则返回路径否则返回null
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		} else {
			return null;
		}
	}

	/**
	 * 是否可以报警
	 * 
	 * @return
	 */
	public static boolean isCanPlayer(String time) {
		int hour = Integer.parseInt(getCurrentTime().substring(11, 13));
		String[] str = time.split("[-,:]");
		int startHour = Integer.parseInt(str[0]);
		if (startHour == 0) {
			startHour = 24;
		}
		int stopHour = Integer.parseInt(str[2]);
		if (stopHour == 0) {
			stopHour = 24;
		}
		Log.e("isCanPlayer", "startHour:" + startHour + ",stopHour;" + stopHour);
		if (stopHour > startHour) {
			if (hour >= startHour && hour < stopHour) {
				return true;
			} else {
				return false;
			}
			// 15 8
		} else if (stopHour < startHour) {
			if (hour >= startHour) {
				return true;
			} else if (hour < stopHour) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}

	}

	/**
	 * 生成二维码
	 * 
	 * @param url
	 * @param QR_WIDTH
	 * @param QR_HEIGHT
	 * @return
	 */
	public static Bitmap createQRImage(String url, int width, int height) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.RGB_565);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			// 显示到一个ImageView上面
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检查本地是否已安装分享的对象应用
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAvilible(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
				context = null;
				return true;
			}

		}
		context = null;
		return false;
	}

	/**
	 * @return 获取最近10年数组
	 */
	public static String[] getYearArray() {
		String[] str = new String[10];
		Time time = new Time();
		time.setToNow();
		int year = time.year;
		for (int i = 0; i < str.length; i++) {
			str[i] = (year - i) + "";
		}
		return str;
	}

	/**
	 * @return 获取月份数组
	 */
	public static String[] getMonthArray() {
		String[] str = new String[12];
		for (int i = 0; i < str.length; i++) {
			if (i + 1 < 10) {
				str[i] = "0" + (i + 1);
			} else {
				str[i] = "" + (i + 1);
			}
		}
		return str;
	}

	public static String[] getDayArray(String year, String month) {

		Calendar calendar = Calendar.getInstance();
		if (year != null && !year.equals("")) {
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String[] str = new String[days];
			for (int i = 0; i < str.length; i++) {
				if (i + 1 < 10) {
					str[i] = "0" + (i + 1);
				} else {
					str[i] = "" + (i + 1);
				}
			}
			return str;
		} else {
			int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String[] str = new String[days];
			for (int i = 0; i < str.length; i++) {
				if (i + 1 < 10) {
					str[i] = "0" + (i + 1);
				} else {
					str[i] = "" + (i + 1);
				}
			}
			return str;
		}
		// calendar.d
	}

}
