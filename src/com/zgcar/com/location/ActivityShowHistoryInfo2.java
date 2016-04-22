package com.zgcar.com.location;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.Entity.HistoryEntity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.PopupWindows;
import com.zgcar.com.util.PopupWindows.PopupWindowCallback;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 历史轨迹详细页面 ,播放
 * 
 * @author mddoscar
 * @name ObservetoActivity
 */
public class ActivityShowHistoryInfo2 extends Activity implements
		OnClickListener, PopupWindowCallback {

	private WebView webView;
	// 查询列表
	private List<HistoryEntity> list;
	// 全局参数
	private MyApplication app;
	private Dialog progressDialog;
	private TextView titleDate;
	private ImageView dateState;
	private LinearLayout selectDate;

	private boolean webViewFinished, isSelectingDate;
	private String date;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 判断是否有数据
				markToMap();
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
		SetTitleBackground.setTitleBg(ActivityShowHistoryInfo2.this,
				R.color.color_4);
		setContentView(R.layout.activity_show_history_info);
		super.onCreate(savedInstanceState);
		initialize();
	}

	private void initialize() {
		isSelectingDate = false;
		app = (MyApplication) getApplication();
		webViewFinished = false;
		webView = (WebView) findViewById(R.id.history_show_infos_webview);
		webView.setVisibility(View.VISIBLE);
		ImageButton back = (ImageButton) findViewById(R.id.show_history_info_back);
		back.setOnClickListener(this);
		selectDate = (LinearLayout) findViewById(R.id.show_history_info_select_date);
		selectDate.setOnClickListener(this);
		titleDate = (TextView) findViewById(R.id.show_history_info_date_desc);
		date = Util.getTodayDate();
		titleDate.setText(date);
		dateState = (ImageView) findViewById(R.id.show_history_info_date_state);
		initWebView();
		showProgressDialog();
		historyRequest();
	}

	/**
	 * 在地图上打点
	 */
	private void markToMap() {
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String icon = "";
				if (i == 0) {
					icon = "amap_start_en";
				} else if (i == list.size() - 1) {
					icon = "amap_end_en";
				} else {
					icon = "amap_throughpoint_en";
				}
				String avaStr3 = String.format(
						"javascript:MapCenter('%s', '%s', '%s');", list.get(i)
								.getLo(), list.get(i).getLa(), "13");
				webView.loadUrl(avaStr3);
				String java = String
						.format("javascript:DrawPosLine('%s', '%s', '%s', '%s', '%s', '%s');",
								i, list.get(i).getLo(), list.get(i).getLa(),
								icon, "0", "1");
				webView.loadUrl(java);
			}
		} else {
			Util.showToastBottom(ActivityShowHistoryInfo2.this,
					getString(R.string.no_baby_history_info));
			if (app.getTempMyLocation() != null) {
				String data = String
						.format("javascript:DrawPos('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
								0, app.getTempMyLocation().getLongitude(), app
										.getTempMyLocation().getLatitude(),
								"locaion", "0", "your current location", "0");
				webView.loadUrl(data);
				String avaStr3 = String.format(
						"javascript:MapCenter('%s', '%s', '%s');", app
								.getTempMyLocation().getLongitude(), app
								.getTempMyLocation().getLatitude(), "13");
				webView.loadUrl(avaStr3);
			}

		}
		dismissDialog();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				webViewFinished = true;
				super.onPageFinished(view, url);
			}
		});
		webView.loadUrl(FinalVariableLibrary.WEB_HOST_HISTORY);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_history_info_back:// 返回历史轨迹
			finish();
			break;
		case R.id.show_history_info_select_date:// 返回历史轨迹
			showSelectDatePopupWindow();
			break;
		default:
			break;
		}

	}

	private void showSelectDatePopupWindow() {
		if (date == null || "".equals(date)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
					Locale.CHINA);
			Date d = new Date(System.currentTimeMillis());
			date = format.format(d);
		}
		if (isSelectingDate) {
			dateState.setImageResource(R.drawable.arr_down);
			isSelectingDate = false;
		} else {
			dateState.setImageResource(R.drawable.arr_up);
			isSelectingDate = true;
		}
		new PopupWindows(ActivityShowHistoryInfo2.this, selectDate, date,
				ActivityShowHistoryInfo2.this)
				.setOnDismissListener(new OnDismissListener() {
					public void onDismiss() {
						dateState.setImageResource(R.drawable.arr_down);
						isSelectingDate = false;
					}
				});
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	/**
	 * 获取日期当天历史记录
	 */
	private String getRequestJson() {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", FinalVariableLibrary.GET_HISTORY_CMD);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("imei", app.getImei());
			b2.put("map_type", FinalVariableLibrary.MAP_TYPE);
			b2.put("date", date);
			array.put(b2);
			b.put("data", array);
			Log.e("json", b.toString());
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	private synchronized void historyRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getRequestJson();
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					list = ResolveServiceData.historyroute();
					while (!webViewFinished) {
					}
					handler.sendMessage(handler.obtainMessage(0));
				} else {
					dismissDialog();
					Looper.prepare();
					Toast.makeText(ActivityShowHistoryInfo2.this,
							SocketUtil.isFail(ActivityShowHistoryInfo2.this),
							Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
			}
		}).start();
	}

	private void dismissDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new Dialog(ActivityShowHistoryInfo2.this,
					R.style.dialog);
			progressDialog.setContentView(R.layout.view_progress_dialog);
		}
		progressDialog.show();
	}

	@Override
	public void getDataHositoryInfos(String date) {
		this.date = date;
		titleDate.setText(date);
		showProgressDialog();
		historyRequest();
	}

}
