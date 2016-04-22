package com.zgcar.com.account;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.Entity.SafetySearchInfos;
import com.zgcar.com.util.SetTitleBackground;

public class ActivitySafetySearch2 extends Activity implements OnClickListener {
	private AutoCompleteTextView searchText;
	private ListView listview;
	private MyAdapter adapter;
	private List<SafetySearchInfos> list;
	private String keyWord;
	private WebView webView;
	private Dialog dialog;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivitySafetySearch2.this,
				R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety_search);
		init();
	}

	/**
	 * 设置页面监听
	 */
	private void init() {
		dialog = new Dialog(ActivitySafetySearch2.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
		handler = new Handler();
		list = new ArrayList<SafetySearchInfos>();
		Button searButton = (Button) findViewById(R.id.safety_search_start);
		ImageButton back = (ImageButton) findViewById(R.id.safety_search_back);
		back.setOnClickListener(this);
		searButton.setOnClickListener(this);
		searchText = (AutoCompleteTextView) findViewById(R.id.safety_search_key_word_tv);
		listview = (ListView) findViewById(R.id.safety_search_lisview);
		adapter = new MyAdapter(ActivitySafetySearch2.this);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent();
				intent.putExtra("lo", list.get(position).getLo());
				intent.putExtra("la", list.get(position).getLa());
				setResult(0, intent);
				finish();
			}
		});
		initWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webView = new WebView(ActivitySafetySearch2.this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.addJavascriptInterface(getHtmlObject(), "jsobj");
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				dialog.dismiss();
			}
		});
		webView.loadUrl(FinalVariableLibrary.WEB_HOST_SEARCH_ADDRESS);
	}

	/**
	 * 点击搜索按钮
	 */
	public void searchButton() {
		keyWord = searchText.getText().toString().trim();
		if ("".equals(keyWord)) {
			Toast.makeText(ActivitySafetySearch2.this, "请输入搜索关键字",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			String avaStr3 = String.format("javascript:ShowAddress('%s');",
					keyWord);
			webView.loadUrl(avaStr3);
			Log.e("String", avaStr3.toString());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(-1);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Button点击事件回调方法
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.safety_search_back:
			setResult(-1);
			finish();
			break;
		case R.id.safety_search_start:
			dialog.show();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					dialog.dismiss();
				}
			}, 3 * 1000);
			list.clear();
			adapter.notifyDataSetChanged();
			searchButton();
			break;
		default:
			break;
		}
	}

	private class MyAdapter extends BaseAdapter {
		private Context context;

		public MyAdapter(Context context) {
			this.context = context;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = View.inflate(context,
						R.layout.adapter_history_search_textview, null);
				viewHolder.tv = (TextView) view
						.findViewById(R.id.adapter_history_textview);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.tv.setText(list.get(position).getAddress());
			return view;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}

	};

	private static class ViewHolder {
		private TextView tv;
	}

	private Object getHtmlObject() {
		Object insertObj = new Object() {
			@SuppressWarnings("unused")
			public void reciverList(final String jsList) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						handler.removeCallbacksAndMessages(null);
						getSearchInfos(jsList);
					}
				});
			}
		};
		return insertObj;
	}

	private void getSearchInfos(String jsList) {
		Log.e("sousuo", jsList);
		try {
			JSONArray array = new JSONArray(jsList);
			List<JSONObject> listJsonObjects = new ArrayList<JSONObject>();
			for (int i = 0; i < array.length(); i++) {
				listJsonObjects.add(array.getJSONObject(i));
			}
			for (int i = 0; i < listJsonObjects.size(); i++) {
				SafetySearchInfos ingo = new SafetySearchInfos();
				ingo.setAddress(listJsonObjects.get(i).getString(
						"formatted_address"));
				JSONObject jsonObject = listJsonObjects.get(i)
						.getJSONObject("geometry").getJSONObject("location");
				ingo.setLa(jsonObject.getDouble("lat"));
				ingo.setLo(jsonObject.getDouble("lng"));
				list.add(ingo);
				adapter.notifyDataSetChanged();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
