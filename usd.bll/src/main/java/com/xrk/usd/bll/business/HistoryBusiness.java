package com.xrk.usd.bll.business;

import com.xrk.usd.bll.common.BUSINESS_CODE;
import com.xrk.usd.bll.common.SysConfig;
import com.xrk.usd.bll.plugin.proxy.IUserChangeNotice;
import com.xrk.usd.bll.service.RulePluginService;
import com.xrk.usd.bll.vo.UserPointHistoryVo;
import com.xrk.usd.common.cache.LRUCache;
import com.xrk.usd.common.entity.SimplePageResponseEntity;
import com.xrk.usd.common.exception.BadRequestException;
import com.xrk.usd.common.exception.BusinessException;
import com.xrk.usd.common.tools.Strings;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.UserPointHistoryDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsUserInfo;
import com.xrk.usd.dal.entity.UgsUserPointHistory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryBusiness implements IUserChangeNotice {

    private Logger logger = LoggerFactory.getLogger(HistoryBusiness.class);
//    private static LRUCache<String,List<UserPointHistoryVo>> cache = new LRUCache<>(1000,SysConfig.getPointHistoryMaxCacheSize(),true,new EvictionListener<String,List<UserPointHistoryVo>>(){
//
//        @Override
//        public void onEviction(String key, List<UserPointHistoryVo> val) {
//
//        }
//    });//params hash=>historiesVO list
    private final LRUCache<String,SimplePageResponseEntity<UserPointHistoryVo>> cache = new LRUCache<>(SysConfig.getPointHistoryMaxCacheSize());
    private final LRUCache<String,Set<String>> historyKeyCache = new LRUCache<>(SysConfig.getPointHistoryMaxCacheSize()); //uid+typeCode => params list

    //private UserPointHistoryDao historyDao = null;

    private static HistoryBusiness instance = null;
    private static Object syncObj = new Object();
    public static HistoryBusiness getInstance(){
    	if (instance == null) {
			synchronized (syncObj) {
				if (instance == null) {
					instance = new HistoryBusiness();
				}
			}
		}
		return instance;
    }

    private HistoryBusiness(){
    	//historyDao = DalService.getDao(UserPointHistoryDao.class);
        RulePluginService.getGrowingService().registerObserver(this);        
    }

    public SimplePageResponseEntity<UserPointHistoryVo> findBy(long uid, String growingTypeCode, int pageSize, int pageNum, String queryDateBegin, String queryDateEnd, String ruleCodes)throws BusinessException{
        String params = String.format("%d:%s:%d:%d:%s:%s:%s", uid, growingTypeCode, pageSize, pageNum, queryDateBegin, queryDateEnd, ruleCodes);
        String key = Strings.getMD5(params.toLowerCase());
        SimplePageResponseEntity<UserPointHistoryVo> ret = cache.get(key);
        if(null==ret){

            ret  = new SimplePageResponseEntity<UserPointHistoryVo>(0,pageSize,pageNum);
            UgsUserInfo user = UserBusiness.getUserInfo(growingTypeCode, uid);
            if(null==user){
                ret.setResult(new ArrayList<>());
                logger.info("query user history, user isn't exist,growingTypeCode:{}, uid:{}", growingTypeCode, uid);
                return ret;
            }

            Date beginDate = parseDateStr(queryDateBegin), endDate = parseDateStr(queryDateEnd);
            UgsGrowingType growingType = new UgsGrowingType();
            growingType.setTypeCode(growingTypeCode);
            UserPointHistoryDao historyDao = DalService.getDao(UserPointHistoryDao.class);
            int total = historyDao.countBy(user, growingType, beginDate, endDate, ruleCodes);

            List<UserPointHistoryVo> list = new ArrayList<>();
            growingType.setTypeCode(growingTypeCode);
            List<UgsUserPointHistory> histories = historyDao.findBy(user, growingType, pageSize, pageNum, beginDate, endDate, ruleCodes);
            for (UgsUserPointHistory history : histories) {
                UserPointHistoryVo historyVo = new UserPointHistoryVo();
                historyVo.setRuleCode(history.getRuleCode());
                historyVo.setDescription(history.getDescription());
                historyVo.setAddDate(history.getAddDate().getTime());
                historyVo.setPoint(history.getPoint());
                historyVo.setRuleName(history.getRuleName());
                list.add(historyVo);
            }
            ret.setTotal(total);
            ret.setResult(list);

            cache.put(key, ret);
            logger.info("query user history cache add,key:{}, params:{}, size:{}", key, params, ret.getResult().size());
            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY,0);
            now.set(Calendar.MINUTE,0);
            now.set(Calendar.SECOND,0);
            now.set(Calendar.MILLISECOND,0);
            long baseTime = now.getTime().getTime();
            long endTime = endDate == null ?  baseTime : endDate.getTime();
            if(endTime >= baseTime){
	            String summaryKey = this.getSummaryKey(user.getUid(), growingTypeCode);
	            Set<String> cacheKeys = historyKeyCache.get(summaryKey);
	            if(null==cacheKeys){
	                cacheKeys = new HashSet<>();
	                historyKeyCache.put(summaryKey, cacheKeys);                    
	            }
	            cacheKeys.add(key);
	            logger.info("query user history checkout cache add,uid:{}, growingTypeCode:{}, key:{}, params:{}, size:{}",
	            		user.getUid(), growingTypeCode, key, params, cacheKeys.size());
            }
        }else{
            logger.info("query user history cache hit,key:{}, params:{}, size:{}", key, params, ret.getResult().size());
        }
        return ret;
    }

    @Override
    public void onUserPointChange(long uid, String growingTypeCode, String pointTypeCode, String ruleCode, int point) {
        String summaryKey = this.getSummaryKey(uid, growingTypeCode);
        Set<String> historyKeys = historyKeyCache.get(summaryKey);
        if(historyKeys != null){
	        historyKeys.forEach(historyKey ->{
                cache.remove(historyKey);
                logger.info("query user history checkout cache remove,key:{}", historyKey);
            });
	        historyKeyCache.remove(summaryKey);
            logger.info("query user history checkout cache remove,key(summary):{}", summaryKey);
        }
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

    private String getSummaryKey(long uid,String growingTypeCode){
        return uid+":"+growingTypeCode.trim().toUpperCase();
    }


    private void historyVoLog(UserPointHistoryVo historyVo){
        logger.info("value(array) item,ruleCode:{},ruleName:{},addDate:{},point:{}",historyVo.getRuleCode(),historyVo.getRuleName(),historyVo.getAddDate(),historyVo.getPoint());
    }
}
