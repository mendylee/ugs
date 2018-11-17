package com.xrk.usd.bll.plugin;

import java.util.ArrayList;
import java.util.List;

import com.xrk.usd.bll.plugin.proxy.IGrowingService;

public abstract class DefaultGrowingPlugin implements IGrowingPlugin
{
	protected String name;
	protected String version;
	protected String description;
	protected IGrowingService service;
	protected List<PluginParameterDefine> lsParams;
	
	public DefaultGrowingPlugin(){
		lsParams = new ArrayList<PluginParameterDefine>();
	}
	
	/**
	 * 
	 * 添加插件参数  
	 *    
	 * @param pluginParam
	 */
	protected void addParams(PluginParameterDefine pluginParam){
		lsParams.add(pluginParam);
	}
	
	@Override
	public List<PluginParameterDefine> getParameterDefine(){
		return lsParams;
	}

	@Override
	public String getName()
	{
		return name;
	}

	protected void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	protected void setVersion(String version)
	{
		this.version = version;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	protected void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public IGrowingService getGrowingService()
	{
		return service;
	}

	@Override
	public void setGrowingService(IGrowingService service)
	{
		this.service = service;
	}
	
}
