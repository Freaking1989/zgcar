package com.zgcar.com.db;

import java.util.ArrayList;

import com.zgcar.com.account.model.MessageInfos;

public interface Manager {



	/**
	 * 查询话费信息
	 * 
	 * @return
	 */
	ArrayList<MessageInfos> query();

	/**
	 * 插入单条话费信息
	 * 
	 * @param infos
	 */
	void insert(MessageInfos infos);

	/**
	 * 
	 * 根据表名删除表
	 * 
	 * @param tableName1
	 *            语音表名
	 * @param tableName2话费表名
	 */
	void deletTable(String imei);

	/**
	 * 
	 * 根据表名删除表
	 * 
	 * @param tableName1
	 *            语音表名
	 * @param tableName2话费表名
	 */
	void deletPhoneTable(String imei);

	void closeDb();
}
