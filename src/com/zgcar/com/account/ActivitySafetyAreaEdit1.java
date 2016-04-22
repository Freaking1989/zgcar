package com.zgcar.com.account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.Entity.SafetyAreaEntity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 修改已保存得安全区域
 * 
 */
public class ActivitySafetyAreaEdit1 extends Activity implements
		OnClickListener, OnMapClickListener, OnSeekBarChangeListener,
		OnGeocodeSearchListener {
	private static final int Circle_modify_MAX = 700;
	private MapView mMapView;
	private AMap mAMap;
	private Button save;
	private ImageButton search;
	private TextView titleName, addressDesc;
	private EditText nameEt;
	private Dialog dialog;
	private LatLng tempLatLng;
	private Circle mCircle;
	private int radius;
	private View view4;
	private BitmapDescriptor markerIcon;
	private ProgressDialog progDialog;
	private GeocodeSearch geocoderSearch;
	private SeekBar seekBar;
	private TextView seekBarDesc;
	private SafetyAreaEntity mSafetyEntity;
	private boolean isCanEdit;
	private Bitmap bitmap;
	private CircleImageView iconImage;
	private boolean isToast;
	private boolean isAddNew;
	private Button school, teacherHome, company, grandpaHome, cramSchool,
			family;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivitySafetyAreaEdit1.this,
				R.color.color_4);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety_area_edit);
		setUpMap(savedInstanceState);
		init();
	}

	private void init() {
		radius = 300;
		isToast = false;
		save = (Button) findViewById(R.id.safety_edit_area_save);
		save.setOnClickListener(this);
		search = (ImageButton) findViewById(R.id.safety_edit_area_search);
		search.setOnClickListener(this);
		ImageButton back = (ImageButton) findViewById(R.id.safety_edit_area_back);
		back.setOnClickListener(this);
		addressDesc = (TextView) findViewById(R.id.safety_edit_area_address_desc);
		seekBarDesc = (TextView) findViewById(R.id.safety_edit_area_seekbar_desc);
		titleName = (TextView) findViewById(R.id.safety_edit_area_title_name);
		titleName.setOnClickListener(this);
		mSafetyEntity = (SafetyAreaEntity) getIntent().getSerializableExtra(
				"ZoneSafetyEntity");
		isAddNew = getIntent().getBooleanExtra("isAddNew", true);
		// 自定义点坐标图像
		view4 = View.inflate(ActivitySafetyAreaEdit1.this,
				R.layout.point_location_pic, null);
		iconImage = (CircleImageView) view4.findViewById(R.id.people);
		seekBar = (SeekBar) findViewById(R.id.safety_edit_area_seekbar);
		seekBar.setMax(Circle_modify_MAX);
		seekBar.setOnSeekBarChangeListener(this);
		initView();
	}

	private void setUpMap(Bundle savedInstanceState) {
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		mMapView = (MapView) findViewById(R.id.mapveiw_modify);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();
		mAMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		mAMap.setOnMapClickListener(this);
		mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
		// 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
		mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	private void initView() {
		int width = (int) Util.dip2px(ActivitySafetyAreaEdit1.this, 50);
		bitmap = Util.decodeSampledBitmapFromResource(
				ListInfosEntity.getPathList().get(
						((MyApplication) getApplication()).getPosition()),
				width, width);
		if (bitmap != null) {
			iconImage.setImageBitmap(bitmap);
		} else {
			iconImage.setImageResource(R.drawable.icon);
		}
		markerIcon = BitmapDescriptorFactory.fromView(view4);
		titleName.setText(mSafetyEntity.getName());
		radius = mSafetyEntity.getRad();
		seekBarDesc.setText(radius + getString(R.string.stroke_circle));
		seekBar.setProgress((radius - 300));
		if (isAddNew) {
			isCanEdit = true;
			MyApplication app = (MyApplication) getApplication();
			if (app.getTempWatchLocation() != null) {
				double la = app.getTempWatchLocation().getLa();
				double lo = app.getTempWatchLocation().getLo();
				tempLatLng = new LatLng(la, lo);
				LatLonPoint latLonPoint = new LatLonPoint(la, lo);
				getAddress(latLonPoint);
			}
		} else {
			save.setText(getString(R.string.press_edit));
			isCanEdit = false;
			double la = mSafetyEntity.getLa();
			double lo = mSafetyEntity.getLo();
			tempLatLng = new LatLng(la, lo);
			LatLonPoint latLonPoint = new LatLonPoint(la, lo);
			getAddress(latLonPoint);
		}
		drawObject(tempLatLng);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.safety_edit_area_save:// 编辑安全区域
			if (!isCanEdit) {
				isCanEdit = true;
				save.setText(getString(R.string.save));
				seekBar.setEnabled(true);
				search.setEnabled(true);
				titleName.setEnabled(true);
				break;
			}
			if ("".equals(titleName.getText().toString().trim())) {
				Util.showToastBottom(ActivitySafetyAreaEdit1.this,
						getString(R.string.title_can_not_null));
			} else {
				if (dialog == null) {
					dialog = new Dialog(ActivitySafetyAreaEdit1.this,
							R.style.dialog);
				}
				dialog.setCancelable(false);
				dialog.setContentView(R.layout.view_progress_dialog);
				dialog.show();
				editSafetyAreaRequest();// 修改围栏命令
			}
			break;
		case R.id.safety_edit_area_search:// 进入搜索页面
			if (!isCanEdit) {
				if (!isToast) {
					Util.showToastBottom(ActivitySafetyAreaEdit1.this,
							getString(R.string.press_to_edit_desc));
					isToast = true;
				}
				break;
			}
			startActivityForResult(new Intent(ActivitySafetyAreaEdit1.this,
					ActivitySafetySearch1.class), 1);
			break;

		case R.id.safety_edit_area_back:// 返回安全区域
			finish();
			break;
		case R.id.safety_edit_area_title_name:// 添加安全区域名称（半透明）
			if (!isCanEdit) {
				if (!isToast) {
					Util.showToastBottom(ActivitySafetyAreaEdit1.this,
							getString(R.string.press_to_edit_desc));
					isToast = true;
				}
				break;
			}
			showEditTitleDialog();
			break;
		case R.id.edit_safety_name_sure:
			titleName.setText(nameEt.getText().toString().trim());
			dialog.dismiss();
			break;
		case R.id.edit_safety_name_school:
			nameEt.setText(school.getText().toString());
			break;
		case R.id.edit_safety_name_teacher_home:
			nameEt.setText(teacherHome.getText().toString());
			break;
		case R.id.edit_safety_name_company:
			nameEt.setText(company.getText().toString());
			break;
		case R.id.edit_safety_name_grandpa_home:
			nameEt.setText(grandpaHome.getText().toString());
			break;
		case R.id.edit_safety_name_cram_school:
			nameEt.setText(cramSchool.getText().toString());
			break;
		case R.id.edit_safety_name_family:
			nameEt.setText(family.getText().toString());
			break;
		case R.id.edit_safety_name_cancel:
			dialog.dismiss();
			break;
		default:
			break;
		}

	}

	/**
	 * 初始化弹出视图
	 */
	private void showEditTitleDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivitySafetyAreaEdit1.this, R.style.dialog);
		}
		if (school == null) {
			View view = View.inflate(ActivitySafetyAreaEdit1.this,
					R.layout.view_edit_safety_area_name, null);
			school = (Button) view.findViewById(R.id.edit_safety_name_school);
			school.setOnClickListener(this);
			teacherHome = (Button) view
					.findViewById(R.id.edit_safety_name_teacher_home);
			teacherHome.setOnClickListener(this);
			company = (Button) view.findViewById(R.id.edit_safety_name_company);
			company.setOnClickListener(this);
			grandpaHome = (Button) view
					.findViewById(R.id.edit_safety_name_grandpa_home);
			grandpaHome.setOnClickListener(this);
			cramSchool = (Button) view
					.findViewById(R.id.edit_safety_name_cram_school);
			cramSchool.setOnClickListener(this);
			family = (Button) view.findViewById(R.id.edit_safety_name_family);
			family.setOnClickListener(this);
			nameEt = (EditText) view
					.findViewById(R.id.edit_safety_name_edittext);
			TextView cancel = (TextView) view
					.findViewById(R.id.edit_safety_name_cancel);
			cancel.setOnClickListener(this);
			Button sure = (Button) view
					.findViewById(R.id.edit_safety_name_sure);
			sure.setOnClickListener(this);
			dialog.setContentView(view);
		}
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 0) {
			addressDesc.setText(data.getExtras().getString("address"));
			tempLatLng = new LatLng(data.getExtras().getDouble("la"), data
					.getExtras().getDouble("lo"));
			mAMap.clear();
			drawObject(tempLatLng);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * {"cmd":"00007","data":[{"imei":"861400000000088","name":"test","lo":
	 * 114.078083,"la":22.623419,"rad":300,"alarmtype":0}]}
	 * 
	 * {"cmd":"00008","data":[{"id":08,"name":"test","lo":114.078083,"la":
	 * 22.623419,"rad":300,"alarmtype":0}]}
	 */
	public String getRequestJson(String cmd) {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", cmd);//
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("imei", ((MyApplication) getApplication()).getImei());// arr[5]
			b2.put("id", mSafetyEntity.getId());// arr[5]
			b2.put("name", titleName.getText().toString().trim());
			b2.put("lo", tempLatLng.longitude);
			b2.put("la", tempLatLng.latitude);
			b2.put("rad", radius);
			b2.put("alarmtype", 0);
			b2.put("map_type", FinalVariableLibrary.MAP_TYPE);
			array.put(b2);
			b.put("data", array);
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 发送的数据：{"cmd":"00007","data": [{ "id":0, "map_type":"2", }], }
	 * 
	 * {"cmd":"00007", "data": [{ }]}
	 */
	public void editSafetyAreaRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = "";
				if (isAddNew) {
					data = getRequestJson(FinalVariableLibrary.ADD_SAFETY_AREA);
				} else {
					data = getRequestJson(FinalVariableLibrary.EDIT_SAFETY_AREA);
				}
				boolean flag = SocketUtil.connectService(data);
				if (flag) {
					dialog.dismiss();
					finish();
				} else {
					dialog.dismiss();
					Looper.prepare();
					Util.showToastBottom(ActivitySafetyAreaEdit1.this,
							SocketUtil.isFail(ActivitySafetyAreaEdit1.this));
					Looper.loop();
				}
			}
		}).start();
	}

	// 点击获取经纬度 设置半径等
	@Override
	public void onMapClick(LatLng latLng) {
		if (!isCanEdit) {
			if (!isToast) {
				Util.showToastBottom(ActivitySafetyAreaEdit1.this,
						getString(R.string.press_to_edit_desc));
				isToast = true;
			}
			return;
		}
		showDialog();

		tempLatLng = latLng;//
		mAMap.clear();
		drawObject(tempLatLng);
		getAddress(new LatLonPoint(latLng.latitude, latLng.longitude));
	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (progDialog != null) {
			progDialog.dismiss();
		}
		if (rCode == 1000) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getFormatAddress() + "";
				addressDesc.setText(addressName);
			} else {
				Util.showToastBottom(ActivitySafetyAreaEdit1.this,
						getString(R.string.no_result));
			}
		} else if (rCode == 27) {
			Util.showToastBottom(ActivitySafetyAreaEdit1.this,
					getString(R.string.error_network));
		} else if (rCode == 32) {
			Util.showToastBottom(ActivitySafetyAreaEdit1.this,
					getString(R.string.error_key));
		} else {
			Util.showToastBottom(ActivitySafetyAreaEdit1.this,
					getString(R.string.error_other) + rCode);
		}

	}

	/**
	 * 显示进度条
	 */
	public void showDialog() {
		if (progDialog == null) {
			progDialog = new ProgressDialog(this);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage(getString(R.string.is_geting_address));
		}
		progDialog.show();
	}

	private void getAddress(LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数的LatLng,第二个范围 多少米
									// ，第三个表示火星坐标系还是GSP原声坐标
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}

	/**
	 * seekBar 滑动获取事件
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!isCanEdit) {
			if (!isToast) {
				Util.showToastBottom(ActivitySafetyAreaEdit1.this,
						getString(R.string.press_to_edit_desc));
				isToast = true;
			}
			seekBar.setProgress((radius - 300));
			return;
		}
		progress += 300;
		if (progress > 0 && progress <= 350) {
			progress = 300;
		} else if (progress > 350 && progress <= 450) {
			progress = 400;
		} else if (progress > 450 && progress <= 550) {
			progress = 500;
		} else if (progress > 550 && progress <= 650) {
			progress = 600;
		} else if (progress > 650 && progress <= 750) {
			progress = 700;
		} else if (progress > 750 && progress <= 850) {
			progress = 800;
		} else if (progress > 850 && progress <= 950) {
			progress = 900;
		} else if (progress > 950 && progress <= 1050) {
			progress = 1000;
		}
		if (seekBar == this.seekBar) {// 1000
			mCircle.setRadius(progress);
			seekBarDesc.setText(progress + getString(R.string.stroke_circle));
			radius = progress;
		}
	}

	/**
	 * 画图
	 * 
	 * @param pLatLng
	 * @param pTitle
	 * @param pRadius
	 */
	private void drawObject(LatLng pLatLng) {
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(pLatLng).radius(radius)
				.fillColor(Color.argb(100, 191, 255, 255))
				.strokeColor(Color.BLUE).strokeWidth(1);
		mCircle = mAMap.addCircle(circleOptions);
		mAMap.addMarker(new MarkerOptions().position(pLatLng).title("")
				.icon(markerIcon).draggable(true));
		mAMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(pLatLng, 15, 0, 30)),
				500, null);
	}

}
