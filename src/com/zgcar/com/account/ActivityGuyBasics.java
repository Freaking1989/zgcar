package com.zgcar.com.account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.GuysInfos;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 
 * 小伙伴详情界面
 */
public class ActivityGuyBasics extends Activity implements OnClickListener {
	private CircleImageView icon;
	private TextView name, sex, age;
	private GuysInfos info;
	private int sexFlag;
	private Dialog dialog, progressDialog;
	private String jsonStr;
	private Bitmap iconBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guy_basics);
		initialize();
	}

	private void initialize() {
		icon = (CircleImageView) findViewById(R.id.guy_basics_icon);
		ImageButton back = (ImageButton) findViewById(R.id.guy_basics_back);
		name = (TextView) findViewById(R.id.guy_basics_name_tv);
		age = (TextView) findViewById(R.id.guy_basics_age_tv);
		sex = (TextView) findViewById(R.id.guy_basics_sex_tv);
		Button deleteBt = (Button) findViewById(R.id.guy_basics_delete_guy);
		deleteBt.setOnClickListener(this);
		back.setOnClickListener(this);
		Intent intent = getIntent();
		info = (GuysInfos) intent.getSerializableExtra("GuysInfos");
		initView();
	}

	private void initView() {

		int width = (int) Util.dip2px(ActivityGuyBasics.this, 50);
		iconBitmap = Util.decodeSampledBitmapFromResource(info.getLocalPath(),
				width, width);
		if (iconBitmap != null) {
			icon.setImageBitmap(iconBitmap);
		} else {
			icon.setImageResource(R.drawable.icon);
		}

		name.setText(info.getName());
		sexFlag = info.getSex();
		age.setText(info.getAge() + "");
		sex.setText(sexFlag == 0 ? getString(R.string.man)
				: getString(R.string.woman));
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.guy_basics_delete_guy:
			delereDialog();
			break;
		case R.id.guy_basics_back:
			finish();
			break;
		case R.id.view_dialog_no:
			dialog.dismiss();
			break;
		case R.id.view_dialog_yes:
			getDeletRequestJson();
			sendDeleteRequest();
			break;
		default:
			break;
		}
	}

	private synchronized void sendDeleteRequest() {
		progressDialog = new Dialog(ActivityGuyBasics.this, R.style.dialog);
		progressDialog.setContentView(R.layout.view_progress_dialog);
		progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {

				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					finish();
					progressDialog.dismiss();
					Looper.prepare();
					Util.showToastBottom(ActivityGuyBasics.this,
							getString(R.string.delete_guy_succeed));
					Looper.loop();
				} else {
					progressDialog.dismiss();
					Looper.prepare();
					Util.showToastBottom(ActivityGuyBasics.this,
							SocketUtil.isFail(ActivityGuyBasics.this));
					Looper.loop();
				}
			}
		}).start();

	}

	private void delereDialog() {
		dialog = new Dialog(ActivityGuyBasics.this, R.style.dialog);
		View view = LayoutInflater.from(ActivityGuyBasics.this).inflate(
				R.layout.view_dialog_yes_or_not, null);
		TextView title = (TextView) view.findViewById(R.id.view_dialog_title);
		title.setText(getString(R.string.sure_delete));
		Button no = (Button) view.findViewById(R.id.view_dialog_no);
		Button yes = (Button) view.findViewById(R.id.view_dialog_yes);
		no.setOnClickListener(this);
		yes.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}

	private void getDeletRequestJson() {
		/***
		 * 
		 * {"cmd":"00063","data":[{"imei":"861400000000088","fimei":
		 * "888888888888888"}]}*
		 */
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.DELETE_GUYS_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", info.getMainImei());
			object1.put("fimei", info.getImei());
			array.put(object1);
			object.put("data", array);
			jsonStr = object.toString();
			Log.e("getDeletRequestJson", jsonStr);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		if (iconBitmap != null) {
			iconBitmap.recycle();
			iconBitmap = null;
		}
		super.onDestroy();
	}

}
