package com.zgcar.com.account;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.ShareItem;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.util.GetScreenShotImage;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 
 * 扫描二维码
 * 
 */
public class ActivityQrCode extends Activity implements OnClickListener {
	private ImageView qr;
	private TextView imeiDesc;
	private Bitmap bitmap;
	private Dialog dialog;
	private PopupWindow popupWindow;
	private List<ShareItem> mListData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_code);
		initialize();
	}

	private void initialize() {
		ImageButton back = (ImageButton) findViewById(R.id.qr_code_back);
		back.setOnClickListener(this);
		ImageButton share = (ImageButton) findViewById(R.id.qr_code_share);
		share.setOnClickListener(this);
		qr = (ImageView) findViewById(R.id.qrcode_image);
		imeiDesc = (TextView) findViewById(R.id.qr_code_imei_desc);
		mListData = new ArrayList<ShareItem>();
		mListData.add(new ShareItem(getString(R.string.wechat),
				"com.tencent.mm.ui.tools.ShareImgUI", "com.tencent.mm"));
		mListData.add(new ShareItem("qq",
				"com.tencent.mobileqq.activity.JumpActivity",
				"com.tencent.mobileqq"));
		createQr();
	}

	private void createQr() {
		MyApplication app = (MyApplication) getApplication();
		String url = "http://www.3galarm.cn/app_v2/?ZG:" + app.getImei();
		imeiDesc.setText(getResources().getString(R.string.imei_desc)
				+ app.getImei());
		bitmap = Util.createQRImage(url, 750, 750);
		if (bitmap == null) {
			Util.showToastBottom(ActivityQrCode.this,
					getString(R.string.get_qr_code_failed));
			finish();
			return;
		}
		qr.setImageBitmap(bitmap);
	}

	@Override
	protected void onDestroy() {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qr_code_back:
			finish();
			break;
		case R.id.qr_code_share:
			popWindow();
			break;
		case R.id.share_to_other_weixin:
			dismissPopupWindow();
			showProgressDialog();
			shareMsg("", "", mListData.get(0));
			break;
		case R.id.share_to_other_qq:
			dismissPopupWindow();
			showProgressDialog();
			shareMsg("", "", mListData.get(1));
			break;
		case R.id.view_share_cancle:
			dismissPopupWindow();
			break;

		default:
			break;
		}
	}

	/**
	 * 分享时弹出的popupWindow
	 */
	@SuppressWarnings("deprecation")
	private void popWindow() {
		final View view2 = View.inflate(ActivityQrCode.this,
				R.layout.activity_qr_code, null);
		if (popupWindow == null) {
			View view = View.inflate(ActivityQrCode.this,
					R.layout.view_share_to_others, null);
			ImageButton shareWeixin = (ImageButton) view
					.findViewById(R.id.share_to_other_weixin);
			ImageButton shareQq = (ImageButton) view
					.findViewById(R.id.share_to_other_qq);
			Button cancle = (Button) view.findViewById(R.id.view_share_cancle);
			shareWeixin.setOnClickListener(this);
			shareQq.setOnClickListener(this);
			cancle.setOnClickListener(this);
			popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					WindowManager.LayoutParams params = getWindow()
							.getAttributes();
					params.alpha = (float) 1;
					getWindow().setAttributes(params);
				}
			});
		}
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = (float) 0.5;
		getWindow().setAttributes(params);
		popupWindow.showAtLocation(view2, Gravity.BOTTOM, 0, 0);
	}

	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	private void showProgressDialog() {
		dialog = getDialog();
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(true);
		dialog.show();
	}

	public Dialog getDialog() {
		return dialog == null ? new Dialog(ActivityQrCode.this, R.style.dialog)
				: dialog;
	}

	/**
	 * 
	 * 分享
	 * 
	 * @param msgTitle
	 * @param msgText
	 * @param share
	 */
	private void shareMsg(final String msgTitle, final String msgText,
			final ShareItem share) {
		if (!share.packageName.isEmpty()
				&& !Util.isAvilible(ActivityQrCode.this, share.packageName)) {
			Util.showToastBottom(ActivityQrCode.this,
					getString(R.string.please_install_first) + share.title);
			dismissDialog();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				GetScreenShotImage screenShot = new GetScreenShotImage(
						ActivityQrCode.this);
				String imgPath = screenShot.getShareImagePath();
				if (imgPath.equals("")) {
					Looper.prepare();
					dismissDialog();
					Util.showToastBottom(ActivityQrCode.this,
							getString(R.string.screenshots_sharing_failure));
					Looper.loop();
					return;
				}
				Intent intent = new Intent("android.intent.action.SEND");
				File f = new File(imgPath);
				if ((f != null) && (f.exists()) && (f.isFile())) {
					intent.setType("image/png");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
				}
				intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
				intent.putExtra(Intent.EXTRA_TEXT, msgText);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (!share.packageName.isEmpty()) {
					dismissDialog();
					intent.setComponent(new ComponentName(share.packageName,
							share.activityName));
					startActivity(intent);
				} else {
					dismissDialog();
					startActivity(Intent.createChooser(intent, msgTitle));
				}
			}
		}).start();

	}

	private void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
