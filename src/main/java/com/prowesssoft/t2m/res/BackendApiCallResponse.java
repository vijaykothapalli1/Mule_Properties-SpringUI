package com.prowesssoft.t2m.res;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackendApiCallResponse implements Serializable{
	private Boolean status;
	@JsonProperty("request_id")
	private String requestId;
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	

}
