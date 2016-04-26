package com.zgcar.com.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.zgcar.com.util.Util;

public class ServiceStartLocation extends Service implements
		AMapLocationListener {
	private AMapLocationClient locationClient;
	private AMapLocationClientOption locationOption;
	private MyApplication app;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		app = (MyApplication) getApplication();
		startLocation();
		super.onCreate();
	}

	private void startLocation() {
		locationClient = new AMapLocationClient(this.getApplicationContext());
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
		locationOption.setInterval(30 * 1000);
		locationClient.setLocationListener(this);
		locationClient.setLocationOption(locationOption);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 启动定位
		locationClient.startLocation();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	@Override
	public void onLocationChanged(AMapLocation loc) {
		if (loc != null) {
			if (loc.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
				app.setTempMyLocation(loc);
				Log.e("asd", loc.toStr());
			} else if (loc.getErrorCode() == AMapLocation.ERROR_CODE_FAILURE_CONNECTION) {
				Util.showToastCenter(ServiceStartLocation.this,
						loc.getErrorInfo() + "错误码:" + loc.getErrorCode());
			} else {
				Util.showToastCenter(ServiceStartLocation.this,
						loc.getErrorInfo() + "错误码:" + loc.getErrorCode());
			}
		}
	}

}
