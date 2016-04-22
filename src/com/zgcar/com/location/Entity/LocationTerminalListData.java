package com.zgcar.com.location.Entity;

//�ն�λ��
public class LocationTerminalListData {

	// �켣���кţ����ԽС��ʱ��Խ�磩
	private int number;
	// ��λ�ɼ�ʱ��
	private String gpstime;
	// ͣ��ʱ�� (��λ����)
	private int continued;
	// ��λ��ʽ
	// 0:GPS��λ
	// 1:LBS��λ
	private int location_type;
	// ����
	private double lo;
	// γ��
	private double la;
	// �ٶ� (��λkm/h)
	private double speed;
	// ����: 0~360��
	private int direction;
	// ��ص���(��λ%)
	private int bat;
	// GSM�ź�(��λ%)
	private int signal;
	// ��������λ����
	private Double temp;
	// ����
	private String status;
	// ����
	private String alarm;
	// ��ַ��Ϣ
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
