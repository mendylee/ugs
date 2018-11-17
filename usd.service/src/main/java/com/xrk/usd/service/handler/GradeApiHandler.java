package com.xrk.usd.service.handler;

import com.xrk.hws.http.context.HttpContext;
import com.xrk.usd.bll.component.IUserPointComponent;
import com.xrk.usd.bll.component.impl.UserPointComponent;
import com.xrk.usd.bll.vo.PointTypeGradeVO;
import com.xrk.usd.common.annotation.HttpMethod;
import com.xrk.usd.common.annotation.HttpRouterInfo;

import java.util.List;

@HttpRouterInfo(router = "grade")
public class GradeApiHandler extends AbstractHttpWorkerHandler {
    IUserPointComponent userComp = null;

    public GradeApiHandler() {
        super();
        userComp = new UserPointComponent();
    }

    @HttpMethod(uri = "grade/(\\w+)", method = HttpMethod.METHOD.GET, code = HttpMethod.STATUS_CODE.OK)
    public List<PointTypeGradeVO> getGradeInfos(CustomParameter head, HttpContext ctx) throws Exception {
        String typeCode = head.getUriGroup().get(0);
        return this.userComp.findSysGrade(typeCode);
    }
}
