package com.xrk.usd.bll.vo;

import java.util.concurrent.atomic.AtomicInteger;

public class UserPointInvokeVo
{
	private long uid;
	private String growingTypeCode;
	private String ruleCode;
	private String description;
	private AtomicInteger invokeNum = new AtomicInteger();
	
	public UserPointInvokeVo(){
//		invokeNum.set(0);
	}
	
	public long getUid()
	{
		return uid;
	}
	public void setUid(long uid)
	{
		this.uid = uid;
	}
	public String getGrowingTypeCode()
	{
		return growingTypeCode;
	}
	public void setGrowingTypeCode(String growingTypeCode)
	{
		this.growingTypeCode = growingTypeCode;
	}
	public String getRuleCode()
	{
		return ruleCode;
	}
	public void setRuleCode(String ruleCode)
	{
		this.ruleCode = ruleCode;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public int getInvokeNum()
    {
	    return invokeNum.get();
    }
	
	public void addInvokeNum()
    {
	    invokeNum.incrementAndGet();
    }
}
