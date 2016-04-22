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
 * ����ҳ�棨����·���滮����������
 * */
public class ActivityNaviRoute1 extends Activity implements OnClickListener,
		AMapNaviListener, AMapNaviViewListener {
	private AMapNaviView mAMapNaviView;
	private AMapNavi mAMapNavi;

	private boolean mDayNightFlag = false;// Ĭ��Ϊ����ģʽ
	private boolean mDeviationFlag = true;// Ĭ�Ͻ���ƫ������
	private boolean mJamFlag = false;// Ĭ�Ͻ���ӵ������
	private boolean mTrafficFlag = true;// Ĭ�Ͻ��н�ͨ����
	private boolean mCameraFlag = true;// Ĭ�Ͻ�������ͷ����
	private boolean mScreenFlag = true;// Ĭ������Ļ����
	private TTSController mTtsManager;
	/**
	 * ����������
	 */
	private TextView title;
	/**
	 * ����
	 */
	// ����յ�����
	private NaviLatLng mNaviStart;
	private NaviLatLng mNaviEnd;
	// ����յ��б�
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	// �滮��·
	/**
	 * ���ڱ���ǲ��л��Ǽݳ�
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
		// ������ֹͣ�㵱ǰ��˵����仰��һ�ᵽ�µ�·�ڻ��ǻ���˵��
		mTtsManager.stopSpeaking();
		// ֹͣ����֮�󣬻ᴥ���ײ�stop��Ȼ��Ͳ������лص��ˣ�����Ѷ�ɵ�ǰ����û��˵��İ�仰���ǻ�˵��
		// mAMapNavi.stopNavi();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAMapNaviView.onDestroy();
		// since 1.6.0
		// ������naviview destroy��ʱ���Զ�ִ��AMapNavi.stopNavi();
		// ������ִ��
		mAMapNavi.stopNavi();
		mAMapNavi.destroy();
		mTtsManager.destroy();
	}

	// ��ʼ��View
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
		viewOptions.setSettingMenuEnabled(false);// ���õ���setting����
		viewOptions.setNaviNight(mDayNightFlag);// ���õ����Ƿ�Ϊ��ҹģʽ
		viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// ���õ�ƫ���Ƿ�����
		viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// ���ý�ͨӵ���Ƿ�����
		viewOptions.setTrafficInfoUpdateEnabled(mTrafficFlag);// �����Ƿ����·��
		viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// ��������ͷ����
		viewOptions.setScreenAlwaysBright(mScreenFlag);// ������Ļ�������
		viewOptions.setNaviViewTopic(AMapNaviViewOptions.WHITE_COLOR_TOPIC);// ���õ�������������ʽ
		mAMapNaviView.setViewOptions(viewOptions);
	}

	// -------------------------Button����¼��ͷ��ؼ������¼�---------------------------------
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

	// ----------------------------------�����ӿ�
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
