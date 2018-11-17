package com.xrk.usd.service.handler;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.usd.bll.component.IUserPointComponent;
import com.xrk.usd.bll.component.impl.UserPointComponent;
import com.xrk.usd.bll.vo.UserInfoVo;
import com.xrk.usd.common.annotation.HttpMethod;
import com.xrk.usd.common.annotation.HttpMethod.METHOD;
import com.xrk.usd.common.annotation.HttpMethod.STATUS_CODE;
import com.xrk.usd.common.annotation.HttpRouterInfo;
import com.xrk.usd.common.entity.SimpleResponseEntity;
import com.xrk.usd.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@HttpRouterInfo(router = "growing")
public class GrowingApiHandler extends AbstractHttpWorkerHandler {
    IUserPointComponent userComp = null;

    public GrowingApiHandler() {
        super();
        userComp = new UserPointComponent();
    }

    @HttpMethod(uri = "growing/(\\w+)\\?.+", method = METHOD.GET, code = STATUS_CODE.OK)
    public Object queryUserGrades(String uid, CustomParameter head, HttpContext ctx) throws BusinessException {
        String typeCode = head.getUriGroup().get(0);
        List<Long> uIds = new ArrayList<>();
        String[] splits = uid.split(",");

        for (int i = 0; i < splits.length; ++i) {
        	String id = splits[i];
        	if(id == null || id.isEmpty()){
        		continue;
        	}
            uIds.add(Long.valueOf(this.parseLong(id)));
        }

        return this.userComp.queryUserGrade(typeCode, uIds);
    }

    @HttpMethod(uri = "/(\\w+)/(\\d+)", method = METHOD.POST, code = STATUS_CODE.CREATED)
    public SimpleResponseEntity<Boolean> updatePoint(String ruleCode, String description, CustomParameter head, HttpContext ctx) throws Exception {
        String typeCode = head.getUriGroup().get(0);
        long uid = this.parseLong(head.getUriGroup().get(1));
        boolean bRtn = userComp.updateUserPoint(uid, typeCode, ruleCode, description);
        return new SimpleResponseEntity<>(bRtn);
    }

    @HttpMethod(uri = "/(\\w+)/(\\d+)", method = METHOD.GET, code = STATUS_CODE.OK)
    public UserInfoVo queryUserPoint(CustomParameter head, HttpContext ctx) throws BusinessException {
        String typeCode = head.getUriGroup().get(0);
        long uid = this.parseLong(head.getUriGroup().get(1));
        return this.userComp.queryUserPoint(uid, typeCode);
    }
}
