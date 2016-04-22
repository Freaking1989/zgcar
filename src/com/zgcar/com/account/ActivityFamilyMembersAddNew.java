package com.zgcar.com.account;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.entity.RequestCode;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.UpLoadFile;
import com.zgcar.com.util.Util;

public class ActivityFamilyMembersAddNew extends Activity implements
		OnClickListener {

	private TextView nickNameTv, phoneNumTv, relationTv;
	private CircleImageView icon;
	private Bitmap tempBitmap;
	private Dialog dialog;
	private File tempFile, iconNewFile;
	private boolean isHasIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family_members_add_new);
		init();
	}

	private void init() {
		isHasIcon = false;
		ImageButton back = (ImageButton) findViewById(R.id.family_members_add_new_back);
		back.setOnClickListener(this);
		ImageButton camera = (ImageButton) findViewById(R.id.family_members_add_new_camera);
		camera.setOnClickListener(this);
		LinearLayout nickName = (LinearLayout) findViewById(R.id.family_members_add_new_nick_name);
		nickName.setOnClickListener(this);
		LinearLayout relationship = (LinearLayout) findViewById(R.id.family_members_add_new_relation);
		relationship.setOnClickListener(this);
		LinearLayout phoneNum = (LinearLayout) findViewById(R.id.family_members_add_new_phone_num);
		phoneNum.setOnClickListener(this);
		Button confirm = (Button) findViewById(R.id.family_members_add_new_confirm);
		confirm.setOnClickListener(this);
		nickNameTv = (TextView) findViewById(R.id.family_members_add_new_nick_name_tv);
		relationTv = (TextView) findViewById(R.id.family_members_add_new_relation_tv);
		phoneNumTv = (TextView) findViewById(R.id.family_members_add_new_phone_num_tv);
		icon = (CircleImageView) findViewById(R.id.family_members_add_new_icon);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.family_members_add_new_back:
			finish();
			break;
		case R.id.family_members_add_new_camera:
			showSelectIconDialog();
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
		case R.id.family_members_add_new_nick_name:
			showEditNickNameDialog();
			break;
		case R.id.family_members_add_new_relation:

			break;
		case R.id.family_members_add_new_phone_num:
			showEditPhoneNum();
			break;
		case R.id.family_members_add_new_confirm:
			showProgressDialog();

			if (nickNameTv.getText().toString().trim().equals("")) {
				Util.showToastBottom(ActivityFamilyMembersAddNew.this,
						getString(R.string.please_input_nick_name));
				dismissDialog();
				break;
			}
			if (relationTv.getText().toString().trim().equals("")) {
				Util.showToastBottom(ActivityFamilyMembersAddNew.this,
						getString(R.string.input_succeeful_relationship));
				dismissDialog();
				break;
			}
			if (phoneNumTv.getText().toString().trim().equals("")) {
				Util.showToastBottom(ActivityFamilyMembersAddNew.this,
						getString(R.string.please_input_num));
				dismissDialog();
				break;
			}
			if (isHasIcon) {
				upLoadIcon();
			} else {
				saveRequest();
			}
			break;
		case R.id.view_input_phone_num_negative_button:
			dismissDialog();
			break;
		case R.id.view_input_phone_num_sysytem_num:
			dismissDialog();
			startActivityForResult(new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI),
					RequestCode.PHONE_CONTACTS);
			break;
		default:
			break;
		}
	}

	/**
	 * 调用手机相册
	 */
	private void selectIconFromPhone() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, RequestCode.PHONE_ALBUM);
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
	 * 弹出编辑头像资源时弹出框
	 */
	private void showSelectIconDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyMembersAddNew.this,
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

	/**
	 * 弹出编辑电话号码弹出框
	 */
	private void showEditPhoneNum() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyMembersAddNew.this,
				R.layout.view_input_phone_num, null);
		final EditText content = (EditText) view
				.findViewById(R.id.view_input_phone_num_editteext);
		content.setHint(phoneNumTv.getText().toString().trim());
		Button confirm = (Button) view
				.findViewById(R.id.view_input_phone_num_positive_button);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String no = content.getText().toString().trim();
				if (no.equals("")) {
					Util.showToastBottom(ActivityFamilyMembersAddNew.this,
							getString(R.string.please_input_num));
					return;
				}
				phoneNumTv.setText(no);
				dismissDialog();
			}
		});
		Button selectPhoneNum = (Button) view
				.findViewById(R.id.view_input_phone_num_sysytem_num);
		selectPhoneNum.setOnClickListener(this);
		Button cancel = (Button) view
				.findViewById(R.id.view_input_phone_num_negative_button);
		cancel.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * 弹出编辑昵称弹出框
	 */
	private void showEditNickNameDialog() {
		dialog = getDialog();
		View view = View.inflate(ActivityFamilyMembersAddNew.this,
				R.layout.view_edit_info, null);
		TextView title = (TextView) view
				.findViewById(R.id.view_dialog_textview);
		title.setText(getString(R.string.please_input_nick_name));
		final EditText content = (EditText) view
				.findViewById(R.id.view_dialog_editteext);
		content.setHint(nickNameTv.getText().toString().trim());
		Button confirm = (Button) view
				.findViewById(R.id.view_dialog_positive_button);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String no = content.getText().toString().trim();
				if (no.equals("")) {
					Util.showToastBottom(ActivityFamilyMembersAddNew.this,
							getString(R.string.please_input_nick_name));
					return;
				}
				nickNameTv.setText(no);
				dismissDialog();
			}
		});
		Button cancel = (Button) view
				.findViewById(R.id.view_dialog_negative_button);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissDialog();
			}
		});
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
	}

	/**
	 * 对得到的图片uri进行裁剪成得到正方形图片
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
			Util.showToastBottom(ActivityFamilyMembersAddNew.this,
					getString(R.string.get_pocture_failed));
		}
	}

	/**
	 * 
	 * 裁剪成正方形图片后进行显示,并缓存到本地
	 * 
	 * @param data
	 */
	private void showImageview(Intent data) {
		recycleBitmap();
		tempBitmap = data.getParcelableExtra("data");
		if (tempFile != null) {
			tempFile.delete();
		}
		iconNewFile = new File(FinalVariableLibrary.PATHS + "/image/"
				+ System.currentTimeMillis() + ".jpg");
		boolean flag = Util.saveBitmapToSDCard(tempBitmap,
				iconNewFile.getPath());
		if (flag) {
			isHasIcon = true;
			icon.setImageBitmap(tempBitmap);
		} else {
			Util.showToastBottom(ActivityFamilyMembersAddNew.this,
					getString(R.string.get_pocture_failed));
		}
	}

	/**
	 * 
	 * 弹出编辑进度弹出框
	 */
	private void showProgressDialog() {
		dialog = getDialog();
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	private Dialog getDialog() {
		return dialog == null ? new Dialog(ActivityFamilyMembersAddNew.this,
				R.style.dialog) : dialog;
	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		recycleBitmap();
		recycleFile();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCode.CODE_THREE
				&& resultCode == RequestCode.RESULT_OK) {
			relationTv.setText(data.getStringExtra("name"));
		} else if (requestCode == RequestCode.PHONE_CONTACTS
				&& resultCode == RESULT_OK) {
			getPhoneNumTv(data.getData());
		} else if (requestCode == RequestCode.PHONE_CAMERA
				&& resultCode == RESULT_OK) {
			startCutImage(Uri.fromFile(tempFile));
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode == RESULT_OK) {
			showImageview(data);
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode != RESULT_OK) {
			if (tempFile != null) {
				tempFile.delete();
			}
		} else if (requestCode == RequestCode.PHONE_ALBUM
				&& resultCode == RESULT_OK) {
			startCutImage(data.getData());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 利用通讯录获得的数据得到号码
	 * 
	 * @param uri
	 */
	public void getPhoneNumTv(Uri uri) {
		// 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor.moveToFirst()) {
			// 条件为联系人ID
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phone.moveToNext()) {
				String usernumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneNumTv.setText(usernumber);
			}
		}
	}

	/**
	 * 上传图片
	 */
	private void upLoadIcon() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String no = phoneNumTv.getText().toString().trim();
				String requestURL = "http://" + FinalVariableLibrary.ip + ":"
						+ FinalVariableLibrary.DSTPORT0 + "/" + no
						+ "/image/up?" + no + ".jpg";
				boolean flag = UpLoadFile.toUploadFile(iconNewFile, requestURL);
				if (flag) {
					String jsonStr = getJsonStr(1);
					boolean flag1 = SocketUtil.connectService(jsonStr);
					if (flag1) {
						dismissDialog();
						finish();
					} else {
						dismissDialog();
					}
				} else {
					Looper.prepare();
					dismissDialog();
					Util.showToastBottom(ActivityFamilyMembersAddNew.this,
							getString(R.string.upload_picture_failed));
					Looper.loop();
				}
			}
		}).start();

	}

	/**
	 * 保存
	 */
	private void saveRequest() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getJsonStr(0);
				if (SocketUtil.connectService(jsonStr)) {
					dismissDialog();
					finish();
				} else {
					Looper.prepare();
					dismissDialog();
					Util.showToastBottom(ActivityFamilyMembersAddNew.this,
							SocketUtil.isFail(ActivityFamilyMembersAddNew.this));
					Looper.loop();
				}
			}
		}).start();
	}

	/**
	 * {"cmd":"80065","data":[{"imei":"865609152214047","user_name":
	 * "13717033460"
	 * ,"u_name":"baba","sos":1,"image_flag":0,"nick_name":”xiaoguang”}]}
	 */
	private String getJsonStr(int flag) {
		try {
			MyApplication app = (MyApplication) getApplication();
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.FAMILY_ADD_NEW_MEMBER_CMD);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", app.getImei());
			object1.put("user_name", phoneNumTv.getText().toString().trim());
			object1.put("u_name", relationTv.getText().toString().trim());
			object1.put("sos", 1);
			object1.put("image_flag", flag);
			object1.put("nick_name", nickNameTv.getText().toString().trim());
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void recycleFile() {
		if (iconNewFile != null) {
			iconNewFile.delete();
		}
	}

	private void recycleBitmap() {
		if (tempBitmap != null) {
			tempBitmap.recycle();
		}
	}
}
