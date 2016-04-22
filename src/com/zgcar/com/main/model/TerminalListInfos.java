package com.zgcar.com.main.model;

/**
 * �ն��б�ʵ����
 * 
 */
public class TerminalListInfos {

	// �ն�ID
	private String imei;
	// �ն�����
	private String name;
	// �ն��ֻ�����
	private String number;
	// ����
	private int age;
	// ���
	private int height;
	// ����
	private int weight;
	// �Ա� 0:���ԣ�1��Ů��
	private int sex;
	// ����
	private String birthday;
	// ����ʱ��
	private String servicedate;
	// 1:���ߣ�0:����
	private int online;
	// ����MAC��ַ;��δ��ʱΪ��0��
	private String bl_mac;
	// ��������ɢģ�������ر�־��
	// ��ON�� Ϊ����
	// ��OFF�� Ϊ�ر�
	private String bl_switch;
	// ͼƬ�汾���ͻ���ֻ��⵽��ͼƬ�뱾�����ݿ� image_flag����ͬʱ���������أ���������ָ�
	private int image_flag;
	// ͼƬ���ص�ַ��
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
