package com.zgcar.com.main.model;

/**
 * 终端列表实体类
 * 
 */
public class TerminalListInfos {

	// 终端ID
	private String imei;
	// 终端名称
	private String name;
	// 终端手机号码
	private String number;
	// 年龄
	private int age;
	// 身高
	private int height;
	// 体重
	private int weight;
	// 性别 0:男性，1：女性
	private int sex;
	// 生日
	private String birthday;
	// 过期时间
	private String servicedate;
	// 1:在线，0:离线
	private int online;
	// 蓝牙MAC地址;当未绑定时为”0”
	private String bl_mac;
	// 蓝牙防走散模报警开关标志，
	// “ON” 为开启
	// “OFF’ 为关闭
	private String bl_switch;
	// 图片版本，客户端只检测到此图片与本地数据库 image_flag不相同时，进行下载，调用下载指令。
	private int image_flag;
	// 图片下载地址，
	private String image_link;

	private boolean admin;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getServicedate() {
		return servicedate;
	}

	public void setServicedate(String servicedate) {
		this.servicedate = servicedate;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public String getBl_mac() {
		return bl_mac;
	}

	public void setBl_mac(String bl_mac) {
		this.bl_mac = bl_mac;
	}

	public String getBl_switch() {
		return bl_switch;
	}

	public void setBl_switch(String bl_switch) {
		this.bl_switch = bl_switch;
	}

	public int getImage_flag() {
		return image_flag;
	}

	public void setImage_flag(int image_flag) {
		this.image_flag = image_flag;
	}

	public String getImage_link() {
		return image_link;
	}

	public void setImage_link(String image_link) {
		this.image_link = image_link;
	}

	

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public String toString() {
		return "deviceInfos [imei=" + imei + ", name=" + name + ", number="
				+ number + ", age=" + age + ", height=" + height + ", weight="
				+ weight + ", sex=" + sex + ", birthday=" + birthday
				+ ", servicedate=" + servicedate + ", online=" + online
				+ ", bl_mac=" + bl_mac + ", bl_switch=" + bl_switch
				+ ", image_flag=" + image_flag + ", image_link=" + image_link
				+ "]";
	}

}
