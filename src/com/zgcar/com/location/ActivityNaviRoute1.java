package com.zgcar.com.location;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.zgcar.com.R;
import com.zgcar.com.location.util.TTSController;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.util.SetTitleBackground;

/**
 * 导航页面（包括路径规划，导航。）
 * */
public class ActivityNaviRoute1 extends Activity implements OnClickListener,
		AMapNaviListener, AMapNaviViewListener {
	private AMapNaviView mAMapNaviView;
	private AMapNavi mAMapNavi;

	private boolean mDayNightFlag = false;// 默认为白天模式
	private boolean mDeviationFlag = true;// 默认进行偏航重算
	private boolean mJamFlag = false;// 默认进行拥堵重算
	private boolean mTrafficFlag = true;// 默认进行交通播报
	private boolean mCameraFlag = true;// 默认进行摄像头播报
	private boolean mScreenFlag = true;// 默认是屏幕常亮
	private TTSController mTtsManager;
	/**
	 * 标题栏名称
	 */
	private TextView title;
	/**
	 * 返回
	 */
	// 起点终点坐标
	private NaviLatLng mNaviStart;
	private NaviLatLng mNaviEnd;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	// 规划线路
	/**
	 * 用于标记是步行还是驾车
	 */
	private int type;
	private MyApplication app;

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		setContentView(R.layout.activity_nav_simple_route);
		initView(savedInstanceState);
		init();
		initData();
	}

	private void init() {
		mTtsManager = TTSController.getInstance(getApplicationContext());
		mTtsManager.init();
		mTtsManager.startSpeaking();
		mAMapNavi = AMapNavi.getInstance(getApplicationContext());
		mAMapNavi.addAMapNaviListener(this);
		mAMapNavi.addAMapNaviListener(mTtsManager);
		mAMapNavi.setEmulatorNaviSpeed(150);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mAMapNaviView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAMapNaviView.onPause();
		// 仅仅是停止你当前在说的这句话，一会到新的路口还是会再说的
		mTtsManager.stopSpeaking();
		// 停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
		// mAMapNavi.stopNavi();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAMapNaviView.onDestroy();
		// since 1.6.0
		// 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();
		// 请自行执行
		mAMapNavi.stopNavi();
		mAMapNavi.destroy();
		mTtsManager.destroy();
	}

	// 初始化View
	private void initView(Bundle savedInstanceState) {
		title = (TextView) findViewById(R.id.nav_simple_route_title);
		ImageButton back = (ImageButton) findViewById(R.id.nav_simple_route_back);
		back.setOnClickListener(this);
		mAMapNaviView = (AMapNaviView) findViewById(R.id.simplenavimap1);
		mAMapNaviView.setVisibility(View.VISIBLE);
		mAMapNaviView.onCreate(savedInstanceState);
		mAMapNaviView.setAMapNaviViewListener(this);
		setAmapNaviViewOptions();
	}

	private void initData() {
		app = (MyApplication) getApplication();
		type = getIntent().getIntExtra("type", 2);
		if (type == 1) {
			title.setText(R.string.navi_empty_foot);
		} else if (type == 2) {
			title.setText(R.string.navi_empty_car);
		}
		mNaviStart = new NaviLatLng(app.getTempMyLocation().getLatitude(), app
				.getTempMyLocation().getLongitude());
		mNaviEnd = new NaviLatLng(app.getTempWatchLocation().getLa(), app
				.getTempWatchLocation().getLo());
		mStartPoints.add(mNaviStart);
		mEndPoints.add(mNaviEnd);
	}

	private void setAmapNaviViewOptions() {
		if (mAMapNaviView == null) {
			return;
		}
		AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
		viewOptions.setSettingMenuEnabled(false);// 设置导航setting可用
		viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
		viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
		viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
		viewOptions.setTrafficInfoUpdateEnabled(mTrafficFlag);// 设置是否更新路况
		viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// 设置摄像头播报
		viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
		viewOptions.setNaviViewTopic(AMapNaviViewOptions.WHITE_COLOR_TOPIC);// 设置导航界面主题样式
		mAMapNaviView.setViewOptions(viewOptions);
	}

	// -------------------------Button点击事件和返回键监听事件---------------------------------
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nav_simple_route_back:
			finish();
			break;
		}

	}

	@Override
	public void onInitNaviFailure() {
		Log.e("ActivityNaviRoute1", "onInitNaviFailure");
		Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInitNaviSuccess() {
		Log.e("ActivityNaviRoute1", "onInitNaviSuccess");
		if (type == 1) {
			mAMapNavi
					.calculateWalkRoute(mStartPoints.get(0), mEndPoints.get(0));
		} else if (type == 2) {
			List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();
			mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints,
					mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);
		}

	}

	// ----------------------------------导航接口
	@Override
	@Deprecated
	public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {
		Log.e("ActivityNaviRoute1", "OnUpdateTrafficFacility");
	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {
		Log.e("ActivityNaviRoute1", "OnUpdateTrafficFacility");
	}

	@Override
	public void onCalculateRouteSuccess() {
		Log.e("ActivityNaviRoute1", "onCalculateRouteSuccess");
		mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
	}

	@Override
	public void hideCross() {
		Log.e("ActivityNaviRoute1", "hideCross");
		// TODO Auto-generated method stub

	}

	@Override
	public void hideLaneInfo() {
		Log.e("ActivityNaviRoute1", "hideLaneInfo");
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyParallelRoad(int arg0) {
		Log.e("ActivityNaviRoute1", "notifyParallelRoad");
		// TODO Auto-generated method stub

	}

	@Override
	public void onArriveDestination() {
		Log.e("ActivityNaviRoute1", "onArriveDestination");
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		Log.e("ActivityNaviRoute1", "onArrivedWayPoint");
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateMultipleRoutesSuccess(int[] arg0) {
		Log.e("ActivityNaviRoute1", "onCalculateMultipleRoutesSuccess");
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		Log.e("ActivityNaviRoute1", "onCalculateRouteFailure");
		if (type == 1) {
			mAMapNavi
					.calculateWalkRoute(mStartPoints.get(0), mEndPoints.get(0));
		} else if (type == 2) {
			List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();
			mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints,
					mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);
		}
		Toast.makeText(this, "init navi Failed !", Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onEndEmulatorNavi");
	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onGetNavigationText");
	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onGpsOpenStatus");
	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onLocationChange");
	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNaviInfoUpdate");
	}

	@Override
	@Deprecated
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNaviInfoUpdated");
	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onReCalculateRouteForTrafficJam");
	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onReCalculateRouteForYaw");
	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onStartNavi");
	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onTrafficStatusUpdate");
	}

	@Override
	public void showCross(AMapNaviCross arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "showCross");
	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "showLaneInfo");
	}

	// ----------------------------
	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onLockMap");
	}

	@Override
	public boolean onNaviBackClick() {
		Log.e("onNaviBackClick", "onNaviBackClick");
		return false;
	}

	@Override
	public void onNaviCancel() {
		Log.e("onNaviCancel", "onNaviCancel");
		finish();
	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNaviMapMode");
	}

	@Override
	public void onNaviSetting() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNaviSetting");
	}

	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNaviTurnClick");
	}

	@Override
	public void onNaviViewLoaded() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNaviViewLoaded");
	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onNextRoadClick");
	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub
		Log.e("ActivityNaviRoute1", "onScanViewButtonClick");
	}

}
