package com.prowesssoft.t2m.res;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prowesssoft.t2m.entity.ProjectConfig;

public class ProjectStatusResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	@JsonProperty("request_id")
	private String requestId;
	@JsonProperty("project_id")
	private Long projectId;
	@JsonProperty("user_id")
	private Long userId;
	@JsonProperty("uploaded_dt")
	private  Date uploadedDt;
	@JsonProperty("current_status")
	private String currentStatus;
	@JsonProperty("old_status_updates")
	private Map<Date,String> oldStatusUpdates;
	@JsonProperty("is_conversion_completed")
	private Boolean isConversionCompleted;
	@JsonProperty("indicator_status")
	private String indicatorStatus;
	@JsonProperty("configs")
	private List<ProjectConfig> configs;
	@JsonProperty("project_path")
	private String projectPath;
	@JsonProperty("source_path")
	private String sourcePath;
	@JsonProperty("report_path")
	private String reportPath;
	@JsonProperty("project_type")
	private String projectType;
	
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public Boolean getIsConversionCompleted() {
		return isConversionCompleted;
	}
	public void setIsConversionCompleted(Boolean isConversionCompleted) {
		this.isConversionCompleted = isConversionCompleted;
	}
	public Date getUploadedDt() {
		return uploadedDt;
	}
	public void setUploadedDt(Date uploadedDt) {
		this.uploadedDt = uploadedDt;
	}
	public Map<Date, String> getOldStatusUpdates() {
		return oldStatusUpdates;
	}
	public void setOldStatusUpdates(Map<Date, String> oldStatusUpdates) {
		this.oldStatusUpdates = oldStatusUpdates;
	}
	public List<ProjectConfig> getConfigs() {
		return configs;
	}
	public void setConfigs(List<ProjectConfig> configs) {
		this.configs = configs;
	}
	public String getIndicatorStatus() {
		return indicatorStatus;
	}
	public void setIndicatorStatus(String indicatorStatus) {
		this.indicatorStatus = indicatorStatus;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getReportPath() {
		return reportPath;
	}
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	
	
	

}
