package com.zgcar.com.account;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.zgcar.com.R;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * AMapV2地图中简单介绍poisearch搜索
 */
public class ActivitySafetySearch1 extends Activity implements TextWatcher,
		OnPoiSearchListener, OnClickListener {
	private AutoCompleteTextView searchText;// 输入搜索关键字
	private String keyWord = "";// 要输入的poi搜索关键字
	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private ListView listview;
	private BaseAdapter adapter;
	private List<PoiItem> poiItems;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Util.showToastBottom(ActivitySafetySearch1.this,
						getString(R.string.no_result));
				break;
			case 0:
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivitySafetySearch1.this,
				R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety_search);
		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		init();
	}

	/**
	 * 设置页面监听
	 */
	private void init() {
		Button searButton = (Button) findViewById(R.id.safety_search_start);
		ImageButton back = (ImageButton) findViewById(R.id.safety_search_back);
		back.setOnClickListener(this);
		searButton.setOnClickListener(this);
		searchText = (AutoCompleteTextView) findViewById(R.id.safety_search_key_word_tv);
		searchText.addTextChangedListener(this);// 添加文本输入框监听事件
		listview = (ListView) findViewById(R.id.safety_search_lisview);
		poiItems = new ArrayList<PoiItem>();
		initView();
	}

	private void initView() {
		adapter = new BaseAdapter() {
			@Override
			public View getView(int arg0, View view, ViewGroup arg2) {

				view = LayoutInflater.from(ActivitySafetySearch1.this).inflate(
						R.layout.adapter_history_search_textview, arg2, false);

				TextView tv = (TextView) view
						.findViewById(R.id.adapter_history_textview);
				tv.setText(poiItems.get(arg0).getTitle());
				return view;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				return poiItems.get(arg0);
			}

			@Override
			public int getCount() {
				return poiItems.size();
			}
		};
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String address = poiItems.get(arg2).getProvinceName()
						+ poiItems.get(arg2).getCityName()
						+ poiItems.get(arg2).getAdName()
						+ poiItems.get(arg2).getTitle()
						+ poiItems.get(arg2).getSnippet();
				Bundle bundle = new Bundle();
				bundle.putDouble("la", poiItems.get(arg2).getLatLonPoint()
						.getLatitude());
				bundle.putDouble("lo", poiItems.get(arg2).getLatLonPoint()
						.getLongitude());
				bundle.putString("address", address);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(0, intent);
				finish();
			}
		});
	}

	/**
	 * 点击搜索按钮
	 */
	public void searchButton() {
		keyWord = searchText.getText().toString().trim();
		if ("".equals(keyWord)) {
			Util.showToastBottom(ActivitySafetySearch1.this,
					getString(R.string.keyword));
			return;
		} else {
			doSearchQuery();
		}
	}

	/**
	 * 点击下一页按钮
	 */
	public void nextButton() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;
				query.setPageNum(currentPage);// 设置查后一页
				poiSearch.searchPOIAsyn();
			} else {
				Util.showToastBottom(ActivitySafetySearch1.this,
						getString(R.string.no_result));
			}
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage(getString(R.string.searching) + "\n" + keyWord);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog();// 显示进度框
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		Log.e("ActivitySafetySearch1", "onTextChanged");
		String newText = s.toString().trim();
		Inputtips inputTips = new Inputtips(ActivitySafetySearch1.this,
				new InputtipsListener() {
					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						Log.e("ActivitySafetySearch1", "InputtipsListener:"+rCode);
						if (rCode == 1000) {// 正确返回
							List<String> listString = new ArrayList<String>();
							for (int i = 0; i < tipList.size(); i++) {
								listString.add(tipList.get(i).getName());

							}
							ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.route_inputs, listString);
							searchText.setAdapter(aAdapter);
							aAdapter.notifyDataSetChanged();
						}
					}
				});
		 try {
			 //InputtipsQuery query = new InputtipsQuery(newText, "");
			 //	inputTips.setQuery(query);
		 inputTips.requestInputtips(newText, "");//
		// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
		 } catch (AMapException e) {
		 e.printStackTrace();
		 }
	}

	/**
	 * POI详情查询回调方法
	 */

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 1000) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

					if (poiItems != null && poiItems.size() > 0) {
						handler.sendMessage(handler.obtainMessage(0));
					} else {
						Util.showToastBottom(ActivitySafetySearch1.this,
								getString(R.string.no_result));
					}
				}
			} else {
				Util.showToastBottom(ActivitySafetySearch1.this,
						getString(R.string.no_result));
			}
		} else if (rCode == 27) {
			Util.showToastBottom(ActivitySafetySearch1.this,
					getString(R.string.error_network));
		} else {
			Util.showToastBottom(ActivitySafetySearch1.this,
					getString(R.string.information_is_null));
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.safety_search_back:
			setResult(-1);
			finish();
			break;
		case R.id.safety_search_start:
			searchButton();
			break;
		// case R.id.nextButton:
		// nextButton();
		// break;
		default:
			break;
		}
	}

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		

	}
}
