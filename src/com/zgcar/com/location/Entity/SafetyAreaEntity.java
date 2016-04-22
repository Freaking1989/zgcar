package com.zgcar.com.location.Entity;

import java.io.Serializable;

/**
 * ���л�ʵ��
 * @author mddoscar
 * @name ZoneSafetyEntity
 */
public class SafetyAreaEntity  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ����Χ��id
	private int id;
	
	//����Χ������
	private String name;
	
	// ����
	private double lo;
	
	// γ��
	private double la;
	
	// �뾶
	private int rad;
	
	// ������ʽ 0:����Χ����������ʾ
	//	1:��Χ��������ʾ
	//	2:��Χ��������ʾ
	private int alarmtype;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getRad() {
		return rad;
	}

	public void setRad(int rad) {
		this.rad = rad;
	}

	public int getAlarmtype() {
		return alarmtype;
	}

	public void setAlarmtype(int alarmtype) {
		this.alarmtype = alarmtype;
	}

	@Override
	public String toString() {
		return "ZoneSafetyEntity [id=" + id + ", name=" + name + ", lo=" + lo
				+ ", la=" + la + ", rad=" + rad + ", alarmtype=" + alarmtype
				+ "]";
	}
	
}
