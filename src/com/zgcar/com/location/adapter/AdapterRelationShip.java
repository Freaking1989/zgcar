package com.zgcar.com.location.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.util.CircleImageView;

public class AdapterRelationShip extends BaseAdapter {
	private	Context context;
	private String[] name;
	private int[] head;


	public AdapterRelationShip(Context context, String[] name, int[] head) {
		this.context = context;
		this.name = name;
		this.head = head;
	}

	@Override
	public int getCount() {
		return name.length;
	}

	@Override
	public Object getItem(int position) {
		return name[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {

		View view1 = View.inflate(context, R.layout.adapter_gridview_family,
				null);
		CircleImageView iconImage = (CircleImageView) view1
				.findViewById(R.id.adapter_gridview_family_icon);
		TextView nameTv = (TextView) view1
				.findViewById(R.id.adapter_gridview_family_name);
		iconImage.setImageResource(head[position]);
		nameTv.setText(name[position]);
		return view1;
	}

}
