package com.zgcar.com.location.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.location.Entity.SafetyAreaEntity;

public class AdapterSafetyArea extends BaseAdapter {
	private Context context;
	private List<SafetyAreaEntity> lst;

	public AdapterSafetyArea(Context context, List<SafetyAreaEntity> lst) {
		this.context = context;
		this.lst = lst;
	}

	@Override
	public int getCount() {
		return lst.size();
	}

	@Override
	public Object getItem(int position) {
		return lst.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_safetya_area, null);
			viewHolder.name = (TextView) view
					.findViewById(R.id.adapter_safety_area_name);
			viewHolder.range = (TextView) view
					.findViewById(R.id.adapter_safety_area_seekbar_range);
			viewHolder.icon = (ImageView) view
					.findViewById(R.id.adapter_safety_area_icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.name.setText(lst.get(position).getName());
		viewHolder.range.setText(context.getString(R.string.range)
				+ lst.get(position).getRad()
				+ context.getString(R.string.stroke_circle));
		if (context.getString(R.string.home)
				.equals(lst.get(position).getName())) {
			viewHolder.icon.setImageResource(R.drawable.safetyzone_icon_home);
		} else if (context.getString(R.string.xuexiao).equals(
				lst.get(position).getName())) {
			viewHolder.icon.setImageResource(R.drawable.safetyzone_icon_school);
		} else if (context.getString(R.string.firm).equals(
				lst.get(position).getName())) {
			viewHolder.icon
					.setImageResource(R.drawable.safetyzone_icon_company);
		} else if (context.getString(R.string.grandpahome).equals(
				lst.get(position).getName())) {
			viewHolder.icon.setImageResource(R.drawable.safetyzone_icon_home);
		} else if (context.getString(R.string.teacherhome).equals(
				lst.get(position).getName())) {
			viewHolder.icon.setImageResource(R.drawable.safetyzone_icon_home);
		} else if (context.getString(R.string.cramschool).equals(
				lst.get(position).getName())) {
			viewHolder.icon.setImageResource(R.drawable.safetyzone_icon_home);
		} else {
			viewHolder.icon
					.setImageResource(R.drawable.safetyzone_icon_otherplace);
		}
		return view;
	}

	private static class ViewHolder {
		private ImageView icon;
		private TextView name, range;

	}
}
