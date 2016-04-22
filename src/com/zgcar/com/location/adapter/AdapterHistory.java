package com.zgcar.com.location.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.location.Entity.HistoryEntityMain;

/**
 * 新的历史轨迹列表
 * 
 * @author mddoscar
 * @name NewHistoryAdapter
 */
public class AdapterHistory extends BaseAdapter {

	private Context mContext;
	private List<HistoryEntityMain> mList;

	public AdapterHistory(Context context, List<HistoryEntityMain> lst) {
		this.mContext = context;
		this.mList = lst;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {

		view = View.inflate(mContext, R.layout.adapter_history, null);
		TextView tText = (TextView) view.findViewById(R.id.date);
		String tDateText = mList.get(position).getDate();
		tText.setText(tDateText);

		return view;
	}

}
