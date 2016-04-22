package com.zgcar.com.account.model;

import java.io.Serializable;

/**
 * 我的闹钟list集合实体类
 * 
 */
public class MyAlarmInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * imei
	 */
	private String imei;

	/**
	 * 闹钟序号
	 */
	private int alarm_sn;
	/**
	 * 是否重复
	 */
	private boolean isRepeat;

	/**
	 * 闹钟时间
	 */
	private String time;
	/**
	 * 星期 共8个字符串 00000000 第1位: 重复10000000 不重复00000000 第2位: 周日01000000
	 * ........... 第8位: 周六00000001
	 */
	private String week;

	private String weekDesc;

	/**
	 * 闹钟开关 0: 关闭 1：开启
	 */
	private int on_off;
	/***
	 * 
	 * 闹钟铃声0:起床 1:睡觉 2:自定义(大于2的另何数字)
	 */
	private int alarm_ring;
	/***
	 * 
	 * 铃声标记
	 */
	private int alarmring_flag;
	/***
	 * 
	 * 闹钟铃声下载路径 备注：只要alarmring_flag大于0而且与本地不相等时，客户端需要去下载铃声。
	 */
	private String alarmring_link;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getWeekDesc() {
		return weekDesc;
	}

	public void setWeekDesc(String weekDesc) {
		this.weekDesc = weekDesc;
	}

	public int getAlarm_sn() {
		return alarm_sn;
	}

	public void setAlarm_sn(int alarm_sn) {
		this.alarm_sn = alarm_sn;
	}

	public String getTime() {
		return time;
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getOn_off() {
		return on_off;
	}

	public void setOn_off(int on_off) {
		this.on_off = on_off;
	}

	public int getAlarm_ring() {
		return alarm_ring;
	}

	public void setAlarm_ring(int alarm_ring) {
		this.alarm_ring = alarm_ring;
	}

	public int getAlarmring_flag() {
		return alarmring_flag;
	}

	public void setAlarmring_flag(int alarmring_flag) {
		this.alarmring_flag = alarmring_flag;
	}

	public String getAlarmring_link() {
		return alarmring_link;
	}

	public void setAlarmring_link(String alarmring_link) {
		this.alarmring_link = alarmring_link;
	}

	@Override
	public String toString() {
		return "MyAlarmInfos [imei=" + imei + ", alarm_sn=" + alarm_sn
				+ ", isRepeat=" + isRepeat + ", time=" + time + ", week="
				+ week + ", weekDesc=" + weekDesc + ", on_off=" + on_off
				+ ", alarm_ring=" + alarm_ring + ", alarmring_flag="
				+ alarmring_flag + ", alarmring_link=" + alarmring_link + "]";
	}

}
