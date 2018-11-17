package com.xrk.usd.dal.entity;

// Generated 2015-9-7 18:09:32 by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * UgsUserGradeConfig generated by hbm2java
 */
@Entity
@Table(name = "ugs_user_grade_config", schema = "public")
public class UgsUserGradeConfig implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
	private int gradeId;
	private UgsGrowingType ugsGrowingType;
	private UgsPointType ugsPointType;
	private String gradeName;
	private Set<UgsUserGradeConfigList> ugsUserGradeConfigLists = new HashSet<UgsUserGradeConfigList>(0);

	public UgsUserGradeConfig() {
	}

	public UgsUserGradeConfig(int gradeId, UgsGrowingType ugsGrowingType,
			UgsPointType ugsPointType, String gradeName) {
		this.gradeId = gradeId;
		this.ugsGrowingType = ugsGrowingType;
		this.ugsPointType = ugsPointType;
		this.gradeName = gradeName;
	}

	public UgsUserGradeConfig(int gradeId, UgsGrowingType ugsGrowingType,
			UgsPointType ugsPointType, String gradeName,
			Set<UgsUserGradeConfigList> ugsUserGradeConfigLists) {
		this.gradeId = gradeId;
		this.ugsGrowingType = ugsGrowingType;
		this.ugsPointType = ugsPointType;
		this.gradeName = gradeName;
		this.ugsUserGradeConfigLists = ugsUserGradeConfigLists;
	}

	@Id
	@SequenceGenerator(name="ugs_user_grade_config_grade_id",sequenceName="ugs_user_grade_config_grade_id_seq", allocationSize=1)
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="ugs_user_grade_config_grade_id")
	@Column(name = "grade_id", unique = true, nullable = false)
	public int getGradeId() {
		return this.gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_code", nullable = false)
	public UgsGrowingType getUgsGrowingType() {
		return this.ugsGrowingType;
	}

	public void setUgsGrowingType(UgsGrowingType ugsGrowingType) {
		this.ugsGrowingType = ugsGrowingType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "point_type_id", nullable = false)
	public UgsPointType getUgsPointType() {
		return this.ugsPointType;
	}

	public void setUgsPointType(UgsPointType ugsPointType) {
		this.ugsPointType = ugsPointType;
	}

	@Column(name = "grade_name", nullable = false, length = 60)
	public String getGradeName() {
		return this.gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ugsUserGradeConfig")
	public Set<UgsUserGradeConfigList> getUgsUserGradeConfigLists() {
		return this.ugsUserGradeConfigLists;
	}

	public void setUgsUserGradeConfigLists(Set<UgsUserGradeConfigList> ugsUserGradeConfigLists) {
		this.ugsUserGradeConfigLists = ugsUserGradeConfigLists;
	}

}