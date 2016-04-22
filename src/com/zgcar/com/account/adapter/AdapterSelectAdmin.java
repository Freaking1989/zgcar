package com.zgcar.com.account.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Util;

public class AdapterSelectAdmin extends BaseAdapter {

	private Context context;
	private List<FamilyParentInfos> familyList;
	private int a;
	private Bitmap bitmap;

	public AdapterSelectAdmin(Context context,
			List<FamilyParentInfos> familyList, int position) {
		a = position;
		this.context = context;
		this.familyList = familyList;
	}

	@Override
	public int getCount() {

		return familyList.size();
	}

	@Override
	public Object getItem(int arg0) {

		return familyList.get(arg0);
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
	public View getView(int arg0, View view, ViewGroup arg2) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.adapter_select_admin, arg2, false);
			holder.icon = (CircleImageView) view
					.findViewById(R.id.adapter_select_admin_icon);
			holder.nickName = (TextView) view
					.findViewById(R.id.adapter_select_admin_nickname);
			holder.uName = (TextView) view
					.findViewById(R.id.adapter_select_admin_u_name);
			holder.desc = (TextView) view
					.findViewById(R.id.adapter_select_admin_desc);
			holder.box1 = (CheckBox) view
					.findViewById(R.id.adapter_select_admin_check_box);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.b = arg0;
		int width = (int) Util.dip2px(context, 50);
		bitmap = Util.decodeSampledBitmapFromResource(familyList.get(arg0)
				.getLocalPath(), width, width);
		if (bitmap != null) {
			holder.icon.setImageBitmap(bitmap);
		} else {
			holder.icon.setImageResource(R.drawable.icon);
		}
		holder.nickName.setText(familyList.get(arg0).getNick_name());
		holder.uName.setText(familyList.get(arg0).getU_name());
		String str = "";
		if (familyList.get(arg0).isAdmin()) {
			str = context.getString(R.string.admin);
			holder.desc.setVisibility(View.VISIBLE);
		}
		if (familyList.get(arg0).getSos()) {
			str = str + context.getString(R.string.family_number);
			holder.desc.setVisibility(View.VISIBLE);
		}

		if (familyList.get(arg0).isMysel()) {
			str = str + "(" + context.getString(R.string.me) + ")";
			holder.desc.setVisibility(View.VISIBLE);
		}

		holder.desc.setText(str);
		if (holder.b == a) {
			holder.box1.setChecked(true);
		} else {
			holder.box1.setChecked(false);
		}
		Log.e("a", "a:" + a + "b:" + arg0);
		holder.box1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg3, boolean arg1) {
				if (arg1) {
					a = holder.b;
					notifyDataSetChanged();
				}
			}
		});
		return view;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}


	private static final class ViewHolder {
		private CircleImageView icon;
		private TextView nickName;
		private TextView uName;
		private TextView desc;
		private CheckBox box1;
		private int b;
	}

}
