package com.zgcar.com.start;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import cn.jpush.android.api.InstrumentedActivity;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.DownFile;
import com.zgcar.com.util.DownFile.OnDownLoadLisener;
import com.zgcar.com.util.Util;

/**
 * 
 * 导航页
 * 
 */
public class ActivityWelcome extends InstrumentedActivity implements
		OnDownLoadLisener {

	private ProgressDialog progressDialog;
	private String link;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				showDialog();
				break;
			case 1:
				update();
				break;
			case 2:
				Log.e("已下载：", "handler");
				progressDialog
						.setMessage(getString(R.string.map_is_downloading)
								+ (String) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		ListInfosEntity.setTerminalListInfos(null);
		record();
		getAppVersionInfo();
	}

	private void showDialog() {
		final Dialog dialog = new Dialog(ActivityWelcome.this, R.style.dialog);
		View view = View.inflate(ActivityWelcome.this,
				R.layout.view_dialog_yes_or_not, null);
		TextView tetle = (TextView) view.findViewById(R.id.view_dialog_title);
		tetle.setText(getString(R.string.udate_app));
		Button yes = (Button) view.findViewById(R.id.view_dialog_yes);

		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				handler.sendMessage(handler.obtainMessage(1));
			}
		});

		Button no = (Button) view.findViewById(R.id.view_dialog_no);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				next();
			}
		});
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void update() {
		if (link.equals("")) {
			next();
			Util.showToastBottom(ActivityWelcome.this,
					getString(R.string.udate_failed));
		} else {
			progressDialog = new ProgressDialog(ActivityWelcome.this);
			progressDialog.setTitle(getString(R.string.is_udate_app));
			progressDialog.setMessage(getString(R.string.map_is_downloading)
					+ "0%");
			progressDialog.setCancelable(false);
			progressDialog.show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Log.e("gengxin", "123");
						File file = new File(Util.getSDPath(),
								FinalVariableLibrary.CACHE_FOLDER + ".apk");
						file.delete();
						file.createNewFile();
						DownFile.upDateApp(link, file, ActivityWelcome.this);
					} catch (IOException e) {
						progressDialog.dismiss();
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void next() {
		startActivity(new Intent(ActivityWelcome.this,
				ActivityLoadingInputPhoneNo.class));
		finish();
	}

	private void record() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(Util.getSDPath(), "temp.amr");
					MediaRecorder recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					recorder.setMaxDuration(500);
					recorder.setAudioChannels(1);
					recorder.setAudioSamplingRate(8000);
					recorder.setOutputFile(file.getAbsolutePath());
					recorder.prepare();
					recorder.start();
					Thread.sleep(1000);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/***
	 * 
	 * {"cmd":"00027","data":""}
	 * **/
	private String getRequestJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject
					.put("cmd", FinalVariableLibrary.GET_APP_VERSION_INFO_CMD);
			jsonObject.put("data", "");
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void getAppVersionInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String json = getRequestJson();
					String name = getPackageManager().getPackageInfo(
							getPackageName(), 0).versionName;
					boolean flag = SocketUtil.connectService(json);
					if (flag) {
						String ver = ResolveServiceData.getAppLink();
						if ("".equals(ver)) {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							next();
						} else {
							Log.e("ver", ver);
							String[] str = ver.split("#");

							if (str[0].equals(name)) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								next();
							} else {
								double ver1 = Double.parseDouble(name);
								double ver2 = Double.parseDouble(str[0]);
								if ((ver2 - ver1) > 0.001) {
									link = str[1];
									handler.sendMessage(handler
											.obtainMessage(0));
								} else {
									try {
										Thread.sleep(3000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									next();
								}
							}
						}
					} else {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						next();
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	@Override
	public void update(String str) {
		Log.e("已下载：", str);
		Message msg = handler.obtainMessage(2);
		Log.e("已下载：", str);
		msg.obj = str;
		handler.sendMessage(msg);
		Log.e("已下载：", str);
	}

	@Override
	public void downSucceed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				File apkfile = new File(Util.getSDPath(),
						FinalVariableLibrary.CACHE_FOLDER + ".apk");
				if (!apkfile.exists()) {
					return;
				}
				// 通过Intent安装APK文件
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
						"application/vnd.android.package-archive");
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	public void downFailed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Util.showToastBottom(ActivityWelcome.this,
						getString(R.string.udate_failed));
				progressDialog.dismiss();
				next();
			}
		});
	}
}
