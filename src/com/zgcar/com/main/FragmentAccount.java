package com.zgcar.com.main;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.ActivityBasics;
import com.zgcar.com.account.ActivityCheckPhone;
import com.zgcar.com.account.ActivityDisarmFortification;
import com.zgcar.com.account.ActivityElectricityAndOilManage;
import com.zgcar.com.account.ActivityFamilyMembers;
import com.zgcar.com.account.ActivitySafetyArea;
import com.zgcar.com.account.ActivitySetTimeZone;
import com.zgcar.com.account.ActivitySetWatchAPN;
import com.zgcar.com.account.ActivitySystemSet;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Util;

/**
 * 账户
 * 
 */
public class FragmentAccount extends Fragment implements OnClickListener {

	private View view;
	private CircleImageView icon;
	private MyApplication app;
	private TextView name, renewalTime;
	private int position;
	private Dialog dialog;
	private Bitmap bitmap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.fragment_account, null);
		init();
		return view;
	}

	private void initView() {
		if (!app.imeiIsEmpty(getActivity(), false)) {
			position = app.getPosition();
			if (ListInfosEntity.getTerminalListInfos() == null) {
				Util.showToastBottom(getActivity(),
						getString(R.string.get_watch_basics_failed));
				return;
			}
			name.setText(ListInfosEntity.getTerminalListInfos().get(position)
					.getName());
			String[] time = ListInfosEntity.getTerminalListInfos()
					.get(position).getServicedate().split(" ");
			String time1 = time[0].replace("/", "-");
			renewalTime.setText(getString(R.string.renewal_time) + time1);
			int width = (int) Util.dip2px(getActivity(), 50);
			bitmap = Util.decodeSampledBitmapFromResource(ListInfosEntity
					.getPathList().get(app.getPosition()), width, width);
			if (bitmap != null) {
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.icon);
			}
		}
	}

	private void init() {
		int width = (int) Util.dip2px(getActivity(), 35);
		app = (MyApplication) getActivity().getApplication();
		LinearLayout systemSet = (LinearLayout) view
				.findViewById(R.id.fragment_account_system_set);

		icon = (CircleImageView) view.findViewById(R.id.fragment_account_icon);
		name = (TextView) view.findViewById(R.id.fragment_account_name);
		renewalTime = (TextView) view
				.findViewById(R.id.fragment_account_renewal);
		LinearLayout basicLinear = (LinearLayout) view
				.findViewById(R.id.fragment_account_basics);
		LinearLayout setAPN = (LinearLayout) view
				.findViewById(R.id.fragment_account_apn);
		LinearLayout setTimeZone = (LinearLayout) view
				.findViewById(R.id.fragment_account_time_zone);

		Button shutDown = (Button) view
				.findViewById(R.id.fragment_account_shut_down);
		setDrawableTop(shutDown, R.drawable.account_icon_shut_down, width);
		Button checkPhone = (Button) view
				.findViewById(R.id.fragment_account_check_phone);
		setDrawableTop(checkPhone, R.drawable.account_icon_check_phone, width);
		Button disarmFortificationBt = (Button) view
				.findViewById(R.id.fragment_account_disarm_fortification_bt);
		setDrawableTop(disarmFortificationBt, R.drawable.icon_acount_table4,
				width);
		Button safetyZone = (Button) view
				.findViewById(R.id.fragment_account_safety_zone);
		setDrawableTop(safetyZone, R.drawable.account_icon_safety_zone, width);
		Button guys = (Button) view
				.findViewById(R.id.fragment_account_oil_electricity_info);
		setDrawableTop(guys, R.drawable.icon_acount_table2, width);
		Button familyBt = (Button) view
				.findViewById(R.id.fragment_account_fimily_members);
		setDrawableTop(familyBt, R.drawable.account_icon_famaly_guys, width);

		String country = getResources().getConfiguration().locale.getCountry();
		if (country.equals("CN") || country.equals("TW")) {
			if (country.equals("CN")) {
				setAPN.setVisibility(View.GONE);
				checkPhone.setEnabled(true);
			}
			setTimeZone.setVisibility(View.GONE);
		} else {
			checkPhone.setEnabled(false);
			setTimeZone.setVisibility(View.VISIBLE);
			setAPN.setVisibility(View.VISIBLE);
		}
		systemSet.setOnClickListener(this);
		shutDown.setOnClickListener(this);
		safetyZone.setOnClickListener(this);
		setTimeZone.setOnClickListener(this);
		setAPN.setOnClickListener(this);
		disarmFortificationBt.setOnClickListener(this);
		checkPhone.setOnClickListener(this);
		familyBt.setOnClickListener(this);
		guys.setOnClickListener(this);
		basicLinear.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_account_time_zone:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(),
						ActivitySetTimeZone.class));
			}
			break;
		case R.id.fragment_account_basics:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(), ActivityBasics.class));
			}
			break;
		case R.id.fragment_account_fimily_members:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(),
						ActivityFamilyMembers.class));
			}
			break;
		case R.id.fragment_account_oil_electricity_info:
			// if (!app.imeiIsEmpty(getActivity(), true)) {
			startActivity(new Intent(getActivity(),
					ActivityElectricityAndOilManage.class));
			// }
			break;
		case R.id.fragment_account_safety_zone:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(),
						ActivitySafetyArea.class));
			}
			break;
		case R.id.fragment_account_check_phone:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(),
						ActivityCheckPhone.class));
			}
			break;
		case R.id.fragment_account_shut_down:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				showShutDownAction();
			}
			break;
		case R.id.fragment_account_disarm_fortification_bt:
			// if (!app.imeiIsEmpty(getActivity(), true)) {
			startActivity(new Intent(getActivity(),
					ActivityDisarmFortification.class));
			// }
			break;

		case R.id.fragment_account_system_set:
			startActivity(new Intent(getActivity(), ActivitySystemSet.class));
			break;
		case R.id.fragment_account_apn:
			if (!app.imeiIsEmpty(getActivity(), true)) {
				startActivity(new Intent(getActivity(),
						ActivitySetWatchAPN.class));
			}
			break;

		case R.id.view_remove_watch_positive:
			dismissDialog();
			showProgressDialog();
			sendFindWatchRequest(FinalVariableLibrary.SHUT_DOWN_CMD);
			break;
		case R.id.view_remove_watch_negative:
			dismissDialog();
			break;

		default:
			break;
		}

	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(getActivity(), R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void sendFindWatchRequest(final String cmd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String string = GetJsonString.getRequestJson(cmd,
						app.getImei(), -1, app.getUserName());
				Log.e("sendFindWatchRequest", string);
				boolean flag = SocketUtil.connectService(string);
				if (flag) {
					dismissDialog();
					if (FinalVariableLibrary.SHUT_DOWN_CMD.equals(cmd)) {
						Looper.prepare();
						Util.showToastBottom(getActivity(),
								getString(R.string.set_succeed));
						Looper.loop();
					}
				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(getActivity(),
							SocketUtil.isFail(getActivity()));
					Looper.loop();
				}
			}
		}).start();

	}

	/**
	 * 远程关机
	 */
	private void showShutDownAction() {
		if (dialog == null) {
			dialog = new Dialog(getActivity(), R.style.dialog);
		}
		View dialogNotAdminView = View.inflate(getActivity(),
				R.layout.view_remove_koala, null);
		TextView title = (TextView) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_tv);
		TextView title1 = (TextView) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_tv1);
		TextView title2 = (TextView) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_tv2);
		Button positive = (Button) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_positive);
		positive.setOnClickListener(this);
		Button negative = (Button) dialogNotAdminView
				.findViewById(R.id.view_remove_watch_negative);
		negative.setOnClickListener(this);
		title1.setVisibility(View.VISIBLE);
		title2.setVisibility(View.VISIBLE);
		title.setVisibility(View.INVISIBLE);
		title1.setText(getString(R.string.sure_to_set)
				+ " "
				+ ListInfosEntity.getTerminalListInfos().get(app.getPosition())
						.getName() + " " + getString(R.string.power_off) + "?");
		dialog.setContentView(dialogNotAdminView);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			dismissDialog();
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onPause() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		initView();
		super.onResume();
	}

	private void setDrawableTop(Button button, int bgId, int width) {
		Drawable topDrawable = getActivity().getResources().getDrawable(bgId);
		topDrawable.setBounds(0, 0, width, width);
		button.setCompoundDrawables(null, topDrawable, null, null);
	}

}
