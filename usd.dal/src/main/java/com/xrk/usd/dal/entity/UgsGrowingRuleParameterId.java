package com.xrk.usd.dal.entity;

// Generated 2015-9-7 18:09:32 by Hibernate Tools 4.3.1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * UgsGrowingRuleParameterId generated by hbm2java
 */
@Embeddable
public class UgsGrowingRuleParameterId implements java.io.Serializable {
    private static final long serialVersionUID = 391200174193202725L;
	private int ruleListId;
	private String paramName;

	public UgsGrowingRuleParameterId() {
	}

	public UgsGrowingRuleParameterId(int ruleListId, String paramName) {
		this.ruleListId = ruleListId;
		this.paramName = paramName;
	}

	@Column(name = "rule_list_id", nullable = false)
	public int getRuleListId() {
		return this.ruleListId;
	}

	public void setRuleListId(int ruleListId) {
		this.ruleListId = ruleListId;
	}

	@Column(name = "param_name", nullable = false, length = 50)
	public String getParamName() {
		return this.paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof UgsGrowingRuleParameterId))
			return false;
		UgsGrowingRuleParameterId castOther = (UgsGrowingRuleParameterId) other;

		return (this.getRuleListId() == castOther.getRuleListId())
				&& ((this.getParamName() == castOther.getParamName()) || (this
						.getParamName() != null
						&& castOther.getParamName() != null && this
						.getParamName().equals(castOther.getParamName())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getRuleListId();
		result = 37 * result
				+ (getParamName() == null ? 0 : this.getParamName().hashCode());
		return result;
	}

}