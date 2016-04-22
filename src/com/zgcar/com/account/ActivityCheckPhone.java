package com.zgcar.com.account;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.R;
import com.zgcar.com.account.adapter.AdapterCheckPhoneMessage;
import com.zgcar.com.account.model.MessageInfos;
import com.zgcar.com.db.DbManager;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityCheckPhone extends Activity implements OnClickListener {

	private ListView listView;
	private String costStr;
	private String trafficStr;
	private ArrayList<MessageInfos> infosList;
	private DbManager dbManager;
	private AdapterCheckPhoneMessage adapter;
	private Dialog dialog;
	private String date;
	private String content;
	private phoneReceiver receive;
	private MyApplication app;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 5:// �ɹ����Ͳ�ѯ������Ϣ�����
				Util.showToastBottom(ActivityCheckPhone.this,
						getString(R.string.send_succeed_and_wait_a_moment));
				date = Util.getCurrentTime();
				content = costStr;
				addMessageInfo(0);
				dismissDialog();
				break;

			case 6:// ��ʱû��ʹ�ø���Ϣ
				Bundle bundle = msg.getData();
				if (bundle != null) {
					getNotifyData(bundle);
				}
				break;
			case 7:// ��ʼ������
				adapter.notifyDataSetChanged();
				listView.setSelection(infosList.size() - 1);
				break;

			case 8:// �ɹ����ʹӲ�ѯ������Ϣ�����
				dismissDialog();
				date = Util.getCurrentTime();
				content = trafficStr;
				addMessageInfo(0);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_phone);
		app = (MyApplication) getApplication();
		initialize();
		initView();
		initReceive();
	}

	private void initView() {
		infosList = dbManager.query();
		adapter = new AdapterCheckPhoneMessage(ActivityCheckPhone.this,
				infosList);
		listView.setAdapter(adapter);
		listView.setSelection(infosList.size() - 1);
	}

	/**
	 * ע�����������Ϣ�㲥
	 */
	private void initReceive() {
		receive = new phoneReceiver();
		IntentFilter intentFilter = new IntentFilter(
				FinalVariableLibrary.ChackBroadcastReceiverAction);
		registerReceiver(receive, intentFilter);
	}

	private void getNotifyData(Bundle bundle) {
		try {
			JSONObject object = new JSONObject(
					bundle.getString(JPushInterface.EXTRA_EXTRA));
			date = object.getString("time");
			content = object.getString("msg");
			addMessageInfo(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		dbManager = new DbManager(ActivityCheckPhone.this, app.getImei(),
				app.getUserName());
		ImageButton back = (ImageButton) findViewById(R.id.check_phone_back);
		Button edit = (Button) findViewById(R.id.check_phone_edit_btn);
		Button queryTraffic = (Button) findViewById(R.id.check_phone_query_traffic);
		Button queryCost = (Button) findViewById(R.id.check_phone_query_cost);
		listView = (ListView) findViewById(R.id.check_phone_listview);
		trafficStr = getResources().getString(R.string.phone_query_traffic)
				.replace(
						"999",
						ListInfosEntity.getTerminalListInfos()
								.get(app.getPosition()).getNumber());
		costStr = getResources().getString(R.string.phone_query_cost).replace(
				"999",
				ListInfosEntity.getTerminalListInfos().get(app.getPosition())
						.getNumber());
		edit.setOnClickListener(this);
		back.setOnClickListener(this);
		queryTraffic.setOnClickListener(this);
		queryCost.setOnClickListener(this);
	}

	/**
	 * ���Ͳ�ѯָ��
	 */
	private void sendRequest(final String cmd, final boolean isCost) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = GetJsonString.getRequestJson(cmd,
						app.getImei(), -1, app.getUserName());
				Log.e("sendRequest", jsonStr);
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					if (isCost) {
						handler.sendMessage(handler.obtainMessage(5));
					} else {
						handler.sendMessage(handler.obtainMessage(8));
					}

				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(ActivityCheckPhone.this,
							SocketUtil.isFail(ActivityCheckPhone.this));
					Looper.loop();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.check_phone_query_traffic:
			showProgressDialog();
			sendRequest(FinalVariableLibrary.QUERY_TRAFFIC, false);
			break;
		case R.id.check_phone_query_cost:
			showProgressDialog();
			sendRequest(FinalVariableLibrary.QUERY_COST, true);
			break;
		case R.id.check_phone_edit_btn:
			showEditDialog();
			break;
		case R.id.check_phone_back:
			finish();
			break;
		case R.id.view_check_phone_clear:
			clearData();
			break;
		case R.id.view_check_phone_cancle:
			dismissDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * �������
	 */
	private void clearData() {
		infosList.clear();
		// listView.removeAllViews();
		adapter.notifyDataSetChanged();
		dbManager.deletPhoneTable(app.getImei());
		dismissDialog();
	}

	private void showProgressDialog() {
		dialog = dialog == null ? new Dialog(ActivityCheckPhone.this,
				R.style.dialog) : dialog;
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void showEditDialog() {
		dialog = dialog == null ? new Dialog(ActivityCheckPhone.this,
				R.style.dialog) : dialog;
		View view = View.inflate(ActivityCheckPhone.this,
				R.layout.view_check_phone_clear_msg, null);
		Button clear = (Button) view.findViewById(R.id.view_check_phone_clear);
		clear.setOnClickListener(this);
		Button cancle = (Button) view
				.findViewById(R.id.view_check_phone_cancle);
		cancle.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	/**
	 * ���ݿ����������
	 */
	private synchronized void addMessageInfo(int flag) {
		MessageInfos info = new MessageInfos();
		info.setFlag(flag);
		info.setTime(date);
		Log.e("shuju", date);
		info.setMessage(content);
		dbManager.insert(info);
		if (infosList != null) {
			infosList.add(info);
			handler.sendMessage(handler.obtainMessage(7));
		}
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		dbManager.closeDb();
		unregisterReceiver(receive);
		dbManager = null;
		super.onDestroy();
	}

	private class phoneReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			MessageInfos info = (MessageInfos) intent
					.getSerializableExtra("MessageInfo");
			if (infosList != null) {
				if (app.getImei().equals(intent.getStringExtra("imei"))) {
					infosList.add(info);
					handler.sendMessage(handler.obtainMessage(7));
				} else {
					Util.showToastBottom(
							ActivityCheckPhone.this,
							getString(R.string.get_other_baby_phone_message_desc));
				}
			}
		}
	};

}
