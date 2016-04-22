package com.zgcar.com.account.model;

import java.io.Serializable;

public class StudyModelInfos implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 序号
	 */
	private int work_sn;
	/**
	 * 上课时间
	 */
	private String start_time;
	/**
	 * 下课时间
	 */
	private String stop_time;
	/**
	 * 星期 共8个字符串 00000000 第1位: 重复10000000 不重复00000000
	 * 
	 * 第2位: 周日01000000 ........... 第8位: 周六00000001
	 */
	private String week;

	/**
	 * 闹钟时间描述
	 */
	private String weekDesc;

	/**
	 * 闹钟开关 0: 关闭 1：开启
	 */
	private int on_off;

	private boolean isOpen;

	private boolean isRepeat;

	
	
	
	public String getWeekDesc() {
		return weekDesc;
	}

	public void setWeekDesc(String weekDesc) {
		this.weekDesc = weekDesc;
	}


	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}

	public int getWork_sn() {
		return work_sn;
	}

	public void setWork_sn(int work_sn) {
		this.work_sn = work_sn;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getStop_time() {
		return stop_time;
	}

	public void setStop_time(String stop_time) {
		this.stop_time = stop_time;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public int getOn_off() {
		return on_off;
	}

	public void setOn_off(int on_off) {
		this.on_off = on_off;
	}

	@Override
	public String toString() {
		return "StudyModelInfos [work_sn=" + work_sn + ", start_time="
				+ start_time + ", stop_time=" + stop_time + ", week=" + week
				+ ", weekDesc=" + weekDesc + ", on_off=" + on_off + ", isOff="
				+ isOpen + ", isRepeat=" + isRepeat + "]";
	}

	

}
