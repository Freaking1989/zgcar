package com.zgcar.com.history.entity;

import java.io.Serializable;

public class RedFlowerNumInfos implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ��ǰ��Ϣ�Ķ�Ӧ��imei��,��ͬ����Ҫ���»�ȡ��Ϣ
	 */
	private String imei;
	/**
	 * С�컨����
	 */
	private int quantity = 0;
	/**
	 * Ŀ������
	 */
	private int tote = 0;
	/**
	 * ��������
	 */
	private String reward;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTote() {
		return tote;
	}

	public void setTote(int tote) {
		this.tote = tote;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	@Override
	public String toString() {
		return "RedFlowerNumInfos [imei=" + imei + ", quantity=" + quantity
				+ ", tote=" + tote + ", reward=" + reward + "]";
	}

}
