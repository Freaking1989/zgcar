package com.zgcar.com.account.model;

import java.io.Serializable;

/**
 * 小伙伴信息实体类
 * 
 */
public class GuysInfos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前宝宝的imei号用于判断是否更新 现在没用
	 */
	private String mainImei;
	/**
	 * 小伙伴名称
	 */
	private String name;

	/**
	 * 性别0:男性，1：女性
	 */
	private int sex;

	/**
	 * 年龄
	 */
	private int age;

	/**
	 * 小伙伴的imei号
	 */
	private String imei;

	/**
	 * 1、当flag为0时，服务器无头像，客户端调用本地默认头像 2、客户端每次获取到用户列表时，需要将image_flag
	 * 与本地image_flag做对比，当不相等时，调用http下载头像。
	 */
	private int imageFlag;

	/**
	 * 图片下载地址
	 */
	private String imageLink;

	/**
	 * 图片本地缓存地址
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
