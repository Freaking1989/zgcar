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
	 * ��λ
	 * */
	private AMapLocationClient locationClient;
	private AMapLocationClientOption locationOption;
	private CallBack callBack;

	public void getPhoneLocation(Context context, CallBack callBack) {
		this.callBack = callBack;
		if (locationClient == null || locationOption == null) {
			locationClient = new AMapLocationClient(context);
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
			 * ���AMapLocationClient���ڵ�ǰActivityʵ�����ģ�
			 * ��Activity��onDestroy��һ��Ҫִ��AMapLocationClient��onDestroy
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
