package com.xrk.usd.bll.plugin.proxy;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserChangeNoticeTask implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserChangeNoticeTask.class);
	long uid =0;
	String growingTypeCode = "";
	String pointTypeCode = "";
	String ruleCode = "";
	int point = 0;
	Set<IUserChangeNotice> observer;

	public UserChangeNoticeTask(long uid,String growingTypeCode, String pointTypeCode, String ruleCode, int point, Set<IUserChangeNotice> notices) {
	    this.uid = uid;
	    this.growingTypeCode = growingTypeCode;
	    this.pointTypeCode = pointTypeCode;
	    this.ruleCode = ruleCode;
	    this.point = point;
	    this.observer = notices;
    }
	
	@Override
	public void run()
	{
		if(observer == null){
			return;
		}
		
		try
		{
			for(IUserChangeNotice notice : observer){
				if(notice == null){
					continue;
				}
				try{
					notice.onUserPointChange(uid, growingTypeCode, pointTypeCode, ruleCode, point);
				}
				catch(Exception ex){
					LOGGER.error("notice observer {} error! errmsg={}", notice, ex.getMessage());
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		}
		catch(Exception ex){
			LOGGER.error(ex.getMessage(), ex);
		}
	}

}
