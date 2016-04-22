package com.zgcar.com.account.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Util;

public class AdapterFamilyMembers extends BaseExpandableListAdapter {

	private List<FamilyParentInfos> familyList;
	private List<TerminalListInfos> terminalListInfos;
	private Context context;
	private List<String> child;
	private int position;
	private Bitmap bitmapTemp;

	public AdapterFamilyMembers(List<FamilyParentInfos> familyList,
			List<TerminalListInfos> terminalListInfos, int position,
			Context context) {
		this.context = context;
		this.familyList = familyList;
		this.terminalListInfos = terminalListInfos;
		this.position = position;
		child = new ArrayList<String>();
		child.add(context.getString(R.string.baby_desc));
		child.add(context.getString(R.string.parent_desc));
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (groupPosition == 0) {
			return terminalListInfos.get(position);
		} else {
			return familyList.get(childPosition);
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.adapter_guy_main_and_family_members,
							parent, false);
			viewHolder.tv = (TextView) convertView
					.findViewById(R.id.family_member_adapter_name);
			viewHolder.rlationDesc = (TextView) convertView
					.findViewById(R.id.adapter_textview_relation_desc);
			viewHolder.descTv = (TextView) convertView
					.findViewById(R.id.adapter_textview_desc);
			viewHolder.icon = (CircleImageView) convertView
					.findViewById(R.id.adapter_image_icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (groupPosition == 0) {
			viewHolder.tv.setText(terminalListInfos.get(position).getName());
			if (ListInfosEntity.getPathList() != null) {
				File file = new File(ListInfosEntity.getPathList()
						.get(position));
				if (file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
					viewHolder.icon.setImageBitmap(bitmap);
				} else {
					viewHolder.icon.setImageResource(R.drawable.icon);
				}
				viewHolder.rlationDesc.setText(context
						.getString(R.string.binding_state));
			}
		} else if (groupPosition == 1) {
			viewHolder.tv.setText(familyList.get(childPosition).getNick_name());
			String path = familyList.get(childPosition).getLocalPath();

			Log.e("º“Õ•≥…‘±icon", path);

			String desc = "";

			if (familyList.get(childPosition).isAdmin()) {
				desc = desc + context.getString(R.string.admin) + " ";
				viewHolder.descTv.setVisibility(View.VISIBLE);
			}

			if (familyList.get(childPosition).getSos()) {
				desc = desc + context.getString(R.string.family_number) + " ";
				viewHolder.descTv.setVisibility(View.VISIBLE);
			}
			if (familyList.get(childPosition).isMysel()) {
				desc = desc + "(" + context.getString(R.string.me) + ")";
				viewHolder.descTv.setVisibility(View.VISIBLE);
			}
			viewHolder.rlationDesc.setText(familyList.get(childPosition)
					.getU_name());
			viewHolder.descTv.setText(desc);

			int width = (int) Util.dip2px(context, 50);
			bitmapTemp = Util.decodeSampledBitmapFromResource(
					familyList.get(childPosition).getLocalPath(), width, width);
			if (bitmapTemp != null) {
				viewHolder.icon.setImageBitmap(bitmapTemp);
			} else {
				viewHolder.icon.setImageResource(R.drawable.icon);
			}
		}

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition == 0) {
			return 1;
		} else {
			return familyList.size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {

		return child.get(groupPosition);
	}

	@Override
	public int getGroupCount() {

		return child.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.adapter_family_expand_group, parent, false);
//		TextView tv = (TextView) convertView
//				.findViewById(R.id.view_adap_family_expand_group_name);
//		tv.setText(child.get(groupPosition));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {

		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	private static class ViewHolder {
		private TextView tv, descTv, rlationDesc;
		private CircleImageView icon;

	}
}
