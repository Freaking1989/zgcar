package com.zgcar.com.main.model;

import java.io.Serializable;

public class NotifyMessageEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/****
	 * 
	 * 
	 * "imei":"865609152200061", "time":"2015/10/13 16:38:50", "litle":"��������",
	 * "msg":"������1�������˹�˾", "lo":"114.083471", "la":"22.629132",
	 * "geo":"�㶫ʡ����������������·,��޷���ӿƼ���˾����61��", "alarm":null
	 * 
	 * 
	 * **/
	/**
	 * ��ǰ�ն˵�imei��
	 */
	private String imei;
	/**
	 * ʱ��
	 */
	private String msg;
	/**
	 * ��������
	 */
	private String litle;
	/**
	 * ��ַ��Ϣ
	 */
	private String geo;
	/**
	 * ʱ��
	 */
	private String time;
	/**
	 * ����
	 */
	private double lo;

	/**
	 * γ��
	 */
	private double la;

	private String alarm;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLitle() {
		return litle;
	}

	public void setLitle(String litle) {
		this.litle = litle;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getLo() {
		return lo;
	}

	public void setLo(double lo) {
		this.lo = lo;
	}

	public double getLa() {
		return la;
	}

	public void setLa(double la) {
		this.la = la;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	@Override
	public String toString() {
		return "NotifyMessageEntity [imei=" + imei + ", msg=" + msg
				+ ", litle=" + litle + ", geo=" + geo + ", time=" + time
				+ ", lo=" + lo + ", la=" + la + ", alarm=" + alarm + "]";
	}
}
