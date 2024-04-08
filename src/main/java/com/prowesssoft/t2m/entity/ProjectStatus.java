package com.prowesssoft.t2m.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="project_status")
public class ProjectStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_status_id")
	private Long projectStatusId;
	@Column(name = "project_id")
	private Long projectId;
	@Column(name = "request_id")
	private String requestId;
	private Date datetime;
	private String status;
	private String percent;
	

	public ProjectStatus(Long projectId, String requestId, Date datetime, String status) {
		super();
		this.projectId = projectId;
		this.requestId = requestId;
		this.datetime = datetime;
		this.status = status;
	}
	
	public ProjectStatus(Long projectId, String requestId, Date datetime, String status,String percent) {
		super();
		this.projectId = projectId;
		this.requestId = requestId;
		this.datetime = datetime;
		this.status = status;
		this.percent = percent;
	}

	public ProjectStatus() {
		super();
	}

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

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public Long getProjectStatusId() {
		return projectStatusId;
	}

	public void setProjectStatusId(Long projectStatusId) {
		this.projectStatusId = projectStatusId;
	}
	

}
