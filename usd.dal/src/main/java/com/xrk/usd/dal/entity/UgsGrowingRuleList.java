package com.xrk.usd.dal.entity;

// Generated 2015-9-7 18:09:32 by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * UgsGrowingRuleList generated by hbm2java
 */
@Entity
@Table(name = "ugs_growing_rule_list", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"rule_group_id", "rule_code" }))
public class UgsGrowingRuleList implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
	private int ruleListId;
	private UgsGrowingRuleGroup ugsGrowingRuleGroup;
	private UgsGrowingRulePlugin ugsGrowingRulePlugin;
	private String ruleCode;
	private String ruleName;
	private Date addDate;
	private Integer userId;
	private Set<UgsGrowingRuleParameter> ugsGrowingRuleParameters = new HashSet<UgsGrowingRuleParameter>(0);

	public UgsGrowingRuleList() {
	}

	public UgsGrowingRuleList(int ruleListId,
			UgsGrowingRuleGroup ugsGrowingRuleGroup,
			UgsGrowingRulePlugin ugsGrowingRulePlugin, String ruleCode,
			String ruleName) {
		this.ruleListId = ruleListId;
		this.ugsGrowingRuleGroup = ugsGrowingRuleGroup;
		this.ugsGrowingRulePlugin = ugsGrowingRulePlugin;
		this.ruleCode = ruleCode;
		this.ruleName = ruleName;
	}

	public UgsGrowingRuleList(int ruleListId,
			UgsGrowingRuleGroup ugsGrowingRuleGroup,
			UgsGrowingRulePlugin ugsGrowingRulePlugin, String ruleCode,
			String ruleName, Date addDate, Integer userId,
			Set<UgsGrowingRuleParameter> ugsGrowingRuleParameters) {
		this.ruleListId = ruleListId;
		this.ugsGrowingRuleGroup = ugsGrowingRuleGroup;
		this.ugsGrowingRulePlugin = ugsGrowingRulePlugin;
		this.ruleCode = ruleCode;
		this.ruleName = ruleName;
		this.addDate = addDate;
		this.userId = userId;
		this.ugsGrowingRuleParameters = ugsGrowingRuleParameters;
	}

	@Id
    @SequenceGenerator(name="rule-list-seq-gen",sequenceName="ugs_growing_rule_list_rule_list_id_seq", allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="rule-list-seq-gen")
	@Column(name = "rule_list_id", unique = true, nullable = false)
	public int getRuleListId() {
		return this.ruleListId;
	}

	public void setRuleListId(int ruleListId) {
		this.ruleListId = ruleListId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rule_group_id", nullable = false)
	public UgsGrowingRuleGroup getUgsGrowingRuleGroup() {
		return this.ugsGrowingRuleGroup;
	}

	public void setUgsGrowingRuleGroup(UgsGrowingRuleGroup ugsGrowingRuleGroup) {
		this.ugsGrowingRuleGroup = ugsGrowingRuleGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plugin_id", nullable = false)
	public UgsGrowingRulePlugin getUgsGrowingRulePlugin() {
		return this.ugsGrowingRulePlugin;
	}

	public void setUgsGrowingRulePlugin(
			UgsGrowingRulePlugin ugsGrowingRulePlugin) {
		this.ugsGrowingRulePlugin = ugsGrowingRulePlugin;
	}

	@Column(name = "rule_code", nullable = false, length = 50)
	public String getRuleCode() {
		return this.ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	@Column(name = "rule_name", nullable = false, length = 100)
	public String getRuleName() {
		return this.ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "add_date", length = 29)
	public Date getAddDate() {
		return this.addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	@Column(name = "user_id")
	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ugsGrowingRuleList")
	public Set<UgsGrowingRuleParameter> getUgsGrowingRuleParameters() {
		return this.ugsGrowingRuleParameters;
	}

	public void setUgsGrowingRuleParameters(Set<UgsGrowingRuleParameter> ugsGrowingRuleParameters) {
		this.ugsGrowingRuleParameters = ugsGrowingRuleParameters;
	}

}