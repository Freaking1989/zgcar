package com.zgcar.com.account;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.entity.RequestCode;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.UpLoadFile;
import com.zgcar.com.util.Util;

public class ActivityFamilyMemberParentBasics extends Activity implements
		OnClickListener {
	private MyApplication app;
	/**
	 * ��ͥ��Ա�ҳ��б����ڻ�ȡ��ǰ�˻�����
	 */
	private FamilyParentInfos memberInfo;
	/**
	 * �Ƿ��ͷ������˱༭
	 */
	private boolean hasImage;

	private TextView nickNameTv, relationTv;
	/**
	 * ������ʾͷ��
	 */
	private CircleImageView icon;
	/**
	 * �޸�����ʱ������dialog
	 */
	private Dialog dialog;
	private File imageFileNew, tempFile;
	/**
	 * �������
	 */
	private CheckBox falimyNumber;

	private Bitmap bitmapTemp;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				SendUpdataRequest();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family_member_parent_basics);
		initialize();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		hasImage = false;

		falimyNumber.setVisibility(View.VISIBLE);
		falimyNumber.setChecked(memberInfo.getSos());
		int width = (int) Util
				.dip2px(ActivityFamilyMemberParentBasics.this, 50);
		bitmapTemp = Util.decodeSampledBitmapFromResource(
				memberInfo.getLocalPath(), width, width);
		if (bitmapTemp != null) {
			icon.setImageBitmap(bitmapTemp);
		} else {
			icon.setImageResource(R.drawable.icon);
		}
		nickNameTv.setText(memberInfo.getNick_name());
		relationTv.setText(memberInfo.getU_name());
		boxListener();
	}

	private void boxListener() {
		falimyNumber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				memberInfo.setSos(arg1);
			}
		});
	}

	private void initialize() {
		app = (MyApplication) getApplication();
		memberInfo = (FamilyParentInfos) getIntent().getSerializableExtra(
				"FamilyParentInfos");
		falimyNumber = (CheckBox) findViewById(R.id.family_member_family_number);
		nickNameTv = (TextView) findViewById(R.id.family_basics_nick_name_tv);
		relationTv = (TextView) findViewById(R.id.family_basics_relation_tv2);
		TextView phoneNoTv = (TextView) findViewById(R.id.family_basics_phone_num_tv);
		phoneNoTv.setText(memberInfo.getUser_name());
		
		Button removefamily = (Button) findViewById(R.id.family_basics_remove_family);
		removefamily.setText(getString(R.string.remove_from_family));
		Button save = (Button) findViewById(R.id.family_basics_save);
		save.setOnClickListener(this);
		icon = (CircleImageView) findViewById(R.id.family_basics_icon);
		ImageButton camera = (ImageButton) findViewById(R.id.family_basics_camera);
		ImageButton back = (ImageButton) findViewById(R.id.family_basics_back);
		TextView title = (TextView) findViewById(R.id.family_basics_title);
		title.setText(getString(R.string.details_of_others));
		LinearLayout nickName = (LinearLayout) findViewById(R.id.family_basics_nick_name);
		LinearLayout relation = (LinearLayout) findViewById(R.id.family_basics_relation);
		nickName.setOnClickListener(this);
		removefamily.setOnClickListener(this);
		camera.setOnClickListener(this);
		relation.setOnClickListener(this);
		back.setOnClickListener(this);
		initView();
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.family_basics_save:
			showProgressDialog();
			sendRequest();
			break;
		// ����
		case R.id.family_basics_back:
			quit();
			break;
		// dialog��ȡ����ť
		case R.id.view_dialog_negative_button:
			dismissDialog();
			break;
		// �����ǳ�
		case R.id.family_basics_nick_name:
			showChangeInfoDialog();
			break;
		// ����ͷ��
		case R.id.family_basics_camera:
			showSelectIconDialog();
			break;
		// ���Ĺ�ϵ
		case R.id.family_basics_relation:
//			Intent intent = new Intent(ActivityFamilyMemberParentBasics.this,
//					ActivityFamilyEditRelationship.class);
//			startActivityForResult(intent, RequestCode.CODE_ZERO);
			break;
		// �Ƴ���ͥ
		case R.id.family_basics_remove_family:
			sendLeaveAloneRequest();
			break;
		case R.id.view_dialog_no:
			dismissDialog();
			break;
		case R.id.view_dialog_yes:
			dismissDialog();
			finish();
			break;
		case R.id.view_select_picture_1:
			dismissDialog();
			startCamera();
			break;
		case R.id.view_select_picture_2:
			dismissDialog();
			selectIconFromPhone();
			break;
		case R.id.view_select_picture_cancle:
			dismissDialog();
			break;
		default:
			break;
		}
	}

	private void showProgressDialog() {
		dialog = getDialog();
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
	}

	private void showChangeInfoDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyMemberParentBasics.this,
				R.layout.view_edit_info, null);
		Button negative = (Button) view
				.findViewById(R.id.view_dialog_negative_button);
		Button positive = (Button) view
				.findViewById(R.id.view_dialog_positive_button);
		TextView dialogTitle = (TextView) view
				.findViewById(R.id.view_dialog_textview);
		final EditText content = (EditText) view
				.findViewById(R.id.view_dialog_editteext);
		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String contentStr = content.getText().toString().trim();
				if (contentStr.equals("")) {
					Util.showToastBottom(ActivityFamilyMemberParentBasics.this,
							getString(R.string.please_input_new_nick_name));
					return;
				}
				nickNameTv.setText(contentStr);
				dismissDialog();
			}
		});
		negative.setOnClickListener(this);
		dialog.setContentView(view);
		dialogTitle.setText(getString(R.string.change_nick_name));
		content.setHint(nickNameTv.getText().toString().trim());
		content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
		dialog.setCancelable(true);
		dialog.show();
	}

	private void showSelectIconDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyMemberParentBasics.this,
				R.layout.view_select_picture, null);
		Button one = (Button) view.findViewById(R.id.view_select_picture_1);
		one.setOnClickListener(this);
		Button two = (Button) view.findViewById(R.id.view_select_picture_2);
		two.setOnClickListener(this);
		Button cancle = (Button) view
				.findViewById(R.id.view_select_picture_cancle);
		cancle.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void selectIconFromPhone() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, RequestCode.PHONE_ALBUM);
	}

	private void quit() {
		dialog = getDialog();
		View view1 = View.inflate(ActivityFamilyMemberParentBasics.this,
				R.layout.view_dialog_yes_or_not, null);
		TextView title = (TextView) view1.findViewById(R.id.view_dialog_title);
		title.setText(R.string.not_save_and_quit);
		Button no = (Button) view1.findViewById(R.id.view_dialog_no);
		Button yes = (Button) view1.findViewById(R.id.view_dialog_yes);
		no.setOnClickListener(this);
		yes.setOnClickListener(this);
		dialog.setContentView(view1);
		dialog.setCancelable(true);
		dialog.show();
	};

	/**
	 * ������Ϣ
	 */
	private void sendRequest() {
		if (hasImage) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String requestURL = "http://" + FinalVariableLibrary.ip
							+ ":"+FinalVariableLibrary.DSTPORT0+"/" + memberInfo.getUser_name()
							+ "/image/up?" + memberInfo.getUser_name() + ".jpg";
					boolean flag = UpLoadFile.toUploadFile(imageFileNew,
							requestURL);
					if (flag) {
						File file = new File(memberInfo.getLocalPath());
						if (file.exists()) {
							file.delete();
						}
						uploadIsSucceed();
						return;
					} else {
						handler.sendMessage(handler.obtainMessage(1));
						imageFileNew.delete();
						Looper.prepare();
						Util.showToastBottom(ActivityFamilyMemberParentBasics.this,
								getString(R.string.upload_picture_failed));
						Looper.loop();
					}
				}
			}).start();
		} else {
			SendUpdataRequest();
		}
	}

	/**
	 * �ϴ���Ƭ�ɹ�
	 */
	private void uploadIsSucceed() {
		int imageFlag = (memberInfo.getImage_flag() + 1) % 3 + 1;
		memberInfo.setImage_flag(imageFlag);
		memberInfo.setLocalPath(imageFileNew.getPath());
		SharedPreferences sf = getSharedPreferences(
				FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
		Editor editor = sf.edit();
		editor.putInt(memberInfo.getUser_name() + "imageFlag", imageFlag);
		editor.putString(memberInfo.getUser_name() + "image",
				memberInfo.getLocalPath());
		editor.commit();
		handler.sendMessage(handler.obtainMessage(1));
	}

	/**
	 * �����ֻ����
	 */
	private void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tempFile = new File(FinalVariableLibrary.PATHS + "/" + "image",
				System.currentTimeMillis() + ".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, RequestCode.PHONE_CAMERA);
	}

	/**
	 * ���ͱ�����Ϣ����
	 */
	private void SendUpdataRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getChangeBasicsInfoJsonStr();
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					dismissDialog();
					finish();
					Looper.prepare();
					Util.showToastBottom(ActivityFamilyMemberParentBasics.this,
							getString(R.string.save_succeed));
					Looper.loop();
				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(
							ActivityFamilyMemberParentBasics.this,
							SocketUtil
									.isFail(ActivityFamilyMemberParentBasics.this));
					Looper.loop();
				}
			}
		}).start();
	}

	/**
	 * 
	 * �ü���������ͼƬ�������ʾ
	 * 
	 * @param data
	 */
	private void showImageview(Intent data) {
		recycleBitmap();
		bitmapTemp = data.getParcelableExtra("data");
		imageFileNew = new File(FinalVariableLibrary.PATHS + "/image/"
				+ System.currentTimeMillis() + ".jpg");
		boolean flag = Util.saveBitmapToSDCard(bitmapTemp,
				imageFileNew.getPath());
		if (tempFile != null) {
			tempFile.delete();
		}
		if (flag) {
			hasImage = true;
			icon.setImageBitmap(bitmapTemp);
		} else {
			Util.showToastBottom(ActivityFamilyMemberParentBasics.this,
					getString(R.string.get_pocture_failed));
		}
	}

	/**
	 * ��������ͷ�ɹ��󣬶�ͼƬ���вü���������
	 */
	private void startCutImage(Uri uri) {
		if (uri != null) {
			Intent intent = new Intent();
			intent.setAction("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");// mUri���Ѿ�ѡ���ͼƬUri
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// �ü������
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", icon.getWidth());// ���ͼƬ��С
			Log.e("ActivityFamilyAdminBasics", icon.getWidth() + "");
			intent.putExtra("outputY", icon.getWidth());
			intent.putExtra("return-data", true);
			startActivityForResult(intent, RequestCode.PHONE_CUT_OUT);
		} else {
			Util.showToastBottom(ActivityFamilyMemberParentBasics.this,
					getString(R.string.get_pocture_failed));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCode.CODE_ZERO
				&& resultCode == RequestCode.RESULT_OK) {
			relationTv.setText(data.getStringExtra("name"));
		} else if (requestCode == RequestCode.PHONE_CAMERA
				&& resultCode == RESULT_OK) {
			startCutImage(Uri.fromFile(tempFile));
		} else if (requestCode == RequestCode.PHONE_ALBUM
				&& resultCode == RESULT_OK) {
			startCutImage(data.getData());
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode == RESULT_OK) {
			showImageview(data);
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode != RESULT_OK) {
			if (tempFile != null) {
				tempFile.delete();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ��ȡ�޸ļ�ͥ��Ա��������Ϣ����
	 */
	private String getChangeBasicsInfoJsonStr() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.CHANGE_MEMBERINFOS_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", app.getImei());
			object1.put("user_name", memberInfo.getUser_name());
			object1.put("u_name", relationTv.getText().toString());
			object1.put("sos", memberInfo.getSos() ? 1 : 0);
			object1.put("image_flag", memberInfo.getImage_flag());
			object1.put("nick_name", nickNameTv.getText().toString());
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * �����뿪��ָͥ��
	 */
	private void sendLeaveAloneRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getLeaveAloneJsonStr();
				if (SocketUtil.connectService(jsonStr)) {
					finish();
					Looper.prepare();
					Util.showToastBottom(ActivityFamilyMemberParentBasics.this,
							getString(R.string.remove_from_family_succeed));
					Looper.loop();
					return;
				} else {
					Looper.prepare();
					Util.showToastBottom(
							ActivityFamilyMemberParentBasics.this,
							SocketUtil
									.isFail(ActivityFamilyMemberParentBasics.this));
					Looper.loop();
				}
			}
		}).start();
	}

	/**
	 * ��ȡ�Ƴ���ͥʱ���͵���������
	 */
	private String getLeaveAloneJsonStr() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.LEAVE_ALONE_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", app.getImei());
			object1.put("user_name", memberInfo.getUser_name());
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	private Dialog getDialog() {
		return dialog == null ? new Dialog(
				ActivityFamilyMemberParentBasics.this, R.style.dialog) : dialog;
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void recycleBitmap() {
		if (bitmapTemp != null) {
			bitmapTemp.recycle();
			bitmapTemp = null;
		}
	}

	@Override
	protected void onDestroy() {
		recycleBitmap();
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
}
