package com.zgcar.com.history;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.zgcar.com.R;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.NotifyMessageEntity;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivitySosMeage1 extends Activity implements OnClickListener {

	private AMap aMap;
	private MapView mapView;
	private TextView titleTV, suggestTv, timeTv, addressTv;
	private MyApplication app;
	private ImageView people;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sos_message);
		mapView = (MapView) findViewById(R.id.sos_message_map);
		mapView.setVisibility(View.VISIBLE);
		mapView.onCreate(savedInstanceState);
		app = (MyApplication) getApplication();
		titleTV = (TextView) findViewById(R.id.sos_message_title);
		suggestTv = (TextView) findViewById(R.id.sos_message_suggest);
		timeTv = (TextView) findViewById(R.id.sos_message_time);
		addressTv = (TextView) findViewById(R.id.sos_message_address);
		ImageButton back = (ImageButton) findViewById(R.id.sos_message_back);
		back.setOnClickListener(this);
		Log.e("SosMeageActivity", "SosMeageActivity");
		initView();
	}

	private void initView() {
		initAmap();
		Intent intent = getIntent();
		NotifyMessageEntity info = (NotifyMessageEntity) intent
				.getSerializableExtra("NotifyMessageEntity");
		titleTV.setText(info.getLitle());
		suggestTv.setText(info.getMsg());
		timeTv.setText(info.getTime());
		addressTv.setText(info.getGeo());
		LatLng sosLatLng = new LatLng(info.getLa(), info.getLo());
		Log.e("info", info.toString());
		getlalo(sosLatLng);
	}

	/**
	 * 初始化AMap对象
	 */
	private void initAmap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		UiSettings uiSettings = aMap.getUiSettings();
		uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	public LatLng getlalo(LatLng lp) {

		aMap.moveCamera(CameraUpdateFactory.changeLatLng(lp));
		aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
		// 设置点坐标图像(自定义View)
		View view = View.inflate(this, R.layout.point_location_pic, null);
		people = (ImageView) view.findViewById(R.id.people);

		int width = (int) Util.dip2px(ActivitySosMeage1.this, 50);
		Bitmap bitmap = Util.decodeSampledBitmapFromResource(ListInfosEntity
				.getPathList().get(app.getPosition()), width, width);
		if (bitmap != null) {
			people.setImageBitmap(bitmap);
		}

		BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
		aMap.addMarker(new MarkerOptions().position(lp).title("")
				.icon(markerIcon).draggable(true));

		// marker.showInfoWindow();
		return lp;

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.sos_message_back:
			finish();
			break;

		default:
			break;
		}

	}
}