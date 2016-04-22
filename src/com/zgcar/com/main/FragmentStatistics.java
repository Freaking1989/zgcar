package com.zgcar.com.main;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.zgcar.com.R;
import com.zgcar.com.statisty.ActivityMileageStatisty;
import com.zgcar.com.statisty.ActivityOverSpeedStatisty;
import com.zgcar.com.statisty.ActivityAlarmStatisty;
import com.zgcar.com.statisty.ActivityTravelStatisty;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 聊天 语音上传成功后即往数据库添加
 */
public class FragmentStatistics extends Fragment implements OnClickListener {
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActivity().getWindow()
				.setBackgroundDrawableResource(R.color.color_4);
		SetTitleBackground.setTitleBg(getActivity(), R.color.color_4);
		super.onCreate(savedInstanceState);
		view = View.inflate(getActivity(), R.layout.fragment_statistics, null);
		init();
	}

	private void init() {
		int width = (int) Util.dip2px(getActivity(), 35);
		Button bt1 = (Button) view.findViewById(R.id.fragment_chat_bt1);
		setDrawableTop(bt1, R.drawable.icon_radio2_table1, width);
		bt1.setOnClickListener(this);
		Button bt2 = (Button) view.findViewById(R.id.fragment_chat_bt2);
		setDrawableTop(bt2, R.drawable.icon_radio2_table2, width);
		bt2.setOnClickListener(this);
		Button bt3 = (Button) view.findViewById(R.id.fragment_chat_bt3);
		setDrawableTop(bt3, R.drawable.icon_radio2_table3, width);
		bt3.setOnClickListener(this);
		Button alarmBt = (Button) view.findViewById(R.id.fragment_chat_bt4);
		setDrawableTop(alarmBt, R.drawable.icon_radio2_table4, width);
		alarmBt.setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return view;
	}

	private void setDrawableTop(Button botton, int bgId, int width) {
		Drawable topDrawable = getActivity().getResources().getDrawable(bgId);
		topDrawable.setBounds(0, 0, width, width);
		botton.setCompoundDrawables(null, topDrawable, null, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_chat_bt1:
			getActivity().startActivity(
					new Intent(getActivity(), ActivityMileageStatisty.class));
			break;
		case R.id.fragment_chat_bt2:
			getActivity().startActivity(
					new Intent(getActivity(), ActivityTravelStatisty.class));
			break;
		case R.id.fragment_chat_bt3:
			getActivity().startActivity(
					new Intent(getActivity(), ActivityOverSpeedStatisty.class));
			break;
		case R.id.fragment_chat_bt4:
			getActivity().startActivity(
					new Intent(getActivity(), ActivityAlarmStatisty.class));
			break;

		default:
			break;
		}
	}
}
