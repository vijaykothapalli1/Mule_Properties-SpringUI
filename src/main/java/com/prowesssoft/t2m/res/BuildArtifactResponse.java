package com.prowesssoft.t2m.res;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildArtifactResponse implements Serializable{
	@JsonProperty("request_id")
	private String requestId;
	@JsonProperty("project_id")
	private Long projectId;
	@JsonProperty("user_id")
	private Long userId;
	@JsonProperty("project_created_at")
	private String projectCreatedAt;
	@JsonProperty("project_expires_at")
	private String projectExpiresAt;
	@JsonProperty("project_download_url")
	private String projectDownloadUrl;
	
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
	public String getProjectCreatedAt() {
		return projectCreatedAt;
	}
	public void setProjectCreatedAt(String projectCreatedAt) {
		this.projectCreatedAt = projectCreatedAt;
	}
	public String getProjectExpiresAt() {
		return projectExpiresAt;
	}
	public void setProjectExpiresAt(String projectExpiresAt) {
		this.projectExpiresAt = projectExpiresAt;
	}
	public String getProjectDownloadUrl() {
		return projectDownloadUrl;
	}
	public void setProjectDownloadUrl(String projectDownloadUrl) {
		this.projectDownloadUrl = projectDownloadUrl;
	}
	
	

}
