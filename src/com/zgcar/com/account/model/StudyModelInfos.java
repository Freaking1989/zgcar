package com.zgcar.com.account.model;

import java.io.Serializable;

public class StudyModelInfos implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ���
	 */
	private int work_sn;
	/**
	 * �Ͽ�ʱ��
	 */
	private String start_time;
	/**
	 * �¿�ʱ��
	 */
	private String stop_time;
	/**
	 * ���� ��8���ַ��� 00000000 ��1λ: �ظ�10000000 ���ظ�00000000
	 * 
	 * ��2λ: ����01000000 ........... ��8λ: ����00000001
	 */
	private String week;

	/**
	 * ����ʱ������
	 */
	private String weekDesc;

	/**
	 * ���ӿ��� 0: �ر� 1������
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
