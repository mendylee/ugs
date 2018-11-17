package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsUserInfo;
import com.xrk.usd.dal.entity.UgsUserPointHistory;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserPointHistoryDao extends DaoBase<UgsUserPointHistory> {
    public List<UgsUserPointHistory> findBy(UgsUserInfo user, UgsGrowingType growingType, int pageSize, int pageIndex){
        DaoProcessObj procObj = new DaoProcessObj("SELECT t FROM UgsUserPointHistory t WHERE t.ugsUserInfo.uidSeqId = :uidSeqId AND t.typeCode = :typeCode ORDER BY t.historyId DESC");
        procObj.addParams("uidSeqId", user.getUidSeqId());
        procObj.addParams("typeCode", growingType.getTypeCode());
        return this.query(procObj, pageSize, pageIndex);
    }

    public List<UgsUserPointHistory> findBy(UgsUserInfo user, UgsGrowingType growingType, int pageSize, int pageIndex,Date beginDate,Date endDate,String ruleCodes){
    	endDate = formatEndDate(endDate);
    	
    	DaoProcessObj procObj = new DaoProcessObj("");
        String hql = "SELECT t FROM UgsUserPointHistory t WHERE t.ugsUserInfo.uidSeqId = :uidSeqId AND t.typeCode = :typeCode";
        if(null!=beginDate && null==endDate){
            hql += " and t.addDate> :beginDate";
            procObj.addParams("beginDate", beginDate);
        }else if(null==beginDate && null!=endDate){
            hql += " and t.addDate< :endDate";
            procObj.addParams("endDate", endDate);
        }else if(null!=beginDate && null!=endDate){
            hql += " and t.addDate between :beginDate and :endDate";
            procObj.addParams("beginDate", beginDate);
            procObj.addParams("endDate", endDate);
        }
                
        if(!StringUtils.isEmpty(ruleCodes)){
        	String[] aryRule = ruleCodes.split(",");
        	List<String> rules = new ArrayList<String>();
        	for(String rule : aryRule){
        		if(rule.isEmpty()){
        			continue;
        		}
        		rules.add(rule);
        	}
            hql += " and ruleCode in (:ruleCodes)";
            procObj.addParams("ruleCodes", rules);
        }
        hql += " ORDER BY t.historyId DESC";
        procObj.addParams("uidSeqId", user.getUidSeqId());
        procObj.addParams("typeCode", growingType.getTypeCode());
        procObj.setHsql(hql);
        procObj.setMapParams(true);
        return this.query(procObj, pageSize, pageIndex);
    }

    private Date formatEndDate(Date dt){
    	Calendar calEnd = Calendar.getInstance();
    	if(dt != null){
    		calEnd.setTime(dt);
    	}
    	else{
    		//将结束年限延后30年
    		calEnd.set(Calendar.YEAR, calEnd.get(Calendar.YEAR)+30);
    	}    	
    	calEnd.set(Calendar.HOUR_OF_DAY, 23);
    	calEnd.set(Calendar.MINUTE, 59);
    	calEnd.set(Calendar.SECOND, 59);
    	return calEnd.getTime();
    }
    
    public int countBy(UgsUserInfo user, UgsGrowingType growingType, Date beginDate,Date endDate,String ruleCodes){    	
    	endDate = formatEndDate(endDate);
    	
        DaoProcessObj procObj = new DaoProcessObj("");
        String hql = "select count(*) FROM UgsUserPointHistory t WHERE t.ugsUserInfo.uidSeqId = :uidSeqId AND t.typeCode = :typeCode";
        if(null==beginDate){
            hql += " and t.addDate< :endDate";
            procObj.addParams("endDate", endDate);
        }else if(null!=beginDate){
            hql += " and t.addDate between :beginDate and :endDate";
            procObj.addParams("beginDate", beginDate);
            procObj.addParams("endDate", endDate);
        }
        if(!StringUtils.isEmpty(ruleCodes)){
        	String[] aryRule = ruleCodes.split(",");
        	List<String> rules = new ArrayList<String>();
        	for(String rule : aryRule){
        		if(rule.isEmpty()){
        			continue;
        		}
        		rules.add(rule);
        	}
            hql += " and ruleCode in (:ruleCodes)";
            procObj.addParams("ruleCodes", rules);
        }
        procObj.addParams("uidSeqId", user.getUidSeqId());
        procObj.addParams("typeCode", growingType.getTypeCode());
        procObj.setHsql(hql);
        procObj.setMapParams(true);
        return this.getCount(procObj);
    }

    public UgsUserPointHistory get(long uidSeqId,String ruleCode){
        DaoProcessObj procObj = new DaoProcessObj("SELECT t FROM UgsUserPointHistory t WHERE t.ugsUserInfo.uidSeqId = :uidSeqId AND t.ruleCode = :ruleCode ORDER BY t.historyId DESC");
        procObj.addParams("uidSeqId", uidSeqId);
        procObj.addParams("ruleCode", ruleCode);
        return this.getSingleResult(procObj);
    }
}
