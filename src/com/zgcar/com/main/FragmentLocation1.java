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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.ActivityLocationModelDesc;
import com.zgcar.com.location.ActivityNaviRoute1;
import com.zgcar.com.location.ActivityScanning;
import com.zgcar.com.location.ActivityShowHistoryInfo1;
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
public class FragmentLocation1 extends Fragment implements OnClickListener,
		InfoWindowAdapter, OnTerminalListLisener, CallBack {

	private Marker marker1, marker2;// 定位雷达小图标
	private View rootView;
	private PopupWindow popWindow;
	private ListView listView;
	private CircleImageView watchIcon;
	private MapView mapView;
	private AMap aMap;
	private MyTextView locateAddress, locateTime;
	private TextView watchName;
	private AdapterAddBaby adapters;
	private List<TerminalListInfos> list;
	private MyApplication app;
	private int timerFlag = 60 * 1000;
	private CircleImageView myIcon;
	private boolean isLocationFinish;
	private Button locateModel;
	private ImageButton mapModel;
	private TextView mapModelDesc;
	private LocationCallBack locationCallBack;

	/**
	 * 当前终端的位置信息
	 */
	private LocationTerminalListData terminalInfo;
	private LinearLayout navigation;
	private Bitmap tempBitmap;
	private SharedPreferences sf;
	/**
	 * 定位
	 * */
	private PhoneLocation phoneLocation;

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3:
				Log.e("LocationFragment1", "6");
				newlocations();
				break;
			case 4:// 点击定位
				break;

			default:
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = View
				.inflate(getActivity(), R.layout.fragment_location, null);
		init(savedInstanceState);
		initMap();
		showGuideView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return rootView;
	}

	/**
	 * 
	 */
	private void initMap() {
		aMap = mapView.getMap();
		aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		aMap.setMyLocationRotateAngle(180);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		setLocationModel();
		// 添加点击marker监听事件
		aMap.setInfoWindowAdapter(this);
		// 获取放大缩小ui设置控件
		UiSettings uiSettings = aMap.getUiSettings();
		uiSettings.setZoomControlsEnabled(false);// 隐藏地图放大缩小按钮
		uiSettings.setScaleControlsEnabled(true);// 显示地图默认比例尺
	}

	private void init(Bundle savedInstanceState) {
		phoneLocation = new PhoneLocation();
		mapModelDesc = (TextView) rootView
				.findViewById(R.id.fragment_location_layers_model_desc_tv);
		sf = getActivity().getSharedPreferences(
				FinalVariableLibrary.CACHE_FOLDER, Context.MODE_PRIVATE);
		mapModel = (ImageButton) rootView
				.findViewById(R.id.fragment_location_layers_model_ib);
		mapModel.setOnClickListener(this);
		terminalInfo = new LocationTerminalListData();
		app = (MyApplication) getActivity().getApplication();
		WebView webView = (WebView) rootView
				.findViewById(R.id.fragment_location_web_view);
		webView.setVisibility(View.GONE);
		mapView = (MapView) rootView.findViewById(R.id.fragment_location_map);
		mapView.setVisibility(View.VISIBLE);
		mapView.onCreate(savedInstanceState);
		myIcon = (CircleImageView) rootView
				.findViewById(R.id.fragment_location_my_icon);
		watchIcon = (CircleImageView) rootView
				.findViewById(R.id.fragment_location_watch_icon);
		watchIcon.setOnClickListener(this);
		watchName = (TextView) rootView
				.findViewById(R.id.fragment_location_watch_name);
		watchName.setOnClickListener(this);
		locateTime = (MyTextView) rootView
				.findViewById(R.id.fragment_location_watch_locate_time);
		locateModel = (Button) rootView
				.findViewById(R.id.fragment_location_watch_locate_model);
		locateModel.setOnClickListener(this);
		locateAddress = (MyTextView) rootView
				.findViewById(R.id.fragment_location_watch_locate_address);
		locateAddress.setOnClickListener(this);
		ImageButton navigationDrive = (ImageButton) rootView
				.findViewById(R.id.fragment_location_navigation_drive);
		ImageButton navigationWalk = (ImageButton) rootView
				.findViewById(R.id.fragment_location_navigation_walk);
		navigation = (LinearLayout) rootView
				.findViewById(R.id.fragment_location_navigation);
		navigation.setVisibility(View.INVISIBLE);
		navigationDrive.setOnClickListener(this);
		navigationWalk.setOnClickListener(this);
		// 定位我的位置
		RelativeLayout phoneLocation = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_phone_location);
		phoneLocation.setOnClickListener(this);
		// 放大缩小地图
		Button magnifyMap = (Button) rootView
				.findViewById(R.id.fragment_location_magnify_map);
		magnifyMap.setOnClickListener(this);
		Button narrowMap = (Button) rootView
				.findViewById(R.id.fragment_location_narrow_map);
		narrowMap.setOnClickListener(this);
		Button goWatchPlace = (Button) rootView
				.findViewById(R.id.fragment_location_go_watch_place);
		goWatchPlace.setOnClickListener(this);
		RelativeLayout callWatch = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_call_watch);
		callWatch.setOnClickListener(this);
		RelativeLayout recordVoice = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_record_voice);
		recordVoice.setOnClickListener(this);
		RelativeLayout locateWatchPlace = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_locate_watch_place);
		locateWatchPlace.setOnClickListener(this);
		RelativeLayout monitoring = (RelativeLayout) rootView
				.findViewById(R.id.fragment_location_monitoring);
		monitoring.setOnClickListener(this);
		ImageButton historyImageButton = (ImageButton) rootView
				.findViewById(R.id.fragment_location_history_imagebutton);
		historyImageButton.setOnClickListener(this);
		initPopupWindow();
	}

	@SuppressWarnings("deprecation")
	private void initPopupWindow() {
		isLocationFinish = false;
		list = new ArrayList<TerminalListInfos>();
		adapters = new AdapterAddBaby(getActivity(), list);
		View layout = View.inflate(getActivity(),
				R.layout.task_detail_popupwindow, null);
		listView = (ListView) layout.findViewById(R.id.lv_popup_list);
		View view1 = View.inflate(getActivity(), R.layout.adapter_add_watch,
				null);
		view1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ActivityScanning.class));
				getActivity().overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
			}
		});
		listView.addFooterView(view1);
		listView.setAdapter(adapters);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				handler.removeCallbacksAndMessages(null);
				popWindow.dismiss();
				showDialog();
				app.setPosition(position);
				app.setImei(list.get(position).getImei());
				watchName.setText(list.get(position).getName());
				// 获取当前终端的手机号as
				int width = (int) Util.dip2px(getActivity(), 55);
				tempBitmap = Util.decodeSampledBitmapFromResource(
						ListInfosEntity.getPathList().get(app.getPosition()),
						width, width);
				if (tempBitmap != null) {
					watchIcon.setImageBitmap(tempBitmap);
				} else {
					watchIcon.setImageResource(R.drawable.icon);
				}
				handler.postDelayed(timerWatchLocationRequest, 0);
			}
		});

		popWindow = new PopupWindow(layout,
				FinalVariableLibrary.ScreenWidth * 4 / 9,
				LayoutParams.WRAP_CONTENT, true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				watchIcon.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_location_go_watch_place:// 宝贝列表
			if (!app.imeiIsEmpty(getActivity(), true)) {
				if (terminalInfo != null) {
					navigation.setVisibility(View.VISIBLE);
					setLocationInfo(false);
				}
			}
			break;
		case R.id.fragment_location_navigation_walk:// 步行导航导航
			doNavWithType(1);
			break;
		case R.id.fragment_location_navigation_drive:// 自驾导航
			doNavWithType(2);
			break;

		case R.id.fragment_location_watch_icon:// 宝贝列表
			RelativeLayout topView = (RelativeLayout) rootView
					.findViewById(R.id.fragment_location_top_title_layout);
			popWindow.showAsDropDown(topView, 0, 0);// 显示宝贝列表
			watchIcon.setVisibility(View.INVISIBLE);
			break;
		case R.id.fragment_location_history_imagebutton:// 进入历史遗迹
			if (!app.imeiIsEmpty(getActivity(), true)) {
				mapView.onPause();
				startActivity(new Intent(getActivity(),
						ActivityShowHistoryInfo1.class));
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
			break;
		case R.id.fragment_location_phone_location:// 我的位置显示
			// if (app.getTempMyLocation() != null) {
			// setLocationInfo(true);
			// }
			// if (isLocationPhone) {
			// break;
			// }
			// isLocationPhone = true;
			phoneLocation.getPhoneLocation(getActivity(), this);
			break;
		case R.id.fragment_location_magnify_map:// 放大
			aMap.moveCamera(CameraUpdateFactory.zoomOut());
			break;
		case R.id.fragment_location_narrow_map:// 缩小
			aMap.moveCamera(CameraUpdateFactory.zoomIn());
			break;
		case R.id.fragment_location_monitoring:// 监听
			if (!app.imeiIsEmpty(getActivity(), true)) {
				sendRequest(FinalVariableLibrary.SEND_MONITOR_CMD);
			}
			break;
		case R.id.fragment_location_record_voice:// 录音指令
			if (!app.imeiIsEmpty(getActivity(), true)) {
				sendRequest(FinalVariableLibrary.RECORD_WATCH_VOICE);
			}
			break;
		case R.id.fragment_location_locate_watch_place:// 定位
			if (!app.imeiIsEmpty(getActivity(), true)) {
				handler.removeCallbacksAndMessages(null);
				handler.postDelayed(timer2, 60 * 1000);
				handler.postDelayed(timerWatchLocationRequest, 10 * 1000);
				timerFlag = 10 * 1000;
				sendRequest(FinalVariableLibrary.LOCATE_WATCH_CMD);
			}
			break;
		case R.id.fragment_location_call_watch:// 呼叫
			// 电话拨打
			if (!app.imeiIsEmpty(getActivity(), true)) {
				String phoneno = list.get(app.getPosition()).getNumber();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneno));
				startActivity(intent);
			}
			break;
		case R.id.fragment_location_layers_model_ib:
			app.setStandardsMap(!app.isStandardsMap());
			setLocationModel();

			break;
		case R.id.fragment_location_watch_locate_model:
			startActivity(new Intent(getActivity(),
					ActivityLocationModelDesc.class));
			getActivity().overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			break;
		default:
			break;
		}
	}

	private void setLocationModel() {
		if (app.isStandardsMap()) {
			aMap.setMapType(AMap.MAP_TYPE_NORMAL);
			mapModel.setImageResource(R.drawable.location_map_model1);
			mapModelDesc.setText(getString(R.string.location_layers_model_1));
			mapModelDesc.setTextColor(getResources().getColor(R.color.color_6));
		} else {
			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
			mapModel.setImageResource(R.drawable.location_map_model2);
			mapModelDesc.setText(getString(R.string.location_layers_model_2));
			mapModelDesc
					.setTextColor(getResources().getColor(R.color.color_11));
		}
	}

	/**
	 * 将坐标点在地图上进行显示
	 * 
	 * @param lp
	 *            经纬度坐标
	 * @param num用于标记对谁定位
	 *            ，以判断显示谁的头像
	 */

	public void getlalo(LatLng lp, boolean isMineSelf) {
		Log.e("LocationFragment1", "8");
		aMap.moveCamera(CameraUpdateFactory.changeLatLng(lp));
		try {
			Log.e("LocationFragment1", "9");
			View bitview = View.inflate(getActivity(),
					R.layout.point_location_pic, null);
			RelativeLayout layout = (RelativeLayout) bitview
					.findViewById(R.id.location_icons);
			if (list.size() > 0 && list.get(app.getPosition()).getOnline() == 1) {
				layout.setBackgroundResource(R.drawable.title_bg_headportrait_online);
			} else {
				layout.setBackgroundResource(R.drawable.title_bg_headportrait_not_online);
			}
			CircleImageView people = (CircleImageView) bitview
					.findViewById(R.id.people);

			if (isMineSelf) {
				if (marker1 != null) {
					marker1.remove();
				}
				people.setImageBitmap(FinalVariableLibrary.bitmap);
				BitmapDescriptor markerIcon = BitmapDescriptorFactory
						.fromView(bitview);
				marker1 = aMap.addMarker(new MarkerOptions().position(lp)
						.title("").icon(markerIcon).draggable(true));
			} else {
				aMap.clear();
				if (tempBitmap != null) {
					people.setImageBitmap(tempBitmap);
				}
				BitmapDescriptor markerIcon = BitmapDescriptorFactory
						.fromView(bitview);
				marker2 = aMap.addMarker(new MarkerOptions().position(lp)
						.title("").icon(markerIcon).draggable(true));

				marker2.showInfoWindow();
			}

		} catch (Exception e) {
			Log.e("LocationFragment1", "10");
		}
	}

	/**
	 * 60获取一次手表位置后显示位置信息
	 */
	public void newlocations() {
		isLocationFinish = true;
		Log.e("LocationFragment1", "7");
		navigation.setVisibility(View.INVISIBLE);
		setLocationInfo(false);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		dismissDialog();
	}

	private void setLocationInfo(boolean isMe) {
		if (isMe) {
			navigation.setVisibility(View.INVISIBLE);
			aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
			locateAddress.setText(app.getTempMyLocation().getAddress());
			locateTime.setText(Util.getCurrentTime());
			locateModel.setText(app.getTempMyLocation().getProvider());
			getlalo(new LatLng(app.getTempMyLocation().getLatitude(), app
					.getTempMyLocation().getLongitude()), isMe);
		} else {
			if (terminalInfo.getLocation_type() == 0) {
				locateModel.setText(getString(R.string.gps_location));
			} else if (terminalInfo.getLocation_type() == 1) {
				locateModel.setText(getString(R.string.network_location));
			} else if (terminalInfo.getLocation_type() == 2) {
				locateModel.setText(getString(R.string.lbs_location));
			}
			locateTime.setText(terminalInfo.getGpstime());
			locateAddress.setText(terminalInfo.getAddress());
			getlalo(new LatLng(terminalInfo.getLa(), terminalInfo.getLo()),
					isMe);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
		}
		handler.removeCallbacksAndMessages(null);
		SocketUtil.close(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		if (FinalVariableLibrary.bitmap != null) {
			myIcon.setImageBitmap(FinalVariableLibrary.bitmap);
		} else {
			myIcon.setBackgroundResource(R.drawable.icon);
		}
		if (!sf.getBoolean("isFirst", true)) {
			getWatchListInfo();
		}
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			SocketUtil.close(true);
			mapView.onPause();
			if (popWindow != null && popWindow.isShowing()) {
				popWindow.dismiss();
			}
			handler.removeCallbacksAndMessages(null);

		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		phoneLocation.destroyLocation();
		handler.removeCallbacksAndMessages(null);
		if (tempBitmap != null) {
			tempBitmap.recycle();
			tempBitmap = null;
		}
	}

	// -----------------------地图显示marker时对marker设置信息------------------------------

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		View Infoview = View.inflate(getActivity(),
				R.layout.point_addbaby_item, null);
		ImageView electric = (ImageView) Infoview.findViewById(R.id.electric);
		bgFictory(electric);
		return Infoview;
	}

	/**
	 * 根据不同终端的电量设置不同背景显示电量信息
	 */
	private void bgFictory(ImageView electric) {
		if (terminalInfo == null) {
			return;
		}
		int bat = terminalInfo.getBat();
		if (bat < 25) {
			electric.setImageResource(R.drawable.account_icon_battery01);
		} else if (bat >= 25 && bat < 50) {
			electric.setImageResource(R.drawable.account_icon_battery02);
		} else if (bat >= 50 && bat < 75) {
			electric.setImageResource(R.drawable.account_icon_battery03);
		} else if (bat >= 75 && bat < 100) {
			electric.setImageResource(R.drawable.account_icon_battery04);
		} else if (bat >= 100) {
			electric.setImageResource(R.drawable.account_icon_battery05);
		} else {
			electric.setImageResource(R.drawable.account_icon_battery03);
		}
	}

	/**
	 * 点击定位按钮时10s进行一次定位，60s后恢复为60s定位一次
	 */
	private Runnable timer2 = new Runnable() {
		@Override
		public void run() {
			handler.removeCallbacks(timer2);
			timerFlag = 60 * 1000;
		}
	};

	/**
	 * 定时发送获取位置请求
	 */
	private Runnable timerWatchLocationRequest = new Runnable() {
		@Override
		public void run() {
			isLocationFinish = false;
			Log.e("LocationFragment1", "3");
			handler.postDelayed(this, timerFlag);
			erifyRequest();
		}
	};

	/**
	 * 发送请求终端位置请求
	 */
	private synchronized void erifyRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = ResolveServiceData.getJson(
						FinalVariableLibrary.GET_WATCH_LOCATION, app.getImei(),
						"", FinalVariableLibrary.MAP_TYPE);
				boolean flag = SocketUtil.connectService(data);
				if (flag) {
					Log.e("LocationFragment1", "4");
					terminalInfo = ResolveServiceData.terminalhistory();
					app.setTempWatchLocation(terminalInfo);
					handler.sendMessage(handler.obtainMessage(3));
				} else {
					isLocationFinish = true;
					Log.e("LocationFragment1", "5");
					dismissDialog();
					try {
						Looper.prepare();
						Util.showToastBottom(getActivity(),
								SocketUtil.isFail(getActivity()));
						Looper.loop();
					} catch (Exception e) {

					}
				}
			}
		}).start();
	}

	// 只发送指令
	public void sendRequest(final String cmd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = ResolveServiceData.getJson(cmd, app.getImei(),
						app.getUserName(), FinalVariableLibrary.MAP_TYPE);
				boolean flag = SocketUtil.connectService(data);
				if (flag) {
					Looper.prepare();
					Util.showToastBottom(getActivity(), getActivity()
							.getString(R.string.send_succeed));
					Looper.loop();
				} else {
					Looper.prepare();
					Util.showToastBottom(getActivity(),
							SocketUtil.isFail(getActivity()));
					Looper.loop();
				}
			}
		}).start();
	}

	/**
	 * 线路规划
	 * 
	 * @param pStart
	 * @param pEnd
	 * @param pType
	 */
	private void doNavWithType(int pType) {
		if (app.getTempMyLocation() == null
				|| app.getTempWatchLocation() == null) {
			Util.showToastBottom(getActivity(),
					getString(R.string.get_location_info_error));
			return;
		}
		Intent intent = new Intent(getActivity(), ActivityNaviRoute1.class);
		intent.putExtra("type", pType);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
	}

	// --------------------获取终端列表回调函数--------------------------------

	@Override
	public synchronized void getTerminalListSucceed(
			List<TerminalListInfos> listbInfos) {
		handler.removeCallbacksAndMessages(null);
		Log.e("LocationFragment1", "1");
		list = listbInfos;
		adapters.setLst(listbInfos);
		adapters.notifyDataSetChanged();
		ListInfosEntity.setTerminalListInfos(listbInfos);
		if (list.size() > 0) {
			if (app.getPosition() > (list.size() - 1)) {
				app.setPosition(0);
			}
			app.setImei(list.get(app.getPosition()).getImei());
			watchName.setText(list.get(app.getPosition()).getName());
			handler.postDelayed(timerWatchLocationRequest, 0);
			Log.e("LocationFragment1", "2");
		} else {
			watchName.setText("");
			app.setPosition(0);
			app.setImei("");
			dismissDialog();
		}
	}

	@Override
	public synchronized void getTerminalIconSucceed(List<String> paths) {
		Log.e("getTerminalIconSucceed", "获取列表头像成功");
		ListInfosEntity.setPathList(paths);
		if (paths.size() > 0) {
			int width = (int) Util.dip2px(getActivity(), 55);
			tempBitmap = Util.decodeSampledBitmapFromResource(
					paths.get(app.getPosition()), width, width);
			if (isLocationFinish) {
				handler.removeCallbacks(timerWatchLocationRequest);
				handler.postDelayed(timerWatchLocationRequest, 0);
			}
			if (tempBitmap != null) {
				watchIcon.setImageBitmap(tempBitmap);
			} else {
				watchIcon.setImageResource(R.drawable.icon);
			}
			adapters.notifyDataSetChanged();
		}
	}

	@Override
	public synchronized void getTerminalIconFail() {
		handler.removeCallbacksAndMessages(null);
	}

	@Override
	public synchronized void getTerminalListFail() {
		handler.removeCallbacksAndMessages(null);
		list = ListInfosEntity.getTerminalListInfos();
		if (list.size() > 0) {
			app.setImei(list.get(app.getPosition()).getImei());
			watchName.setText(list.get(app.getPosition()).getName());
			handler.postDelayed(timerWatchLocationRequest, 0);
		} else {
			app.setPosition(0);
			app.setImei("");
		}
		dismissDialog();
	}

	private synchronized void showDialog() {
		if (locationCallBack != null) {
			locationCallBack.showDialog();
		}
	}

	private synchronized void dismissDialog() {
		if (locationCallBack != null) {
			locationCallBack.dismissDialog();
		}
	}

	public void setLocationCallBack(LocationCallBack locationCallBack) {
		this.locationCallBack = locationCallBack;
	}

	// ******************************定位***********************************

	@Override
	public void callBack(AMapLocation loc) {
		if (loc.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
			app.setTempMyLocation(loc);
			setLocationInfo(true);
			Log.e("asd", loc.toStr());
		} else {
			Util.showToastCenter(getActivity(), loc.getErrorInfo() + "错误码:"
					+ loc.getErrorCode());
		}
	}

}