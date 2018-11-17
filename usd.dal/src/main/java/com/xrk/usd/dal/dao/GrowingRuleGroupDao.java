package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingRuleGroup;

import java.util.List;

public class GrowingRuleGroupDao extends DaoBase<UgsGrowingRuleGroup>{

	/**
	 * 按会员体系类别查询分组
	 * @param typeCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UgsGrowingRuleGroup> findByType(String typeCode){
		DaoProcessObj procObj = new DaoProcessObj("SELECT c FROM UgsGrowingRuleGroup c where typeCode=:typeCode");
		procObj.addParams("typeCode", typeCode.toUpperCase());
		return (List<UgsGrowingRuleGroup>) super.query(procObj);
	}
}
