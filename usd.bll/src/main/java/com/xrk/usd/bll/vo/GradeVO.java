package com.xrk.usd.bll.vo;

public class GradeVO {
    private Integer level;
    private Integer point;

    public GradeVO(Integer point, Integer level) {
        this.point = point;
        this.level = level;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
