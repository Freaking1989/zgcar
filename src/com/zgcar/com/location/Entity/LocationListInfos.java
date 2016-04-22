package com.zgcar.com.location.Entity;

import java.util.List;

public class LocationListInfos {
	private static List<HistoryEntity> historylistInfos;

	public static List<HistoryEntity> getHistorylistInfos() {
		return historylistInfos;
	}

	public static void setHistorylistInfos(List<HistoryEntity> historylistInfos) {
		LocationListInfos.historylistInfos = historylistInfos;
	}

}
