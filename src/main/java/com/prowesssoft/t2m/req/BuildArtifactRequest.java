package com.prowesssoft.t2m.req;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildArtifactRequest implements Serializable {
@JsonProperty("request_id")
private String requestId;
@JsonProperty("project_path")
private String projectPath;
@JsonProperty("report_path")
private String reportPath;

public String getRequestId() {
	return requestId;
}
public void setRequestId(String requestId) {
	this.requestId = requestId;
}
public String getProjectPath() {
	return projectPath;
}
public void setProjectPath(String projectPath) {
	this.projectPath = projectPath;
}
public String getReportPath() {
	return reportPath;
}
public void setReportPath(String reportPath) {
	this.reportPath = reportPath;
}

}
