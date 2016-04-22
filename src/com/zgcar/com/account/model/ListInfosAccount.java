package com.zgcar.com.account.model;

import java.util.List;

/**
 * account信息缓存集合
 * 
 */
public class ListInfosAccount {

	/**
	 * 家庭成员的家长信息
	 */
	private static List<FamilyParentInfos> familyList;

	public static List<FamilyParentInfos> getFamilyList() {
		return familyList;
	}

	public static void setFamilyList(List<FamilyParentInfos> familyList) {
		ListInfosAccount.familyList = familyList;
	}

}
