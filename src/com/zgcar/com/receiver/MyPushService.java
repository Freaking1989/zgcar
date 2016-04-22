package com.zgcar.com.receiver;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.R;
import com.zgcar.com.account.model.MessageInfos;
import com.zgcar.com.db.DbManager;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MainActivity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.Util;

public class MyPushService extends Service {

	private MediaPlayer mediaPlayer;
	private AssetFileDescriptor assetFileDescriptor;
	private WindowManager windowManager;
	private View mView;
	private MyApplication app;
	private String imei;
	private SharedPreferences sf;

	@Override
	public void onCreate() {
		sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER,
				MODE_PRIVATE);
		Log.e("MyPushService", "onCreate");
		app = (MyApplication) getApplication();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.e("MyPushService", "onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 
	 * 0:系统消息（当flag 为0时，不需处理后面的数据）
	 * 
	 * 1:报警消息 2:语音消息 3:话费短信
	 * **/
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		boolean flag2 = sf.getBoolean("notifyRing", true);
		boolean flag3 = sf.getBoolean("notifyShake", true);
		if (!flag2 && flag3) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
			vibrator.vibrate(pattern, -1);
		}
		Log.e("MyPushService", "onStartCommand");
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean hasPush = true;
				int a = 0;
				while (hasPush) {
					Log.e("MyPushService", "while开始");
					a++;
					Log.e("MyPushService", "onStartCommand:" + a);
					Bundle bundle = app.getPushBundle();

					if (bundle != null) {
						dealPushMessage(bundle);
					} else {
						hasPush = false;
					}
					Log.e("MyPushService", "while结束");
				}
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	private synchronized void dealPushMessage(Bundle bundle) {
		Log.e("MyPushService", "开始");
		try {
			JSONObject object = new JSONObject(
					bundle.getString(JPushInterface.EXTRA_EXTRA));
			Log.e("MyPushService", object.toString());
			int flag = object.getInt("flag");
			if (flag == 3) {
				MessageInfos info = sendRequestPhoneMessage(object
						.getString("imei"));
				if (info != null) {
					Intent intent2 = new Intent(
							FinalVariableLibrary.ChackBroadcastReceiverAction);
					intent2.putExtra("MessageInfo", info);
					intent2.putExtra("imei", object.getString("imei"));
					sendOrderedBroadcast(intent2, null);
				}
			} else if (flag == 2 || flag == 12) {

				
				
				
			} else {
				Log.e("MyPushService", "报警1!!!");
				if (object.getString("alarm").equals("1")) {
					if (windowManager == null && mediaPlayer == null) {

						Log.e("MyPushService", object.getString("msg"));
						Message message = handler.obtainMessage();
						message.what = 1;
						message.obj = object.getString("msg");
						imei = object.getString("imei");
						handler.sendMessage(message);
					}
				}
				Intent intent2 = new Intent(
						FinalVariableLibrary.SystemNotifyMessageAction);
				intent2.putExtras(bundle);
				sendOrderedBroadcast(intent2, null);
			}
		} catch (JSONException e) {
			Log.e("MyPushService", "失败");
			e.printStackTrace();
		}
		Log.e("MyPushService", "结束");
	}


	private void setWindowDialog(String mMsg) {
		windowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams para = new WindowManager.LayoutParams();
		para.height = LayoutParams.MATCH_PARENT;
		para.width = LayoutParams.MATCH_PARENT;
		para.type = LayoutParams.TYPE_SYSTEM_ALERT;
		para.format = PixelFormat.TRANSPARENT;
		para.gravity = Gravity.CENTER;
		mView = View.inflate(MyPushService.this, R.layout.window_dialog, null);
		TextView window_dialogSure = (TextView) mView
				.findViewById(R.id.window_dialogClose);
		TextView window_dialogLook = (TextView) mView
				.findViewById(R.id.window_dialoglook);
		TextView dialog_textView = (TextView) mView
				.findViewById(R.id.dialog_textView);
		TextView dialog_texttitle = (TextView) mView
				.findViewById(R.id.dialog_texttitle);
		dialog_texttitle.setText(getString(R.string.jpush_dialogtitle));
		dialog_textView.setText("" + mMsg);

		window_dialogSure.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeWindowSos();
			}
		});
		window_dialogLook.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeWindowSos();
				init();
				Intent intent = new Intent(MyPushService.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		windowManager.addView(mView, para);
	}

	/**
	 * 将全局中保存的当前终端切换为接收到的消息终端
	 * 
	 * @param app
	 * @param object
	 */
	private void init() {
		if (!"".equals(imei)) {
			app.setImei(imei);
			if (ListInfosEntity.getTerminalListInfos() != null
					&& ListInfosEntity.getTerminalListInfos().size() > 0) {
				for (int i = 0; i < ListInfosEntity.getTerminalListInfos()
						.size(); i++) {
					if (ListInfosEntity.getTerminalListInfos().get(i).getImei()
							.equals(imei)) {
						app.setPosition(i);
						break;
					}
				}
			}
		}
	}

	private void closeWindowSos() {
		windowManager.removeView(mView);
		stopSos();
		windowManager = null;
	}

	private void startSos() {
		AssetManager assetManager = getAssets();
		try {
			assetFileDescriptor = assetManager.openFd("sos.mp3");
			if (assetFileDescriptor != null) {
				Log.e("assetFileDescriptor", "assetFileDescriptor != null");
				mediaPlayer = getMedia();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(
						assetFileDescriptor.getFileDescriptor(),
						assetFileDescriptor.getStartOffset(),
						assetFileDescriptor.getLength());
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					public void onCompletion(MediaPlayer arg0) {
						if (null != mediaPlayer) {
							mediaPlayer.start();
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				String str = android.os.Build.MODEL;
				String str2 = android.os.Build.VERSION.RELEASE;
				boolean flag2 = sf.getBoolean("notifyRing", true);
				// boolean flag3 = sf.getBoolean("notifyShake", false);
				boolean flag4 = sf.getBoolean("notifyNight", false);

				if (!str.contains("MI") && !str2.contains("MI")) {
					if (flag4) {
						boolean isCanPlay = Util.isCanPlayer(sf.getString(
								"timePeriod", "20:00-8:00"));
						if (isCanPlay) {
							startSos();
						}
					} else if (flag2) {
						startSos();
					}

					String mMsg = (String) msg.obj;
					setWindowDialog(mMsg);
				}
			}
		};
	};

	private synchronized MessageInfos sendRequestPhoneMessage(String imei) {
		String str = GetJsonString.getRequestJson(
				FinalVariableLibrary.GET_PHONE_MESSAGE, imei, -1,
				app.getUserName());
		Boolean flag = SocketUtil.connectService(str);
		MessageInfos info = flag ? ResolveServiceData.getPhoneMessage() : null;
		if (info != null) {
			DbManager db = new DbManager(MyPushService.this, imei,
					app.getUserName());
			db.insert(info);
			db.closeDb();
			return info;
		}
		return null;
	}

	private MediaPlayer getMedia() {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		return mediaPlayer;
	}

	private void stopSos() {
		if (null != mediaPlayer) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (null != assetFileDescriptor) {
			assetFileDescriptor = null;
		}
	}

}
