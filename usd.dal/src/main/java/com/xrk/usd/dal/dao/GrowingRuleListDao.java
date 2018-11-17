package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingRuleList;

import java.util.List;

/**
 * 用户成长规则分组DAO
 */
public class GrowingRuleListDao extends DaoBase<UgsGrowingRuleList>{

    /**
     * 按会员体系类别查询分组
     * @param groupId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<UgsGrowingRuleList> findByGroup(Integer groupId){
        DaoProcessObj procObj = new DaoProcessObj("SELECT c FROM UgsGrowingRuleList c where ruleGroupId=:groupId", true);
        procObj.addParams("groupId", groupId);
        return super.query(procObj);
    }

    public List<UgsGrowingRuleList> findByPlugin(Integer pluginId){
        DaoProcessObj procObj = new DaoProcessObj("SELECT c FROM UgsGrowingRuleList c where pluginId=:pluginId", true);
        procObj.addParams("pluginId", pluginId);
        return  super.query(procObj);
    }

    public UgsGrowingRuleList findByCode(String code){
        DaoProcessObj procObj = new DaoProcessObj("SELECT c FROM UgsGrowingRuleList c where ruleCode=:code", true);
        procObj.addParams("code", code);
        List<UgsGrowingRuleList> list = super.query(procObj);
        return  list.isEmpty()?null:list.get(0);
    }

    @Override
    public boolean persist(Object entity) {
        boolean ret = super.persist(entity);
        if(ret){
        }
        return ret;
    }
}
