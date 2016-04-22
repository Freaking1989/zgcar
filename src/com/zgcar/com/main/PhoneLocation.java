package com.zgcar.com.main;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

public class PhoneLocation implements AMapLocationListener {
	/**
	 * 定位
	 * */
	private AMapLocationClient locationClient;
	private AMapLocationClientOption locationOption;
	private CallBack callBack;

	public void getPhoneLocation(Context context, CallBack callBack) {
		this.callBack = callBack;
		if (locationClient == null || locationOption == null) {
			locationClient = new AMapLocationClient(context);
			locationOption = new AMapLocationClientOption();
			// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
			locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 设置是否返回地址信息（默认返回地址信息）
			locationOption.setNeedAddress(true);
			// 设置是否只定位一次,默认为false
			locationOption.setOnceLocation(false);
			// 设置是否强制刷新WIFI，默认为强制刷新
			locationOption.setWifiActiveScan(true);
			// 设置是否允许模拟位置,默认为false，不允许模拟位置
			locationOption.setMockEnable(false);
			// 设置定位间隔,单位毫秒,默认为2000ms
			locationOption.setInterval(2000);
			locationClient.setLocationListener(this);
			locationClient.setLocationOption(locationOption);
		}
		Log.e("getPhoneLocation", "" + locationClient.isStarted());
		if (!locationClient.isStarted()) {
			locationClient.startLocation();
		}
	}

	public void destroyLocation() {
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.stopLocation();
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		if (callBack != null) {
			locationClient.stopLocation();
			callBack.callBack(arg0);
		}
	}

	public interface CallBack {
		void callBack(AMapLocation arg0);
	}
}
