package com.zgcar.com.location.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Util;

public class AdapterAddBaby extends BaseAdapter {
	private Context context;
	private List<TerminalListInfos> lst;

	public AdapterAddBaby(Context context, List<TerminalListInfos> lst) {
		this.context = context;
		this.lst = lst;
	}

	@Override
	public int getCount() {
		return lst.size();
	}

	public void setLst(List<TerminalListInfos> lst) {
		this.lst = lst;
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
	public View getView(int position, View view, ViewGroup viewgroup) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = View.inflate(context, R.layout.adapter_add_watch, null);
			viewHolder.text = (TextView) view.findViewById(R.id.tv_list_item);
			viewHolder.imageView = (CircleImageView) view
					.findViewById(R.id.add_babys);
			viewHolder.onlineState = (ImageView) view
					.findViewById(R.id.add_new_terminal);
			viewHolder.onlineState.setVisibility(View.VISIBLE);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.text.setText(lst.get(position).getName());
		int width = (int) Util.dip2px(context, 50);
		Bitmap bitmap1 = Util.decodeSampledBitmapFromResource(ListInfosEntity
				.getPathList().get(position), width, width);
		if (bitmap1 != null) {
			viewHolder.imageView.setImageBitmap(bitmap1);
		} else {
			viewHolder.imageView.setImageResource(R.drawable.icon);
		}
		if (lst.get(position).getOnline() == 1) {
			viewHolder.onlineState.setImageResource(R.drawable.icon_online);
		} else {
			viewHolder.onlineState.setImageResource(R.drawable.icon_offline);
		}

		return view;
	}

	static class ViewHolder {
		private TextView text;
		private CircleImageView imageView;
		private ImageView onlineState;

	}
}
