package com.zgcar.com.account;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.DownFile;
import com.zgcar.com.util.DownFile.OnDownLoadLisener;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityAboutApp extends Activity implements OnClickListener,
		OnDownLoadLisener {
	private TextView versionTv, versionTv1;
	private Dialog dialog;
	private String newVersionLink, version;
	private ProgressDialog progressDialog;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				versionTv1.setText(getString(R.string.no_version_info));
				break;
			case 0:
				showUpDetaDialog();
				break;
			case 1:
				versionTv1.setText(getString(R.string.latest_version));
				break;
			case 2:
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
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app);
		initialize();
		connectService();
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
				String json = getRequestJson();
				boolean flag = SocketUtil.connectService(json);
				if (flag) {
					String ver = ResolveServiceData.getAppLink();
					if (!"".equals(ver)) {
						dismissDialog();
						Log.e("ver", ver);
						String[] str = ver.split("#");
						try {
							double ver1 = Double.parseDouble(version);
							double ver2 = Double.parseDouble(str[0]);
							if ((ver2 - ver1) > 0.001) {
								newVersionLink = str[1];
								handler.sendMessage(handler.obtainMessage(0));
							} else {
								handler.sendMessage(handler.obtainMessage(1));
							}
						} catch (Exception e) {
							handler.sendMessage(handler.obtainMessage(-1));
						}
					} else {
						dismissDialog();
						handler.sendMessage(handler.obtainMessage(-1));
					}
				} else {
					dismissDialog();
					handler.sendMessage(handler.obtainMessage(-1));
				}
			}
		}).start();
	}

	private void connectService() {
		try {
			int code = getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionCode;
			version = getPackageManager().getPackageInfo(this.getPackageName(),
					0).versionName;
			versionTv.setText(version);
			Log.e("version", version + ":" + code);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		LinearLayout officialWebsite = (LinearLayout) findViewById(R.id.about_us_official_website);
		officialWebsite.setOnClickListener(this);
		LinearLayout permission = (LinearLayout) findViewById(R.id.about_us_permission);
		permission.setOnClickListener(this);
		LinearLayout update = (LinearLayout) findViewById(R.id.about_us_update_version);
		update.setOnClickListener(this);
		versionTv = (TextView) findViewById(R.id.about_us_app_version);
		versionTv1 = (TextView) findViewById(R.id.about_us_app_version_1);
		ImageButton back = (ImageButton) findViewById(R.id.account_about_app_back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.account_about_app_back:
			finish();
			break;
		case R.id.about_us_permission:
			startActivity(new Intent(ActivityAboutApp.this,
					ActivityAboutAppPermission.class));
			break;
		case R.id.about_us_update_version:
			showProdressDialog();
			getAppVersionInfo();
			break;
		case R.id.about_us_official_website:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(FinalVariableLibrary.WEB_OFFICIAL_WEBSITE));
			startActivity(intent);
			break;
		case R.id.view_dialog_yes:
			update();
			break;
		case R.id.view_dialog_no:
			dismissDialog();
			break;
		default:
			break;
		}
	}

	private void update() {
		if (newVersionLink.equals("")) {
			handler.sendMessage(handler.obtainMessage(-1));
		} else {
			progressDialog = new ProgressDialog(ActivityAboutApp.this);
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
						DownFile.upDateApp(newVersionLink, file,
								ActivityAboutApp.this);
					} catch (IOException e) {
						progressDialog.dismiss();
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void showProdressDialog() {
		dialog = getDialog();
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	private Dialog getDialog() {
		return dialog == null ? new Dialog(ActivityAboutApp.this,
				R.style.dialog) : dialog;

	}

	private void showUpDetaDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityAboutApp.this,
				R.layout.view_dialog_yes_or_not, null);
		TextView tetle = (TextView) view.findViewById(R.id.view_dialog_title);
		tetle.setText(getString(R.string.udate_app));
		Button yes = (Button) view.findViewById(R.id.view_dialog_yes);
		yes.setOnClickListener(this);
		Button no = (Button) view.findViewById(R.id.view_dialog_no);
		no.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void update(String str) {
		Message msg = handler.obtainMessage(2);
		msg.obj = str;
		handler.sendMessage(msg);
		Log.e("已下载：", str);
	}

	@Override
	public void downSucceed() {
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
	}

	@Override
	public void downFailed() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Util.showToastBottom(ActivityAboutApp.this,
						getString(R.string.update_has_unknown_error));
				progressDialog.dismiss();
			}
		});

	}

}
