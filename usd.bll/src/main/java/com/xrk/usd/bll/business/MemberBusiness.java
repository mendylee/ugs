package com.xrk.usd.bll.business;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xrk.usd.bll.common.BUSINESS_CODE;
import com.xrk.usd.bll.common.SysConfig;
import com.xrk.usd.bll.vo.MemberVo;
import com.xrk.usd.common.cache.LRUCache;
import com.xrk.usd.common.exception.InternalServerException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MemberBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberBusiness.class);

    private static final LRUCache<Long,MemberVo> cache = new LRUCache<>(SysConfig.getLRUCacheSize());

    private static MemberBusiness instance = new MemberBusiness();
    private static MemberBusiness getInstance(){
        return instance;
    }
    private MemberBusiness(){}

    public static boolean isMember(long uid)throws InternalServerException{
        MemberVo member = cache.get(uid);
        if(null==member || member.IsExpire()){
            if(member != null){
                cache.remove(uid);
                LOGGER.info("cache remove,key:{}", uid);
            }
            member = getMemberFromHttp(uid);
            cache.put(uid, member);
            LOGGER.info("cache add,key:{},value:{},isVIP:{},cacheTime:{}",uid,member.isMember(),member.isVIP(),member.getCacheTime());
            return member.isMember();
        }else{
            LOGGER.info("get user info cache hit,key:{},value:{},isVIP:{},cacheTime:{}",
            		uid,member.isMember(),member.isVIP(),member.getCacheTime());
        }
        return member == null ? false : member.isMember();
    }

    public static boolean isVIP(long uid)throws InternalServerException{
        boolean isM = isMember(uid);
        if(isM){
            MemberVo member = cache.get(uid);
           // LOGGER.info("get user info cache hit,key:{}, isMember:{}, isVIP:{}, cacheTime:{}",
           // 		uid,member.isMember(), member.isVIP(),member.getCacheTime());
            return member.isVIP();
        }
        return false;
    }

    public static MemberVo getMemberFromHttp(long uid) throws InternalServerException {
        MemberVo member = new MemberVo();
        member.setUid(uid);
        member.isMember(false);
        member.isVIP(false);
        boolean querySuccess = getFromHttp(member);
        if(!querySuccess){
            LOGGER.error("查找是否诚信通失败，再试一次,uid:"+uid);
            querySuccess = getFromHttp(member);
        }
        if(!querySuccess){
            LOGGER.error("查找是否诚信通失败,uid:"+uid);
            throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR,"查找是否诚信通失败");
        }
        return member;
    }

    private static boolean getFromHttp(MemberVo member)throws InternalServerException{
        try {
            HttpResponse<JsonNode> response = Unirest.get(SysConfig.getMemberUrl() + member.getUid()).asJson();
            JsonNode body = response.getBody();
            LOGGER.debug("get user info: uid={}, body={}", member.getUid(), body);
            if (response.getStatus() < 300 && null != body) {
                JSONObject data = (JSONObject) body.getObject().get("data");
                JSONObject userProfile = (JSONObject) data.get("user_profile");
                if (null != userProfile) {
                    member.isMember(true);
                    String isVip = String.valueOf(userProfile.get("is_vip"));
                    if (null != isVip && (isVip.equals("1") || isVip.equals("1.0"))) {
                        member.isVIP(true);
                    }
                }
            }
            else if(response.getStatus() == 404){
            	return true;
            }
            else {
                return false;
            }
        } catch (UnirestException e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerException(BUSINESS_CODE.INTERNAL_SERVER_ERROR,e.getMessage());
        }
        return true;
    }
}
