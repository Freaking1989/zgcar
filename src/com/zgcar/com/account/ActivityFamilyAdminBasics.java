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
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.account.model.ListInfosAccount;
import com.zgcar.com.db.DbManager;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.entity.RequestCode;
import com.zgcar.com.main.MainActivity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.socket.GetJsonString;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.UpLoadFile;
import com.zgcar.com.util.Util;

public class ActivityFamilyAdminBasics extends Activity implements
		OnClickListener {
	private MyApplication app;
	// 家庭成员家长列表，用于获取当前账户资料
	private FamilyParentInfos memberInfo;
	// 是否对头像进行了编辑
	private boolean hasImage;
	private TextView relationTv, nickNameTv;
	// 用于显示头像
	private CircleImageView icon;
	private Dialog dialog;
	private File imageFileNew, tempFile;

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
		setContentView(R.layout.activity_family_member_admin_basics);
		init();
	}

	private void init() {
		memberInfo = (FamilyParentInfos) getIntent().getSerializableExtra(
				"FamilyParentInfos");
		app = (MyApplication) getApplication();
		nickNameTv = (TextView) findViewById(R.id.family_basics_admin_nick_name_tv);
		relationTv = (TextView) findViewById(R.id.family_basics_admin_relation_tv);
		TextView phoneNo = (TextView) findViewById(R.id.family_basics_admin_phone_no_tv);
		phoneNo.setText(memberInfo.getUser_name());
		Button removefamily = (Button) findViewById(R.id.family_basics_admin_leave_alone);
		Button changeAdmin = (Button) findViewById(R.id.family_basics_admin_change_admin);
		icon = (CircleImageView) findViewById(R.id.family_basics_admin_icon);
		ImageButton camera = (ImageButton) findViewById(R.id.family_basics_admin_camera);
		ImageButton back = (ImageButton) findViewById(R.id.family_basics_admin_back);
		LinearLayout nickName = (LinearLayout) findViewById(R.id.family_basics_admin_nick_name);
		LinearLayout relation = (LinearLayout) findViewById(R.id.family_basics_admin_relation);
		Button save = (Button) findViewById(R.id.family_basics_admin_save);
		save.setOnClickListener(this);
		nickName.setOnClickListener(this);
		removefamily.setOnClickListener(this);
		changeAdmin.setOnClickListener(this);
		camera.setOnClickListener(this);
		relation.setOnClickListener(this);
		back.setOnClickListener(this);
		initView();
	}

	private void initView() {
		hasImage = false;
		int width = (int) Util.dip2px(ActivityFamilyAdminBasics.this, 50);
		bitmapTemp = Util.decodeSampledBitmapFromResource(
				memberInfo.getLocalPath(), width, width);
		if (bitmapTemp != null) {
			icon.setImageBitmap(bitmapTemp);
		} else {
			icon.setImageResource(R.drawable.icon);
		}
		nickNameTv.setText(memberInfo.getNick_name());
		relationTv.setText(memberInfo.getU_name());
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent();
		switch (arg0.getId()) {
		case R.id.family_basics_admin_save:
			showProgressDialog();
			sendRequest();
			break;
		// 返回
		case R.id.family_basics_admin_back:
			quit();
			break;
		// 宝贝昵称
		case R.id.family_basics_admin_nick_name:
			showEditNickNameDialog();
			break;
		// 更改头像
		case R.id.family_basics_admin_camera:
			showSelectIconDialog();
			break;
		// 更改关系
		case R.id.family_basics_admin_relation:
//			intent.setClass(ActivityFamilyAdminBasics.this,
//					ActivityFamilyEditRelationship.class);
//			startActivityForResult(intent, RequestCode.CODE_ZERO);
			break;
		// 离开家庭
		case R.id.family_basics_admin_leave_alone:
			dialogChangeAdmin();
			break;
		case R.id.view_dialog_no:
			dismissDialog();
			break;
		case R.id.view_dialog_yes:
			dismissDialog();
			finish();
			break;
		case R.id.family_basics_admin_change_admin:
			if (ListInfosAccount.getFamilyList().size() < 2) {
				Util.showToastBottom(ActivityFamilyAdminBasics.this,
						getString(R.string.change_admin_desc_));
				break;
			}
			intent.setClass(ActivityFamilyAdminBasics.this,
					ActivitySelectAdmin.class);
			intent.putExtra("IsleaveFimily", false);
			startActivity(intent);
			break;
		case R.id.view_dialog_change_admin_unwrap_alone:
			dismissDialog();
			if (ListInfosAccount.getFamilyList().size() < 2) {
				unwrapAllMembers();
				break;
			}
			intent.setClass(ActivityFamilyAdminBasics.this,
					ActivitySelectAdmin.class);
			intent.putExtra("IsleaveFimily", true);
			startActivity(intent);
			break;
		case R.id.view_dialog_change_admin_unwrap_all:
			dismissDialog();
			showUnbindWearningDialog();
			break;
		case R.id.view_dialog_change_admin_no:
			dismissDialog();
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
		case R.id.view_unbind_warning_negative:
			dismissDialog();
			break;
		case R.id.view_unbind_warning_positive:
			dismissDialog();
			unwrapAllMembers();
			break;
		// dialog的取消按钮
		case R.id.view_dialog_negative_button:
			dismissDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 解绑管理员时弹出警告信息
	 */
	private void showUnbindWearningDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyAdminBasics.this,
				R.layout.view_unbind_all_warning, null);
		Button yes = (Button) view
				.findViewById(R.id.view_unbind_warning_positive);
		Button no = (Button) view
				.findViewById(R.id.view_unbind_warning_negative);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

	private void showEditNickNameDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyAdminBasics.this,
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
				dismissDialog();
				if (content.getText().toString().trim().equals("")) {
					Util.showToastBottom(ActivityFamilyAdminBasics.this,
							getString(R.string.please_input_new_nick_name));
					return;
				}
				nickNameTv.setText(content.getText().toString().trim());
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
		View view = View.inflate(ActivityFamilyAdminBasics.this,
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

	/**
	 * 解散家庭
	 */
	private void unwrapAllMembers() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = GetJsonString.getRequestJson(
						FinalVariableLibrary.LEAVE_ALONE_CMD, app.getImei(),
						-1, app.getUserName());
				boolean flag = SocketUtil.connectService(json);
				if (flag) {
					ListInfosEntity.getTerminalListInfos().remove(
							app.getPosition());
					DbManager dbManager = new DbManager(
							ActivityFamilyAdminBasics.this, app.getImei(),
							app.getUserName());
					dbManager.deletTable(app.getImei());
					ListInfosEntity.getPathList().remove(app.getPosition());

					if (ListInfosEntity.getTerminalListInfos().size() > 0) {
						app.setPosition(0);
						app.setImei(ListInfosEntity.getTerminalListInfos()
								.get(0).getImei());
					} else {
						app.setPosition(0);
						app.setImei("");
					}
					Intent intent = new Intent(ActivityFamilyAdminBasics.this,
							MainActivity.class);
					startActivity(intent);
					finish();
					Quit.recycling(-1);
					Looper.prepare();
					Util.showToastBottom(ActivityFamilyAdminBasics.this,
							getString(R.string.operate_succeed));
					Looper.loop();
					return;
				} else {
					Looper.prepare();
					Util.showToastBottom(ActivityFamilyAdminBasics.this,
							SocketUtil.isFail(ActivityFamilyAdminBasics.this));
					Looper.loop();
					return;
				}

			}
		}).start();

	}

	/**
	 * 解绑管理员
	 */
	private void dialogChangeAdmin() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyAdminBasics.this,
				R.layout.view_dialog_change_admin, null);
		Button unwrapAlone = (Button) view
				.findViewById(R.id.view_dialog_change_admin_unwrap_alone);
		Button unwrapAll = (Button) view
				.findViewById(R.id.view_dialog_change_admin_unwrap_all);
		Button no = (Button) view
				.findViewById(R.id.view_dialog_change_admin_no);
		no.setOnClickListener(this);
		unwrapAll.setOnClickListener(this);
		unwrapAlone.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * 退出
	 */
	private void quit() {
		dialog = getDialog();
		View view1 = View.inflate(ActivityFamilyAdminBasics.this,
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
	 * 保存信息
	 */
	private void sendRequest() {
		if (hasImage) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String requestURL = "http://" + FinalVariableLibrary.ip
							+ ":" + FinalVariableLibrary.DSTPORT0 + "/"
							+ app.getUserName() + "/image/up?"
							+ app.getUserName() + ".jpg";
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
						Util.showToastBottom(ActivityFamilyAdminBasics.this,
								getString(R.string.upload_picture_failed));
						Looper.loop();
					}
				}
			}).start();
			return;
		} else {
			SendUpdataRequest();
		}
	}

	/**
	 * 上传照片成功
	 */
	private void uploadIsSucceed() {
		int imageFlag = memberInfo.getImage_flag();
		memberInfo.setImage_flag((imageFlag + 1) % 3 + 1);
		memberInfo.setImage_link(imageFileNew.getPath());
		memberInfo.setLocalPath(imageFileNew.getPath());
		SharedPreferences sf = getSharedPreferences(
				FinalVariableLibrary.CACHE_FOLDER, MODE_PRIVATE);
		Editor editor = sf.edit();
		editor.putInt(app.getUserName() + "imageFlag", imageFlag);
		editor.putString(app.getUserName() + "image", memberInfo.getLocalPath());
		editor.commit();
		handler.sendMessage(handler.obtainMessage(1));
	}

	/**
	 * 调用手机相机
	 */
	private void startCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tempFile = new File(FinalVariableLibrary.PATHS + "/" + "image",
				System.currentTimeMillis() + ".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, RequestCode.PHONE_CAMERA);
	}

	/**
	 * 发送保存信息请求
	 */
	private void SendUpdataRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getChangeBasicsInfoJsonStr();
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					saveBasicsInfos();
					dismissDialog();
					finish();
					Looper.prepare();
					Util.showToastBottom(ActivityFamilyAdminBasics.this,
							getString(R.string.save_succeed));
					Looper.loop();
				} else {
					dismissDialog();
					Looper.prepare();
					Util.showToastBottom(ActivityFamilyAdminBasics.this,
							SocketUtil.isFail(ActivityFamilyAdminBasics.this));
					Looper.loop();
				}
			}
		}).start();
	}

	/**
	 * 保存到本地
	 */
	private void saveBasicsInfos() {
		if (hasImage) {
			if (FinalVariableLibrary.bitmap != null) {
				FinalVariableLibrary.bitmap.recycle();
			}
			FinalVariableLibrary.bitmap = BitmapFactory.decodeFile(memberInfo
					.getLocalPath());
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
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode != RESULT_OK) {
			if (tempFile != null) {
				tempFile.delete();
			}
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode == RESULT_OK) {
			showImageview(data);
		} else if (requestCode == RequestCode.PHONE_ALBUM
				&& resultCode == RESULT_OK) {
			Uri uri = data.getData();
			startCutImage(uri);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪成正方形图片后进行显示
	 * 
	 * @param data
	 */
	private void showImageview(Intent data) {
		recycleImage();
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
			Util.showToastBottom(ActivityFamilyAdminBasics.this,
					getString(R.string.get_pocture_failed));
		}
	}

	/**
	 * 调用摄像头成功后，对图片进行裁剪成正方形
	 */
	private void startCutImage(Uri uri) {
		if (uri != null) {
			Intent intent = new Intent();
			intent.setAction("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// 裁剪框比例
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", icon.getWidth());// 输出图片大小
			Log.e("ActivityFamilyAdminBasics", icon.getWidth() + "");
			intent.putExtra("outputY", icon.getWidth());
			intent.putExtra("return-data", true);
			startActivityForResult(intent, RequestCode.PHONE_CUT_OUT);
		} else {
			Util.showToastBottom(ActivityFamilyAdminBasics.this,
					getString(R.string.get_pocture_failed));
		}
	}

	/**
	 * 获取修改我的资料信息数据
	 */
	private String getChangeBasicsInfoJsonStr() {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.CHANGE_MEMBERINFOS_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", app.getImei());
			object1.put("user_name", app.getUserName());
			object1.put("u_name", relationTv.getText().toString());
			object1.put("sos", 1);
			object1.put("image_flag", memberInfo.getImage_flag());
			object1.put("nick_name", nickNameTv.getText().toString());
			array.put(object1);
			object.put("data", array);
			Log.e("保存信息", object.toString());
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showProgressDialog() {
		dialog = getDialog();
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void recycleImage() {
		if (bitmapTemp != null) {
			bitmapTemp.recycle();
			bitmapTemp = null;
		}
	}

	private Dialog getDialog() {
		return dialog == null ? new Dialog(ActivityFamilyAdminBasics.this,
				R.style.dialog) : dialog;
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		recycleImage();
		super.onDestroy();
	}

}
