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
 * AMapV2��ͼ�м򵥽���poisearch����
 */
public class ActivitySafetySearch1 extends Activity implements TextWatcher,
		OnPoiSearchListener, OnClickListener {
	private AutoCompleteTextView searchText;// ���������ؼ���
	private String keyWord = "";// Ҫ�����poi�����ؼ���
	private ProgressDialog progDialog = null;// ����ʱ������
	private PoiResult poiResult; // poi���صĽ��
	private int currentPage = 0;// ��ǰҳ�棬��0��ʼ����
	private PoiSearch.Query query;// Poi��ѯ������
	private PoiSearch poiSearch;// POI����
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
		 * �������ߵ�ͼ�洢Ŀ¼�����������ߵ�ͼ���ʼ����ͼ����; ʹ�ù����п���������, ���������������ߵ�ͼ�洢��·����
		 * ����Ҫ�����ߵ�ͼ���غ�ʹ�õ�ͼҳ�涼����·������
		 */
		// Demo��Ϊ�������������ʹ�����ص����ߵ�ͼ��ʹ��Ĭ��λ�ô洢���������Զ�������
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		init();
	}

	/**
	 * ����ҳ�����
	 */
	private void init() {
		Button searButton = (Button) findViewById(R.id.safety_search_start);
		ImageButton back = (ImageButton) findViewById(R.id.safety_search_back);
		back.setOnClickListener(this);
		searButton.setOnClickListener(this);
		searchText = (AutoCompleteTextView) findViewById(R.id.safety_search_key_word_tv);
		searchText.addTextChangedListener(this);// ����ı����������¼�
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
	 * ���������ť
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
	 * �����һҳ��ť
	 */
	public void nextButton() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > currentPage) {
				currentPage++;
				query.setPageNum(currentPage);// ���ò��һҳ
				poiSearch.searchPOIAsyn();
			} else {
				Util.showToastBottom(ActivitySafetySearch1.this,
						getString(R.string.no_result));
			}
		}
	}

	/**
	 * ��ʾ���ȿ�
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
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * ��ʼ����poi����
	 */
	protected void doSearchQuery() {
		showProgressDialog();// ��ʾ���ȿ�
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "");// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(10);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ
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
						if (rCode == 1000) {// ��ȷ����
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
		// ��һ��������ʾ��ʾ�ؼ��֣��ڶ�������Ĭ�ϴ���ȫ����Ҳ����Ϊ��������
		 } catch (AMapException e) {
		 e.printStackTrace();
		 }
	}

	/**
	 * POI�����ѯ�ص�����
	 */

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// ���ضԻ���
		if (rCode == 1000) {
			if (result != null && result.getQuery() != null) {// ����poi�Ľ��
				if (result.getQuery().equals(query)) {// �Ƿ���ͬһ��
					poiResult = result;
					// ȡ����������poiitems�ж���ҳ
					poiItems = poiResult.getPois();// ȡ�õ�һҳ��poiitem���ݣ�ҳ��������0��ʼ

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
