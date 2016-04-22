package com.zgcar.com.account.model;

import java.io.Serializable;

public class TerminalInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前imei号
	 */
	private String imei;
	/**
	 * 音量
	 */
	private int volume;
	/**
	 * 亮度
	 */
	private int brightness;
	/**
	 * 定位模式 0:正常 1:省电
	 */
	private int local_mode;
	/**
	 * 佩戴习惯 0:左手 1右手
	 */
	private int wear;
	/**
	 * 温度提醒 0:关 1:开
	 */
	private int temperature_alarm;
	/**
	 * 静音模式 0:关 1：开
	 */
	private int mute;
	/**
	 * 体感模式 0:关 1：开
	 */
	private int bodyAnswer;
	/**
	 * 固件版本号
	 */
	private String terminal_ver;
	/**
	 * 绑定时间
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
