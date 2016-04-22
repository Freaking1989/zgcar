package com.zgcar.com.main;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.MapView;
import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.ActivityLocationModelDesc;
import com.zgcar.com.location.ActivityNaviRoute2;
import com.zgcar.com.location.ActivityScanning;
import com.zgcar.com.location.ActivityShowHistoryInfo2;
import com.zgcar.com.location.Entity.LocationTerminalListData;
import com.zgcar.com.location.adapter.AdapterAddBaby;
import com.zgcar.com.main.PhoneLocation.CallBack;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.main.util.SendLocationRequest;
import com.zgcar.com.main.util.SendLocationRequest.OnTerminalListLisener;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.MyTextView;
import com.zgcar.com.util.Util;

/**
 * 地图定位
 * 
 */
public class FragmentLocation2 extends Fragment implements
		OnTerminalListLisener, OnClickListener, CallBack {
	private View rootView;
	private WebView webView;
	private CircleImageView watchIcon, phoneIcon;
	private TextView watchName, locationTime;
	private MyTextView locationAddress;
	private LinearLayout navigateLinearLayout;
	private List<TerminalListInfos> listbInfos;
	private MyApplication app;
	private Bitmap watchIconBitmap;
	private PopupWindow popupWindow;
	private AdapterAddBaby adapter;
	private RelativeLayout showPopRelativeLayout;
	private ListView listView;
	private LocationTerminalListData watchLocationInfo;
	private int timerFlag = 60 * 1000;
	private boolean webViewFinished;
	private double tempLo, tempLa;
	private int flag;
	private Button locateModel;
	private SharedPreferences sf;
	private TextView mapModelDesc;
	private ImageButton mapModel;
	private LocationCallBack locationCallBack;
	private PhoneLocation phoneLocation;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				navigateLinearLayout.setVisibility(View.INVISIBLE);
				showWatchPositionInfos();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("LocationFragment2", "onCreate");
		rootView = View
				.inflate(getActivity(), R.layout.fragment_location, null);
		initialize();
		showGuideView();
	}

	private void getWatchListInfo() {
		SendLocationRequest sdds = new SendLocationRequest(getActivity(), sf);
		sdds.setLisener(this);
		sdds.sendRequest();
	}

	private void showGuideView() {
		boolean isFirst = sf.getBoolean("isFirst", true);
		if (!isFirst) {
			return;
		}
		dismissDialog();
		final Dialog dialog = new Dialog(getActivity(), R.style.dialog2);
		dialog.setCancelable(false);
		dialog.show();
		View view = View.inflate(getActivity(),
				R.layout.view_main_guide_the_first, null);
		Button start = (Button) view
				.findViewById(R.id.view_main_guide_bt_start_use);

		dialog.addContentView(view, new LayoutParams(
				FinalVariableLibrary.ScreenWidth,
				FinalVariableLibrary.ScreenHeight));
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				sf.edit().putBoolean("isFirst", false).commit();
				showDialog();
				getWatchListInfo();
			}
		});
	}

	/**
	 * 初始化控件，变量
	 */
	private void initialize() {
		phoneLocation = new PhoneLocation();
		sf = getActivity().getSharedPreferences(
				FinalVariableLibrary.CACHE_FOLDER, Context.MODE_PRIVATE);
		webViewFinished = false;
		listbInfos = new ArrayList<TerminalListInfos>();
		mapModelDesc = (TextView) rootView
				.findViewById(R.id.fragment_location_layers_model_desc_tv);
		mapModel = (ImageButton) rootView
				.findViewById(R.id.fragment_location_layers_model_ib);
		mapModel.setOnClickListener(this);
		app = (MyApplication) getActivity().getApplication();
		ImageButton ibHistory = (ImageButton) rootView
				.findViewById(R.id.fragment_location_history_imagebutton);
		ibHistory.setOnClickListener(this);
		webView = (WebView) rootView
				.findViewById(R.id.fragment_location_web_view);
		webView.setVisibility(View.VISIBLE);
		MapView mapView = (MapView) rootView
				.findViewById(R.id.fragment_location_map);
		mapView.setVisibility(View.GONE);
		watchIcon = (CircleImageView) rootView
				.findViewById(R.id.fragment_location_watch_icon);
		watchIcon.setOnClickListener(this);
		phoneIcon = (CircleImageView) rootView
				.findViewById(R.id.fragment_location_my_icon);
		watchName = (TextView) rootView
				.findViewById(R.id.fragment_location_watch_name);
		navigateLinearLayout = (LinearLayout) rootView
				.findViewById(R.id.fragment_location_navigation);
		locateModel = (Button) rootView
				.findViewById(R.id.fragment_location_watch_locate_model);
		locateModel.setOnClickListener(this);

		ImageButton navigateCar = (ImageButton) rootView
				.findViewById(R.id.fragment_location_navigation_drive);
		navigateCar.setOnClickListener(this);
		ImageButton navigateWalk = (ImageButton) rootView
				.findViewById(R.id.fragment_location_navigation_walk);
		navigateWalk.setOnClickListener(this);

		RelativeLayout locationPhone = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_phone_location);
		locationPhone.setOnClickListener(this);
		RelativeLayout callPhone = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_call_watch);
		callPhone.setOnClickListener(this);
		RelativeLayout recorder = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_record_voice);
		recorder.setOnClickListener(this);
		RelativeLayout locate = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_locate_watch_place);
		locate.setOnClickListener(this);
		RelativeLayout monitor = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_monitoring);
		monitor.setOnClickListener(this);
		Button magnifyMap = (Button) rootView
				.findViewById(R.id.fragment_location_magnify_map);
		magnifyMap.setOnClickListener(this);
		Button narrowMap = (Button) rootView
				.findViewById(R.id.fragment_location_narrow_map);
		narrowMap.setOnClickListener(this);
		locationTime = (TextView) rootView
				.findViewById(R.id.fragment_location_watch_locate_time);
		locationAddress = (MyTextView) rootView
				.findViewById(R.id.fragment_location_watch_locate_address);
		showPopRelativeLayout = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_top_title_layout);
		Button goToWatch = (Button) rootView
				.findViewById(R.id.fragment_location_go_watch_place);
		goToWatch.setOnClickListener(this);
		setLocationModel();
		initWebView();
	}

	/**
	 * 在地图上打点
	 * 
	 * @param id
	 *            点唯一标识
	 * @param lo
	 * @param la
	 */
	private void markToMap(int id, double lo, double la) {
		tempLo = lo;
		flag = 13;
		tempLa = la;
		if (id == 1) {
			webView.loadUrl("javascript:ClearAll()");
		}
		String data = String
				.format("javascript:DrawPos('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
						id, lo, la, "locaion", "0", "", "0");
		webView.loadUrl(data);
		String javaCenter = String.format(
				"javascript:MapCenter('%s','%s','%s');", lo, la, "13");
		webView.loadUrl(javaCenter);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				webViewFinished = true;
				dismissDialog();
				super.onPageFinished(view, url);
			}
		});
		webView.loadUrl(app.isStandardsMap() ? FinalVariableLibrary.WEB_HOST_LOCATION1
				: FinalVariableLibrary.WEB_HOST_LOCATION2);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("LocationFragment2", "onCreateView");
		return rootView;
	}

	@Override
	public void onResume() {
		Log.e("LocationFragment2", "onResume1");
		super.onResume();
		if (!sf.getBoolean("isFirst", true)) {
			getWatchListInfo();
		}
		initView();
	}

	/**
	 * 更新界面信息
	 */
	private void initView() {
		if (FinalVariableLibrary.bitmap != null) {
			phoneIcon.setImageBitmap(FinalVariableLibrary.bitmap);
		} else {
			phoneIcon.setImageResource(R.drawable.icon);
		}
		navigateLinearLayout.setVisibility(View.INVISIBLE);
		initPopuWindow();
	}

	/**
	 * 初始化终端列表的弹出框
	 */
	@SuppressWarnings("deprecation")
	private void initPopuWindow() {
		adapter = new AdapterAddBaby(getActivity(), listbInfos);
		View layout = View.inflate(getActivity(),
				R.layout.task_detail_popupwindow, null);
		listView = (ListView) layout.findViewById(R.id.lv_popup_list);
		View view1 = View.inflate(getActivity(), R.layout.adapter_add_watch,
				null);
		view1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ActivityScanning.class));
			}
		});
		listView.addFooterView(view1);
		popupWindow = new PopupWindow(layout,
				FinalVariableLibrary.ScreenWidth * 4 / 9,
				LayoutParams.WRAP_CONTENT, true);
		listView.setAdapter(adapter);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				watchIcon.setVisibility(View.VISIBLE);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				handler.removeCallbacks(getWatchLocationTimer);
				handler.postDelayed(getWatchLocationTimer, 0);
				webView.loadUrl("javascript:ClearAll()");
				app.setPosition(position);
				app.setImei(listbInfos.get(position).getImei());
				watchName.setText(listbInfos.get(position).getName());
				int width = (int) Util.dip2px(getActivity(), 55);
				watchIconBitmap = Util.decodeSampledBitmapFromResource(
						ListInfosEntity.getPathList().get(app.getPosition()),
						width, width);
				if (watchIconBitmap != null) {
					watchIcon.setImageBitmap(watchIconBitmap);
				} else {
					watchIcon.setImageResource(R.drawable.icon);
				}
				popupWindow.dismiss();
			}
		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			SocketUtil.close(true);
			handler.removeCallbacksAndMessages(null);
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onPause() {
		SocketUtil.close(true);
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		handler.removeCallbacksAndMessages(null);
		super.onPause();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.fragment_location_history_imagebutton:
			startActivity(new Intent(getActivity(),
					ActivityShowHistoryInfo2.class));
			break;
		case R.id.fragment_location_watch_icon:
			if (popupWindow != null) {
				popupWindow.showAsDropDown(showPopRelativeLayout, 0, 0);
				watchIcon.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.fragment_location_layers_model_ib:
			app.setStandardsMap(!app.isStandardsMap());
			webView.loadUrl(app.isStandardsMap() ? FinalVariableLibrary.WEB_HOST_LOCATION1
					: FinalVariableLibrary.WEB_HOST_LOCATION2);

			setLocationModel();
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					showWatchPositionInfos();
					super.onPageFinished(view, url);
				}
			});
			break;
		case R.id.fragment_location_navigation_drive:
			naviRoute(true);
			break;
		case R.id.fragment_location_navigation_walk:
			naviRoute(false);
			break;
		case R.id.fragment_location_phone_location:// 手机的位置
			phoneLocation.getPhoneLocation(getActivity(), this);
			break;
		case R.id.fragment_location_call_watch:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				String phoneno = listbInfos.get(app.getPosition()).getNumber();
				startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneno)));
			}
			break;
		case R.id.fragment_location_record_voice:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				getDataRequest(FinalVariableLibrary.RECORD_WATCH_VOICE);
			}
			break;
		case R.id.fragment_location_locate_watch_place:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				timerFlag = 10 * 1000;
				getDataRequest(FinalVariableLibrary.LOCATE_WATCH_CMD);
				handler.removeCallbacks(getWatchLocationTimer);
				handler.postDelayed(getWatchLocationTimer, 10 * 1000);
				handler.postDelayed(locateWatchOver, 60 * 1000);
			}
			break;
		case R.id.fragment_location_monitoring:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				getDataRequest(FinalVariableLibrary.SEND_MONITOR_CMD);
			}
			break;
		case R.id.fragment_location_magnify_map:
			if (flag > 0) {
				flag = flag - 3;
				if (flag < 0) {
					flag = 0;
				}
				Log.e("flag", flag + "");
				String javaCenter1 = String.format(
						"javascript:MapCenter('%s','%s','%s');", tempLo,
						tempLa, flag);
				webView.loadUrl(javaCenter1);
			}

			break;
		case R.id.fragment_location_narrow_map:
			if (flag < 22) {
				flag = flag + 3;
				if (flag > 22) {
					flag = 22;
				}
				Log.e("flag", flag + "");
				String javaCenter = String.format(
						"javascript:MapCenter('%s','%s','%s');", tempLo,
						tempLa, flag);
				webView.loadUrl(javaCenter);
			}
			break;
		case R.id.fragment_location_go_watch_place:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				if (watchLocationInfo != null) {
					navigateLinearLayout.setVisibility(View.VISIBLE);
					showWatchPositionInfos();
				}
			}
			break;
		case R.id.fragment_location_watch_locate_model:
			startActivity(new Intent(getActivity(),
					ActivityLocationModelDesc.class));
			break;
		default:
			break;
		}
	}

	private void showPhoneLocationInfo(AMapLocation location) {
		navigateLinearLayout.setVisibility(View.INVISIBLE);
		locationAddress.setText(location.getAddress());
		locationTime.setText(Util.getTime2(location.getTime() + ""));
		locateModel.setText(location.getProvider());
		markToMap(2, location.getLongitude(), location.getLatitude());
	}

	/**
	 * 导航
	 */
	private void naviRoute(Boolean isDrive) {
		if (!app.imeiIsEmpty(getActivity(), false)) {
			if (watchLocationInfo != null && app.getTempMyLocation() != null) {
				Intent intent = new Intent(getActivity(),
						ActivityNaviRoute2.class);
				intent.putExtra("isDrive", isDrive);
				getActivity().startActivity(intent);
			} else {
				Util.showToastBottom(getActivity(),
						getString(R.string.get_location_info_error));
			}
		}
	}

	private void showWatchPositionInfos() {
		try {
			if (watchLocationInfo.getLocation_type() == 0) {
				locationTime.setText(watchLocationInfo.getGpstime());
				locateModel.setText(getActivity().getString(
						R.string.gps_location));
			} else if (watchLocationInfo.getLocation_type() == 1) {
				locationTime.setText(watchLocationInfo.getGpstime());
				locateModel.setText(getActivity().getString(
						R.string.network_location));
			} else if (watchLocationInfo.getLocation_type() == 2) {
				locationTime.setText(watchLocationInfo.getGpstime());
				locateModel.setText(getActivity().getString(
						R.string.lbs_location));
			}
			locationAddress.setText(watchLocationInfo.getAddress());
			markToMap(1, watchLocationInfo.getLo(), watchLocationInfo.getLa());
			dismissDialog();
		} catch (Exception e) {
		}
	}

	/**
	 * 定位1分钟后回复原来10s定位一次
	 */
	private Runnable locateWatchOver = new Runnable() {
		@Override
		public void run() {
			timerFlag = 60 * 1000;
		}
	};
	/**
	 * 定时发送获取手表位置指令
	 */
	private Runnable getWatchLocationTimer = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(this, timerFlag);
			getDataRequest(FinalVariableLibrary.GET_WATCH_LOCATION);
		}
	};

	/**
	 * 发送数据到终端：监听、录音、定位。
	 */
	private synchronized void getDataRequest(final String cmd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = ResolveServiceData.getJson(cmd, app.getImei(),
						app.getUserName(), FinalVariableLibrary.MAP_TYPE);
				boolean flag = SocketUtil.connectService(data);
				if (flag) {
					if (FinalVariableLibrary.GET_WATCH_LOCATION.equals(cmd)) {
						watchLocationInfo = ResolveServiceData
								.terminalhistory();
						app.setTempWatchLocation(watchLocationInfo);
						while (!webViewFinished) {
						}
						handler.sendMessage(handler.obtainMessage(0));
					} else {
						Looper.prepare();
						Toast.makeText(getActivity(), R.string.send_succeed,
								Toast.LENGTH_SHORT).show();
						Looper.loop();
					}
				} else {
					dismissDialog();
					Looper.prepare();
					Toast.makeText(getActivity(),
							SocketUtil.isFail(getActivity()),
							Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
			}
		}).start();
	}

	// --------------------获取终端列表回调函数--------------------------------
	@Override
	public void getTerminalListSucceed(List<TerminalListInfos> listbInfos) {
		Log.e("LocationFragment2", "获取终端列表成功");
		ListInfosEntity.setTerminalListInfos(listbInfos);
		this.listbInfos = listbInfos;
		adapter.setLst(this.listbInfos);
		adapter.notifyDataSetChanged();
		if (listbInfos.size() > 0) {
			if (app.getPosition() > (listbInfos.size() - 1)) {
				app.setPosition(0);
			}
			app.setImei(listbInfos.get(app.getPosition()).getImei());
			watchName.setText(listbInfos.get(app.getPosition()).getName());
			handler.postDelayed(getWatchLocationTimer, 0);
		} else {
			dismissDialog();
			app.setImei("");
			app.setPosition(0);
		}
	}

	@Override
	public void getTerminalIconSucceed(List<String> paths) {
		Log.e("LocationFragment2", "获取终端列表头像成功");
		if (paths.size() > 0) {
			int width = (int) Util.px2dip(getActivity(), watchIcon.getWidth());
			watchIconBitmap = Util.decodeSampledBitmapFromResource(
					paths.get(app.getPosition()), width, width);

			if (watchIconBitmap != null) {
				watchIcon.setImageBitmap(watchIconBitmap);
			} else {
				watchIcon.setImageResource(R.drawable.icon);
			}
		}
	}

	@Override
	public void getTerminalIconFail() {
		Log.e("LocationFragment2", "获取终端列表头像失败");
	}

	@Override
	public void getTerminalListFail() {
		Log.e("LocationFragment2", "获取终端列表失败");
		if (ListInfosEntity.getTerminalListInfos().size() > 0) {
			listbInfos = ListInfosEntity.getTerminalListInfos();
			app.setImei(listbInfos.get(app.getPosition()).getImei());
			watchName.setText(listbInfos.get(app.getPosition()).getName());
			handler.postDelayed(getWatchLocationTimer, 0);
		} else {
			app.setImei("");
			app.setPosition(0);
		}
		dismissDialog();
	}

	private void setLocationModel() {
		if (app.isStandardsMap()) {
			mapModel.setImageResource(R.drawable.location_map_model1);
			mapModelDesc.setText(getString(R.string.location_layers_model_1));
			mapModelDesc.setTextColor(getResources().getColor(R.color.color_6));
		} else {
			mapModel.setImageResource(R.drawable.location_map_model2);
			mapModelDesc.setText(getString(R.string.location_layers_model_2));
			mapModelDesc
					.setTextColor(getResources().getColor(R.color.color_11));
		}
	}

	private void showDialog() {
		if (locationCallBack != null) {
			locationCallBack.showDialog();
		}
	}

	private void dismissDialog() {
		if (locationCallBack != null) {
			locationCallBack.dismissDialog();
		}
	}

	public void setLocationCallBack(LocationCallBack locationCallBack) {
		this.locationCallBack = locationCallBack;
	}

	@Override
	public void callBack(AMapLocation loc) {
		if (loc.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
			app.setTempMyLocation(loc);
			showPhoneLocationInfo(loc);
			Log.e("asd", loc.toStr());
		} else {
			Util.showToastCenter(getActivity(), loc.getErrorInfo() + "错误码:"
					+ loc.getErrorCode());
		}
		
	}

}