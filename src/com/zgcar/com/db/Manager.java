package com.zgcar.com.db;

import java.util.ArrayList;

import com.zgcar.com.account.model.MessageInfos;

public interface Manager {



	/**
	 * ��ѯ������Ϣ
	 * 
	 * @return
	 */
	ArrayList<MessageInfos> query();

	/**
	 * ���뵥��������Ϣ
	 * 
	 * @param infos
	 */
	void insert(MessageInfos infos);

	/**
	 * 
	 * ���ݱ���ɾ����
	 * 
	 * @param tableName1
	 *            ��������
	 * @param tableName2���ѱ���
	 */
	void deletTable(String imei);

	/**
	 * 
	 * ���ݱ���ɾ����
	 * 
	 * @param tableName1
	 *            ��������
	 * @param tableName2���ѱ���
	 */
	void deletPhoneTable(String imei);

	void closeDb();
}
