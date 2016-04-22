package com.zgcar.com.history;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.main.model.NotifyMessageEntity;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;

public class ActivitySosMeage2 extends Activity implements OnClickListener {

	private NotifyMessageEntity info;
	private WebView webView;
	private TextView title, suggestionTv, timeTv, addressTv;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sos_message);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		dialog = new Dialog(ActivitySosMeage2.this, R.style.dialog);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.show();
		title = (TextView) findViewById(R.id.sos_message_title);
		suggestionTv = (TextView) findViewById(R.id.sos_message_suggest);
		timeTv = (TextView) findViewById(R.id.sos_message_time);
		addressTv = (TextView) findViewById(R.id.sos_message_address);
		info = (NotifyMessageEntity) getIntent().getSerializableExtra(
				"NotifyMessageEntity");
		webView = (WebView) findViewById(R.id.sos_message_webview);
		webView.setVisibility(View.VISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		ImageButton back = (ImageButton) findViewById(R.id.sos_message_back);
		back.setOnClickListener(this);
		initView();
	}

	private void initView() {
		title.setText(info.getLitle());
		suggestionTv.setText(info.getMsg());
		timeTv.setText(info.getTime());
		addressTv.setText(info.getGeo());
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				String data = String
						.format("javascript:DrawPos('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
								0, info.getLo(), info.getLa(), "locaion", "0",
								"", "0");
				webView.loadUrl(data);
				String javaCenter = String.format(
						"javascript:MapCenter('%s','%s','%s');", info.getLo(),
						info.getLa(), "13");
				webView.loadUrl(javaCenter);
				dialog.dismiss();
				super.onPageFinished(view, url);
			}
		});
		webView.loadUrl(FinalVariableLibrary.WEB_HOST_LOCATION1);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.sos_message_back:
			finish();
			break;

		default:
			break;
		}
	}

}