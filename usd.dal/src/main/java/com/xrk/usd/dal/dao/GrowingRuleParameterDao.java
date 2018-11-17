package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingRuleParameter;

import java.util.List;

/**
 * 成长规则参数信息DAO
 */
public class GrowingRuleParameterDao extends DaoBase<UgsGrowingRuleParameter>{

    /**
     * 根据规则列出参数
     * @param rid 规则id
     * @return 参数列表
     */
    public List<UgsGrowingRuleParameter> findByRule(Integer rid){
        DaoProcessObj procObj = new DaoProcessObj("SELECT c FROM UgsGrowingRuleParameter c where c.ugsGrowingRuleList.ruleListId=:rid", true);
        procObj.addParams("rid", rid);
        List<UgsGrowingRuleParameter> dd = super.query(procObj);
        return dd;
    }
}
