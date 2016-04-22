package com.zgcar.com.location.Entity;

import java.io.Serializable;

/**
 * 序列化实体
 * @author mddoscar
 * @name ZoneSafetyEntity
 */
public class SafetyAreaEntity  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 电子围栏id
	private int id;
	
	//电子围栏名称
	private String name;
	
	// 经度
	private double lo;
	
	// 纬度
	private double la;
	
	// 半径
	private int rad;
	
	// 报警方式 0:进出围栏都报警提示
	//	1:出围栏报警提示
	//	2:进围栏报警提示
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
