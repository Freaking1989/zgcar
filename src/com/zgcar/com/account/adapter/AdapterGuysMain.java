package com.zgcar.com.account.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.GuysInfos;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Util;

public class AdapterGuysMain extends BaseAdapter {
	private List<GuysInfos> guysInfosListInfos;
	private Context context;
	private Bitmap bitmap;

	public AdapterGuysMain(List<GuysInfos> guysInfosListInfos, Context context) {
		this.context = context;
		this.guysInfosListInfos = guysInfosListInfos;
	}

	@Override
	public int getCount() {

		return guysInfosListInfos.size();
	}

	@Override
	public Object getItem(int arg0) {

		return guysInfosListInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_guy_main_and_family_members, arg2, false);
			viewHolder.icon = (CircleImageView) view
					.findViewById(R.id.adapter_image_icon);
			viewHolder.name = (TextView) view
					.findViewById(R.id.family_member_adapter_name);
			viewHolder.sex = (TextView) view
					.findViewById(R.id.adapter_textview_relation_desc);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		int width = (int) Util.dip2px(context, 45);
		bitmap = Util.decodeSampledBitmapFromResource(
				guysInfosListInfos.get(position).getLocalPath(), width, width);
		if (bitmap != null) {
			viewHolder.icon.setImageBitmap(bitmap);
		} else {
			viewHolder.icon.setImageResource(R.drawable.icon);
		}
		viewHolder.name.setText(guysInfosListInfos.get(position).getName());
		String sex = guysInfosListInfos.get(position).getSex() == 0 ? context
				.getString(R.string.man) : context.getString(R.string.woman);
		viewHolder.sex.setText(sex);

		return view;
	}

	private static class ViewHolder {
		private TextView sex;
		private TextView name;
		private CircleImageView icon;
	}
}
