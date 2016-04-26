package com.zgcar.com.socket;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.zgcar.com.R;
import com.zgcar.com.account.model.FamilyParentInfos;
import com.zgcar.com.account.model.GuysInfos;
import com.zgcar.com.account.model.ListInfosAccount;
import com.zgcar.com.account.model.MessageInfos;
import com.zgcar.com.account.model.MyAlarmInfos;
import com.zgcar.com.account.model.StudyModelInfos;
import com.zgcar.com.account.model.TerminalInfos;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.history.entity.RedFlowerNumInfos;
import com.zgcar.com.location.Entity.HistoryEntity;
import com.zgcar.com.location.Entity.LocationListInfos;
import com.zgcar.com.location.Entity.LocationTerminalListData;
import com.zgcar.com.location.Entity.SafetyAreaEntity;
import com.zgcar.com.main.MyApplication;
import com.zgcar.com.main.model.ListInfosEntity;
import com.zgcar.com.main.model.NotifyMessageEntity;
import com.zgcar.com.main.model.TerminalListInfos;
import com.zgcar.com.util.DownFile;
import com.zgcar.com.util.Util;

public class ResolveServiceData {

	/**
	 * 获取app版本信息
	 * 
	 * @return
	 */
	public static String getAppLink() {
		try {
			JSONArray array = SocketUtil.getData().getJSONArray("data");
			String json = "";
			JSONObject object = array.getJSONObject(0);
			json = object.getString("ver");
			json = json + "#" + object.getString("link");
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 
	 * 登录时获取当前账户基本资料
	 * 
	 * @param sf
	 * @param context
	 */
	public static Bitmap getUserBasics(SharedPreferences sf, Context context,
			MyApplication app) {
		/**
		 * "data":[ { "user_name":"17709690345", "image_link":
		 * "http://124.232.150.158:2015/17709690345/image/down?17709690345.jpg",
		 * "image_flag":0, "res_link":"http://124.232.150.158:2015" } ]
		 **/
		try {
			JSONArray array = SocketUtil.getData().getJSONArray("data");
			JSONObject object = array.getJSONObject(0);
			String user = object.getString("user_name");
			// FinalVariableLibrary.ip = object.getString("res_link");
			String imageLink = object.getString("image_link");
			String path = sf.getString(user + "image", "");
			File file = new File(path);
			int flag = sf.getInt(user + "imageFlag", -1);
			int flag1 = object.getInt("image_flag");

			if (FinalVariableLibrary.PATHS == null) {
				Bitmap bitmap1 = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.icon);
				return bitmap1;
			}
			if (flag1 == flag && file.exists()) {
				int width = (int) Util.dip2px(context, 55);
				Bitmap bitmap1 = Util.decodeSampledBitmapFromResource(
						file.getPath(), width, width);
				return bitmap1;
			}
			if (flag1 == 0) {
				Bitmap bitmap1 = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.icon);
				return bitmap1;
			}
			file.delete();
			File file1 = new File(Util.getSDPath() + "/"
					+ FinalVariableLibrary.CACHE_FOLDER + "/" + user);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			file1 = new File(file1.getPath(), System.currentTimeMillis()
					+ ".jpg");
			boolean flag2 = DownFile.downFile(imageLink, file1);
			if (flag2) {
				Editor editor = sf.edit();
				editor.putInt(user + "imageFlag", flag1);
				editor.putString(user + "image", file1.getPath());
				editor.commit();
				int width = (int) Util.dip2px(context, 55);
				Bitmap bitmap1 = Util.decodeSampledBitmapFromResource(
						file1.getPath(), width, width);
				return bitmap1;
			}
			Bitmap bitmap1 = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon);
			return bitmap1;
		} catch (JSONException e) {
			e.printStackTrace();
			Bitmap bitmap1 = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon);
			return bitmap1;
		}

	}

	/**
	 * 获取手表流量或者话费信息
	 * 
	 * @return 返回信息实体类
	 */
	public static synchronized MessageInfos getPhoneMessage() {
		try {
			JSONObject object = SocketUtil.getData().getJSONArray("data")
					.getJSONObject(0);
			MessageInfos info = new MessageInfos();
			info.setFlag(1);
			info.setMessage(object.getString("sms"));
			info.setTime(Util.getCurrentTime());
			return info;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 地理围栏
	public static synchronized List<SafetyAreaEntity> zoneFence() {
		List<SafetyAreaEntity> listbInfos = new ArrayList<SafetyAreaEntity>();
		try {
			JSONObject data = SocketUtil.getData();
			if (data.getInt("flag") == 1) {
				JSONArray deviceInfos = data.getJSONArray("data");
				if (deviceInfos.length() > 0) {
					List<JSONObject> jsonList = new ArrayList<JSONObject>();
					for (int i = 0; i < deviceInfos.length(); i++) {
						jsonList.add(deviceInfos.getJSONObject(i));
					}

					for (JSONObject jsonObject2 : jsonList) {

						SafetyAreaEntity subInfos = new SafetyAreaEntity();

						subInfos.setId(jsonObject2.getInt("id"));

						subInfos.setName(jsonObject2.getString("name"));

						subInfos.setLo(jsonObject2.getDouble("lo"));
						subInfos.setLa(jsonObject2.getDouble("la"));
						subInfos.setRad(jsonObject2.getInt("rad"));
						subInfos.setAlarmtype(jsonObject2.getInt("alarmtype"));
						// 地理围栏
						listbInfos.add(subInfos);
					}

					ListInfosEntity.setZoneSafetyData(listbInfos);
					Log.e("GetLocationServiceData", listbInfos.toString());
					return listbInfos;
				} else {
					ListInfosEntity.setZoneSafetyData(listbInfos);
					Log.e("GetLocationServiceData", listbInfos.toString());
					return listbInfos;
				}

			} else {
				return listbInfos;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return listbInfos;
		}
	}

	/**
	 * 获取当前终端的基本资料
	 * 
	 * @param imei
	 */
	public static TerminalInfos setInfos(String imei) {
		try {

			JSONObject object = SocketUtil.getData();
			int a = object.getInt("flag");
			if (a == 1) {
				JSONArray array = object.getJSONArray("data");
				JSONObject jsonObject = array.getJSONObject(0);
				TerminalInfos infos = new TerminalInfos();
				infos.setImei(imei);
				infos.setBrightness(jsonObject.getInt("brightness"));
				infos.setJoinTime(jsonObject.getString("joinTime"));
				infos.setLocal_mode(jsonObject.getInt("local_mode"));
				infos.setMute(jsonObject.getInt("mute"));
				infos.setTemperature_alarm(jsonObject
						.getInt("temperature_alarm"));
				infos.setBodyAnswer(jsonObject.getInt("answer"));
				infos.setTerminal_ver(jsonObject.getString("terminal_ver"));
				infos.setVolume(jsonObject.getInt("volume"));
				infos.setWear(jsonObject.getInt("wear"));
				return infos;
			} else {
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 解析上课模式数据
	 */
	public static List<StudyModelInfos> getStudyModelInfos(Activity activity) {
		List<StudyModelInfos> studyModelListInfos = new ArrayList<StudyModelInfos>();
		try {
			JSONObject object = SocketUtil.getData();
			if (object.getInt("flag") == 1) {
				JSONArray array = object.getJSONArray("data");
				JSONObject jsonObject = array.getJSONObject(0);
				JSONObject jsObject1 = jsonObject.getJSONObject("work1");
				JSONObject jsObject2 = jsonObject.getJSONObject("work2");
				JSONObject jsObject3 = jsonObject.getJSONObject("work3");
				StudyModelInfos info1 = getStudyinfo(jsObject1, activity);
				studyModelListInfos.add(info1);
				StudyModelInfos info2 = getStudyinfo(jsObject2, activity);
				studyModelListInfos.add(info2);
				StudyModelInfos info3 = getStudyinfo(jsObject3, activity);
				studyModelListInfos.add(info3);
				return studyModelListInfos;
			} else {
				return studyModelListInfos;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return studyModelListInfos;
		}
	}

	private static StudyModelInfos getStudyinfo(JSONObject jsObject1,
			Activity activity) {
		StudyModelInfos info = new StudyModelInfos();
		try {
			info.setStart_time(jsObject1.getString("start_time"));
			info.setStop_time(jsObject1.getString("stop_time"));
			info.setWork_sn(jsObject1.getInt("work_sn"));
			int a = jsObject1.getInt("on_off");
			info.setOn_off(a);
			String str = jsObject1.getString("week");
			info.setWeek(str);
			String[] s2 = activity.getResources().getStringArray(R.array.days);
			String days = "";
			for (int i = 0; i < str.length(); i++) {
				if (i == 0) {
					if ((str.charAt(i) + "").equals("1")) {
						info.setRepeat(true);
					} else {
						info.setRepeat(false);
					}
				} else {
					if ((str.charAt(i) + "").equals("1")) {
						days = days + s2[i - 1] + " ";
					}
				}
			}

			info.setWeekDesc(days);

			boolean isOpen = (a == 1 ? true : false);
			info.setOpen(isOpen);

			return info;
		} catch (JSONException e) {
			e.printStackTrace();
			return info;
		}

	}

	/**
	 * 获取alarm 的list信息
	 */
	public static List<MyAlarmInfos> getMyAlarmInfos(Activity activity,
			String imei) {
		List<MyAlarmInfos> alarmList = new ArrayList<MyAlarmInfos>();
		try {
			JSONObject object = SocketUtil.getData();
			if (object.getInt("flag") == 1) {
				JSONArray array = object.getJSONArray("data");
				List<JSONObject> list = new ArrayList<JSONObject>();
				for (int i = 0; i < array.length(); i++) {
					list.add(array.getJSONObject(i));
				}
				for (JSONObject jsonObject : list) {
					MyAlarmInfos infos = new MyAlarmInfos();
					infos.setAlarm_sn(jsonObject.getInt("alarm_sn"));
					infos.setTime(jsonObject.getString("time"));
					String a = jsonObject.getString("week");
					infos.setWeek(a);
					String[] s2 = activity.getResources().getStringArray(
							R.array.days);
					String days = "";
					for (int i = 0; i < a.length(); i++) {
						if (i == 0) {
							if ((a.charAt(i) + "").equals("1")) {
								infos.setRepeat(true);
							} else {
								infos.setRepeat(false);
							}
						} else {
							if ((a.charAt(i) + "").equals("1")) {
								days = days + s2[i - 1] + " ";
							}
						}
					}
					infos.setWeekDesc(days);
					infos.setImei(imei);
					infos.setOn_off(jsonObject.getInt("on_off"));
					infos.setAlarm_ring(jsonObject.getInt("alarm_ring"));
					infos.setAlarmring_flag(jsonObject.getInt("alarmring_flag"));
					infos.setAlarmring_link(jsonObject
							.getString("alarmring_link"));
					alarmList.add(infos);
				}
				return alarmList;
			} else {
				return alarmList;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 获取家庭成员列表信息
	 */
	public static List<FamilyParentInfos> getFimilyInfos(SharedPreferences sf,
			MyApplication app, Context context) {
		List<FamilyParentInfos> familyList = new ArrayList<FamilyParentInfos>();
		try {
			JSONObject object = SocketUtil.getData();
			if (object.getInt("flag") == 1) {
				JSONArray array = object.getJSONArray("data");
				List<JSONObject> list = new ArrayList<JSONObject>();
				for (int i = 0; i < array.length(); i++) {
					list.add(array.getJSONObject(i));
				}
				for (JSONObject jsonObject : list) {

					FamilyParentInfos infos = new FamilyParentInfos();
					String name = jsonObject.getString("user_name");
					infos.setUser_name(name);
					int a = jsonObject.getInt("admin");
					if (app.getUserName().equals(name)) {
						infos.setMysel(true);
						ListInfosEntity.getTerminalListInfos()
								.get(app.getPosition())
								.setAdmin(a == 1 ? true : false);
					}
					infos.setAdmin(a == 1 ? true : false);
					String imageLink = jsonObject.getString("image_link");
					infos.setImage_link(imageLink);
					int flag = jsonObject.getInt("image_flag");

					int flag1 = sf.getInt(name + "imageFlag", -1);

					String path = sf.getString(name + "image", "");

					File file = new File(path);

					String imagePath = "";

					if (flag == 0) {
						infos.setLocalPath("");
					} else {
						if (flag != flag1 || !file.exists()) {
							imagePath = downImage(FinalVariableLibrary.PATHS,
									name, imageLink, sf, flag);
							if (imagePath.equals("")) {
								infos.setLocalPath("");
							} else {
								infos.setLocalPath(imagePath);
								if (infos.isMysel()) {
									if (FinalVariableLibrary.bitmap != null) {
										FinalVariableLibrary.bitmap.recycle();
										FinalVariableLibrary.bitmap = null;
									}
									int width = (int) Util.dip2px(context, 50);
									FinalVariableLibrary.bitmap = Util
											.decodeSampledBitmapFromResource(
													imagePath, width, width);
								}
							}
						} else {
							infos.setLocalPath(path);
						}
					}
					infos.setImage_flag(flag);
					infos.setImei(jsonObject.getString("imei"));
					infos.setNick_name(jsonObject.getString("nick_name"));
					int b = jsonObject.getInt("sos");
					boolean sos = (b == 1 ? true : false);
					infos.setSos(sos);
					infos.setU_name(jsonObject.getString("u_name"));
					familyList.add(infos);
				}
				ListInfosAccount.setFamilyList(familyList);
				return familyList;
			} else {
				ListInfosAccount.setFamilyList(familyList);
				return familyList;
			}
		} catch (JSONException e) {
			ListInfosAccount.setFamilyList(familyList);
			e.printStackTrace();
			return familyList;
		}

	}

	/**
	 * 
	 * 获取小伙伴信息
	 * 
	 * @param sf
	 * @param app
	 * @param context
	 */
	public static List<GuysInfos> getGuysListInfos(SharedPreferences sf,
			MyApplication app, Context context) {
		List<GuysInfos> listinfos = new ArrayList<GuysInfos>();
		try {
			JSONArray array = SocketUtil.getData().getJSONArray("data");
			List<JSONObject> objectcList = new ArrayList<JSONObject>();
			for (int i = 0; i < array.length(); i++) {
				objectcList.add(array.getJSONObject(i));
			}
			for (JSONObject jsonObject : objectcList) {
				GuysInfos infos = new GuysInfos();
				infos.setMainImei(app.getImei());
				String imei = jsonObject.getString("imei");
				infos.setImei(imei);
				infos.setAge(jsonObject.getInt("age"));
				infos.setName(jsonObject.getString("name"));
				infos.setSex(jsonObject.getInt("sex"));
				int flag = jsonObject.getInt("image_flag");
				infos.setImageFlag(flag);

				String path = jsonObject.getString("image_link");

				int flag2 = sf.getInt(imei + "imageFlag", -1);

				String imagePath = sf.getString(imei + "image", "");

				File file = new File(imagePath);
				infos.setImageLink(path);
				if (flag == 0) {

					infos.setLocalPath("");

				} else if (flag2 == flag && file.exists()) {
					infos.setLocalPath(imagePath);
				} else {
					if (path.equals("")) {
						infos.setLocalPath("");

					} else {
						String filePath = downImage(FinalVariableLibrary.PATHS,
								imei, path, sf, flag);
						File file2 = new File(filePath);
						if (file2.exists()) {
							infos.setLocalPath(filePath);
						} else {
							infos.setLocalPath("");
						}

					}
				}
				listinfos.add(infos);
			}
			return listinfos;
		} catch (JSONException e) {
			e.printStackTrace();
			return listinfos;
		}

	}

	/**
	 * 
	 * 
	 * 下载图片资源
	 * 
	 * @param sdPath
	 *            本地缓存路径
	 * @param name
	 *            账户名称（手机号）
	 * @param path
	 *            资源下载路径
	 * @param sf
	 *            保存相关信息
	 * @param flag
	 *            图片flag
	 * @return 下载后的文件路径
	 */
	private static synchronized String downImage(String sdPath, String name,
			String path, SharedPreferences sf, int flag) {
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDefaultUseCaches(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				File file = new File(sdPath + "/" + "image");
				if (!file.exists()) {
					file.mkdirs();
				}
				File imageFile = new File(file.getPath() + "/"
						+ System.currentTimeMillis() + ".jpg");
				imageFile.createNewFile();
				OutputStream outputStream = new FileOutputStream(imageFile);
				byte[] buffer = new byte[1024];
				int b;
				while ((b = bis.read(buffer, 0, 1024)) != -1) {
					outputStream.write(buffer, 0, b);
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				httpURLConnection.disconnect();
				Editor editor = sf.edit();
				editor.putInt(name + "imageFlag", flag);
				editor.putString(name + "image", imageFile.getPath());
				editor.commit();
				return imageFile.getPath();
			}
			return "";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 连接服务器发送数据
	 * 
	 * @param jsonStr
	 *            需要发送的json数据
	 * @return 服务器连接成功返回true否则返回false
	 */

	public static RedFlowerNumInfos setRedFlowerInfos(String imei) {
		RedFlowerNumInfos infos = new RedFlowerNumInfos();
		try {
			JSONObject object = SocketUtil.getData();
			int flag = object.getInt("flag");
			if (flag == 0) {
				return infos;
			}
			JSONObject jsonObject = object.getJSONArray("data")
					.getJSONObject(0);
			infos.setQuantity(jsonObject.getInt("quantity"));
			infos.setImei(imei);
			infos.setTote(jsonObject.getInt("tote"));
			infos.setReward(jsonObject.getString("reward"));
			return infos;
		} catch (JSONException e) {
			e.printStackTrace();
			return infos;
		}

	}

	public static synchronized List<TerminalListInfos> isSucceed() {
		List<TerminalListInfos> listbInfos = new ArrayList<TerminalListInfos>();
		try {
			JSONObject object = SocketUtil.getData();
			if (object.getInt("flag") == 1) {
				JSONArray deviceInfos = object.getJSONArray("data");
				if (deviceInfos.length() > 0) {
					List<JSONObject> jsonList = new ArrayList<JSONObject>();
					for (int i = 0; i < deviceInfos.length(); i++) {
						jsonList.add(deviceInfos.getJSONObject(i));
					}
					for (JSONObject jsonObject2 : jsonList) {

						TerminalListInfos subInfos = new TerminalListInfos();
						subInfos.setImei(jsonObject2.getString("imei"));
						subInfos.setName(jsonObject2.getString("name"));
						subInfos.setNumber(jsonObject2.getString("number"));
						subInfos.setAge(jsonObject2.getInt("age"));
						subInfos.setHeight(jsonObject2.getInt("height"));
						subInfos.setWeight(jsonObject2.getInt("weight"));
						subInfos.setSex(jsonObject2.getInt("sex"));
						subInfos.setBirthday(jsonObject2.getString("birthday"));
						subInfos.setServicedate(jsonObject2
								.getString("servicedate"));
						subInfos.setOnline(jsonObject2.getInt("online"));
						subInfos.setBl_mac(jsonObject2.getString("bl_mac"));
						subInfos.setBl_switch(jsonObject2
								.getString("bl_switch"));
						subInfos.setImage_flag(jsonObject2.getInt("image_flag"));
						subInfos.setImage_link(jsonObject2
								.getString("image_link"));
						subInfos.setAdmin(jsonObject2.getInt("admin") == 1 ? true
								: false);
						listbInfos.add(subInfos);
					}
					return listbInfos;
				} else {
					return listbInfos;
				}

			} else {
				return listbInfos;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return listbInfos;
		}
	}

	public static List<NotifyMessageEntity> getHistoryInfos() {
		List<NotifyMessageEntity> list = new ArrayList<NotifyMessageEntity>();
		try {
			List<JSONObject> listObject = new ArrayList<JSONObject>();
			JSONArray jsonArray = SocketUtil.getData().getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				listObject.add(jsonArray.getJSONObject(i));
			}
			for (JSONObject jsonObject : listObject) {
				NotifyMessageEntity infos = new NotifyMessageEntity();
				infos.setAlarm(jsonObject.getString("alarm"));
				infos.setGeo(jsonObject.getString("geo"));
				infos.setImei(jsonObject.getString("imei"));
				infos.setLa(jsonObject.getDouble("la"));
				infos.setLitle(jsonObject.getString("litle"));
				infos.setLo(jsonObject.getDouble("lo"));
				infos.setMsg(jsonObject.getString("msg"));
				infos.setTime(jsonObject.getString("time"));
				list.add(infos);
			}
			Log.e("GetChatServiceData", list.toString());
			return list;
		} catch (JSONException e) {
			return list;
		}

	}

	/**
	 * 获取终端位置 {"cmd":"00004","data":[{"imei":"861400000000088","map_type":0}]}
	 * 监听
	 * {"cmd":"00022","data":[{"imei":"861400000000088","number":"13717033460"
	 * }]}
	 * 
	 * 录音指令 {"cmd":"00018","data":[{"imei":"861400000000088"}]} 地位界面json构造
	 * 
	 * @return
	 */
	public static synchronized String getJson(String cmd, String imei,
			String number, int map_type) {
		try {
			JSONObject object = new JSONObject();
			object.put("cmd", cmd);
			JSONArray array = new JSONArray();
			JSONObject object1 = new JSONObject();
			object1.put("imei", imei);
			object1.put("number", number);
			object1.put("map_type", map_type);
			array.put(object1);
			object.put("data", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 历史轨迹某天的轨迹记录
	 * 
	 * @return
	 */
	public static synchronized List<HistoryEntity> historyroute() {
		List<HistoryEntity> listbInfos = new ArrayList<HistoryEntity>();
		try {
			JSONObject data = SocketUtil.getData();
			Log.e("接收到的数据:", data.toString());
			if (data.getInt("flag") == 1) {
				JSONArray deviceInfos = data.getJSONArray("data");
				if (deviceInfos.length() > 0) {
					List<JSONObject> jsonList = new ArrayList<JSONObject>();
					for (int i = 0; i < deviceInfos.length(); i++) {
						jsonList.add(deviceInfos.getJSONObject(i));
					}
					for (JSONObject jsonObject2 : jsonList) {
						HistoryEntity subInfos = new HistoryEntity();
						subInfos.setNumber(jsonObject2.getInt("number"));
						subInfos.setGpstime(jsonObject2.getString("gpstime"));
						subInfos.setContinued(jsonObject2
								.getString("continued"));
						subInfos.setLocation_type(jsonObject2
								.getInt("location_type"));
						subInfos.setLo(jsonObject2.getDouble("lo"));
						subInfos.setLa(jsonObject2.getDouble("la"));
						subInfos.setSpeed(jsonObject2.getDouble("speed"));
						subInfos.setDirection(jsonObject2.getInt("direction"));
						subInfos.setBat(jsonObject2.getInt("bat"));
						subInfos.setSignal(jsonObject2.getInt("signal"));
						// subInfos.setSetp(jsonObject2.getInt("setp"));
						subInfos.setStatus(jsonObject2.getString("status"));
						subInfos.setAlarm(jsonObject2.getString("alarm"));
						subInfos.setAddress(jsonObject2.getString("address"));
						listbInfos.add(subInfos);
					}
					return listbInfos;
				} else {
					return listbInfos;
				}
			} else {
				return listbInfos;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return listbInfos;
		}

	}

	public static List<HistoryEntity> getHistoryInfo() {
		/**
		 * { "ret":0, "msg":"", "data": [ { "gpstime":"2015-08-29 17:55:20",
		 * "location_type":0,
		 * 
		 * **/
		List<HistoryEntity> list = new ArrayList<HistoryEntity>();
		try {
			JSONArray array = SocketUtil.getData().getJSONArray("data");
			List<JSONObject> listObjects = new ArrayList<JSONObject>();
			for (int i = 0; i < array.length(); i++) {
				listObjects.add(array.getJSONObject(i));
			}
			for (JSONObject jsonObject : listObjects) {
				HistoryEntity info = new HistoryEntity();
				info.setAddress(jsonObject.getString("address"));
				info.setAlarm(jsonObject.getString("alarm"));
				info.setSetp(jsonObject.getInt("step"));
				info.setSignal(jsonObject.getInt("signal"));
				info.setStatus(jsonObject.getString("status"));
				info.setBat(jsonObject.getInt("bat"));
				info.setDirection(jsonObject.getInt("direction"));
				info.setSpeed(jsonObject.getDouble("speed"));
				info.setLa(jsonObject.getDouble("la"));
				info.setLo(jsonObject.getDouble("lo"));
				info.setLocation_type(jsonObject.getInt("location_type"));
				info.setGpstime(jsonObject.getString("gpstime"));
				list.add(info);
			}
			LocationListInfos.setHistorylistInfos(list);
			return list;
		} catch (JSONException e) {
			LocationListInfos.setHistorylistInfos(list);
			e.printStackTrace();
			return list;
		}

	}

	/**
	 * 获取当前终端位置信息
	 */
	public static synchronized LocationTerminalListData terminalhistory() {
		LocationTerminalListData listbInfos = new LocationTerminalListData();
		try {
			JSONObject data = SocketUtil.getData();
			if (data.getInt("flag") == 1) {
				JSONArray deviceInfos = data.getJSONArray("data");
				if (deviceInfos.length() > 0) {
					JSONObject jsonList = deviceInfos.getJSONObject(0);
					listbInfos.setGpstime(jsonList.getString("gpstime"));
					listbInfos.setLocation_type(jsonList
							.getInt("location_type"));
					listbInfos.setLo(jsonList.getDouble("lo"));
					listbInfos.setLa(jsonList.getDouble("la"));
					listbInfos.setSpeed(jsonList.getDouble("speed"));
					listbInfos.setDirection(jsonList.getInt("direction"));
					listbInfos.setBat(jsonList.getInt("bat"));
					listbInfos.setSignal(jsonList.getInt("signal"));
					listbInfos.setTemp(jsonList.getDouble("temp"));
					listbInfos.setStatus(jsonList.getString("status"));
					listbInfos.setAlarm(jsonList.getString("alarm"));
					listbInfos.setAddress(jsonList.getString("address"));
					return listbInfos;
				} else {
					return listbInfos;
				}
			} else {
				return listbInfos;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return listbInfos;
		}
	}

}
