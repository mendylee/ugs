package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.entity.UgsUserGradeConfigList;

import java.util.List;

public class UserGradeConfigListDao extends DaoBase<UgsUserGradeConfigList> {

    public List<UgsUserGradeConfigList> findBy(int gradeId){
        DaoProcessObj procObj = new DaoProcessObj("SELECT c FROM UgsUserGradeConfigList c where c.id.gradeId=:gradeId order by c.level", true);
        procObj.addParams("gradeId", gradeId);
        return super.query(procObj);
    }

    public List<UgsUserGradeConfigList> findBy(String growingTypeCode) {
        DaoProcessObj processObj = new DaoProcessObj("FROM UgsUserGradeConfigList t1 INNER JOIN FETCH t1.ugsUserGradeConfig t2 WHERE t2.ugsGrowingType.typeCode = :growingTypeCode");
        processObj.addParams("growingTypeCode", growingTypeCode.toUpperCase());
        return this.query(processObj);
    }
}