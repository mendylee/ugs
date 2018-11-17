package com.xrk.usd.bll.plugin.proxy;

public interface IUserChangeNotice
{
	/**
	 * 
	 * 在用户积分发生变化的时候触发  
	 *    
	 * @param uid
	 * @param growingTypeCode
	 * @param pointTypeCode
	 * @param ruleCode
	 * @param point
	 */
	void onUserPointChange(long uid,String growingTypeCode, String pointTypeCode, String ruleCode, int point);
}
