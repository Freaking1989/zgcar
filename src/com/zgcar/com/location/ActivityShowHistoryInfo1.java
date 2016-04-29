package com.zgcar.com.location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.Entity.HistoryEntity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.MyTextView;
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
public class ActivityShowHistoryInfo1 extends Activity implements
		OnClickListener, OnMarkerClickListener, PopupWindowCallback,
		OnSeekBarChangeListener {
	private MapView mapView;
	private AMap aMap;
	private PopupWindow popWindow;
	// 查询列表
	private List<HistoryEntity> list, list1;
	// 全局参数
	private MyApplication app;
	// 控件
	private TextView titleDate, seekBarDescTv;
	private MyTextView addressTv, timeTv;
	// 编号
	private Dialog progressDialog;
	private boolean isSelectingDate;
	private String date;
	private LinearLayout selectDate;
	private ImageView dateState;
	private int[] drawables;
	private int tempPosition;
	private boolean isPlayer;
	private Polyline polyline;
	private Marker mMoveMarker, tempMarker;
	private Button player;
	private LatLng moveMarkerPosition;
	private int playerTime;
	private LinearLayout seekBarLayout;
	private Button isAll;
	private boolean filterIsOpen;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.e("isStarting", "what" + msg.what);
			switch (msg.what) {
			case 0:// 判断是否有数据
				dismissDialog();
				if (list != null && list.size() > 0) {
					filterIsOpen = false;
					doDrawJPoint(true);
				} else {
					Util.showToastBottom(ActivityShowHistoryInfo1.this,
							getString(R.string.no_baby_history_info));
				}
				break;
			case 1:// 判断是否有数据
				clearPlayerData();
				break;
			case 2:// 判断是否有数据
				if (tempMarker != null) {
					tempMarker.hideInfoWindow();
				}
				break;
			case 3:// 判断是否有数据
				addressTv.setText(list.get(msg.arg1).getAddress());
				if (list.get(msg.arg1).getLocation_type() == 0) {
					timeTv.setText(list.get(msg.arg1).getGpstime() + " "
							+ getString(R.string.gps_location));
				} else if (list.get(msg.arg1).getLocation_type() == 1) {
					timeTv.setText(list.get(msg.arg1).getGpstime() + " "
							+ getString(R.string.network_location));
				} else {
					timeTv.setText(list.get(msg.arg1).getGpstime() + " "
							+ getString(R.string.lbs_location));
				}
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
		SetTitleBackground.setTitleBg(ActivityShowHistoryInfo1.this,
				R.color.color_4);
		setContentView(R.layout.activity_show_history_info);
		super.onCreate(savedInstanceState);
		init();
		setUpMap(savedInstanceState);
	}

	// 初始化
	private void init() {
		filterIsOpen = false;
		playerTime = 80;
		tempPosition = 0;
		app = (MyApplication) getApplication();
		ImageButton back = (ImageButton) findViewById(R.id.show_history_info_back);
		back.setOnClickListener(this);
		isAll = (Button) findViewById(R.id.button2);
		isAll.setVisibility(View.VISIBLE);
		isAll.setOnClickListener(this);
		player = (Button) findViewById(R.id.button1);
		player.setVisibility(View.VISIBLE);
		player.setOnClickListener(this);
		selectDate = (LinearLayout) findViewById(R.id.show_history_info_select_date);
		selectDate.setOnClickListener(this);
		titleDate = (TextView) findViewById(R.id.show_history_info_date_desc);
		date = Util.getTodayDate();
		titleDate.setText(date);
		dateState = (ImageView) findViewById(R.id.show_history_info_date_state);
		timeTv = (MyTextView) findViewById(R.id.show_history_info_time);
		addressTv = (MyTextView) findViewById(R.id.show_history_info_desc);
		showProgressDialog();
		HistoryRequest();
		SeekBar seekbar = (SeekBar) findViewById(R.id.history_show_infos_seekbar);
		seekbar.setOnSeekBarChangeListener(this);
		seekBarDescTv = (TextView) findViewById(R.id.history_show_infos_seekbar_desc);
		seekBarLayout = (LinearLayout) findViewById(R.id.history_show_infos_seekbar_layout);
		seekBarLayout.setVisibility(View.GONE);
		drawables = new int[] { R.drawable.icon_map_taxi1,
				R.drawable.icon_map_taxi2, R.drawable.icon_map_taxi3,
				R.drawable.icon_map_taxi4, R.drawable.icon_map_taxi5,
				R.drawable.icon_map_taxi6, R.drawable.icon_map_taxi7,
				R.drawable.icon_map_taxi8 };
	}

	private void setUpMap(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.show_history_info_mapview);
		mapView.setVisibility(View.VISIBLE);
		mapView.onCreate(savedInstanceState);// 必须要写
		aMap = mapView.getMap();
		aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		aMap.setMyLocationRotateAngle(180);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		aMap.setOnMarkerClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button2:// 返回历史轨迹
			aMap.clear();
			if (polyline != null) {
				polyline.setPoints(new ArrayList<LatLng>());
			}
			clearPlayerData();
			if (!filterIsOpen) {
				if (list1 != null && list1.size() > 0) {
					isAll.setText("过滤模糊:开");
					filterIsOpen = true;
					doDrawJPoint(false);
				} else {
					Util.showToastBottom(ActivityShowHistoryInfo1.this,
							getString(R.string.no_baby_history_info));
				}
				break;
			}
			isAll.setText("过滤模糊:关");
			handler.sendEmptyMessage(0);

			break;
		case R.id.button1:// 返回历史轨迹
			if (!isPlayer && polyline != null
					&& polyline.getPoints().size() > 1) {
				isPlayer = !isPlayer;
				seekBarLayout.setVisibility(View.VISIBLE);
				player.setText("暂停");
				playBack();
			} else {
				isPlayer = !isPlayer;
				seekBarLayout.setVisibility(View.GONE);
				player.setText("回放");
			}
			break;
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

	/**
	 * 根据点获取图标转的角度
	 */
	private double getAngle(int startIndex) {
		if ((startIndex + 1) >= polyline.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = polyline.getPoints().get(startIndex);
		LatLng endPoint = polyline.getPoints().get(startIndex + 1);
		return getAngle(startPoint, endPoint);
	}

	/**
	 * 根据两点算取图标转的角度
	 */
	private double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}

	/**
	 * 根据点和斜率算取截距
	 */
	private double getInterception(double slope, LatLng point) {
		double interception = point.latitude - slope * point.longitude;
		return interception;
	}

	/**
	 * 算取斜率
	 */
	@SuppressWarnings("unused")
	private double getSlope(int startIndex) {
		if ((startIndex + 1) >= polyline.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = polyline.getPoints().get(startIndex);
		LatLng endPoint = polyline.getPoints().get(startIndex + 1);
		return getSlope(startPoint, endPoint);
	}

	/**
	 * 算斜率
	 */

	private double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}

	/**
	 * 计算x方向每次移动的距离
	 */
	private double getXMoveDistance(double slope) {
		if (slope == Double.MAX_VALUE) {
			return 0.0001;
		} else if (Math.abs(slope) < 0.0000001) {
			return 0.0001;
		}
		return Math.abs((0.0001 * slope) / Math.sqrt(1 + slope * slope));
	}

	private synchronized void playBack() {
		new Thread() {
			public void run() {
				synchronized (this) {
					try {
						if (mMoveMarker != null) {
							mMoveMarker.remove();
							mMoveMarker.destroy();
							mMoveMarker = null;
						}
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.setFlat(true);
						markerOptions.anchor(0.5f, 0.5f);
						markerOptions.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker));
						markerOptions
								.position(moveMarkerPosition == null ? polyline
										.getPoints().get(0)
										: moveMarkerPosition);
						mMoveMarker = aMap.addMarker(markerOptions);
						mMoveMarker.setRotateAngle((float) getAngle(0));
						aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
						for (int i = tempPosition; i < polyline.getPoints()
								.size() - 1; i++) {
							handler.sendEmptyMessage(2);
							Message msg = handler.obtainMessage(3);
							msg.arg1 = i;
							handler.sendMessage(msg);
							LatLng startPoint = polyline.getPoints().get(i);
							LatLng endPoint = polyline.getPoints().get(i + 1);
							aMap.moveCamera(CameraUpdateFactory
									.changeLatLng(moveMarkerPosition == null ? startPoint
											: moveMarkerPosition));
							mMoveMarker
									.setPosition(moveMarkerPosition == null ? startPoint
											: moveMarkerPosition);
							mMoveMarker.setRotateAngle((float) getAngle(
									startPoint, endPoint));
							double slope = getSlope(startPoint, endPoint);

							if (Math.abs(slope) < 0.000001) {
								// 向右为正方向
								boolean isReverse = (startPoint.longitude > endPoint.longitude);
								double xMoveDistance = isReverse ? getXMoveDistance(slope)
										: -1 * getXMoveDistance(slope);
								int m = 0;
								for (double j = startPoint.longitude; Math
										.abs(j - endPoint.longitude) < 0.000001; j = j
										- xMoveDistance) {
									m++;
									m = m % 50;
									LatLng latLng = new LatLng(
											startPoint.latitude, j);
									if (moveMarkerPosition != null) {
										double la1 = moveMarkerPosition.latitude
												- latLng.latitude;
										if (Math.abs(la1) >= 0.00000001) {
											continue;
										}
									}
									mMoveMarker.setPosition(latLng);
									if (m == 0) {
										aMap.moveCamera(CameraUpdateFactory
												.changeLatLng(latLng));
									}
									if (!isPlayer) {
										moveMarkerPosition = latLng;
										break;
									} else {
										moveMarkerPosition = null;
									}
									try {
										Thread.sleep(playerTime);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} else {
								// 是不是正向的标示（向上设为正向）
								boolean isReverse = (startPoint.latitude > endPoint.latitude);
								double intercept = getInterception(slope,
										startPoint);
								double xMoveDistance = isReverse ? getXMoveDistance(slope)
										: -1 * getXMoveDistance(slope);

								int m = 0;
								for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse); j = j
										- xMoveDistance) {
									m++;
									m = m % 50;
									LatLng latLng = null;
									if (slope != Double.MAX_VALUE) {
										latLng = new LatLng(j, (j - intercept)
												/ slope);
									} else {
										latLng = new LatLng(j,
												startPoint.longitude);
									}

									if (moveMarkerPosition != null) {
										double la1 = moveMarkerPosition.latitude
												- latLng.latitude;

										if (Math.abs(la1) >= 0.00000001) {
											continue;
										}
									}
									mMoveMarker.setPosition(latLng);
									if (m == 0) {
										aMap.moveCamera(CameraUpdateFactory
												.changeLatLng(latLng));
									}
									if (!isPlayer) {
										moveMarkerPosition = latLng;
										break;
									} else {
										moveMarkerPosition = null;
									}
									try {
										Thread.sleep(playerTime);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							tempPosition = i;
							if (!isPlayer) {
								break;
							}
						}
						if (tempPosition == polyline.getPoints().size() - 2
								&& moveMarkerPosition == null) {
							handler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						Log.e("playBack", "播放轨迹失败!");
					}
				}
			}
		}.start();
	}

	private void showSelectDatePopupWindow() {
		if (date == null || "".equals(date)) {
			date = Util.getTodayDate();
		}
		if (isSelectingDate) {
			dateState.setImageResource(R.drawable.arr_down);
			isSelectingDate = false;
		} else {
			dateState.setImageResource(R.drawable.arr_up);
			isSelectingDate = true;
		}
		new PopupWindows(ActivityShowHistoryInfo1.this, selectDate, date,
				ActivityShowHistoryInfo1.this)
				.setOnDismissListener(new OnDismissListener() {
					public void onDismiss() {
						dateState.setImageResource(R.drawable.arr_down);
						isSelectingDate = false;
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (popWindow != null && popWindow.isShowing()) {
				popWindow.dismiss();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 地图打点
	 */
	private synchronized void doDrawJPoint(final boolean isAll) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<HistoryEntity> tempList = new ArrayList<HistoryEntity>();
				if (isAll) {
					tempList = list;
				} else {
					tempList = list1;
				}
				PolylineOptions options = new PolylineOptions();
				options.width(25);
				options.setCustomTexture(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_line));
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						tempList.get(0).getLa(), tempList.get(0).getLo())));
				for (int i = 0; i < tempList.size(); i++) {

					LatLng latLng = new LatLng(tempList.get(i).getLa(),
							tempList.get(i).getLo());
					options.add(latLng);
					aMap.addMarker(new MarkerOptions()
							.position(latLng)
							.title(getString(R.string.duration_of_stay)
									+ tempList.get(i).getContinued())
							.icon(BitmapDescriptorFactory
									.fromResource(drawables[new Random()
											.nextInt(7) + 1])).draggable(true));
				}
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						tempList.get(tempList.size() - 1).getLa(), tempList
								.get(tempList.size() - 1).getLo())));
				polyline = aMap.addPolyline(options);
			}
		}).start();
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

	private synchronized void HistoryRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (this) {
					String jsonStr = getRequestJson();
					boolean flag = SocketUtil.connectService(jsonStr);
					if (flag) {
						list = ResolveServiceData.historyroute();
						getList1();
						handler.sendMessage(handler.obtainMessage(0));
					} else {
						list = new ArrayList<HistoryEntity>();
						list1 = new ArrayList<HistoryEntity>();
						dismissDialog();
						Looper.prepare();
						Util.showToastBottom(ActivityShowHistoryInfo1.this,
								SocketUtil
										.isFail(ActivityShowHistoryInfo1.this));
						Looper.loop();
					}
				}
			}
		}).start();
	}

	private void getList1() {
		list1 = new ArrayList<HistoryEntity>();
		if (list == null || list.size() <= 0) {
			return;
		}
		for (HistoryEntity info : list) {
			if (info.getLocation_type() != 2) {
				list1.add(info);
			}
		}
	}

	// 清空
	public void doClear() {
		aMap.clear();
		mapView.invalidate();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		tempMarker = marker;
		marker.showInfoWindow();
		double lo1 = marker.getPosition().longitude;
		double la1 = marker.getPosition().latitude;
		for (int i = 0; i < list.size(); i++) {
			double lo2 = list.get(i).getLo();
			double la2 = list.get(i).getLa();
			if (Math.abs(lo2 - lo1) < 0.00001 && Math.abs(la2 - la1) < 0.00001) {
				Log.e("HistoryRequest", "位置：" + i + ",地址:"
						+ list.get(i).getAddress());
				addressTv.setText(list.get(i).getAddress());
				if (list.get(i).getLocation_type() == 0) {
					timeTv.setText(list.get(i).getGpstime() + " "
							+ getString(R.string.gps_location));
				} else if (list.get(i).getLocation_type() == 1) {
					timeTv.setText(list.get(i).getGpstime() + " "
							+ getString(R.string.network_location));
				} else {
					timeTv.setText(list.get(i).getGpstime() + " "
							+ getString(R.string.lbs_location));
				}
				break;
			}
		}
		return false;
	}

	@Override
	public void getDataHositoryInfos(String date) {
		isAll.setText("过滤模糊:关");
		filterIsOpen = false;
		showProgressDialog();
		clearPlayerData();
		this.date = date;
		titleDate.setText(date);
		aMap.clear();
		if (polyline != null) {
			polyline.setPoints(new ArrayList<LatLng>());
		}
		HistoryRequest();
	}

	private void clearPlayerData() {
		isPlayer = false;
		seekBarLayout.setVisibility(View.GONE);
		player.setText("回放");
		addressTv.setText("");
		timeTv.setText("");
		moveMarkerPosition = null;
		if (mMoveMarker != null) {
			mMoveMarker.remove();
			mMoveMarker.destroy();
			mMoveMarker = null;
		}
		tempPosition = 0;
	}

	// ************************progress
	private void dismissDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new Dialog(ActivityShowHistoryInfo1.this,
					R.style.dialog);
			progressDialog.setContentView(R.layout.view_progress_dialog);
		}
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	// ***********************seekbar接口****************************
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		String str = "";
		if (progress == 5) {
			playerTime = 80;
			str = "1";
		} else if (progress > 5) {
			playerTime = 80 / ((progress - 5) * 2);
			str = "" + (progress - 5) * 2;
		} else if (progress == 0) {
			playerTime = 800;
			str = "0.1";
		} else {
			playerTime = (int) (80 / (0.2 * progress));
			DecimalFormat df = new DecimalFormat("######0.0");
			str = "" + df.format(0.2 * progress);
		}
		Log.e("playerTime", "进度:" + progress + "，时间:" + playerTime);
		seekBarDescTv.setText("播放速度 X " + str);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// 0 1 2 3 4 5 6 7 8 9 10
		// 0.1 0.2 0.4 0.6 0.8 1 2 4 6 8 10
		// 800 400 200 133 100 80 40 20 13.3 10 8
	}

}
