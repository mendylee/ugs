package com.xrk.usd.bll.plugin;

public class PluginParameterDefine
{
	private String paramCode;
	private String paramName;
	private int paramType;
	private int controlType;
	private String description;
	
	
	public String getParamCode()
	{
		return paramCode;
	}
	public void setParamCode(String paramCode)
	{
		this.paramCode = paramCode;
	}
	public String getParamName()
	{
		return paramName;
	}
	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}
	public int getParamType()
	{
		return paramType;
	}
	public void setParamType(int paramType)
	{
		this.paramType = paramType;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public int getControlType()
    {
	    return controlType;
    }
	public void setControlType(int controlType)
    {
	    this.controlType = controlType;
    }
	
}
