package com.xrk.usd.dal.entity;

// Generated 2015-9-7 18:09:32 by Hibernate Tools 4.3.1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * UgsGrwoingActiveGroup generated by hbm2java
 */
@Entity
@Table(name = "ugs_growing_active_group", schema = "public")
public class UgsGrowingActiveGroup implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
	private String typeCode;
	private UgsGrowingRuleGroup ugsGrowingRuleGroup;
	private UgsGrowingType ugsGrowingType;

	public UgsGrowingActiveGroup() {
	}

	public UgsGrowingActiveGroup(UgsGrowingRuleGroup ugsGrowingRuleGroup,
			UgsGrowingType ugsGrowingType) {
		this.ugsGrowingRuleGroup = ugsGrowingRuleGroup;
		this.ugsGrowingType = ugsGrowingType;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "ugsGrowingType"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "type_code", unique = true, nullable = false, length = 30)
	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = UgsGrowingRuleGroup.class)
	@JoinColumn(name = "rule_group_id", nullable = false)
	public UgsGrowingRuleGroup getUgsGrowingRuleGroup() {
		return this.ugsGrowingRuleGroup;
	}

	public void setUgsGrowingRuleGroup(UgsGrowingRuleGroup ugsGrowingRuleGroup) {
		this.ugsGrowingRuleGroup = ugsGrowingRuleGroup;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	public UgsGrowingType getUgsGrowingType() {
		return this.ugsGrowingType;
	}

	public void setUgsGrowingType(UgsGrowingType ugsGrowingType) {
		this.ugsGrowingType = ugsGrowingType;
	}

}
