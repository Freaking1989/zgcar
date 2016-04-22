package com.zgcar.com.main;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.NotifyMessageEntity;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Util;

public class ChatNotifyAdapter extends BaseAdapter {
	private List<NotifyMessageEntity> list;
	private Context context;
	private int positionBit;

	public ChatNotifyAdapter(List<NotifyMessageEntity> list, Context context,
			int positionBit) {
		this.context = context;
		this.list = list;
		this.positionBit = positionBit;
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
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_chat_notify, arg2, false);
			view.setTag(viewHolder);
			viewHolder.warn = (ImageView) view
					.findViewById(R.id.message_notify_warn);
			viewHolder.icon = (CircleImageView) view
					.findViewById(R.id.adapter_chat_notify_icon);
			viewHolder.title = (TextView) view
					.findViewById(R.id.adapter_chat_notify_title);
			viewHolder.time = (TextView) view
					.findViewById(R.id.adapter_chat_notify_time);
			viewHolder.content = (TextView) view
					.findViewById(R.id.adapter_chat_notify_content);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (list.get(position).getAlarm().equals("1")) {
			viewHolder.warn.setVisibility(View.VISIBLE);
		} else {
			viewHolder.warn.setVisibility(View.INVISIBLE);
		}
		int width = (int) Util.dip2px(context, 50);

		Bitmap bitmap = Util.decodeSampledBitmapFromResource(ListInfosEntity
				.getPathList().get(positionBit), width, width);
		if (bitmap != null) {
			viewHolder.icon.setImageBitmap(bitmap);
		}
		viewHolder.title.setText(list.get(position).getLitle());
		String[] a = list.get(position).getTime().split(" ");
		viewHolder.time.setText(a[0]);
		viewHolder.content.setText(a[1] + " " + list.get(position).getMsg());
		return view;
	}

	private static class ViewHolder {
		private CircleImageView icon;
		private TextView title, time, content;
		private ImageView warn;
	}

}
