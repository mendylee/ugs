package com.xrk.usd.dal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO数据处理辅助对象，存储可以执行的SQL，参数对象等
 * DaoProcessObj: DaoProcessObj.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年9月10日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DaoProcessObj
{
	private String hsql;
	private boolean isMapParams;
	private Map<String, Object> dictParams = null;
	private List<Object> lsParams = null;
	
	public DaoProcessObj(String hsql){
		this(hsql, hsql.contains(":"));
	}
	
	public DaoProcessObj(String hsql, boolean isMapParams){
		this.setHsql(hsql);
		this.setMapParams(isMapParams);
		this.dictParams = new HashMap<String, Object>();
		this.lsParams = new ArrayList<Object>();
	}
	
	public void addParams(Object obj){
		this.lsParams.add(obj);
	}
	
	public void addParams(String paramName, Object paramVal){
		this.dictParams.put(paramName, paramVal);
	}
	
	public List<Object> getParams(){
		return this.lsParams;
	}
	
	public Map<String, Object> getMapParams(){
		return this.dictParams;
	}
	
	public String getHsql()
    {
	    return hsql;
    }
	public void setHsql(String hsql)
    {
	    this.hsql = hsql;
    }
	public boolean isMapParams()
    {
	    return isMapParams;
    }
	public void setMapParams(boolean isMapParams)
    {
	    this.isMapParams = isMapParams;
    }	
}
