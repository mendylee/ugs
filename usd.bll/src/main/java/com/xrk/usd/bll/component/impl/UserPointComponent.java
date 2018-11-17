package com.xrk.usd.bll.component.impl;

import com.xrk.usd.bll.business.*;
import com.xrk.usd.bll.common.BUSINESS_CODE;
import com.xrk.usd.bll.component.IUserPointComponent;
import com.xrk.usd.bll.service.RulePluginService;
import com.xrk.usd.bll.service.UserPointQueueService;
import com.xrk.usd.bll.vo.*;
import com.xrk.usd.common.entity.SimplePageResponseEntity;
import com.xrk.usd.common.exception.BadRequestException;
import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.common.exception.VerifyException;
import com.xrk.usd.common.tools.Strings;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.UserPointHistoryDao;
import com.xrk.usd.dal.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserPointComponent implements IUserPointComponent {

    private Logger logger = LoggerFactory.getLogger(UserPointComponent.class);
    private GradeBusiness gradeBusiness = GradeBusiness.getInstance();
    private PointTypeBusiness pointTypeBusiness = PointTypeBusiness.getInstance();
    private HistoryBusiness historyBusiness = HistoryBusiness.getInstance();

    @Override
    public boolean updateUserPoint(long uid, String growingTypeCode, String ruleCode, String description) throws BusinessException {
        if (!RulePluginService.contains(growingTypeCode, ruleCode)) {
            throw new VerifyException(BUSINESS_CODE.PARAMER_INVAILD, "不存在的成长体系及规则代码");
        }

        UserPointInvokeVo userPointVo = new UserPointInvokeVo();
        userPointVo.setUid(uid);
        userPointVo.setDescription(description);
        userPointVo.setGrowingTypeCode(growingTypeCode);
        userPointVo.setRuleCode(ruleCode);
        return UserPointQueueService.queue(userPointVo);
    }

    @Override
    public UserInfoVo queryUserPoint(long uid, String growingTypeCode) throws BusinessException {
        GrowingTypeVo growingType = this.findGrowingType(growingTypeCode);

        UserInfoVo info = new UserInfoVo();
        info.setUid(Long.toString(uid));
        info.setTypeCode(growingType.getTypeCode());
        info.setTypeName(growingType.getTypeName());
        info.setPoints(this.findUserPoint(uid, growingTypeCode));
        return info;
    }

    @Override
    public Map<Long, List<Map<String, Object>>> queryUserGrade(String growingTypeCode, List<Long> uIds) throws BusinessException {
        List<UgsUserGradeConfig> gradeConfigs = gradeBusiness.findConfigs(growingTypeCode);
        List<UgsUserGradeConfigList> gradeConfigLists = gradeBusiness.findConfigListBy(growingTypeCode);
        List<UgsUserPoint> userPoints = UserBusiness.getUserPoints(growingTypeCode, uIds);
        Map<Long, List<Map<String, Object>>> result = new HashMap<>();

        userPoints.forEach(userPoint -> {
            Long uId = userPoint.getUgsUserInfo().getUid();
            List<Map<String, Object>> mapInfos = result.get(uId);
            Map<String, Object> mapInfo = new HashMap<>();
            UgsPointType pointType = userPoint.getUgsPointType();

            if (null == mapInfos) {
                mapInfos = new ArrayList<>();
                result.put(uId, mapInfos);
            }

            mapInfo.put("level", this.getLevel(gradeConfigs, gradeConfigLists, pointType.getPointTypeId(), userPoint.getPoint()));
            mapInfo.put("point", userPoint.getPoint());
            mapInfo.put("pointTypeId",  pointType.getPointTypeId());
            mapInfo.put("pointTypeCode", pointType.getPointTypeCode());
            mapInfos.add(mapInfo);
        });
        return result;
    }

    @Override
    public List<UserPointVo> findUserPoint(long uid, String growingTypeCode) throws BusinessException {
        List<UserPointVo> ret = new ArrayList<>();

        UgsUserInfo user = UserBusiness.getUserInfo(growingTypeCode, uid);
        if(null==user){
            List<PointTypeVo> pointTypes = pointTypeBusiness.findBy(growingTypeCode);
            for(PointTypeVo pointType:pointTypes){
                UserPointVo pointVo = new UserPointVo();
                pointVo.setPointCode(pointType.getPointTypeCode());
                pointVo.setPointName(pointType.getPointTypeName());
                pointVo.setPoint(0);
                int level = this.getLevel(gradeBusiness.findConfigs(growingTypeCode), gradeBusiness.findConfigListBy(growingTypeCode), pointType.getPointTypeId(), 0);
                pointVo.setGrade(level);
                ret.add(pointVo);
            }
            return ret;
        }

        List<UgsUserGradeConfig> gradeConfigs = gradeBusiness.findConfigs(growingTypeCode);
        List<UgsUserGradeConfigList> gradeConfigLists = gradeBusiness.findConfigListBy(growingTypeCode);
        List<UgsUserPoint> userPoints = UserBusiness.getUserPoint(growingTypeCode, uid);

        for (UgsUserPoint userPoint : userPoints) {

            int pointTypeId = userPoint.getUgsPointType().getPointTypeId();
            PointTypeVo pointType = pointTypeBusiness.findById(pointTypeId);

            UserPointVo pointVo = new UserPointVo();
            pointVo.setPointCode(pointType.getPointTypeCode());
            pointVo.setPointName(pointType.getPointTypeName());
            pointVo.setPoint(userPoint.getPoint());

            pointVo.setGrade(this.getLevel(gradeConfigs, gradeConfigLists, pointTypeId, userPoint.getPoint()));

            ret.add(pointVo);

        }
        return ret;
    }

    @Override
    public SimplePageResponseEntity<UserPointHistoryVo> findUserPointHistory(long uid, String growingTypeCode, int pageSize, int pageNum, String queryDateBegin, String queryDateEnd, String ruleCodes) throws BusinessException {
        if (pageNum < 1) {
            pageNum = 1;
        }
        return historyBusiness.findBy(uid, growingTypeCode, pageSize, pageNum, queryDateBegin, queryDateEnd, ruleCodes);
    }

    @Override
    public int countUserPointHistory(long uid, String growingTypeCode, String queryDateBegin, String queryDateEnd, String ruleCodes) throws BusinessException {
        UgsUserInfo user = UserBusiness.getUserInfo(growingTypeCode, uid);
        if(null==user){
            return 0 ;
        }
        UgsGrowingType growingType = new UgsGrowingType();
        growingType.setTypeCode(growingTypeCode);
        Date beginDate = parseDateStr(queryDateBegin), endDate = parseDateStr(queryDateEnd);
        UserPointHistoryDao historyDao = DalService.getDao(UserPointHistoryDao.class);
        return historyDao.countBy(user, growingType, beginDate, endDate, ruleCodes);
    }

    @Override
    public List<PointTypeGradeVO> findSysGrade(String growingTypeCode) throws BusinessException {
        List<PointTypeGradeVO> ret = new ArrayList<>();

        List<PointTypeVo> pointTypes = pointTypeBusiness.findBy(growingTypeCode);
        for (PointTypeVo pointType : pointTypes) {
            List<UgsUserGradeConfig> grades = gradeBusiness.findConfigsBy(growingTypeCode, pointType.getPointTypeId());
            for (UgsUserGradeConfig grade : grades) {
                PointTypeGradeVO ptgVO = new PointTypeGradeVO();
                ptgVO.setPointCode(pointType.getPointTypeCode());
                ptgVO.setPointName(pointType.getPointTypeName());
                ptgVO.setGradeName(grade.getGradeName());
                List<GradeVO> details = new ArrayList<>();
                ptgVO.setDetails(details);
                List<UgsUserGradeConfigList> gradeLists = gradeBusiness.findConfigListBy(growingTypeCode,grade.getGradeId());
                for (UgsUserGradeConfigList item : gradeLists) {
                    GradeVO gradeVO = new GradeVO(item.getPoint(), (int) item.getId().getLevel());
                    details.add(gradeVO);
                }
                ret.add(ptgVO);
            }
        }
        return ret;
    }

    @Override
    public GrowingTypeVo findGrowingType(String typeCode) throws BusinessException {
        if (Strings.isNullOrEmpty(typeCode)) {
            throw new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, "无效的成长体系代码");
        }

        UgsGrowingType growingType = GrowingTypeBusiness.getInstance().findBy(typeCode);
        if (null == growingType) {
            throw new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, "无效的成长体系代码");
        }

        return GrowingTypeVo.parse(growingType);
    }

    /**
     * 获取等级
     * @param configs
     * @param configItems 等级列表
     * @param pointTypeId
     * @param points
     * @return
     */
    private int getLevel(List<UgsUserGradeConfig> configs, List<UgsUserGradeConfigList> configItems, int pointTypeId, int points) {
        int result = Integer.MAX_VALUE;
        int level;
        int maxLevel = 1;

        for (UgsUserGradeConfig config : configs) {
            if (config.getUgsPointType().getPointTypeId() == pointTypeId) {
                for (UgsUserGradeConfigList configDetail : configItems) {
                    if (configDetail.getUgsUserGradeConfig().getGradeId() == config.getGradeId()) {
                        level = configDetail.getId().getLevel();
                        maxLevel = Math.max(maxLevel, level);

                        if (points <= configDetail.getPoint() && level < result) {
                            result = level;
                        }
                    }
                }

                break;
            }
        }

        return result != Integer.MAX_VALUE ? result : maxLevel;
    }

    private Date parseDateStr(String dateStr) throws BusinessException {
        Date ret = null;
        if (!StringUtils.isEmpty(dateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                ret = sdf.parse(dateStr);
            } catch (ParseException e) {
                logger.error(e.getMessage());
                throw new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, "时间解析出错");
            }
        }
        return ret;
    }
}
