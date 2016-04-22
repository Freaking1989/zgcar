package com.zgcar.com.account;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.adapter.AdapterSelectAdmin;
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.account.model.ListInfosAccount;
import com.zgcar.com.db.DbManager;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MainActivity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 变更管理员
 * 
 */
public class ActivitySelectAdmin extends Activity implements OnClickListener {
	/**
	 * 我在family集合中的位置
	 */
	private ListView listView;
	private Dialog dialog;
	private Button finish;
	private AdapterSelectAdmin adapter;
	private List<FamilyParentInfos> familyList;
	private boolean isLeaveFimily;
	private MyApplication app;
	private int myPosition;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Util.showToastBottom(ActivitySelectAdmin.this,
						getString(R.string.unbind_myself_warning));
				initView();
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
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		setContentView(R.layout.activity_select_admin);
		super.onCreate(savedInstanceState);
		initialize();
	}

	private void initialize() {
		app = (MyApplication) getApplication();
		isLeaveFimily = getIntent().getBooleanExtra("IsleaveFimily", false);
		listView = (ListView) findViewById(R.id.select_admin_listview);
		ImageButton back = (ImageButton) findViewById(R.id.select_admin_back);
		finish = (Button) findViewById(R.id.select_admin_finish);
		familyList = ListInfosAccount.getFamilyList();
		back.setOnClickListener(this);
		finish.setOnClickListener(this);
		if (familyList != null) {
			initView();
		} else {
			getFimilyMembersInfos();
		}

	}

	/**
	 * 获取家庭成员家长信息
	 */
	private void getFimilyMembersInfos() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sf = getSharedPreferences(
						FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
				String jsonStr = GetJsonString.getRequestJson(
						FinalVariableLibrary.MEMBERS_OF_FIMILY_CMD,
						app.getImei(), -1, app.getUserName());
				boolean flag1 = SocketUtil.connectService(jsonStr);
				if (flag1) {
					familyList = ResolveServiceData.getFimilyInfos(sf, app,
							ActivitySelectAdmin.this);
					ListInfosAccount.setFamilyList(familyList);
					handler.sendMessage(handler.obtainMessage(0));
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivitySelectAdmin.this,
							SocketUtil.isFail(ActivitySelectAdmin.this));
					Looper.loop();
				}
			}
		}).start();
	}

	private void initView() {
		adapter = new AdapterSelectAdmin(ActivitySelectAdmin.this, familyList,
				getMyselfPosition());
		listView.setAdapter(adapter);
	}

	private int getMyselfPosition() {
		for (int i = 0; i < familyList.size(); i++) {
			if (familyList.get(i).isMysel()) {
				myPosition = i;
				return i;
			}
		}
		return 0;

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.select_admin_back:
			showBackDialog();
			break;
		case R.id.select_admin_finish:
			Log.e("onClick", "1");
			if (adapter != null) {
				if (familyList.get(adapter.getA()).isMysel()) {
					if (isLeaveFimily) {
						Util.showToastBottom(ActivitySelectAdmin.this,
								getString(R.string.unbind_myself_warning));
						break;
					} else {
						Util.showToastBottom(ActivitySelectAdmin.this,
								getString(R.string.please_select_others));
						break;
					}
				}
				Log.e("onClick", "2");
				sendChangeAdminRequest();
			}
			break;
		case R.id.view_dialog_yes:
			dismissDialog();
			finish();
			break;
		case R.id.view_dialog_no:
			dismissDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取指令数据
	 */
	private String getRequestJsonStr() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", FinalVariableLibrary.CHANGE_ADMIN_CMD);
			JSONArray array = new JSONArray();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("imei", app.getImei());
			jsonObject1.put("user_name", app.getUserName());
			jsonObject1.put("to_name", familyList.get(adapter.getA())
					.getUser_name());
			array.put(jsonObject1);
			jsonObject.put("data", array);
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivitySelectAdmin.this, R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 发送指令进行管理员变更
	 */
	private void sendChangeAdminRequest() {
		Log.e("onClick", "3");
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.e("onClick", "4");
				String jsonStr = getRequestJsonStr();
				Log.e("onClick", "5");
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					Log.e("onClick", "6");
					ListInfosEntity.getTerminalListInfos()
							.get(app.getPosition()).setAdmin(false);
					if (!isLeaveFimily) {
						Log.e("onClick", "7");
						dismissDialog();
						familyList.get(adapter.getA()).setAdmin(false);
						Intent intent = new Intent();
						intent.setClass(ActivitySelectAdmin.this,
								ActivityFamilyMemberMyselfBasics.class);
						intent.putExtra("FamilyParentInfos",
								familyList.get(myPosition));
						startActivity(intent);
						Looper.prepare();
						Util.showToastBottom(ActivitySelectAdmin.this,
								getString(R.string.change_succeed));
						finish();
						Looper.loop();
					} else {
						Log.e("onClick", "8");
						sendLeaveAloneRequest();
					}
					return;
				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(ActivitySelectAdmin.this,
							SocketUtil.isFail(ActivitySelectAdmin.this));
					Looper.loop();
					return;
				}

			}
		}).start();
	}

	/**
	 * 发送离开家庭指令
	 */
	private void sendLeaveAloneRequest() {
		Log.e("onClick", "9");
		String data = getLeaveAloneJsonStr();
		boolean flag = SocketUtil.connectService(data);
		if (flag) {
			Log.e("onClick", "10");
			ListInfosEntity.getTerminalListInfos().remove(app.getPosition());
			ListInfosEntity.getPathList().remove(app.getPosition());
			if (ListInfosEntity.getTerminalListInfos().size() > 0) {
				app.setPosition(0);
				app.setImei(ListInfosEntity.getTerminalListInfos().get(0)
						.getImei());
			} else {
				app.setPosition(0);
				app.setImei("");
			}
			Log.e("onClick", "12");
			DbManager dbManager = new DbManager(ActivitySelectAdmin.this,
					app.getImei(), app.getUserName());
			dbManager.deletTable(app.getImei());
			Intent intent = new Intent(ActivitySelectAdmin.this,
					MainActivity.class);
			startActivity(intent);
			Log.e("onClick", "13");
			Quit.recycling(-1);
			dismissDialog();
			finish();
			Looper.prepare();
			Util.showToastBottom(ActivitySelectAdmin.this,
					getString(R.string.leave_family_succeed));
			Looper.loop();
			return;
		} else {
			Log.e("onClick", "11");
			dismissDialog();
			Looper.prepare();
			Util.showToastBottom(ActivitySelectAdmin.this,
					SocketUtil.isFail(ActivitySelectAdmin.this));
			Looper.loop();
		}
	}

	/**
	 * 获取离开家庭时发送的请求数据
	 */
	private String getLeaveAloneJsonStr() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.LEAVE_ALONE_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", app.getImei());
			object1.put("user_name", app.getUserName());
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showBackDialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 点击返回键时弹出对话框
	 */
	private void showBackDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivitySelectAdmin.this, R.style.dialog);
		}
		View view = View.inflate(ActivitySelectAdmin.this,
				R.layout.view_dialog_yes_or_not, null);
		TextView title = (TextView) view.findViewById(R.id.view_dialog_title);
		Button yes = (Button) view.findViewById(R.id.view_dialog_yes);
		Button no = (Button) view.findViewById(R.id.view_dialog_no);
		title.setText(R.string.quit_dialog_title_desc);
		no.setOnClickListener(this);
		yes.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

}
