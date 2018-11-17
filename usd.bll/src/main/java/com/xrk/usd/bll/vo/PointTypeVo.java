package com.xrk.usd.bll.vo;

public class PointTypeVo
{
	private int pointTypeId;
	private String growingTypeCode;
	private String growingTypeName;
	private String pointTypeCode;
	private String pointTypeName;
	
	public PointTypeVo(int typeId, String pointTypeCode, String pointTypeName,
	                   String growingTypeCode, String growingTypeName){
		this.pointTypeCode = pointTypeCode;
		this.pointTypeId = typeId;
		this.pointTypeName = pointTypeName;
		this.growingTypeCode = growingTypeCode;
		this.growingTypeName = growingTypeName;
	}
	
	public int getPointTypeId()
    {
	    return pointTypeId;
    }
	public void setPointTypeId(int pointTypeId)
    {
	    this.pointTypeId = pointTypeId;
    }
	public String getPointTypeCode()
    {
	    return pointTypeCode;
    }
	public void setPointTypeCode(String pointTypeCode)
    {
	    this.pointTypeCode = pointTypeCode;
    }
	public String getPointTypeName()
    {
	    return pointTypeName;
    }
	public void setPointTypeName(String pointTypeName)
    {
	    this.pointTypeName = pointTypeName;
    }

	public String getGrowingTypeCode()
    {
	    return growingTypeCode;
    }

	public void setGrowingTypeCode(String growingTypeCode)
    {
	    this.growingTypeCode = growingTypeCode;
    }

	public String getGrowingTypeName()
    {
	    return growingTypeName;
    }

	public void setGrowingTypeName(String growingTypeName)
    {
	    this.growingTypeName = growingTypeName;
    }
}
