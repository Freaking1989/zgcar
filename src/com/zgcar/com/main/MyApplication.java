package com.zgcar.com.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.amap.api.location.AMapLocation;
import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.history.entity.TreePosition;
import com.zgcar.com.location.Entity.LocationTerminalListData;
import com.zgcar.com.util.Util;

public class MyApplication extends Application {
	private List<Bundle> pushList;
	private int position;
	private String userName;
	private String imei;
	private boolean isFirst;
	private boolean isVoicePush;
	private boolean isStandardsMap;
	/**
	 * 蓝牙是否连接
	 */
	private boolean bluetoothDeviceIsConnected;
	/**
	 * 接收到的语音信息条数
	 */
	private int messageNum;
	/**
	 * 是否允许打开蓝牙
	 */
	private boolean isAllowedToOpenBlue;
	private BluetoothDevice tempBluetoothDevice;
	/**
	 * 我的位置
	 */
	private AMapLocation tempMyLocation;
	/**
	 * 手表位置
	 */
	private LocationTerminalListData tempWatchLocation;

	@Override
	public void onCreate() {
		Log.e("MyApplication", "onCreate");
		super.onCreate();
//		CrashHandler catchHandler = CrashHandler.getInstance();
//		catchHandler.init(getApplicationContext());
		FinalVariableLibrary.ScreenHeight = getResources().getDisplayMetrics().heightPixels;
		FinalVariableLibrary.ScreenWidth = getResources().getDisplayMetrics().widthPixels;
		isVoicePush = false;
		messageNum = 0;
		isStandardsMap = true;
		isFirst = true;
		position = 0;
		imei = "";
		userName = "";
		pushList = new ArrayList<Bundle>();
		getFlowerXY();

	}

	/**
	 * 为用户创建缓存目录
	 */
	private void createFileCache(String userName) {
		String path = Util.getSDPath();
		if (path != null) {
			File file = new File(path + "/" + FinalVariableLibrary.CACHE_FOLDER
					+ "/" + userName);
			if (!file.exists())
				file.mkdirs();
			FinalVariableLibrary.PATHS = file.toString();
		} else {
			FinalVariableLibrary.PATHS = null;
		}
	}

	private void getFlowerXY() {
		for (int i = 0; i < TreePosition.x.length; i++) {
			TreePosition.floatX[i] = Util.dip2px(getApplicationContext(),
					TreePosition.positionX[i]);
			TreePosition.floatY[i] = Util.dip2px(getApplicationContext(),
					TreePosition.positionY[i]);
		}
	}

	public final TagAliasCallback mTagsCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			Log.e("phone", "qidong");
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.e("phone", "Set tag and alias success");
				break;
			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.e("phone", logs);
				if (Util.isConnected(getApplicationContext())) {
					handle.sendMessageDelayed(handle.obtainMessage(1), 6000);
				} else {
					Log.e("phone", "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e("phone", logs);
			}

		}

	};

	public synchronized void addPushBundle(Bundle bundle) {
		pushList.add(bundle);
	}

	public Bundle getPushBundle() {
		if (pushList.size() > 0) {
			Bundle bundle = pushList.get(0);
			pushList.remove(0);
			Log.e("MyPushService", "Has Bundle");
			return bundle;
		}
		Log.e("MyPushService", "no Bundle");
		return null;
	}

	@SuppressLint("HandlerLeak")
	public Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				JPushInterface.setAliasAndTags(getApplicationContext(),
						userName, null, mTagsCallback);
				Log.e("phone", userName);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean userNameIsEmpty() {
		return userName.equals("") ? true : false;
	}

	public boolean imeiIsEmpty(Context context, boolean isShow) {
		if (imei.equals("")) {
			if (isShow) {
				Util.showToastBottom(context,
						getString(R.string.please_banding_watch_first));
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isSelected() {
		return position == -1 ? true : false;
	}

	public String getUserName() {
		Log.e("userName", userName);
		return userName;
	}

	public void setUserName(String userName) {
		createFileCache(userName);
		this.userName = userName;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isVoicePush() {
		return isVoicePush;
	}

	public void setVoicePush(boolean isVoicePush) {
		this.isVoicePush = isVoicePush;
	}

	public boolean isStandardsMap() {
		return isStandardsMap;
	}

	public void setStandardsMap(boolean isStandardsMap) {
		this.isStandardsMap = isStandardsMap;
	}

	public BluetoothDevice getTempBluetoothDevice() {
		return tempBluetoothDevice;
	}

	public void setTempBluetoothDevice(BluetoothDevice tempBluetoothDevice) {
		this.tempBluetoothDevice = tempBluetoothDevice;
	}

	public boolean isBluetoothDeviceIsConnected() {
		return bluetoothDeviceIsConnected;
	}

	public void setBluetoothDeviceIsConnected(boolean bluetoothDeviceIsConnected) {
		this.bluetoothDeviceIsConnected = bluetoothDeviceIsConnected;
	}

	public boolean isAllowedToOpenBlue() {
		return isAllowedToOpenBlue;
	}

	public void setAllowedToOpenBlue(boolean isAllowedToOpenBlue) {
		this.isAllowedToOpenBlue = isAllowedToOpenBlue;
	}

	public AMapLocation getTempMyLocation() {
		return tempMyLocation;
	}

	public void setTempMyLocation(AMapLocation tempMyLocation) {
		this.tempMyLocation = tempMyLocation;
	}

	public LocationTerminalListData getTempWatchLocation() {
		return tempWatchLocation;
	}

	public void setTempWatchLocation(LocationTerminalListData tempWatchLocation) {
		this.tempWatchLocation = tempWatchLocation;
	}

}
