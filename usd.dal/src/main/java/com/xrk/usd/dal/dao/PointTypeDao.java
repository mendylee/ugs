package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsPointType;

import java.util.List;

public class PointTypeDao extends DaoBase<UgsPointType> {
    public List<UgsPointType> findBy(UgsGrowingType growingType) {
        DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsPointType t WHERE t.ugsGrowingType.typeCode = :growingType");
        processObj.addParams("growingType", growingType.getTypeCode());
        return this.query(processObj);
    }

    public UgsPointType getByCode(String pointTypeCode){
        DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsPointType t WHERE t.pointTypeCode = :pointTypeCode");
        processObj.addParams("pointTypeCode", pointTypeCode);
        return this.getSingleResult(processObj);
    }
    
    @Override
    public List<UgsPointType>findAll(){
    	DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsPointType t LEFT JOIN FETCH t.ugsGrowingType");
        return this.query(processObj);
    }
}
