package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsPointType;
import com.xrk.usd.dal.entity.UgsUserGradeConfig;

import java.util.List;

public class UserGradeConfigDao extends DaoBase<UgsUserGradeConfig> {
    @Deprecated
    public List<UgsUserGradeConfig> findBy(UgsGrowingType growingType, UgsPointType pointType) {
        DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsUserGradeConfig t WHERE t.ugsPointType.pointTypeId = :pointTypeId AND t.ugsGrowingType.typeCode = :typeCode");
        processObj.addParams("pointTypeId", pointType.getPointTypeId());
        processObj.addParams("typeCode", growingType.getTypeCode());
        return this.query(processObj);
    }

    @Deprecated
    public List<UgsUserGradeConfig> findBy(String growingTypeCode) {
        DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsUserGradeConfig t WHERE t.ugsGrowingType.typeCode = :growingTypeCode");
        processObj.addParams("growingTypeCode", growingTypeCode.toUpperCase());
        return this.query(processObj);
    }
}
