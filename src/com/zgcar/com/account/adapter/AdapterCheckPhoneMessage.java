package com.zgcar.com.account.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.MessageInfos;

public class AdapterCheckPhoneMessage extends BaseAdapter {

	private Context context;
	private ArrayList<MessageInfos> list;

	public AdapterCheckPhoneMessage(Context context,
			ArrayList<MessageInfos> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	public void addItem(MessageInfos info) {
		list.add(info);
		Log.e("dada", list.toString());
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_check_phone, arg2, false);
			viewHolder.time = (TextView) view
					.findViewById(R.id.adapter_check_phone_time);
			viewHolder.tv1 = (TextView) view
					.findViewById(R.id.adapter_check_phone_get_content1);
			viewHolder.tv2 = (TextView) view
					.findViewById(R.id.adapter_check_phone_send_content1);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		MessageInfos info = list.get(position);
		viewHolder.time.setText(info.getTime());
		if (info.getFlag() == 0) {
			viewHolder.tv1.setVisibility(View.INVISIBLE);
			viewHolder.tv2.setVisibility(View.VISIBLE);
			viewHolder.tv2.setText(info.getMessage());
			viewHolder.tv1.setText("");
			return view;
		} else if (info.getFlag() == 1) {
			viewHolder.tv2.setVisibility(View.INVISIBLE);
			viewHolder.tv1.setVisibility(View.VISIBLE);
			viewHolder.tv1.setText(info.getMessage());
			viewHolder.tv2.setText("");
			return view;
		}
		return view;
	}

	private static class ViewHolder {
		private TextView tv1;
		private TextView tv2;
		private TextView time;
	}
}
