package com.xrk.usd.bll.plugin.impl;

import com.xrk.usd.bll.business.MemberBusiness;
import com.xrk.usd.bll.common.BUSINESS_CODE;
import com.xrk.usd.bll.plugin.DefaultGrowingPlugin;
import com.xrk.usd.bll.plugin.PluginParameterDefine;
import com.xrk.usd.bll.vo.MemberVo;
import com.xrk.usd.common.exception.BadRequestException;
import com.xrk.usd.common.exception.InternalServerException;
import com.xrk.usd.dal.entity.UgsUserPointHistory;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixPointGrowingPlugin extends DefaultGrowingPlugin {
    private ConcurrentHashMap mutex;
    private org.slf4j.Logger logger;

    public FixPointGrowingPlugin() {
        this.mutex = new ConcurrentHashMap();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void init() {
        PluginParameterDefine param = new PluginParameterDefine();
        param.setParamCode("experience_points");
        param.setDescription("积分");
        param.setParamName("experience_points");
        param.setParamType(0);
        param.setControlType(0);
        this.addParams(param);
    }

    @Override
    public void destory() {
    }

    @Override
    public Object invoke(long uid, String growingTypeCode, String ruleCode, String description, Map<String, Object> params) throws BadRequestException {
        int point = Integer.parseInt(params.get("experience_points").toString());
        String mutexKey = String.format("%s:%s:%s", uid, growingTypeCode, ruleCode).toUpperCase();
        Object lastMutex = this.mutex.putIfAbsent(mutexKey, new Object());

        if (lastMutex != null) {
            //已经有一个并发请求在执行加分操作
            this.logger.warn(String.format("Duplicate operation of key: {}", mutexKey));
            return false;
        }

        try {
            MemberVo member = MemberBusiness.getMemberFromHttp(uid);

            if(!member.isMember()){
            	logger.warn("uid is not member, skip update! uid={}, growingTypeCode={}, ruleCode={}, description={}", 
            			uid, growingTypeCode, ruleCode, description);
                return false;            
            }

            UgsUserPointHistory history = this.service.getLastestHistory(uid, growingTypeCode, ruleCode);

            if (null != history) {
                logger.info("uid point was update! skip it! uid={}, growingTypeCode={}, ruleCode={}, description={}",
                        uid, growingTypeCode, ruleCode, description);
                return false;
            }
            return this.service.updateUserPoint(uid, growingTypeCode, "experience_points", ruleCode, point, description);
        } catch (InternalServerException e) {
            logger.error(e.getMessage());
            throw new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD, e.getMessage());
        } finally {
            this.mutex.remove(mutexKey);
        }
    }
}
