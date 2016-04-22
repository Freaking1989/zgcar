package com.zgcar.com.account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.location.Entity.SafetyAreaEntity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.socket.SocketUtil;
import com.zgcar.com.util.SetTitleBackground;
import com.zgcar.com.util.Util;

/**
 * 添加电子围栏
 * 
 */
public class ActivitySafetyAreaEdit2 extends Activity implements
		OnClickListener, OnSeekBarChangeListener {
	private WebView webView;
	private TextView addzonehome, addzoneschool, addzonefirm, addzonegrandpa,
			addzoneteacher, addzonecramschool, editTitle;
	private EditText areaNameEt;
	private MyApplication app;
	private final int Circle_MAX = 700;
	private SeekBar seekBar;
	private TextView seekbarDesc;
	private Dialog dialogCgName;
	private int tempRadius;
	private double tempLo, tempLa;
	private Dialog dialog;
	private boolean isAddNew;
	private SafetyAreaEntity zoneSafetyEntity;
	private String jsonStr;
	private boolean isCanEdit;
	private Button save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SetTitleBackground.setTitleBg(ActivitySafetyAreaEdit2.this,
				R.color.color_4);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety_area_edit);
		init();
	}

	private void init() {
		isAddNew = getIntent().getBooleanExtra("isAddNew", false);
		dialog = new Dialog(ActivitySafetyAreaEdit2.this, R.style.dialog);
		dialog.setContentView(R.layout.view_progress_dialog);
		dialog.setCancelable(false);
		dialog.show();
		webView = (WebView) findViewById(R.id.add_safety_area_webview);
		app = (MyApplication) getApplication();
		save = (Button) findViewById(R.id.safety_edit_area_save);
		save.setOnClickListener(this);
		ImageButton search = (ImageButton) findViewById(R.id.safety_edit_area_search);
		search.setOnClickListener(this);
		ImageButton back = (ImageButton) findViewById(R.id.safety_edit_area_back);
		back.setOnClickListener(this);
		editTitle = (TextView) findViewById(R.id.safety_edit_area_title_name);
		editTitle.setOnClickListener(this);
		seekBar = (SeekBar) findViewById(R.id.safety_edit_area_seekbar);
		seekBar.setMax(Circle_MAX);
		seekBar.setProgress(0);
		seekBar.setOnSeekBarChangeListener(this);
		seekbarDesc = (TextView) findViewById(R.id.safety_edit_area_seekbar_desc);
		initWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		tempRadius = 300;
		if (!isAddNew) {
			isCanEdit = false;
			save.setText(getString(R.string.press_edit));
			zoneSafetyEntity = (SafetyAreaEntity) getIntent()
					.getSerializableExtra("ZoneSafetyEntity");
			tempLo = zoneSafetyEntity.getLo();
			tempLa = zoneSafetyEntity.getLa();
			tempRadius = zoneSafetyEntity.getRad();
			editTitle.setText(zoneSafetyEntity.getName());

		} else {
			isCanEdit = true;
			if (app.getTempWatchLocation() != null) {
				tempLo = app.getTempWatchLocation().getLo();
				tempLa = app.getTempWatchLocation().getLa();
			} else {
				if (app.getTempMyLocation() != null) {
					tempLo = app.getTempMyLocation().getLongitude();
					tempLa = app.getTempMyLocation().getLatitude();
				} else {
					tempLo = 0;
					tempLa = 0;
				}
			}
		}

		seekBar.setProgress(tempRadius - 300);
		seekbarDesc.setText((tempRadius) + getString(R.string.stroke_circle));

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.addJavascriptInterface(getHtmlObject(), "jsObj");
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (app.getTempMyLocation() != null
						&& app.getTempWatchLocation() != null || isAddNew) {
					String javaStr2 = String.format(
							"javascript:DrawCircle('%s', '%s', '%s');", tempLo,
							tempLa, tempRadius);
					String avaStr3 = String.format(
							"javascript:MapCenter('%s', '%s', '%s');", tempLo,
							tempLa, "15");
					String javaScriptCmdString = String
							.format("javascript:DrawPos('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
									"1", tempLo, tempLa, "safe", "0", "", "0");
					webView.loadUrl(javaStr2);
					webView.loadUrl(avaStr3);
					webView.loadUrl(javaScriptCmdString);
				}
				dialog.dismiss();
				super.onPageFinished(view, url);
			}
		});
		webView.loadUrl(FinalVariableLibrary.WEB_HOST_EDIT_SAFETY_AREA);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.safety_edit_area_save:// 添加安全区域名称
			if (!isCanEdit) {
				if (!isCanEdit) {
					isCanEdit = true;
					save.setText(getString(R.string.save));
					break;
				}
			}

			dialog.show();
			saveSatefyArea();
			break;
		case R.id.safety_edit_area_search:// 进入搜索页面
			if (isCanEdit) {
				startActivityForResult(new Intent(ActivitySafetyAreaEdit2.this,
						ActivitySafetySearch2.class), 1);
			} else {
				Util.showToastBottom(ActivitySafetyAreaEdit2.this,
						getString(R.string.press_to_edit_desc));
			}

			break;
		case R.id.safety_edit_area_back:// 返回安全区域
			finish();
			break;
		case R.id.safety_edit_area_title_name:// 编辑安全区域（半透明）
			if (!isCanEdit) {
				Util.showToastBottom(ActivitySafetyAreaEdit2.this,
						getString(R.string.press_to_edit_desc));
			} else {
				if (dialogCgName == null) {
					initPopupWindows();
				}
				dialogCgName.show();
			}
			break;
		default:
			break;
		}

	}

	private void saveSatefyArea() {
		if ("".equals(editTitle.getText().toString().trim())) {
			Toast.makeText(ActivitySafetyAreaEdit2.this,
					"Fence name can not be empty!", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			return;
		}
		addzoneRequest();
	}

	private void initPopupWindows() {
		View view = View.inflate(ActivitySafetyAreaEdit2.this,
				R.layout.view_edit_safety_area_name, null);
		addzoneschool = (TextView) view
				.findViewById(R.id.edit_safety_name_school);
		addzoneschool.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addschool = addzoneschool.getText().toString();
				areaNameEt.setText(addschool);
			}
		});
		// 老师家
		addzoneteacher = (TextView) view
				.findViewById(R.id.edit_safety_name_teacher_home);
		addzoneteacher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addteacher = addzoneteacher.getText().toString();
				areaNameEt.setText(addteacher);
			}
		});
		// 公司
		addzonefirm = (TextView) view
				.findViewById(R.id.edit_safety_name_company);
		addzonefirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addfirm = addzonefirm.getText().toString();
				areaNameEt.setText(addfirm);
			}
		});
		// 补习班
		addzonegrandpa = (TextView) view
				.findViewById(R.id.edit_safety_name_grandpa_home);
		addzonegrandpa.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addgrandpa = addzonegrandpa.getText().toString();
				areaNameEt.setText(addgrandpa);
			}
		});
		// 学校
		addzonecramschool = (TextView) view
				.findViewById(R.id.edit_safety_name_cram_school);
		addzonecramschool.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addcramschool = addzonecramschool.getText().toString();
				areaNameEt.setText(addcramschool);
			}
		});
		// 家
		addzonehome = (TextView) view
				.findViewById(R.id.edit_safety_name_family);
		addzonehome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String home = addzonehome.getText().toString();
				areaNameEt.setText(home);
			}
		});
		// 获取编辑框信息
		areaNameEt = (EditText) view
				.findViewById(R.id.edit_safety_name_edittext);
		TextView idquxiao = (TextView) view
				.findViewById(R.id.edit_safety_name_cancel);
		idquxiao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogCgName.dismiss();
			}
		});
		TextView yesaddzone = (TextView) view
				.findViewById(R.id.edit_safety_name_sure);
		yesaddzone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String addzone = areaNameEt.getText().toString();
				editTitle.setText(addzone);
				dialogCgName.dismiss();
			}
		});
		dialogCgName = new Dialog(ActivitySafetyAreaEdit2.this, R.style.dialog);
		dialogCgName.setContentView(view);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (dialogCgName != null && dialogCgName.isShowing()) {
			dialogCgName.dismiss();
		}
		return super.onTouchEvent(event);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (dialogCgName != null && dialogCgName.isShowing()) {
				dialogCgName.dismiss();
			} else {
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 添加新的安全区域或者编辑当前安全区域时构造json数据
	 * 
	 * @param cmd
	 *            指令
	 * @param id
	 *            编辑安全区域时id
	 * @return
	 */
	public String getAddNewRequestJson(String cmd, int id) {
		try {
			JSONObject b = new JSONObject();
			b.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject b2 = new JSONObject();
			b2.put("imei", app.getImei());
			b2.put("id", id);
			b2.put("name", editTitle.getText().toString().trim());
			b2.put("lo", tempLo);
			b2.put("la", tempLa);
			b2.put("rad", tempRadius);
			b2.put("alarmtype", 0);
			b2.put("map_type", FinalVariableLibrary.MAP_TYPE);
			array.put(b2);
			b.put("data", array);
			Log.e("json", b.toString());
			return b.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void addzoneRequest() {
		if (isAddNew) {
			jsonStr = getAddNewRequestJson(
					FinalVariableLibrary.ADD_SAFETY_AREA, 0);
		} else {
			jsonStr = getAddNewRequestJson(
					FinalVariableLibrary.EDIT_SAFETY_AREA,
					zoneSafetyEntity.getId());
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean flag = SocketUtil.connectService(jsonStr);
				if (flag) {
					dialog.dismiss();
					finish();
				} else {
					dialog.dismiss();
					Looper.prepare();
					Toast.makeText(ActivitySafetyAreaEdit2.this,
							SocketUtil.isFail(ActivitySafetyAreaEdit2.this),
							Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
			}
		}).start();
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		Log.e("seekBar", "onStopTrackingTouch");
		String javaStr = String.format(
				"javascript:DrawCircleWithCenter('%f', '%f', '%d','%s');",
				tempLo, tempLa, tempRadius, "15");
		webView.loadUrl(javaStr);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!isCanEdit) {
			seekBar.setProgress(tempRadius - 300);
			return;
		}

		progress += 300;
		if (progress > 0 && progress <= 350) {
			progress = 300;
		} else if (progress > 350 && progress <= 450) {
			progress = 400;
		} else if (progress > 450 && progress <= 550) {
			progress = 500;
		} else if (progress > 550 && progress <= 650) {
			progress = 600;
		} else if (progress > 650 && progress <= 750) {
			progress = 700;
		} else if (progress > 750 && progress <= 850) {
			progress = 800;
		} else if (progress > 850 && progress <= 950) {
			progress = 900;
		} else if (progress > 950 && progress <= 1050) {
			progress = 1000;
		}
		seekbarDesc.setText(progress + getString(R.string.stroke_circle));
		tempRadius = progress;
	}

	private Object getHtmlObject() {
		Object insertObj = new Object() {
			@SuppressWarnings("unused")
			public String HtmlCallJava(final String pLo, final String pLa) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!isCanEdit) {
							Util.showToastBottom(ActivitySafetyAreaEdit2.this,
									getString(R.string.press_to_edit_desc));
							return;
						}
						tempLo = Double.parseDouble(pLo);
						tempLa = Double.parseDouble(pLa);
						String javaStr = String
								.format("javascript:DrawCircleWithCenter('%f', '%f', '%d','%s');",
										tempLo, tempLa, tempRadius, "15");
						webView.loadUrl(javaStr);
					}
				});
				return pLa + "," + pLo;
			}
		};
		return insertObj;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 0) {

			tempLo = data.getExtras().getDouble("lo");
			tempLa = data.getExtras().getDouble("la");
			String javaStr = String.format(
					"javascript:DrawCircleWithCenter('%f', '%f', '%d','%s');",
					tempLo, tempLa, tempRadius, "15");
			webView.loadUrl(javaStr);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
