package com.zgcar.com.main;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.jpush.android.api.JPushInterface;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.history.ActivitySosMeage1;
import com.zgcar.com.history.ActivitySosMeage2;
import com.zgcar.com.location.ActivityShowHistoryInfo1;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.NotifyMessageEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.Util;

/**
 * 趣味
 * 
 */
public class FragmentHistory extends Fragment implements OnItemClickListener,
		OnClickListener {

	private ListView listview;
	private MyApplication app;
	private ChatNotifyAdapter adapter;
	private List<NotifyMessageEntity> list;
	private MyReceiver receiver;
	private NotifyMessageEntity pushInfo;
	private String imei;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initView();
				break;
			case 1:
				getPushInfo(msg.getData());
				distinguishType1();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, container,
				false);
		initReceiver();
		init(view);
		getNotifyMessage();
		return view;
	}

	@Override
	public void onResume() {
		if (!imei.equals(app.getImei())) {
			imei = app.getImei();
			getNotifyMessage();
		}
		super.onResume();
	}

	private void init(View view) {
		app = (MyApplication) getActivity().getApplication();
		imei = app.getImei();
		ImageButton showInfo = (ImageButton) view
				.findViewById(R.id.fragment_history_show_info_ib);
		showInfo.setOnClickListener(this);
		listview = (ListView) view.findViewById(R.id.fragment_history_listview);
		listview.setOnItemClickListener(this);
	}

	private void initReceiver() {
		receiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter(
				FinalVariableLibrary.SystemNotifyMessageAction);
		getActivity().registerReceiver(receiver, intentFilter);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		adapter = new ChatNotifyAdapter(list, getActivity(), app.getPosition());
		listview.setAdapter(adapter);
	}

	/**
	 * 停留在当前界面时，收到推送消息，进行账户判断
	 */
	private void distinguishType1() {
		if (imei.equals(pushInfo.getImei())) {
			list.add(0, pushInfo);
			adapter.notifyDataSetChanged();
			return;
		}
		if (ListInfosEntity.getTerminalListInfos() != null) {
			List<TerminalListInfos> list = ListInfosEntity
					.getTerminalListInfos();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getImei().equals(pushInfo.getImei())) {
					imei = pushInfo.getImei();
					app.setImei(pushInfo.getImei());
					app.setPosition(i);
					getNotifyMessage();
					return;
				}
			}
		} else {
			// 暂时没考虑
		}

	}

	/**
	 * 当停留在该界面收到消息时直接添加到list集合中
	 * 
	 * @param bundle
	 */
	private void getPushInfo(Bundle bundle) {
		try {
			JSONObject object = new JSONObject(
					bundle.getString(JPushInterface.EXTRA_EXTRA));
			pushInfo = new NotifyMessageEntity();
			pushInfo.setAlarm(object.getString("alarm"));
			pushInfo.setGeo(object.getString("geo"));
			pushInfo.setImei(object.getString("imei"));
			pushInfo.setLa(object.getDouble("la"));
			pushInfo.setLitle(object.getString("litle"));
			pushInfo.setLo(object.getDouble("lo"));
			pushInfo.setMsg(object.getString("msg"));
			pushInfo.setTime(object.getString("time"));
		} catch (JSONException e) {
			pushInfo = null;
			e.printStackTrace();
		}
	}

	/**
	 * 获取服务端数据信息
	 */
	private synchronized void getNotifyMessage() {
		if (!app.imeiIsEmpty(getActivity(), true)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String jsonStr = getJson();
					boolean flag = SocketUtil.connectService(jsonStr);
					if (flag) {
						list = ResolveServiceData.getHistoryInfos();
						handler.sendMessage(handler.obtainMessage(0));
					} else {
						Looper.prepare();
						Util.showToastBottom(getActivity(),
								SocketUtil.isFail(getActivity()));
						Looper.loop();
					}
				}
			}).start();
		}
	}

	private String getJson() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.GET_NOTIFY_MESSAGE_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			String country = getResources().getConfiguration().locale
					.getCountry();
			if (country.equals("CN") || country.equals("TW")) {
				object1.put("language", "CN");
			} else {
				object1.put("language", "EN");
			}
			object1.put("imei", app.getImei());
			object1.put("map_type", FinalVariableLibrary.MAP_TYPE);
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Message msg = handler.obtainMessage(1);
			msg.setData(arg1.getExtras());
			handler.sendMessage(msg);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			handler.removeCallbacksAndMessages(null);
			SocketUtil.close(true);
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		getActivity().unregisterReceiver(receiver);
		SocketUtil.close(true);
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		String country = getResources().getConfiguration().locale.getCountry();
		if (country.equals("CN") || country.equals("TW")) {
			intent.setClass(getActivity(), ActivitySosMeage1.class);
		} else {
			intent.setClass(getActivity(), ActivitySosMeage2.class);
		}
		intent.putExtra("NotifyMessageEntity", list.get(position));
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_history_show_info_ib:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(),
						ActivityShowHistoryInfo1.class));
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
			break;

		default:
			break;
		}

	}

}
