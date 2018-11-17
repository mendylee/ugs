package com.xrk.usd.bll.component;

import com.xrk.usd.bll.vo.*;
import com.xrk.usd.common.entity.SimplePageResponseEntity;
import com.xrk.usd.common.exception.BusinessException;

import java.util.List;
import java.util.Map;

public interface IUserPointComponent {

    /**
     * 更新用户积分
     *
     * @param uid
     * @param growingTypeCode
     * @param ruleCode
     * @param description
     * @return
     */
    boolean updateUserPoint(long uid, String growingTypeCode, String ruleCode, String description) throws BusinessException;

    List<UserPointVo> findUserPoint(long uid, String growingTypeCode) throws BusinessException;

    SimplePageResponseEntity<UserPointHistoryVo> findUserPointHistory(long uid, String growingTypeCode, int pageSize, int pageNum, String queryDateBegin, String queryDateEnd, String ruleCodes) throws BusinessException;

    int countUserPointHistory(long uid, String growingTypeCode, String queryDateBegin, String queryDateEnd, String ruleCodes) throws BusinessException;

    List<PointTypeGradeVO> findSysGrade(String growingTypeCode) throws BusinessException;

    GrowingTypeVo findGrowingType(String typeCode) throws BusinessException;

    UserInfoVo queryUserPoint(long uid, String growingTypeCode) throws BusinessException;

    Map<Long, List<Map<String, Object>>> queryUserGrade(String growingTypeCode, List<Long> uIds) throws BusinessException;
}
