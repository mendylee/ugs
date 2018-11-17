package com.xrk.usd.bll.vo;

import java.util.List;

public class UserInfoVo
{
	private String uid;
	private String typeCode;
	private String typeName;
	private List<UserPointVo> points;
	
	public String getUid()
	{
		return uid;
	}
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	public String getTypeCode()
	{
		return typeCode;
	}
	public void setTypeCode(String typeCode)
	{
		this.typeCode = typeCode;
	}
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
	public List<UserPointVo> getPoints()
	{
		return points;
	}
	public void setPoints(List<UserPointVo> points)
	{
		this.points = points;
	}
}
