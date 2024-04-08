package com.prowesssoft.t2m.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectStatusRequest implements Serializable {
	@JsonProperty("request_id")
	private String requestId;
	private String status;
	@JsonProperty("current_stage_percent_completed")
	private String currentStagePercentCompleted;
	@JsonProperty("overall_percent_completed")
	private String overallPercentCompleted;

	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCurrentStagePercentCompleted() {
		return currentStagePercentCompleted;
	}
	public void setCurrentStagePercentCompleted(String currentStagePercentCompleted) {
		this.currentStagePercentCompleted = currentStagePercentCompleted;
	}
	public String getOverallPercentCompleted() {
		return overallPercentCompleted;
	}
	public void setOverallPercentCompleted(String overallPercentCompleted) {
		this.overallPercentCompleted = overallPercentCompleted;
	}
	
	
	
	

}
