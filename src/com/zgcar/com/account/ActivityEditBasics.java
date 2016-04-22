package com.zgcar.com.account;

import java.io.File;
import java.util.List;

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
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.entity.RequestCode;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.CircleImageView;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.UpLoadFile;
import com.zgcar.com.util.Util;

/**
 * 编辑个人资料
 * 
 */
public class ActivityEditBasics extends Activity implements OnClickListener {

	private CircleImageView icon;
	private int sexFlag;
	private PopupWindow popWindow;
	private MyApplication app;
	private EditText content;
	private TextView phoneNoTv, birthdayTv, sexTv, nickNameTv, heightTv,
			weightTv;
	private List<TerminalListInfos> infos;
	private int position;
	private int imageFlag;
	private File imageFileOld;
	private File imageFileTemp;
	private File imageFileNew;
	private SharedPreferences sf;
	private Dialog dialog;
	private String url;
	private boolean hasImage;
	private RadioGroup group;
	/**
	 * 拍照时返回的临时头像
	 */
	private Bitmap bitmapTempIcon;
	private int type;

	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		setContentView(R.layout.activity_edit_basics);
		initialize();
	}

	private void initView() {
		infos = ListInfosEntity.getTerminalListInfos();
		position = app.getPosition();
		url = "http://" + FinalVariableLibrary.ip + ":"
				+ FinalVariableLibrary.DSTPORT0 + "/" + app.getImei()
				+ "/image/up?" + app.getImei() + ".jpg";
		if (ListInfosEntity.getPathList() != null) {
			int width = (int) Util.dip2px(ActivityEditBasics.this, 50);
			bitmapTempIcon = Util.decodeSampledBitmapFromResource(
					ListInfosEntity.getPathList().get(position), width, width);
			if (bitmapTempIcon != null) {
				icon.setImageBitmap(bitmapTempIcon);
			}
		}
		imageFlag = infos.get(position).getImage_flag();
		imageFileOld = new File(sf.getString(app.getImei() + "image", ""));
		Log.e("birthdayTv", infos.get(position).getBirthday());
		if (null == infos.get(position).getBirthday()
				|| infos.get(position).getBirthday().equals("")) {
			birthdayTv.setText(Util.getTodayDate());
		} else {
			birthdayTv.setText(infos.get(position).getBirthday());
		}
		heightTv.setText(infos.get(position).getHeight() + "");
		weightTv.setText(infos.get(position).getWeight() + "");
		nickNameTv.setText(infos.get(position).getName());
		phoneNoTv.setText(infos.get(position).getNumber());
		sexFlag = infos.get(position).getSex();
		if (sexFlag == 0) {
			sexTv.setText(R.string.man);
		} else if (sexFlag == 1) {
			sexTv.setText(R.string.woman);
		}
	}

	private void initialize() {
		app = (MyApplication) getApplication();
		sf = getSharedPreferences(FinalVariableLibrary.CACHE_FOLDER,
				MODE_PRIVATE);
		icon = (CircleImageView) findViewById(R.id.edit_basics_icon);
		birthdayTv = (TextView) findViewById(R.id.edit_basics_type_tv);
		sexTv = (TextView) findViewById(R.id.edit_basics_sex_textview);
		phoneNoTv = (TextView) findViewById(R.id.edit_basics_phone_no_textview);
		nickNameTv = (TextView) findViewById(R.id.edit_basics_car_name_tv);
		heightTv = (TextView) findViewById(R.id.edit_basics_set_car_color_tv);
		weightTv = (TextView) findViewById(R.id.edit_basics_weight_textview);
		LinearLayout setPhoneNo = (LinearLayout) findViewById(R.id.edit_basics_phone_no);
		ImageButton camera = (ImageButton) findViewById(R.id.edit_basics_camera);
		Button save = (Button) findViewById(R.id.edit_basics_save);
		ImageButton back = (ImageButton) findViewById(R.id.edit_basics_back);
		LinearLayout nickName = (LinearLayout) findViewById(R.id.edit_basics_car_name);
		LinearLayout setCarNo = (LinearLayout) findViewById(R.id.edit_basics_set_car_no);
		LinearLayout setBirthday = (LinearLayout) findViewById(R.id.edit_basics_set_type);
		LinearLayout setHeight = (LinearLayout) findViewById(R.id.edit_basics_set_car_color);
		LinearLayout setWeight = (LinearLayout) findViewById(R.id.edit_basics_set_weight);
		setHeight.setOnClickListener(this);
		save.setOnClickListener(this);
		setPhoneNo.setOnClickListener(this);
		setWeight.setOnClickListener(this);
		setCarNo.setOnClickListener(this);
		camera.setOnClickListener(this);
		back.setOnClickListener(this);
		setBirthday.setOnClickListener(this);
		nickName.setOnClickListener(this);
		hasImage = false;
		initView();
	}

	private void popWindowDismiss() {
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
			popWindow = null;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		popWindowDismiss();
		return super.onTouchEvent(event);
	}

	private void getBack() {
		if (dialog == null) {
			dialog = new Dialog(ActivityEditBasics.this, R.style.dialog);
		}
		View view2 = View.inflate(ActivityEditBasics.this,
				R.layout.view_dialog_yes_or_not, null);
		Button yes = (Button) view2.findViewById(R.id.view_dialog_yes);
		Button no = (Button) view2.findViewById(R.id.view_dialog_no);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		dialog.setContentView(view2);
		dialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			getBack();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.edit_basics_car_name:
			showEditDialog(1);
			break;
		case R.id.edit_basics_set_car_color:
			showEditDialog(2);
			break;
		case R.id.edit_basics_set_weight:
			showEditDialog(3);
			break;
		case R.id.edit_basics_phone_no:
			showEditDialog(4);
			break;
		case R.id.edit_basics_set_car_no:

			break;
		case R.id.edit_basics_set_type:

			break;
		case R.id.edit_basics_camera:
			showSelectIconDialog();
			break;
		case R.id.edit_basics_back:
			getBack();
			break;
		case R.id.edit_basics_save:
			showProgressDialog();
			upLoadingImage();
			break;

		case R.id.view_dialog_yes:
			closeDoalog();
			finish();
			break;
		case R.id.view_dialog_no:
			closeDoalog();
			break;
		case R.id.view_select_picture_1:
			closeDoalog();
			camera();
			break;
		case R.id.view_select_picture_2:
			closeDoalog();
			selectIconFromPhone();
			break;
		case R.id.view_select_picture_cancle:
			closeDoalog();
			break;
		case R.id.view_dialog_negative_button:
			closeDoalog();
			break;
		case R.id.view_dialog_positive_button:
			getEditContent(type);
			break;
		// sexButton
		case R.id.view_select_sex_bt_positive:
			closeDoalog();
			if (group.getCheckedRadioButtonId() == R.id.view_select_sex_rb1 ? true
					: false) {
				sexFlag = 0;
				sexTv.setText(getString(R.string.man));
			} else {
				sexFlag = 1;
				sexTv.setText(getString(R.string.woman));
			}
			break;
		case R.id.view_select_sex_bt_negative:
			closeDoalog();

			break;
		// 生日
		case R.id.popwindow_birthday_positive:
			break;
		case R.id.popwindow_birthday_negative:
			break;
		default:
			break;
		}

	}

	private void showProgressDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityEditBasics.this, R.style.dialog);
		}
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
	}

	private void closeDoalog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void showEditDialog(int type) {
		this.type = type;
		if (dialog == null) {
			dialog = new Dialog(ActivityEditBasics.this, R.style.dialog);
		}
		View view = View.inflate(ActivityEditBasics.this,
				R.layout.view_edit_info, null);
		TextView title = (TextView) view
				.findViewById(R.id.view_dialog_textview);
		Button negative = (Button) view
				.findViewById(R.id.view_dialog_negative_button);
		Button positive = (Button) view
				.findViewById(R.id.view_dialog_positive_button);
		content = (EditText) view.findViewById(R.id.view_dialog_editteext);
		negative.setOnClickListener(this);
		positive.setOnClickListener(this);
		dialog.setContentView(view);
		switch (type) {
		case 1:
			title.setText(R.string.change_nick_name);
			content.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL);
			content.setHint(R.string.please_input_nick_name);
			content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					8) });
			content.setHint(nickNameTv.getText().toString());
			content.setText("");
			dialog.show();
			break;
		case 2:
			title.setText(R.string.change_height);
			content.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_VARIATION_NORMAL);
			content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					4) });
			content.setHint(R.string.please_input_height);
			content.setHint(heightTv.getText().toString());
			content.setText("");
			dialog.show();
			break;
		case 3:

			title.setText(R.string.change_weight);
			content.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_VARIATION_NORMAL);
			content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					4) });
			content.setHint(R.string.please_input_weight);
			content.setHint(weightTv.getText().toString());
			content.setText("");
			dialog.show();
			break;
		case 4:
			title.setText(R.string.change_phone_no);
			content.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_VARIATION_NORMAL);
			content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					20) });
			content.setHint(R.string.please_input_phone_no);
			content.setHint(phoneNoTv.getText().toString());
			content.setText("");
			dialog.show();
			break;

		default:
			break;
		}

	}

	private void showSelectIconDialog() {
		if (dialog == null) {
			dialog = new Dialog(ActivityEditBasics.this, R.style.dialog);
		}
		View view = View.inflate(ActivityEditBasics.this,
				R.layout.view_select_picture, null);
		Button one = (Button) view.findViewById(R.id.view_select_picture_1);
		one.setOnClickListener(this);
		Button two = (Button) view.findViewById(R.id.view_select_picture_2);
		two.setOnClickListener(this);
		Button cancle = (Button) view
				.findViewById(R.id.view_select_picture_cancle);
		cancle.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}

	private void selectIconFromPhone() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, RequestCode.PHONE_ALBUM);
	}

	/**
	 * 调用摄像机
	 */
	private void camera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		app = (MyApplication) getApplication();
		imageFileTemp = new File(FinalVariableLibrary.PATHS + "/" + "image",
				System.currentTimeMillis() + ".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFileTemp));
		startActivityForResult(intent, RequestCode.PHONE_CAMERA);
	}

	/**
	 * 
	 * 裁剪成正方形图片后进行显示
	 * 
	 * @param data
	 */
	private void showImageview(Intent data) {
		if (bitmapTempIcon != null) {
			bitmapTempIcon.recycle();
		}
		bitmapTempIcon = data.getParcelableExtra("data");
		imageFileNew = new File(FinalVariableLibrary.PATHS + "/image/"
				+ System.currentTimeMillis() + ".jpg");
		boolean flag = Util.saveBitmapToSDCard(bitmapTempIcon,
				imageFileNew.getPath());
		if (imageFileTemp != null) {
			imageFileTemp.delete();
		}
		if (flag) {
			hasImage = true;
			icon.setImageBitmap(bitmapTempIcon);
		} else {
			Util.showToastBottom(ActivityEditBasics.this,
					getString(R.string.get_pocture_failed));
		}
	}

	/**
	 * 调用摄像头成功后，对图片进行裁剪成正方形
	 */
	private void startCutImage(Uri uri) {
		imageFileNew = new File(FinalVariableLibrary.PATHS + "/image/"
				+ System.currentTimeMillis() + ".jpg");
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
			if (imageFileNew.exists()) {
				imageFileNew.delete();
			}
			Util.showToastBottom(ActivityEditBasics.this,
					getString(R.string.get_pocture_failed));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCode.PHONE_CAMERA
				&& resultCode == Activity.RESULT_OK) {
			startCutImage(Uri.fromFile(imageFileTemp));
		} else if (requestCode == RequestCode.PHONE_ALBUM
				&& resultCode == Activity.RESULT_OK) {
			startCutImage(data.getData());
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode == Activity.RESULT_OK) {
			showImageview(data);
		} else if (requestCode == RequestCode.PHONE_CUT_OUT
				&& resultCode != Activity.RESULT_OK) {
			if (imageFileTemp != null) {
				imageFileTemp.delete();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 */
	private void connectService() {
		Log.e("handler", "handler");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonStr = getRequestJsonstr();
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					if (hasImage) {
						if (imageFileOld.exists()) {
							imageFileOld.delete();
						}
						ListInfosEntity.getPathList().set(position,
								imageFileNew.getPath());
						Editor editor = sf.edit();
						editor.putString(app.getImei() + "image",
								imageFileNew.getPath());
						editor.putInt(app.getImei() + "imageFlag", imageFlag);
						editor.commit();
					}
					changeListInfos();
					Looper.prepare();
					Util.showToastBottom(ActivityEditBasics.this,
							getString(R.string.save_succeed));
					closeDoalog();
					finish();
					Looper.loop();
					return;
				} else {
					Looper.prepare();
					closeDoalog();
					imageFileNew.delete();
					Util.showToastBottom(ActivityEditBasics.this,
							SocketUtil.isFail(ActivityEditBasics.this));
					Looper.loop();
					return;
				}

			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.e("handler", "handler");
				connectService();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 上传图片
	 */
	private synchronized void upLoadingImage() {
		if (hasImage) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean flag = UpLoadFile.toUploadFile(imageFileNew, url);
					if (flag) {
						imageFlag = (imageFlag + 1) % 3 + 1;
						handler.sendMessage(handler.obtainMessage(1));
						Looper.prepare();
						Util.showToastBottom(ActivityEditBasics.this,
								getString(R.string.upload_picture_succeed));
						Looper.loop();
						return;
					} else {
						handler.sendMessage(handler.obtainMessage(1));
						Looper.prepare();
						Util.showToastBottom(ActivityEditBasics.this,
								getString(R.string.upload_picture_failed));
						Looper.loop();
						return;
					}
				}
			}).start();
		}
		handler.sendMessage(handler.obtainMessage(1));
	}

	/**
	 * 对本地信息进行更新
	 */
	private void changeListInfos() {
		infos.get(position).setBirthday(birthdayTv.getText().toString());
		infos.get(position).setSex(sexFlag);
		infos.get(position).setNumber(phoneNoTv.getText().toString());
		infos.get(position).setName(nickNameTv.getText().toString());
		infos.get(position).setWeight(
				Integer.parseInt(weightTv.getText().toString().trim()));
		infos.get(position).setHeight(
				Integer.parseInt(heightTv.getText().toString().trim()));
		infos.get(position).setImage_flag(imageFlag);
		Log.e("修改资料后", infos.get(position).toString());
	}

	private String getRequestJsonstr() {
		Log.e("修改资料前", infos.get(position).toString());
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", FinalVariableLibrary.EDIT_WATCH_BASIC);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("username", app.getUserName());
			object1.put("imei", app.getImei());
			object1.put("age", "2");
			object1.put("name", nickNameTv.getText().toString().trim());
			object1.put("number", phoneNoTv.getText().toString().trim());
			object1.put("height", heightTv.getText().toString().trim());
			object1.put("weight", weightTv.getText().toString().trim());
			object1.put("sex", sexFlag);
			object1.put("birthday", birthdayTv.getText().toString().trim());
			object1.put("image_flag", imageFlag);
			array.put(object1);
			object.put("data", array);
			Log.e("jsonStr", object.toString());
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	private void getEditContent(int flag) {
		String cache = content.getText().toString().trim();
		if (cache.equals("")) {
			Util.showToastBottom(ActivityEditBasics.this,
					getString(R.string.input_content_can_not_null));
			return;
		}
		switch (flag) {
		// 更改昵称
		case 1:
			nickNameTv.setText(cache);
			closeDoalog();
			content.setText("");
			break;
		// 更改身高
		case 2:
			heightTv.setText(cache);
			closeDoalog();
			content.setText("");
			break;
		// 更改体重
		case 3:
			weightTv.setText(cache);
			closeDoalog();
			content.setText("");
			break;
		case 4:
			phoneNoTv.setText(cache);
			closeDoalog();
			content.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		if (bitmapTempIcon != null) {
			bitmapTempIcon.recycle();
			bitmapTempIcon = null;
		}
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

}
