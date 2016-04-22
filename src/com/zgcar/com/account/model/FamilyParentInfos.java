package com.zgcar.com.account.model;

import java.io.Serializable;

public class FamilyParentInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// imei��
	private String imei;
	// �˺�
	private String user_name;
	// �ƺ�
	private String u_name;
	// �ǳ�
	private String nick_name;
	// �����Լ�
	private boolean isMysel = false;
	// �������0������1����
	private boolean sos;
	// ����Ա0�����ǣ�1����
	private boolean admin;
	// ͷ����
	private int image_flag;
	// ͷ�����ص�ַ
	private String image_link;

	// ͷ�񱾵ػ����ַ
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
