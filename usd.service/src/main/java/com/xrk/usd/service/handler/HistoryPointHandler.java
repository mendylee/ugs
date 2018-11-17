package com.xrk.usd.service.handler;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.usd.bll.component.IUserPointComponent;
import com.xrk.usd.bll.component.impl.UserPointComponent;
import com.xrk.usd.bll.vo.UserPointHistoryVo;
import com.xrk.usd.common.annotation.HttpMethod;
import com.xrk.usd.common.annotation.HttpMethod.METHOD;
import com.xrk.usd.common.annotation.HttpMethod.STATUS_CODE;
import com.xrk.usd.common.annotation.HttpRouterInfo;
import com.xrk.usd.common.entity.SimplePageResponseEntity;
import com.xrk.usd.common.exception.BusinessException;

import java.util.List;

@HttpRouterInfo(router = "history")
public class HistoryPointHandler extends AbstractHttpWorkerHandler {
    IUserPointComponent userComp = null;

    public HistoryPointHandler() {
        super();
        userComp = new UserPointComponent();
    }

    @HttpMethod(uri = "/(\\w+)/(\\d+)(?:/)?(\\d+)?(?:/)?(\\d+)?", method = METHOD.GET, code = STATUS_CODE.OK)
    public SimplePageResponseEntity<UserPointHistoryVo> queryUserPointHistory(String queryDateBegin, String queryDateEnd, String ruleCodes, CustomParameter head, HttpContext ctx) throws BusinessException {
        List<String> matches = head.getUriGroup();
        String typeCode = matches.get(0);
        long uId = this.parseLong(matches.get(1));
        int pageIndex = matches.get(2) != null ? Integer.parseInt(matches.get(2)) : 1;
        int pageSize = matches.get(3) != null ? Integer.parseInt(matches.get(3)) : 10;

        if (pageIndex < 1) {
            pageIndex = 1;
        }

        if (pageSize < 1) {
            pageSize = 1;
        }

        if (pageSize > 50) {
            pageSize = 50;
        }

        return this.userComp.findUserPointHistory(uId, typeCode, pageSize, pageIndex, queryDateBegin, queryDateEnd, ruleCodes);
    }
}
