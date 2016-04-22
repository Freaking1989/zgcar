package com.zgcar.com.entity;

import android.graphics.Bitmap;

/**
 * 常量集合
 * 
 */
public class FinalVariableLibrary {

	/**
	 * 缓存文件夹名
	 */
	public static final String CACHE_FOLDER = "zgcar";

	/**
	 * 话费消息广播action
	 */
	public static final String ChackBroadcastReceiverAction = "CHECKPHONEMESSAGESZBUS";
	/**
	 * 系统消息广播action
	 */
	public static final String SystemNotifyMessageAction = "NOTIFYMESSAGESZBUS";

	/**
	 * 语音消息广播action
	 */
	public static final String VoiceNotifyMessageAction = "THEVOICENUMHASCHANGEDSZBUS";
	/**
	 * 蓝牙防走丢消息广播action
	 */
	public static final String BlueToothStateAction = "BLUETOOTHSTATESZBUS";

	public static int ScreenHeight;

	public static int ScreenWidth;
	/**
	 * 官网
	 */
	public static final String WEB_OFFICIAL_WEBSITE = "http://www.3galarm.cn";

	/**
	 * 修改电子围栏主页
	 */
	public static final String WEB_HOST_EDIT_SAFETY_AREA = "file:///android_asset/mapmain/gmap_point.html";
	/**
	 * 搜索地理位置主页（编辑电子围栏信息中的搜索）
	 */
	public static final String WEB_HOST_SEARCH_ADDRESS = "file:///android_asset/mapmain/gmap_getArea.html";

	/**
	 * 显示历史轨迹信息主页
	 */
	public static final String WEB_HOST_HISTORY = "file:///android_asset/mapmain/gmap_main.html";
	/**
	 * locationFragment主页标准地图
	 */
	public static final String WEB_HOST_LOCATION1 = "file:///android_asset/mapmain/gmap_1.html";

	/**
	 * locationFragment主页卫星地图
	 */
	public static final String WEB_HOST_LOCATION2 = "file:///android_asset/mapmain/gmap_2.html";

	public static String requestURL = "http://" + FinalVariableLibrary.ip + ":"
			+ FinalVariableLibrary.DSTPORT0 + "/****/image/up?****.jpg";
	/**
	 * 地图类型
	 */
	public static String MAP_TYPE;

	/**
	 * 当前账户头像
	 */
	public static Bitmap bitmap;

	/**
	 * 本地缓存路径
	 */
	public static String PATHS;
	/**
	 * 服务端ip地址124.232.150.158,192.168.2.107
	 */
	public static final String ip = "124.232.150.158";
	/**
	 * 资源端口号
	 */
	public static final int DSTPORT0 = 2015;
	/**
	 * 通用端口号
	 */
	public static final int DSTPORT1 = 9801;
	/**
	 * 聊天端口号
	 */
	public static final int DSTPORT2 = 7500;
	/**
	 * 设置新用户密码
	 */
	public static final String SET_REGISTER_PSW_CMD = "00001";
	/**
	 * 用户输入密码登陆
	 */
	public static final String INPUT_PASSWOR_AND_LOADING = "00002";

	/**
	 * 添加新考拉
	 */
	public static final String ADD_SZBUS_CMA = "00003";
	/**
	 * 获取手表位置
	 */
	public static final String GET_WATCH_LOCATION = "00004";
	/**
	 * 历史轨迹
	 */
	public static final String GET_HISTORY_CMD = "00005";

	/**
	 * 获取安全区域围栏指令
	 */
	public static final String GET_SAFETY_AREA_CMD = "00006";

	/**
	 * 添加安全区域
	 */
	public static final String ADD_SAFETY_AREA = "00007";
	/**
	 * 编辑安全区域
	 */
	public static final String EDIT_SAFETY_AREA = "00008";
	/**
	 * 删除安全区域
	 */
	public static final String DELETE_SAFETY_AREA_CMD = "00009";

	/**
	 * 差找手表
	 */
	public static final String FIND_WATCH_CMD = "00016";
	/**
	 * 对手表录音
	 */

	public static final String RECORD_WATCH_VOICE = "00018";
	/**
	 * 定位手表
	 */
	public static final String LOCATE_WATCH_CMD = "00020";
	/**
	 * 远程关机
	 */
	public static final String SHUT_DOWN_CMD = "00021";
	/**
	 * 远程监控
	 */
	public static final String SEND_MONITOR_CMD = "00022";
	/**
	 * 设置闹钟
	 */
	public static final String SET_ALARM_CMD = "00023";
	/**
	 * 获取 报警/推送消息记录
	 */
	public static final String GET_NOTIFY_MESSAGE_CMD = "00026";
	/**
	 * 获取App最新版本信息，电话手表27，小天才29
	 */
	public static final String GET_APP_VERSION_INFO_CMD = "00027";

	/**
	 * 获取终端参数
	 */
	public static final String GET_TERMINAL_INFOS = "00040";

	/**
	 * 设置音量
	 */
	public static final String SET_VOLUME_CMD = "00041";
	/**
	 * 设置亮度
	 */
	public static final String SET_BRIGHTNESS_CMD = "00042";
	/**
	 * 设置铃声
	 */
	public static final String SET_RING_VOICE_CMD = "00043";
	/**
	 * 设置定位模式
	 */
	public static final String SET_LOCATION_MODEL_CMD = "00044";
	/**
	 * 设置佩戴习惯
	 */
	public static final String SET_WEARING_HABITS_CMD = "00045";
	/**
	 * 温度提醒指令
	 */
	public static final String TEMPERATUTE_AlARM_CMD = "00046";
	/**
	 * 静音设置指令
	 */
	public static final String SIlENCE_CMD = "00047";

	/**
	 * 获取上课模式数据cmd
	 */
	public static final String STUDY_MODEL = "00048";

	/**
	 * 修改上课模式cmd
	 */
	public static final String SET_STUDY_MODEL = "00049";
	/**
	 * 检测手机号是否注册
	 */
	public static final String TEST_PHONE_NUMBER = "00050";
	/**
	 * 获取小红花数量
	 */
	public static final String GET_RED_FLOWER_NUM_INFOS = "00051";

	/**
	 * 奖励小红花
	 */
	public static final String REWARD_BABY_FLOWERS = "00052";

	/**
	 * 设置小红花目标
	 */
	public static final String SET_RED_FLOWER_GOAL = "00053";
	/**
	 * 查询手表话费
	 */
	public static final String QUERY_COST = "00054";

	/**
	 * 查询手表流量
	 */
	public static final String QUERY_TRAFFIC = "00055";

	/**
	 * 获取手表话费流量信息
	 */
	public static final String GET_PHONE_MESSAGE = "00056";
	/**
	 * 体感模式
	 */
	public static final String SET_BODY_ANSWER = "00057";
	/**
	 * 备注:此指令对应两个地方
	 * 
	 * 1. 帐户-》小飞飞-》解除绑定
	 * 
	 * 2. 帐户-》家庭成员-》管理员-》退出家庭组-》解绑所有人
	 */
	// public static final String REMOVE_szalarm_CMD = "00060";
	/**
	 * 获取家庭成员列表
	 */
	public static final String MEMBERS_OF_FIMILY_CMD = "00061";

	/**
	 * 获取小伙伴列表
	 */
	public static final String GUYS_INFOS_CMD = "00062";
	/**
	 * 删除小伙伴
	 */
	public static final String DELETE_GUYS_CMD = "00063";
	/**
	 * 离开家庭
	 */
	public static final String LEAVE_ALONE_CMD = "00064";
	/**
	 * 修改家庭成员信息
	 */
	public static final String CHANGE_MEMBERINFOS_CMD = "00065";

	/**
	 * 变更管理员指令
	 */
	public static final String CHANGE_ADMIN_CMD = "00066";
	/**
	 * 意见反馈
	 */
	public static final String FEED_BACK_CMD = "00067";
	/**
	 * 闹钟信息列表
	 */
	public static final String CLOCK_INFOS_CMD = "00068";

	/**
	 * 铃声信息
	 */
	public static final String RING_MESSAGE_CMD = "00069";
	/**
	 * 聊天时保持长连接的心跳包请求cmd
	 */
	public static final String HEART_PACKAGE_CMD = "00070";
	/**
	 * 客户端发送语音到终端
	 */
	public static final String CHAT_VOICE_CMD = "00071";
	/**
	 * 修改用户登录密码
	 */
	public static final String CHANGE_USER_PSW = "10001";

	/**
	 * 找回密码重新设置
	 */
	public static final String CHANGE_USER_PSW_LOADING = "10010";
	/**
	 * 新用户注册获取验证码
	 */
	public static final String GET_PHONE_CODE = "20001";
	/**
	 * 找回密码
	 */
	public static final String GET_BACK_PSW_CMD = "20002";

	/**
	 * 查看验证码是否正确
	 */
	public static final String GET_CODE_STATE_CMD = "20003";

	/**
	 * .获取终端列表
	 */
	public static final String GET_WATCH_LIST_CMD = "30001";
	/**
	 * 编辑终端
	 */
	public static final String EDIT_WATCH_BASIC = "30003";
	/**
	 * 查询绑定的imei是否可用
	 */
	public static final String CHECK_IMEI_CMD = "80003";

	/**
	 * 上课禁用更改接口后指令
	 */
	public static final String STUDY_MODEL_SAVE_CMD = "80049";

	/**
	 * 管理员添加新成员
	 */
	public static final String FAMILY_ADD_NEW_MEMBER_CMD = "80065";

	/**
	 * socket超时短
	 */
	public static final int SOCKET_TIME_OUT_SHORT = 1000;
	/**
	 * socket超时中长
	 */
	public static final int SOCKET_TIME_OUT_LONG = 5000;
	/**
	 * socket超时中
	 */
	public static final int SOCKET_TIME_OUT_MIDDLE = 3000;
	/**
	 * 最大超时次数
	 */
	public static final int SOCKET_TIME_OUT_MAXCOUNT = 3;

}