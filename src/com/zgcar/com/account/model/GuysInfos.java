package com.zgcar.com.account.model;

import java.io.Serializable;

/**
 * С�����Ϣʵ����
 * 
 */
public class GuysInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ��ǰ������imei�������ж��Ƿ���� ����û��
	 */
	private String mainImei;
	/**
	 * С�������
	 */
	private String name;

	/**
	 * �Ա�0:���ԣ�1��Ů��
	 */
	private int sex;

	/**
	 * ����
	 */
	private int age;

	/**
	 * С����imei��
	 */
	private String imei;

	/**
	 * 1����flagΪ0ʱ����������ͷ�񣬿ͻ��˵��ñ���Ĭ��ͷ�� 2���ͻ���ÿ�λ�ȡ���û��б�ʱ����Ҫ��image_flag
	 * �뱾��image_flag���Աȣ��������ʱ������http����ͷ��
	 */
	private int imageFlag;

	/**
	 * ͼƬ���ص�ַ
	 */
	private String imageLink;

	/**
	 * ͼƬ���ػ����ַ
	 */
	private String localPath;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}


	public String getMainImei() {
		return mainImei;
	}

	public void setMainImei(String mainImei) {
		this.mainImei = mainImei;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getImageFlag() {
		return imageFlag;
	}

	public void setImageFlag(int imageFlag) {
		this.imageFlag = imageFlag;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	@Override
	public String toString() {
		return "GuysInfos [mainImei=" + mainImei + ", name=" + name + ", sex="
				+ sex + ", imei=" + imei + ", imageFlag=" + imageFlag
				+ ", imageLink=" + imageLink + "]";
	}

}
