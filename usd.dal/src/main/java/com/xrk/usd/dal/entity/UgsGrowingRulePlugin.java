package com.xrk.usd.dal.entity;

// Generated 2015-9-7 18:09:32 by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * UgsGrowingRulePlugin generated by hbm2java
 */
@Entity
@Table(name = "ugs_growing_rule_plugin", schema = "public")
public class UgsGrowingRulePlugin implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
	private int pluginId;
	private String version;
	private String pluginName;
	private String pluginClass;
	private String description;
	private Date addDate;
	private Set<UgsGrowingRuleList> ugsGrowingRuleLists = new HashSet<UgsGrowingRuleList>(0);

	public UgsGrowingRulePlugin() {
	}

	public UgsGrowingRulePlugin(int pluginId, String pluginName,
			String pluginClass) {
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.pluginClass = pluginClass;
	}

	public UgsGrowingRulePlugin(int pluginId, String pluginName,
			String pluginClass, String description, Date addDate,
			Set<UgsGrowingRuleList> ugsGrowingRuleLists) {
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.pluginClass = pluginClass;
		this.description = description;
		this.addDate = addDate;
		this.ugsGrowingRuleLists = ugsGrowingRuleLists;
	}

	@Id
    @SequenceGenerator(name="plugin-seq-gen",sequenceName="ugs_growing_rule_plugin_plugin_id_seq", allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="plugin-seq-gen")
	@Column(name = "plugin_id", unique = true, nullable = false)
	public int getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(int pluginId) {
		this.pluginId = pluginId;
	}

	@Column(name = "version", length = 20)
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "plugin_name", nullable = false, length = 100)
	public String getPluginName() {
		return this.pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@Column(name = "plugin_class", nullable = false, length = 200)
	public String getPluginClass() {
		return this.pluginClass;
	}

	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "add_date", length = 29)
	public Date getAddDate() {
		return this.addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ugsGrowingRulePlugin")
	public Set<UgsGrowingRuleList> getUgsGrowingRuleLists() {
		return this.ugsGrowingRuleLists;
	}

	public void setUgsGrowingRuleLists(Set<UgsGrowingRuleList> ugsGrowingRuleLists) {
		this.ugsGrowingRuleLists = ugsGrowingRuleLists;
	}

}
