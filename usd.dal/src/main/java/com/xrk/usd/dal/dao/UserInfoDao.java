package com.xrk.usd.dal.dao;

import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsUserInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.exception.JDBCConnectionException;

import java.util.Date;

public class UserInfoDao extends DaoBase<UgsUserInfo> {

    public UgsUserInfo findOrCreate(long uId, UgsGrowingType growingType) {
        DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsUserInfo t WHERE t.uid = :uid AND t.ugsGrowingType.typeCode = :growingType");
        processObj.addParams("uid", uId);
        processObj.addParams("growingType", growingType.getTypeCode());
        UgsUserInfo userInfo = this.getSingleResult(processObj);

        if (null == userInfo) {
            userInfo = new UgsUserInfo();
            userInfo.setAddDate(new Date());
            userInfo.setUid(uId);
            userInfo.setUgsGrowingType(growingType);

            EntityManager entityManager = factory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();
                entityManager.persist(userInfo);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error(e.getMessage(), e);
                
                DalService.onError(e);
                //必须保证创建一条用户记录，创建失败就抛异常
                throw e;
            } finally {
                entityManager.close();
            }
        }

        return userInfo;
    }

    public UgsUserInfo find(long uId, String growingTypeCode) {
        DaoProcessObj processObj = new DaoProcessObj("SELECT t FROM UgsUserInfo t WHERE t.uid = :uid AND t.ugsGrowingType.typeCode = :growingTypeCode");
        processObj.addParams("uid", uId);
        processObj.addParams("growingTypeCode", growingTypeCode.toUpperCase());
        return this.getSingleResult(processObj);
    }
}
