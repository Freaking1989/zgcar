package com.zgcar.com.location.Entity;

//终端位置
public class LocationTerminalListData {

	// 轨迹序列号（序号越小，时间越早）
	private int number;
	// 定位采集时间
	private String gpstime;
	// 停留时间 (单位分钟)
	private int continued;
	// 定位方式
	// 0:GPS定位
	// 1:LBS定位
	private int location_type;
	// 经度
	private double lo;
	// 纬度
	private double la;
	// 速度 (单位km/h)
	private double speed;
	// 航向: 0~360度
	private int direction;
	// 电池电量(单位%)
	private int bat;
	// GSM信号(单位%)
	private int signal;
	// 步数（单位步）
	private Double temp;
	// 保留
	private String status;
	// 保留
	private String alarm;
	// 地址信息
	private String address;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getGpstime() {
		return gpstime;
	}

	public void setGpstime(String gpstime) {
		this.gpstime = gpstime;
	}

	public int getContinued() {
		return continued;
	}

	public void setContinued(int continued) {
		this.continued = continued;
	}

	public int getLocation_type() {
		return location_type;
	}

	public void setLocation_type(int location_type) {
		this.location_type = location_type;
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

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getBat() {
		return bat;
	}

	public void setBat(int bat) {
		this.bat = bat;
	}

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

	
	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "LocationTerminalListData [number=" + number + ", gpstime="
				+ gpstime + ", continued=" + continued + ", location_type="
				+ location_type + ", lo=" + lo + ", la=" + la + ", speed="
				+ speed + ", direction=" + direction + ", bat=" + bat
				+ ", signal=" + signal + ", temp=" + temp + ", status="
				+ status + ", alarm=" + alarm + ", address=" + address + "]";
	}

	
}
