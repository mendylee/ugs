package com.xrk.usd.bll.vo;

import java.util.Date;

public class UserPointHistoryVo
{
	private String ruleCode;
	private String ruleName;
	private long addDate;
	private int point;
	private String description;
	
	public UserPointHistoryVo(){
		
	}
	
	public UserPointHistoryVo(String ruleCode, String ruleName, Date addDate, int point, String description){
		this.ruleCode = ruleCode;
		this.ruleName = ruleName;
		this.addDate = addDate.getTime();
		this.point = point;
		this.description = description;
	}
	
	public String getRuleCode()
	{
		return ruleCode;
	}
	public void setRuleCode(String ruleCode)
	{
		this.ruleCode = ruleCode;
	}
	public String getRuleName()
	{
		return ruleName;
	}
	public void setRuleName(String ruleName)
	{
		this.ruleName = ruleName;
	}
	public long getAddDate()
	{
		return addDate;
	}
	public void setAddDate(long addDate)
	{
		this.addDate = addDate;
	}
	public int getPoint()
	{
		return point;
	}
	public void setPoint(int point)
	{
		this.point = point;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}

}
