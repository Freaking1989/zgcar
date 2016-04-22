package com.zgcar.com.account.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.MyAlarmInfos;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.Util;

public class AdapterMyAlarmClock extends BaseAdapter {

	private List<MyAlarmInfos> list;
	private Context context;
	private String imei;
	private String jsonStr;
	private CheckBox box;
	private TextView time;
	private TextView days;

	public AdapterMyAlarmClock(List<MyAlarmInfos> list, Context context,
			String imei) {
		this.context = context;
		this.list = list;
		this.imei = imei;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {

		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_alarm_clock, arg2, false);
			time = (TextView) view.findViewById(R.id.adapter_alartm_clock_time);
			box = (CheckBox) view
					.findViewById(R.id.adapter_alartm_clock_checkbox);
			days = (TextView) view.findViewById(R.id.adapter_alartm_clock_days);
		}
		time.setText(list.get(position).getTime());
		days.setText(list.get(position).getWeekDesc());

		if (list.get(position).getOn_off() == 0) {
			box.setChecked(false);
		} else if (list.get(position).getOn_off() == 1) {
			box.setChecked(true);
		}
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				getReequeestJsonStr(position);
				sendRequesrt(position, arg1);
			}
		});
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private synchronized void sendRequesrt(final int position,
			final boolean isChecked) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					if (isChecked) {
						list.get(position).setOn_off(1);
						return;
					} else {
						list.get(position).setOn_off(0);
						return;
					}
				}
				Looper.prepare();
				Util.showToastBottom(context, SocketUtil.isFail(context));
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 构造数据
	 */
	private synchronized void getReequeestJsonStr(int position) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cmd", FinalVariableLibrary.SET_ALARM_CMD);
			JSONArray array = new JSONArray();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("imei", imei);
			jsonObject1.put("time", list.get(position).getTime());
			jsonObject1.put("week", list.get(position).getWeek());
			jsonObject1.put("on_off", (list.get(position).getOn_off() + 1) % 2);
			jsonObject1.put("alarm_ring", list.get(position).getAlarm_ring());
			jsonObject1.put("alarm_sn", list.get(position).getAlarm_sn());
			jsonObject1.put("alarmring_flag", list.get(position)
					.getAlarmring_flag());
			array.put(jsonObject1);
			jsonObject.put("data", array);
			jsonStr = jsonObject.toString();
			Log.e("adapter", jsonObject.toString());
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}
}
