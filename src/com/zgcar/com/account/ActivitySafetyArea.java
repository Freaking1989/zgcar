package com.zgcar.com.account;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.Entity.SafetyAreaEntity;
import com.zgcar.com.location.adapter.AdapterSafetyArea;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 安全区域
 * 
 */
public class ActivitySafetyArea extends Activity implements OnClickListener {
	private ListView lisview;
	private AdapterSafetyArea adapter;
	private List<SafetyAreaEntity> list;
	private MyApplication app;
	private int romveoposition;
	private int id;
	private String name;
	private boolean isDelete;
	private Dialog dialog;

	// 操作状态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivitySafetyArea.this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("onResume", "onResume");
		dialog.show();
		getSafetyAreaInfoRequest();
	}

	private void init() {
		app = (MyApplication) getApplication();
		ImageButton back = (ImageButton) findViewById(R.id.safety_area_back);
		back.setOnClickListener(this);
		ImageButton addNewSafety = (ImageButton) findViewById(R.id.safety_area_add_new_safety);
		addNewSafety.setOnClickListener(this);
		lisview = (ListView) findViewById(R.id.safety_area_listview);
		dialog = new Dialog(ActivitySafetyArea.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
	}

	private void setAdapter() {
		adapter = new AdapterSafetyArea(ActivitySafetyArea.this, list);
		isDelete = false;
		lisview.setAdapter(adapter);
		lisview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!isDelete) {
					isDelete = false;
					Intent in = new Intent();
					String country = getResources().getConfiguration().locale
							.getCountry();
					if (country.equals("CN") || country.equals("TW")) {
						in.setClass(ActivitySafetyArea.this,
								ActivitySafetyAreaEdit1.class);
					} else {
						in.setClass(ActivitySafetyArea.this,
								ActivitySafetyAreaEdit2.class);
					}
					in.putExtra("isAddNew", false);
					in.putExtra("ZoneSafetyEntity", list.get(position));
					startActivity(in);
				}
			}
		});

		lisview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				isDelete = true;
				Log.e("info1111", list.get(position).getId() + "");
				id = list.get(position).getId();
				name = list.get(position).getName();
				romveoposition = position;
				dialog();
				return false;
			}
		});
	}

	private void dialog() {
		final AlertDialog alert = new AlertDialog.Builder(
				ActivitySafetyArea.this).create();
		View veiwView = View.inflate(ActivitySafetyArea.this,
				R.layout.view_safety_area_delete, null);
		Button safety_dialogdelete_bt = (Button) veiwView
				.findViewById(R.id.safety_dialogdelete_bt);
		Button safety_dialog_bt = (Button) veiwView
				.findViewById(R.id.safety_dialog_bt);
		alert.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				isDelete = false;
			}
		});
		alert.setView(veiwView);
		// 确定
		safety_dialog_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				delFenceRequest();
				alert.dismiss();
			}
		});
		// 退出
		safety_dialogdelete_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});
		alert.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.safety_area_back:// 返回定位页面
			finish();
			break;
		case R.id.safety_area_add_new_safety:// 添加电子围栏围栏
			SafetyAreaEntity info = new SafetyAreaEntity();
			info.setName("");
			info.setRad(300);
			info.setId(0);
			Intent in = new Intent();
			String country = getResources().getConfiguration().locale
					.getCountry();
			if (country.equals("CN") || country.equals("TW")) {
				in.setClass(ActivitySafetyArea.this,
						ActivitySafetyAreaEdit1.class);
			} else {
				in.setClass(ActivitySafetyArea.this,
						ActivitySafetyAreaEdit2.class);
			}
			in.putExtra("ZoneSafetyEntity", info);
			in.putExtra("isAddNew", true);
			startActivity(in);
			break;
		default:
			break;
		}

	}

	/**
	 * 获取安全区域列表
	 */
	private void getSafetyAreaInfoRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = safetyAreaRequestJson(
						FinalVariableLibrary.GET_SAFETY_AREA_CMD, 0, "");
				Log.e("getSafetyAreaInfoRequest", data);
				boolean flag = SocketUtil.connectService(data);
				if (flag) {
					list = ResolveServiceData.zoneFence();
					handler.sendMessage(handler.obtainMessage(0));
				} else {
					dialog.dismiss();
					Looper.prepare();
					Util.showToastBottom(ActivitySafetyArea.this,
							SocketUtil.isFail(ActivitySafetyArea.this));
					Looper.loop();
				}
			}
		}).start();
	}

	/**
	 * 安全区域构造请求输据，获取安全区域信息，以及删除安全区域信息
	 * 
	 * {"cmd":"00009","data":[{"id":10,"name": "test","imei":"55555555555"}]}
	 */
	private String safetyAreaRequestJson(String cmd, int id, String name) {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("id", id);
			b2.put("name", name);
			b2.put("imei", app.getImei());
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
	 * 删除电子围栏
	 */
	private void delFenceRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = safetyAreaRequestJson(
						FinalVariableLibrary.DELETE_SAFETY_AREA_CMD, id, name);
				boolean flag = SocketUtil.connectService(data);
				if (flag) {
					handler.sendMessage(handler.obtainMessage(1));
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivitySafetyArea.this,
							SocketUtil.isFail(ActivitySafetyArea.this));
					Looper.loop();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				setAdapter();
				dialog.dismiss();
				break;
			case 1:
				Util.showToastBottom(ActivitySafetyArea.this,
						getString(R.string.operate_succeed));
				list.remove(romveoposition);
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
}
