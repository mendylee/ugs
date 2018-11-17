package com.xrk.usd.bll.vo;

import java.util.HashMap;
import java.util.Map;

import com.xrk.usd.bll.plugin.IGrowingPlugin;
import com.xrk.usd.bll.service.RulePluginService;

public class GrowingRuleVo
{
	private String growingTypeCode;
	private String ruleCode;
	private String ruleName;
	private String key;
	private IGrowingPlugin plugin;
	private Map<String, Object> params;
	
	public GrowingRuleVo(IGrowingPlugin plugin, String growingTypeCode, String ruleCode, String ruleName){
		this.plugin = plugin;
		this.growingTypeCode = growingTypeCode;
		this.ruleCode = ruleCode;
		this.ruleName = ruleName;
		this.key = RulePluginService.formatKey(growingTypeCode, ruleCode);
		this.params = new HashMap<String, Object>();
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

	public Map<String, Object> getParams()
	{
		return params;
	}

	public void setParams(Map<String, Object> params)
	{
		this.params = params;
	}
	
	public void addParams(String key, Object val){
		this.params.put(key, val);
	}

	public String getKey()
	{
		return key;
	}

	public IGrowingPlugin getPlugin()
    {
	    return plugin;
    }

	public String getRuleName()
    {
	    return ruleName;
    }

	public void setRuleName(String ruleName)
    {
	    this.ruleName = ruleName;
    }
	
}
