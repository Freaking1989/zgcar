package com.zgcar.com.main.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.start.ActivityLoadingInputPhoneNo;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.Util;

public class SendLocationRequest {

	private Activity activity;
	private MyApplication app;
	private SharedPreferences sf;
	private List<String> paths;
	private OnTerminalListLisener lisener;
	private List<TerminalListInfos> listbInfos;

	public SendLocationRequest(Activity activity, SharedPreferences sf) {
		this.sf = sf;
		this.activity = activity;
		app = (MyApplication) activity.getApplication();
	}

	public synchronized void sendRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getRequestJson();
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					listbInfos = ResolveServiceData.isSucceed();
					handler.sendMessage(handler.obtainMessage(0));
				} else {
					if (SocketUtil.errorCode == 1003) {
						activity.startActivity(new Intent(activity,
								ActivityLoadingInputPhoneNo.class));
						Quit.recycling(0);
						Quit.quit();
						return;
					}
					handler.sendMessage(handler.obtainMessage(1));
					Looper.prepare();
					Util.showToastBottom(activity, SocketUtil.isFail(activity));
					Looper.loop();
				}
			}
		}).start();
	}

	private String getRequestJson() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.GET_WATCH_LIST_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("user_name", app.getUserName());
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (lisener != null) {
					lisener.getTerminalListSucceed(listbInfos);
				}
				if (listbInfos.size() > 0) {
					MyAsyncTask task = new MyAsyncTask();
					TerminalListInfos[] infoArray = new TerminalListInfos[listbInfos
							.size()];
					for (int i = 0; i < listbInfos.size(); i++) {
						infoArray[i] = listbInfos.get(i);
					}
					task.execute(infoArray);
				}
				break;
			case 1:
				if (lisener != null) {
					lisener.getTerminalListFail();
				}
				break;
			case 2:
				if (lisener != null) {
					lisener.getTerminalIconSucceed(paths);
				}
				break;
			case 3:
				if (lisener != null) {
					lisener.getTerminalIconFail();
				}
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * 
	 * ÏÂÔØÍ¼Æ¬
	 * 
	 * @param imagPath
	 *            Í¼Æ¬ÍøÂçµØÖ·
	 * @param imei
	 *            ÖÕ¶ËimeiºÅ
	 * @param flag
	 *            Í¼Æ¬ÊÇ·ñÒÑÏÂÔØ±êÖ¾
	 */
	private synchronized void downloadImage(String imagPath, String imei,
			int flag) {
		try {
			URL url = new URL(imagPath);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDefaultUseCaches(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);

				File file = new File(FinalVariableLibrary.PATHS + "/" + "image");
				if (!file.exists()) {
					file.mkdirs();
				}
				File imageFile = new File(file.getPath(),
						System.currentTimeMillis() + ".jpg");

				imageFile.createNewFile();

				OutputStream outputStream = new FileOutputStream(imageFile);
				byte[] buffer = new byte[1024];
				int b;
				while ((b = bis.read(buffer, 0, 1024)) != -1) {
					outputStream.write(buffer, 0, b);
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				httpURLConnection.disconnect();
				paths.add(imageFile.getPath());
				Editor editor = sf.edit();
				editor.putString(imei + "image", imageFile.getPath());
				editor.putInt(imei + "imageFlag", flag);
				editor.commit();
			} else {
				paths.add("");
			}

		} catch (MalformedURLException e) {
			paths.add("");
			e.printStackTrace();
		} catch (IOException e) {
			paths.add("");
			e.printStackTrace();
		}
	}

	/**
	 * ÏÂÔØÍ¼Æ¬×ÊÔ´
	 */
	private class MyAsyncTask extends AsyncTask<TerminalListInfos, Void, Void> {
		@Override
		protected Void doInBackground(TerminalListInfos... arg0) {
			paths = new ArrayList<String>();
			for (TerminalListInfos infos : arg0) {
				int flag1 = infos.getImage_flag();
				int flag2 = sf.getInt(infos.getImei() + "imageFlag", -1);
				String path = sf.getString(infos.getImei() + "image", "");
				File fileTemp = new File(path);

				if (flag1 == flag2 && fileTemp.exists()) {
					paths.add(path);
				} else {
					if (flag1 > 0 && !infos.getImage_link().equals("")) {
						downloadImage(infos.getImage_link(), infos.getImei(),
								flag1);
					} else {
						paths.add("");
					}
				}
			}
			try {
				ListInfosEntity.setPathList(paths);
				if (ListInfosEntity.getTerminalListInfos().size() == paths
						.size()) {
					handler.sendMessage(handler.obtainMessage(2));
				} else {
					handler.sendMessage(handler.obtainMessage(3));
				}
			} catch (Exception e) {
			}

			return null;
		}
	}

	public void setLisener(OnTerminalListLisener lisener) {
		this.lisener = lisener;
	}

	public interface OnTerminalListLisener {

		void getTerminalListSucceed(List<TerminalListInfos> listbInfos);

		void getTerminalIconSucceed(List<String> paths);

		void getTerminalIconFail();

		void getTerminalListFail();

	}
}
