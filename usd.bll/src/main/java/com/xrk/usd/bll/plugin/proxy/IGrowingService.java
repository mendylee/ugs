package com.xrk.usd.bll.plugin.proxy;

import java.util.List;

import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.common.exception.InternalServerException;
import com.xrk.usd.dal.entity.UgsGrowingRuleParameter;
import com.xrk.usd.dal.entity.UgsUserPointHistory;

public interface IGrowingService
{

	/**
	 * 
	 * 注册一个用户积分信息变更观察者  
	 *    
	 * @param observer
	 * @return
	 */
	boolean registerObserver(IUserChangeNotice observer);
	
	/**
	 * 
	 * 移除一个用户积分信息变更观察者  
	 *    
	 * @param observer
	 * @return
	 */
	boolean unregisterObserver(IUserChangeNotice observer);
	/**
	 * 
	 * 更新用户积分  
	 *    
	 * @param uid
	 * @param pointTypeCode
	 * @param ruleCode
	 * @param point
	 * @param description
	 * @return
	 */
    boolean updateUserPoint(long uid,String growingTypeCode, String pointTypeCode, String ruleCode, int point, String description);

   
    /**
     * 根据条件查询历史记录最新一条
     * @param uid 用户id
     * @param ruleCode 规则代码
     * @return
     */
    UgsUserPointHistory getLastestHistory(long uid,String growingTypeCode,String ruleCode);

}
