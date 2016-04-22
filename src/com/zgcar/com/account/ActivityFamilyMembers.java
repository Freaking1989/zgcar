package com.zgcar.com.account;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;

import com.zgcar.com.R;
import com.zgcar.com.account.adapter.AdapterFamilyMembers;
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.account.model.ListInfosAccount;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.ResolveServiceData;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

public class ActivityFamilyMembers extends Activity implements OnClickListener {

	private ExpandableListView listview;
	private List<FamilyParentInfos> familyList;
	private AdapterFamilyMembers adapter;
	private MyApplication app;
	private Dialog dialog;
	private ImageButton addNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family_members);
		init();
	}

	@Override
	protected void onResume() {
		getFimilyMembersInfos();
		super.onResume();
	}

	/**
	 * 获取家庭成员家长信息
	 */
	private void getFimilyMembersInfos() {
		if (!app.imeiIsEmpty(ActivityFamilyMembers.this, true)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					SharedPreferences sf = getSharedPreferences(
							FinalVariableLibrary.CACHE_FOLDER,
							Context.MODE_PRIVATE);
					String jsonStr = GetJsonString.getRequestJson(
							FinalVariableLibrary.MEMBERS_OF_FIMILY_CMD,
							app.getImei(), -1, app.getUserName());
					boolean flag1 = SocketUtil.connectService(jsonStr);
					if (flag1) {
						dismissDialog();
						familyList = ResolveServiceData.getFimilyInfos(sf,
								app, ActivityFamilyMembers.this);
						ListInfosAccount.setFamilyList(familyList);
						handler.sendMessage(handler.obtainMessage(0));
					} else {
						dismissDialog();
						Looper.prepare();
						Util.showToastBottom(ActivityFamilyMembers.this,
								SocketUtil.isFail(ActivityFamilyMembers.this));
						Looper.loop();
					}
				}
			}).start();
		} else {
			dismissDialog();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initView();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void initView() {
		if (ListInfosEntity.getTerminalListInfos().get(app.getPosition())
				.isAdmin()) {
			addNew.setVisibility(View.VISIBLE);
		} else {
			addNew.setVisibility(View.GONE);
		}
		adapter = new AdapterFamilyMembers(familyList,
				ListInfosEntity.getTerminalListInfos(), app.getPosition(),
				ActivityFamilyMembers.this);
		listview.setAdapter(adapter);
		expandListLisenner();
	}

	/**
	 * 初始化
	 */
	private void init() {
		showDialog();
		app = (MyApplication) getApplication();
		listview = (ExpandableListView) findViewById(R.id.family_members_expand_listview);
		ImageButton back = (ImageButton) findViewById(R.id.family_members_back);
		back.setOnClickListener(this);
		addNew = (ImageButton) findViewById(R.id.family_members_add_new);
		addNew.setOnClickListener(this);
		Button inviteOnther = (Button) findViewById(R.id.invite_onther_fimaly_members);
		inviteOnther.setOnClickListener(this);
	}

	private void expandListLisenner() {
		listview.expandGroup(0);
		listview.expandGroup(1);
		listview.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int arg2, long arg3) {
				return true;
			}
		});

		listview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				if (groupPosition == 0) {
					startActivity(new Intent(ActivityFamilyMembers.this,
							ActivityEditBasics.class));
				} else if (groupPosition == 1) {

					if (familyList.get(childPosition).isMysel()) {
						if (!familyList.get(childPosition).isAdmin()) {
							Intent intent = new Intent();
							intent.setClass(ActivityFamilyMembers.this,
									ActivityFamilyMemberMyselfBasics.class);
							intent.putExtra("FamilyParentInfos",
									familyList.get(childPosition));
							startActivity(intent);
							return false;
						}
						Intent intent = new Intent();
						intent.setClass(ActivityFamilyMembers.this,
								ActivityFamilyAdminBasics.class);
						intent.putExtra("FamilyParentInfos",
								familyList.get(childPosition));
						startActivity(intent);
						return false;
					}

					if (!ListInfosEntity.getTerminalListInfos()
							.get(app.getPosition()).isAdmin()) {
						Util.showToastBottom(ActivityFamilyMembers.this,
								getString(R.string.not_admin_can_not_set));
						return false;
					}
					Intent intent = new Intent();
					intent.setClass(ActivityFamilyMembers.this,
							ActivityFamilyMemberParentBasics.class);
					intent.putExtra("FamilyParentInfos",
							familyList.get(childPosition));
					startActivity(intent);
					return false;
				}
				return false;
			}
		});

	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

	}

	private void showDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityFamilyMembers.this, R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			handler.removeCallbacksAndMessages(null);
			finish();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.family_members_back:
			finish();
			break;
		case R.id.family_members_add_new:
			startActivity(new Intent(ActivityFamilyMembers.this,
					ActivityFamilyMembersAddNew.class));
			break;
		case R.id.invite_onther_fimaly_members:
			startActivity(new Intent(ActivityFamilyMembers.this,
					ActivityQrCode.class));
			break;
		default:
			break;
		}

	}
}
