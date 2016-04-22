package com.zgcar.com.account.model;

import java.io.Serializable;

public class MessageInfos implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	private String time;

	private int flag;

	private int id;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MessageInfos [message=" + message + ", flag=" + flag + ", id="
				+ id + "]";
	}

}
