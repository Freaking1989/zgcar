package com.zgcar.com.main.model;

import java.util.ArrayList;
import java.util.List;

import com.zgcar.com.location.Entity.SafetyAreaEntity;

public class ListInfosEntity {
	/**
	 * 终端列表信息
	 */
	private static List<TerminalListInfos> terminalListInfos;

	/**
	 * 终端列表头像path集合
	 */
	private static List<String> pathList;

	/**
	 * 终端电子围栏
	 */
	private static List<SafetyAreaEntity> zoneSafetyData;



	public static List<TerminalListInfos> getTerminalListInfos() {
		if (terminalListInfos==null) {
			terminalListInfos=new ArrayList<TerminalListInfos>();
		}
		return terminalListInfos;
	}

	public static void setTerminalListInfos(
			List<TerminalListInfos> terminalListInfos) {
		ListInfosEntity.terminalListInfos = terminalListInfos;
	}


	// 电子围栏
	public static List<SafetyAreaEntity> getZoneSafetyData() {
		return zoneSafetyData;
	}

	public static void setZoneSafetyData(List<SafetyAreaEntity> zoneSafetyData) {

		ListInfosEntity.zoneSafetyData = zoneSafetyData;
	}

	public static List<String> getPathList() {
		if (pathList == null) {
			pathList = new ArrayList<String>();
			if (ListInfosEntity.getTerminalListInfos() != null) {
				for (int i = 0; i < ListInfosEntity.getTerminalListInfos()
						.size(); i++) {
					pathList.add("");
				}
			}
		}

		return pathList;
	}

	public static void setPathList(List<String> pathList) {
		ListInfosEntity.pathList = pathList;
	}

}
