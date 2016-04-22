package com.zgcar.com.account.model;

import java.io.Serializable;

public class FamilyParentInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// imei号
	private String imei;
	// 账号
	private String user_name;
	// 称呼
	private String u_name;
	// 昵称
	private String nick_name;
	// 是我自己
	private boolean isMysel = false;
	// 亲情号码0：不是1：是
	private boolean sos;
	// 管理员0：不是，1：是
	private boolean admin;
	// 头像标记
	private int image_flag;
	// 头像下载地址
	private String image_link;

	// 头像本地缓存地址
	private String localPath;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public boolean isMysel() {
		return isMysel;
	}

	public void setMysel(boolean isMysel) {
		this.isMysel = isMysel;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getU_name() {
		return u_name;
	}

	public void setU_name(String u_name) {
		this.u_name = u_name;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public boolean getSos() {
		return sos;
	}

	public void setSos(boolean sos) {
		this.sos = sos;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
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

	@Override
	public String toString() {
		return "FamilyParentInfos [imei=" + imei + ", user_name=" + user_name
				+ ", u_name=" + u_name + ", nick_name=" + nick_name
				+ ", isMysel=" + isMysel + ", sos=" + sos + ", admin=" + admin
				+ ", image_flag=" + image_flag + ", image_link=" + image_link
				+ "]";
	}

}
