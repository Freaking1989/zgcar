package com.zgcar.com.account.model;

import java.io.Serializable;

/**
 * �ҵ�����list����ʵ����
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
	 * �������
	 */
	private int alarm_sn;
	/**
	 * �Ƿ��ظ�
	 */
	private boolean isRepeat;

	/**
	 * ����ʱ��
	 */
	private String time;
	/**
	 * ���� ��8���ַ��� 00000000 ��1λ: �ظ�10000000 ���ظ�00000000 ��2λ: ����01000000
	 * ........... ��8λ: ����00000001
	 */
	private String week;

	private String weekDesc;

	/**
	 * ���ӿ��� 0: �ر� 1������
	 */
	private int on_off;
	/***
	 * 
	 * ��������0:�� 1:˯�� 2:�Զ���(����2���������)
	 */
	private int alarm_ring;
	/***
	 * 
	 * �������
	 */
	private int alarmring_flag;
	/***
	 * 
	 * ������������·�� ��ע��ֻҪalarmring_flag����0�����뱾�ز����ʱ���ͻ�����Ҫȥ����������
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
