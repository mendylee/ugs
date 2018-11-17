package com.xrk.usd.bll.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xrk.usd.bll.common.SysConfig;
import com.xrk.usd.bll.plugin.proxy.IUserChangeNotice;
import com.xrk.usd.bll.service.RulePluginService;
import com.xrk.usd.common.cache.LRUCache;
import com.xrk.usd.dal.DalService;
import com.xrk.usd.dal.dao.UserInfoDao;
import com.xrk.usd.dal.dao.UserPointDao;
import com.xrk.usd.dal.entity.UgsGrowingType;
import com.xrk.usd.dal.entity.UgsUserInfo;
import com.xrk.usd.dal.entity.UgsUserPoint;

public final class UserBusiness implements IUserChangeNotice {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBusiness.class);
    private static final LRUCache<String, List<UgsUserPoint>> cache = new LRUCache<>(SysConfig.getPointLRUCacheSize());
    private static final LRUCache<String,UgsUserInfo> userCache = new LRUCache<>(SysConfig.getUserLRUCacheSize());
    public static final UserBusiness instance = new UserBusiness();

    private UserBusiness() {
        RulePluginService.getGrowingService().registerObserver(this);
    }
    
    private static String formatKey(String growingTypeCode, long uid){
    	return  String.format("%s:%d", growingTypeCode.trim().toUpperCase(), uid);
    }

    public static List<UgsUserPoint> getUserPoints(String growingTypeCode, List<Long> uIds) {
        Set<Long> unCacheIds = new HashSet<>();
        List<UgsUserPoint> userPoints = new ArrayList<>(uIds.size());

        for (Long uId : uIds) {
            String key = formatKey(growingTypeCode, uId);
            List<UgsUserPoint> value = cache.get(key);

            if (value != null) {
                userPoints.addAll(value);
                LOGGER.info("get user point cache hit,key:{}", key);                
            } else if(!unCacheIds.contains(uId)){
                unCacheIds.add(uId);
            }
        }       
        
                
        if(unCacheIds.size() > 0){        	
	        List<Long> queryList = new ArrayList<Long>();
	    	queryList.addAll(unCacheIds);
	        List<UgsUserPoint> lsRtn = DalService.getDao(UserPointDao.class).findBy(queryList, growingTypeCode);               
	        unCacheIds.forEach(uid ->{
	        	String key = formatKey(growingTypeCode, uid);
	        	List<UgsUserPoint> list = new ArrayList<>();
	        	for(UgsUserPoint point : lsRtn){
	    			if(point.getUgsUserInfo().getUid() == uid){
	    				list = cache.get(key);
			            if(null==list){
			                list = new ArrayList<>();
			            }
			            list.add(point);
			            LOGGER.info("get user point cache add, key:{}, size:{}, point:{}, point_type:{}",
			            		key, list.size(), point.getPoint(), point.getUgsPointType().getPointTypeCode());
	    			}
	        	}
	        	cache.put(key, list);
	        	LOGGER.info("get user point add to lru cache, key:{}, lru_size:{}",
	        			key, cache.size());
	        });
	      
	        userPoints.addAll(lsRtn);
        }
        return userPoints;
    }

    public static List<UgsUserPoint> getUserPoint(String growingTypeCode,long uid){
        List<Long> uids  = new ArrayList<>();
        uids.add(uid);
        return getUserPoints(growingTypeCode,uids);
    }

    public static UgsUserInfo getUserInfo(String growingTypeCode, long uid){
        String key = formatKey(growingTypeCode, uid);
        UgsUserInfo user = userCache.get(key);
        if(null==user){
            UgsUserInfo userInfo = DalService.getDao(UserInfoDao.class).find(uid, growingTypeCode);
            if(null!=userInfo){
                userCache.put(key, userInfo);
                userInfoLog("get user point cache add", key, userInfo);
            }
            return userInfo;
        }
        else{
        	LOGGER.info("get user point cache hit, key:{}, uid:{}, uid_seq_id:{}", key, user.getUid(), user.getUidSeqId());
        }
        return user;
    }

    public static UgsUserInfo getOrCreateUserInfo(UgsGrowingType growingType,long uid){
        String key = formatKey(growingType.getTypeCode(), uid);
        UgsUserInfo user = userCache.get(key);
        if(null==user){
            UgsUserInfo userInfo = DalService.getDao(UserInfoDao.class).findOrCreate(uid, growingType);
            if(null!=userInfo){
                userCache.put(key,userInfo);
                userInfoLog("get user point cache add",key, user);
            }
            return userInfo;
        }else{
            userInfoLog("get user point cache hit",key, user);
        }
        return user;
    }

    @Override
    public void onUserPointChange(long uid, String growingTypeCode, String pointTypeCode, String ruleCode, int point) {
        cache.remove(formatKey(growingTypeCode, uid));
    }

    private static void userInfoLog(String pre,String key, UgsUserInfo userInfo){
        LOGGER.info(pre+",key:{}, uid_seq_id:{}, uid:{}, type_code:{}, add_date:{}", 
        		key, userInfo.getUidSeqId(), userInfo.getUid(), userInfo.getUgsGrowingType().getTypeCode(), userInfo.getAddDate());
    }

}
