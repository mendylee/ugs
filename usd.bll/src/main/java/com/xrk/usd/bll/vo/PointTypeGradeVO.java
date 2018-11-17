package com.xrk.usd.bll.vo;

import java.util.List;

public class PointTypeGradeVO {
    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getPointCode() {
        return pointCode;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public List<GradeVO> getDetails() {
        return details;
    }

    public void setDetails(List<GradeVO> details) {
        this.details = details;
    }

    private String gradeName;
    private String pointCode;
    private String pointName;
    private List<GradeVO> details;
}
