package com.prowesssoft.t2m.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "project_config")
public class ProjectConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_config_id")
	private Long projectConfigId;
	@Column(name = "project_id")
	private Long projectId;
	@Column(name = "config_name")
	private String configName;
	@Column(name = "config_value")
	private String configValue;
	@Column(name = "created_dt")
	private  Date createdDt;
	
	
	
	public ProjectConfig() {
		super();
	}
	
	public ProjectConfig(Long projectId, String configName, String configValue, Date createdDt) {
		super();
		this.projectId = projectId;
		this.configName = configName;
		this.configValue = configValue;
		this.createdDt = createdDt;
	}
	public Long getProjectConfigId() {
		return projectConfigId;
	}
	public void setProjectConfigId(Long projectConfigId) {
		this.projectConfigId = projectConfigId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public Date getCreatedDt() {
		return createdDt;
	}
	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}
	
}
