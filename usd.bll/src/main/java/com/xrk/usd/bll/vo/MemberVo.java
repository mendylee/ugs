package com.xrk.usd.bll.vo;

import java.util.Date;

import com.xrk.usd.bll.common.SysConfig;

public class MemberVo {
    private Long uid;
    private boolean isMember;
    private boolean isVIP;
    private Date cacheTime;
    private long expireTime;
    
    public MemberVo(){
    	this.cacheTime = new Date();
    	this.expireTime = this.cacheTime.getTime() + 1000*60*60*SysConfig.getMemberCacheTime();
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public boolean isMember() {
        return isMember;
    }

    public void isMember(boolean isMember) {
        this.isMember = isMember;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void isVIP(boolean isVIP) {
        this.isVIP = isVIP;
    }

    public Date getCacheTime() {
        return cacheTime;
    }
    
    public boolean IsExpire(){
    	return new Date().getTime() > this.expireTime;
    }
}
