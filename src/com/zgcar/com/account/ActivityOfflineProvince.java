package com.zgcar.com.account;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapView;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.zgcar.com.R;
import com.zgcar.com.util.Quit;
import com.zgcar.com.util.SetTitleBackground;

/**
 * AMapV2��ͼ�м򵥽������ߵ�ͼ����
 */
public class ActivityOfflineProvince extends Activity implements
		OfflineMapDownloadListener, OnClickListener {
	private OfflineMapManager amapManager = null;// ���ߵ�ͼ���ؿ�����
	private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// ����һ��Ŀ¼��ʡֱϽ��
	private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<Object, List<OfflineMapCity>>();// �������Ŀ¼����
	private int groupPosition;// ��¼һ��Ŀ¼��position
	private int childPosition;// ��¼����Ŀ¼��position
	private int completeCode;// ��¼���ر���
	private boolean isStart = false;// �ж��Ƿ�ʼ����,true��ʾ��ʼ���أ�false��ʾ����ʧ��
	private boolean[] isOpen;// ��¼һ��Ŀ¼�Ƿ��
	private String cityName;
	private MapView mapView;
	private ExpandableListView expandableListView;

	protected void onCreate(Bundle savedInstanceState) {
		Quit.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(this, R.color.color_4);
		super.onCreate(savedInstanceState);
		/*
		 * �������ߵ�ͼ�洢Ŀ¼�����������ߵ�ͼ���ʼ����ͼ����; ʹ�ù����п���������, ���������������ߵ�ͼ�洢��·����
		 * ����Ҫ�����ߵ�ͼ���غ�ʹ�õ�ͼҳ�涼����·������
		 */
		// Demo��Ϊ�������������ʹ�����ص����ߵ�ͼ��ʹ��Ĭ��λ�ô洢���������Զ�������
		// MapsInitialihenger.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		setContentView(R.layout.activity_offline_province);
		init();
	}

	@Override
	protected void onResume() {
		getInfoThread();
		super.onResume();
	}

	private void getInfoThread() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				provinceList = amapManager.getOfflineMapProvinceList();
				List<OfflineMapProvince> bigCityList = new ArrayList<OfflineMapProvince>();// ��ʡ��ʽ����ֱϽ�С��۰ġ�ȫ����Ҫͼ
				List<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// ���и�ʽ����ֱϽ�С��۰ġ�ȫ����Ҫͼ
				List<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// ����۰ĳ���
				List<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// �����Ҫͼ
				for (int i = 0; i < provinceList.size(); i++) {

					OfflineMapProvince offlineMapProvince = provinceList.get(i);
					List<OfflineMapCity> city = new ArrayList<OfflineMapCity>();
					OfflineMapCity aMapCity = getCicy(offlineMapProvince);
					if (offlineMapProvince.getCityList().size() != 1) {
						city.add(aMapCity);
						city.addAll(offlineMapProvince.getCityList());
					} else {
						cityList.add(aMapCity);
						bigCityList.add(offlineMapProvince);
					}
					cityMap.put(i + 3, city);
				}
				OfflineMapProvince title = new OfflineMapProvince();
				title.setProvinceName(getString(R.string.map_schematic));
				provinceList.add(0, title);
				title = new OfflineMapProvince();
				title.setProvinceName(getString(R.string.map_zhixiashi));
				provinceList.add(1, title);
				title = new OfflineMapProvince();
				title.setProvinceName(getString(R.string.map_hong_kong_and_macao));
				provinceList.add(2, title);
				provinceList.removeAll(bigCityList);

				for (OfflineMapProvince aMapProvince : bigCityList) {
					if (aMapProvince.getProvinceName().contains(
							getString(R.string.map_hong_kong))
							|| aMapProvince.getProvinceName().contains(
									getString(R.string.map_macau))) {
						gangaoList.add(getCicy(aMapProvince));
					} else if (aMapProvince.getProvinceName().contains(
							getString(R.string.map_the_national_profile_chart))) {
						gaiyaotuList.add(getCicy(aMapProvince));
					}
				}
				try {
					cityList.remove(4);// ��List��������ɾ�����
					cityList.remove(4);// ��List��������ɾ������
					cityList.remove(4);// ��List��������ɾ��ȫ����Ҫͼ
				} catch (Throwable e) {
					e.printStackTrace();
				}
				cityMap.put(0, gaiyaotuList);// ��HashMap�е�0λ�����ȫ����Ҫͼ
				cityMap.put(1, cityList);// ��HashMap�е�1λ�����ֱϽ��
				cityMap.put(2, gangaoList);// ��HashMap�е�2λ����Ӹ۰�
				isOpen = new boolean[provinceList.size()];
				// Ϊ�б������Դ
				expandableListView.setAdapter(adapter);
			}
		});

	}

	/**
	 * ��ʼ��UI�����ļ�
	 */
	private void init() {
		groupPosition = -1;
		childPosition = -1;
		// �˰汾���ƣ�ʹ�����ߵ�ͼ�����ʼ��һ��MapView
		mapView = new MapView(this);
		amapManager = new OfflineMapManager(this, this);
		ImageButton back = (ImageButton) findViewById(R.id.account_offline_province_back);
		back.setOnClickListener(this);
		expandableListView = (ExpandableListView) findViewById(R.id.account_offline_province_listview);
		expandableListView.setGroupIndicator(null);

		expandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					@Override
					public void onGroupCollapse(int groupPosition) {
						isOpen[groupPosition] = false;
					}
				});

		expandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int groupPosition) {
						isOpen[groupPosition] = true;
					}
				});
		// ���ö���item����ļ�����
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				try {
					if (!isStart) {
						// ����ȫ����Ҫͼ��ֱϽ�С��۰����ߵ�ͼ����
						ActivityOfflineProvince.this.groupPosition = groupPosition;
						ActivityOfflineProvince.this.childPosition = childPosition;

						if (groupPosition == 0 || groupPosition == 1
								|| groupPosition == 2) {
							amapManager.downloadByProvinceName(cityMap
									.get(groupPosition).get(childPosition)
									.getCity());
							isStart = true;
						}
						// ���ظ�ʡ�����ߵ�ͼ����
						else {
							// ���ظ�ʡ�б��е�ʡ�����ߵ�ͼ����
							if (childPosition == 0) {
								// amapManager.downloadByProvinceName(provinceList
								// .get(groupPosition).getProvinceName());
							}
							// ���ظ�ʡ�б��еĳ������ߵ�ͼ����
							else if (childPosition > 0) {
								isStart = true;
								amapManager.downloadByCityName(cityMap
										.get(groupPosition).get(childPosition)
										.getCity());
							}
						}
					} else {
						// Util.showToastBottom(ActivityOfflineProvince.this,
						// getString(R.string.map_is_downloading_hold_on));
						if (ActivityOfflineProvince.this.groupPosition == groupPosition
								&& ActivityOfflineProvince.this.childPosition == childPosition) {
							isStart = false;
							amapManager.stop();
						}
						return false;
					}

				} catch (AMapException e) {
					e.printStackTrace();
				}
				// ���浱ǰ������������ʡ�ݻ��߳��е�positionλ��
				return false;
			}
		});
	}

	/**
	 * ��һ��ʡ�Ķ���ת��Ϊһ���еĶ���
	 */
	public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
		OfflineMapCity aMapCity = new OfflineMapCity();
		aMapCity.setCity(aMapProvince.getProvinceName());
		aMapCity.setSize(aMapProvince.getSize());
		aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
		aMapCity.setState(aMapProvince.getState());
		aMapCity.setUrl(aMapProvince.getUrl());
		return aMapCity;
	}

	final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
		@Override
		public int getGroupCount() {
			return provinceList.size();
		}

		/**
		 * ��ȡһ����ǩ����
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return provinceList.get(groupPosition).getProvinceName();
		}

		/**
		 * ��ȡһ����ǩ��ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return cityMap.get(groupPosition).size();
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return cityMap.get(groupPosition).get(childPosition).getCity();
		}

		/**
		 * ��ȡ������ǩ��ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * ָ��λ����Ӧ������ͼ
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * ��һ����ǩ��������
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView group_text;
			ImageView group_image;
			if (convertView == null) {
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getBaseContext(), R.layout.offlinemap_group, null);
			}
			group_text = (TextView) convertView.findViewById(R.id.group_text);
			group_image = (ImageView) convertView
					.findViewById(R.id.group_image);
			group_text.setText(provinceList.get(groupPosition)
					.getProvinceName());
			if (isOpen[groupPosition]) {
				group_image.setImageDrawable(getResources().getDrawable(
						R.drawable.downarrow));
			} else {
				group_image.setImageDrawable(getResources().getDrawable(
						R.drawable.rightarrow));
			}
			return convertView;
		}

		/**
		 * ��һ����ǩ�µĶ�����ǩ��������
		 */
		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = (LinearLayout) LinearLayout.inflate(
						getBaseContext(), R.layout.adapter_offline_province,
						null);
			}
			ViewHolder holder = new ViewHolder(convertView);

			holder.cityName.setText(cityMap.get(groupPosition)
					.get(childPosition).getCity());

			float scale = (cityMap.get(groupPosition).get(childPosition)
					.getSize()) / (1024 * 1024f);
			DecimalFormat fnum = new DecimalFormat("##0.00");
			String dd = fnum.format(scale);
			System.out.println(dd);
			holder.citySize.setText(dd + "MB");
			String str = initAdapterState(cityMap.get(groupPosition)
					.get(childPosition).getState());

			if (ActivityOfflineProvince.this.childPosition == childPosition
					&& ActivityOfflineProvince.this.groupPosition == groupPosition) {
				if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
					holder.cityDown
							.setText(getString(R.string.map_is_downloading)
									+ completeCode + "%");
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.UNZIP) {
					holder.cityDown
							.setText(getString(R.string.map_is_decompressed)
									+ completeCode + "%");
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.SUCCESS) {
					holder.cityDown
							.setText(getString(R.string.download_succeed));
				}

			} else {
				holder.cityDown.setText(str);
			}
			if (cityMap.get(groupPosition).get(childPosition).getCity()
					.equals(cityName)) {
				if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
					holder.cityDown
							.setText(getString(R.string.map_is_downloading)
									+ completeCode + "%");
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.UNZIP) {
					holder.cityDown
							.setText(getString(R.string.map_is_decompressed)
									+ completeCode + "%");
				} else if (cityMap.get(groupPosition).get(childPosition)
						.getState() == OfflineMapStatus.SUCCESS) {
					holder.cityDown
							.setText(getString(R.string.download_succeed));
				}
			}
			return convertView;
		}

		class ViewHolder {
			TextView cityName;
			TextView citySize;
			TextView cityDown;

			public ViewHolder(View view) {
				cityName = (TextView) view
						.findViewById(R.id.adapter_offline_province_name);
				citySize = (TextView) view
						.findViewById(R.id.adapter_offline_province_size);
				cityDown = (TextView) view
						.findViewById(R.id.adapter_offline_province_download);
			}
		}

		/**
		 * ��ѡ���ӽڵ��ʱ�򣬵��ø÷���
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};

	private String initAdapterState(int state) {
		Log.e("initAdapterState", "state=" + state);
		switch (state) {
		// case OfflineMapStatus.CHECKUPDATES:
		// return "����";
		// case OfflineMapStatus.ERROR:
		// return "����";
		// case OfflineMapStatus.EXCEPTION_AMAP:
		// return "����";
		// case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
		// return "���ع����з�����������";
		// case OfflineMapStatus.EXCEPTION_SDCARD:
		// return "SD����д�쳣";
		// case OfflineMapStatus.LOADING:
		// return "��������";
		// case OfflineMapStatus.PAUSE:
		// return "����";
		case OfflineMapStatus.START_DOWNLOAD_FAILD:
			return getString(R.string.map_is_download);
			// case OfflineMapStatus.STOP:
			// return "����";
		case OfflineMapStatus.SUCCESS:
			return getString(R.string.download_succeed);
			// case OfflineMapStatus.UNZIP:
			// return "���ڽ�ѹ";
		case OfflineMapStatus.WAITING:
			return getString(R.string.download);
		default:
			break;
		}
		return "";

	}

	/**
	 * ���ߵ�ͼ���ػص�����
	 */
	@Override
	public void onDownload(int status, int completeCode, String downName) {
		cityName = downName;
		changeOfflineMapTitle(status, downName);
		switch (status) {
		case OfflineMapStatus.SUCCESS:
			isStart = false;
			break;
		case OfflineMapStatus.LOADING:
			ActivityOfflineProvince.this.completeCode = completeCode;
			break;
		case OfflineMapStatus.UNZIP:
			ActivityOfflineProvince.this.completeCode = completeCode;
			break;
		case OfflineMapStatus.WAITING:
			break;
		case OfflineMapStatus.PAUSE:
			isStart = false;
			break;
		case OfflineMapStatus.STOP:
			isStart = false;
			break;
		case OfflineMapStatus.CHECKUPDATES:// return "������״̬";
			isStart = false;
			break;

		// case OfflineMapStatus.ERROR:// return "����ʧ��";
		// groupPosition = -1;
		// childPosition = -1;
		// isStart = false;
		// break;
		//
		// case OfflineMapStatus.EXCEPTION_AMAP:// return "AMap��֤���쳣";
		// groupPosition = -1;
		// childPosition = -1;
		// isStart = false;
		// break;
		//
		// case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:// return
		// "���ع����з�����������";
		// groupPosition = -1;
		// childPosition = -1;
		// isStart = false;
		// break;
		//
		// case OfflineMapStatus.EXCEPTION_SDCARD:// return "SD����д�쳣";
		// groupPosition = -1;
		// childPosition = -1;
		// isStart = false;
		// break;

		case OfflineMapStatus.START_DOWNLOAD_FAILD:// return "��ʼ����ʧ�ܣ������ظó��е�ͼ";
			isStart = false;
			break;
		default:
			isStart = false;
			break;
		}
		((BaseExpandableListAdapter) adapter).notifyDataSetChanged();

	}

	/**
	 * �������ߵ�ͼ����״̬����
	 */
	private void changeOfflineMapTitle(int status, String name) {
		if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2) {
			cityMap.get(groupPosition).get(childPosition).setState(status);
		} else {
			if (childPosition == 0) {
				cityMap.get(groupPosition).get(childPosition).setState(status);
				for (int i = 0; i < cityMap.get(groupPosition).size(); i++) {
					if (cityMap.get(groupPosition).get(i).getCity()
							.equals(name)) {
						cityMap.get(groupPosition).get(i).setState(status);
					}
				}
			} else {
				cityMap.get(groupPosition).get(childPosition).setState(status);
			}
		}
	}

	/**
	 * ��ȡmap ����Ͷ�ȡĿ¼
	 */
	// private String getSdCacheDir(Context context) {
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// java.io.File fExternalStorageDirectory = Environment
	// .getExternalStorageDirectory();
	// java.io.File autonaviDir = new java.io.File(
	// fExternalStorageDirectory, "amapsdk");
	// boolean result = false;
	// if (!autonaviDir.exists()) {
	// result = autonaviDir.mkdir();
	// }
	// java.io.File minimapDir = new java.io.File(autonaviDir,
	// "offlineMap");
	// if (!minimapDir.exists()) {
	// result = minimapDir.mkdir();
	// }
	// return minimapDir.toString() + "/";
	// } else {
	// return "";
	// }
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			mapView.onDestroy();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.account_offline_province_back:
			amapManager.stop();
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			amapManager.stop();
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckUpdate(boolean arg0, String arg1) {

	}

	@Override
	public void onRemove(boolean arg0, String arg1, String arg2) {

	}
}
