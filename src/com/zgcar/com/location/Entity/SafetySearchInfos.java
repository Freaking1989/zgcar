package com.zgcar.com.location.Entity;

import java.io.Serializable;

public class SafetySearchInfos implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String address;

	private double la;

	private double lo;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLa() {
		return la;
	}

	public void setLa(double la) {
		this.la = la;
	}

	public double getLo() {
		return lo;
	}

	public void setLo(double lo) {
		this.lo = lo;
	}

	@Override
	public String toString() {
		return "SafetySearchInfos [address=" + address + ", la=" + la + ", lo="
				+ lo + "]";
	}

}
