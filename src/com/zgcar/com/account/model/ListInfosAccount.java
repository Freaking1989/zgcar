package com.zgcar.com.account.model;

import java.util.List;

/**
 * account��Ϣ���漯��
 * 
 */
public class ListInfosAccount {

	/**
	 * ��ͥ��Ա�ļҳ���Ϣ
	 */
	private static List<FamilyParentInfos> familyList;

	public static List<FamilyParentInfos> getFamilyList() {
		return familyList;
	}

	public static void setFamilyList(List<FamilyParentInfos> familyList) {
		ListInfosAccount.familyList = familyList;
	}

}
