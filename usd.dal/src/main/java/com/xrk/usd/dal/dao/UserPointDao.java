package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.exception.JDBCConnectionException;

import java.util.Date;
import java.util.List;

public class UserPointDao extends DaoBase<UgsUserPoint> {
    public boolean increment(UgsPointType pointType, UgsUserInfo user, UgsUserPointHistory history) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            if (entityManager.createQuery("UPDATE UgsUserPoint t SET t.point = t.point + :point,t.editDate=:editDate WHERE t.id.uidSeq = :uid and t.id.pointTypeId = :pointTypeId")
                    .setParameter("point", history.getPoint())
                    .setParameter("uid", user.getUidSeqId())
                    .setParameter("pointTypeId", pointType.getPointTypeId())
                    .setParameter("editDate", new Date())
                    .executeUpdate() == 0) {
                UgsUserPoint point = new UgsUserPoint();
                UgsUserPointId pointId = new UgsUserPointId();
                pointId.setUidSeq(user.getUidSeqId());
                pointId.setPointTypeId(pointType.getPointTypeId());
                point.setId(pointId);
                point.setEditDate(new Date());
                point.setPoint(history.getPoint());
                point.setUgsPointType(pointType);
                point.setUgsUserInfo(user);
                entityManager.persist(point);
            }

            history.setUgsUserInfo(user);
            entityManager.persist(history);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            logger.error(e.getMessage(), e);
            DalService.onError(e);
        } finally {
            entityManager.close();
        }

        return false;
    }

    public List<UgsUserPoint> findBy(long uId, String growingTypeCode) {
        DaoProcessObj processObj = new DaoProcessObj("FROM UgsUserPoint t1 INNER JOIN FETCH t1.ugsUserInfo t2 WHERE t2.uid = :uId AND t2.ugsGrowingType.typeCode = :growingTypeCode");
        processObj.addParams("uId", uId);
        processObj.addParams("growingTypeCode", growingTypeCode.toUpperCase());
        return this.query(processObj);
    }

    public List<UgsUserPoint> findBy(List<Long> uIds, String growingTypeCode) {
        DaoProcessObj processObj = new DaoProcessObj("FROM UgsUserPoint t1 INNER JOIN FETCH t1.ugsPointType INNER JOIN FETCH t1.ugsUserInfo t2 WHERE t2.uid IN (:uIds) AND t2.ugsGrowingType.typeCode = :growingTypeCode");
        processObj.addParams("uIds", uIds);
        processObj.addParams("growingTypeCode", growingTypeCode.toUpperCase());
        return this.query(processObj);
    }
}
