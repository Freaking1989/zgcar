package com.zgcar.com.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.R;

/**
 * 导航页面（包括路径规划，导航。）
 * */
public class ActivityNaviRoute2 extends Activity implements OnClickListener {
	private boolean isDrive;
	private WebView webView;
	private MyApplication app;
	private Dialog dialog;

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		setContentView(R.layout.activity_nav_simple_route);
		initialize();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initialize() {
		dialog = new Dialog(ActivityNaviRoute2.this, R.style.dialog);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
		app = (MyApplication) getApplication();
		isDrive = getIntent().getBooleanExtra("isDrive", false);
		ImageButton back = (ImageButton) findViewById(R.id.nav_simple_route_back);
		back.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.nav_simple_route_title);
		if (isDrive) {
			title.setText(getString(R.string.navi_empty_car));
		} else {
			title.setText(getString(R.string.navi_empty_foot));
		}
		webView = (WebView) findViewById(R.id.nav_route_webview);
		webView.setVisibility(View.VISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.e("ActivityNaviRoute2", isDrive + "");
				if (isDrive) {
					doMapDir(app.getTempMyLocation().getLongitude(), app
							.getTempMyLocation().getLatitude(), app
							.getTempWatchLocation().getLo(), app
							.getTempWatchLocation().getLa(), 1);
				} else {
					doMapDir(app.getTempMyLocation().getLongitude(), app
							.getTempMyLocation().getLatitude(), app
							.getTempWatchLocation().getLo(), app
							.getTempWatchLocation().getLa(), 2);
				}
				dialog.dismiss();
				super.onPageFinished(view, url);
			}
		});
		webView.loadUrl(FinalVariableLibrary.WEB_HOST_LOCATION1);
	}

	/**
	 * @param from_lo
	 * @param from_la
	 * @param to_lo
	 * @param to_la
	 * @param mode
	 *            交通方式 0:公交 1:自驾 2:步行
	 */
	public void doMapDir(final double from_lo, final double from_la,
			final double to_lo, final double to_la, final int mode) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String javaScriptCmdString = String.format(
						"javascript:calculateAndDisplayRoute(%s,%s,%s,%s,%d);",
						from_lo, from_la, to_lo, to_la, mode);
				webView.loadUrl(javaScriptCmdString);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.nav_simple_route_back:
			finish();
			break;

		default:
			break;
		}

	}
}
