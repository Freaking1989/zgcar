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
		// ���ö�λģʽΪ�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ
		locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// �����Ƿ񷵻ص�ַ��Ϣ��Ĭ�Ϸ��ص�ַ��Ϣ��
		locationOption.setNeedAddress(true);
		// �����Ƿ�ֻ��λһ��,Ĭ��Ϊfalse
		locationOption.setOnceLocation(false);
		// �����Ƿ�ǿ��ˢ��WIFI��Ĭ��Ϊǿ��ˢ��
		locationOption.setWifiActiveScan(true);
		// �����Ƿ�����ģ��λ��,Ĭ��Ϊfalse��������ģ��λ��
		locationOption.setMockEnable(false);
		// ���ö�λ���,��λ����,Ĭ��Ϊ2000ms
		locationOption.setInterval(30 * 1000);
		locationClient.setLocationListener(this);
		locationClient.setLocationOption(locationOption);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// ������λ
		locationClient.startLocation();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != locationClient) {
			/**
			 * ���AMapLocationClient���ڵ�ǰActivityʵ�����ģ�
			 * ��Activity��onDestroy��һ��Ҫִ��AMapLocationClient��onDestroy
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
						loc.getErrorInfo() + "������:" + loc.getErrorCode());
			} else {
				Util.showToastCenter(ServiceStartLocation.this,
						loc.getErrorInfo() + "������:" + loc.getErrorCode());
			}
		}
	}

}
