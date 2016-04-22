package com.zgcar.com.account.model;

import java.io.Serializable;

public class TerminalInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ��ǰimei��
	 */
	private String imei;
	/**
	 * ����
	 */
	private int volume;
	/**
	 * ����
	 */
	private int brightness;
	/**
	 * ��λģʽ 0:���� 1:ʡ��
	 */
	private int local_mode;
	/**
	 * ���ϰ�� 0:���� 1����
	 */
	private int wear;
	/**
	 * �¶����� 0:�� 1:��
	 */
	private int temperature_alarm;
	/**
	 * ����ģʽ 0:�� 1����
	 */
	private int mute;
	/**
	 * ���ģʽ 0:�� 1����
	 */
	private int bodyAnswer;
	/**
	 * �̼��汾��
	 */
	private String terminal_ver;
	/**
	 * ��ʱ��
	 */
	private String joinTime;

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getLocal_mode() {
		return local_mode;
	}

	public void setLocal_mode(int local_mode) {
		this.local_mode = local_mode;
	}

	public int getWear() {
		return wear;
	}

	public void setWear(int wear) {
		this.wear = wear;
	}

	public int getTemperature_alarm() {
		return temperature_alarm;
	}

	public void setTemperature_alarm(int temperature_alarm) {
		this.temperature_alarm = temperature_alarm;
	}

	public int getMute() {
		return mute;
	}

	public void setMute(int mute) {
		this.mute = mute;
	}

	public int getBodyAnswer() {
		return bodyAnswer;
	}

	public void setBodyAnswer(int bodyAnswer) {
		this.bodyAnswer = bodyAnswer;
	}

	public String getTerminal_ver() {
		return terminal_ver;
	}

	public void setTerminal_ver(String terminal_ver) {
		this.terminal_ver = terminal_ver;
	}

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public String toString() {
		return "TerminalInfos [imei=" + imei + ", volume=" + volume
				+ ", brightness=" + brightness + ", local_mode=" + local_mode
				+ ", wear=" + wear + ", temperature_alarm=" + temperature_alarm
				+ ", mute=" + mute + ", terminal_ver=" + terminal_ver
				+ ", joinTime=" + joinTime + "]";
	}

}
