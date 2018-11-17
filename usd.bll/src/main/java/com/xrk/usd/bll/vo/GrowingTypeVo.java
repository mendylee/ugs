package com.xrk.usd.bll.vo;

import com.xrk.usd.dal.entity.UgsGrowingType;

public class GrowingTypeVo {
    private String typeCode;
    private String typeName;

    public GrowingTypeVo(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return this.typeCode;
    }

    public String getTypeName() {
        return this.typeName;
    }

	public static GrowingTypeVo parse(UgsGrowingType type)
    {
	    GrowingTypeVo vo = new GrowingTypeVo(type.getTypeCode(), type.getTypeName());
	    return vo;
    }
}
