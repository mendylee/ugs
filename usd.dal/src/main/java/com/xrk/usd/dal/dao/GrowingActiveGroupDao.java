package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingActiveGroup;
import com.xrk.usd.dal.entity.UgsGrowingRuleList;
import com.xrk.usd.dal.entity.UgsGrowingType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

public class GrowingActiveGroupDao extends DaoBase<UgsGrowingActiveGroup> {
    public Map<UgsGrowingType, Set<UgsGrowingRuleList>> getGrowingMap() {
        EntityManager entityManager = factory.createEntityManager();
        List<UgsGrowingActiveGroup> growingActiveGroups;
        Map<UgsGrowingType, Set<UgsGrowingRuleList>> resultMap = new HashMap<>();

        try {
            TypedQuery<UgsGrowingActiveGroup> query = entityManager.createQuery("FROM UgsGrowingActiveGroup t1 INNER JOIN FETCH t1.ugsGrowingRuleGroup t2 INNER JOIN FETCH t2.ugsGrowingRuleLists t3 INNER JOIN FETCH t3.ugsGrowingRuleParameters INNER JOIN FETCH t3.ugsGrowingRulePlugin", UgsGrowingActiveGroup.class);
            growingActiveGroups = query.getResultList();
        } finally {
            entityManager.close();
        }

        for (UgsGrowingActiveGroup growingActiveGroup : growingActiveGroups) {
            Set<UgsGrowingRuleList> growingRuleLists;

            if (resultMap.containsKey(growingActiveGroup.getUgsGrowingType())) {
                growingRuleLists = resultMap.get(growingActiveGroup.getUgsGrowingType());
            } else {
                growingRuleLists = new HashSet<>();
                resultMap.put(growingActiveGroup.getUgsGrowingType(), growingRuleLists);
            }

            growingActiveGroup.getUgsGrowingRuleGroup().getUgsGrowingRuleLists().forEach(item -> growingRuleLists.add(item));
        }

        return resultMap;
    }
}
