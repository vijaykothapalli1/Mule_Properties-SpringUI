package com.prowesssoft.t2m.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.repository.Query;

@Entity
@Table(name = "projects")
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long projectId;
	@Column(name = "request_id")
	private String requestId;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "project_type")
	private String projectType;
	@Column(name = "uploaded_dt")
	private  Date uploadedDt;
	@Column(name = "current_status")
	private String currentStatus;
	private int expired;
	@Column(name = "percent_completed")
	private String percentCompleted;
	@Column(name = "project_path")
	private String projectPath;
	@Column(name = "source_path")
	private String sourcePath;
	@Column(name = "report_path")
	private String reportPath;

	

	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Date getUploadedDt() {
		return uploadedDt;
	}
	public void setUploadedDt(Date uploadedDt) {
		this.uploadedDt = uploadedDt;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public int getExpired() {
		return expired;
	}
	public void setExpired(int expired) {
		this.expired = expired;
	}
	public String getPercentCompleted() {
		return percentCompleted;
	}
	public void setPercentCompleted(String percentCompleted) {
		this.percentCompleted = percentCompleted;
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

	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	
	public String getReportPath() {
		return reportPath;
	}
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	@Override
	public String toString() {
		return "Project [projectId=" + projectId + ", requestId=" + requestId + ", userId=" + userId + ", uploadedDt="
				+ uploadedDt + ", currentStatus=" + currentStatus + ", expired=" + expired + ", percentCompleted="
				+ percentCompleted + ", projectPath=" + projectPath + ", sourcePath=" + sourcePath +"]";
	}
	
	
	
}
