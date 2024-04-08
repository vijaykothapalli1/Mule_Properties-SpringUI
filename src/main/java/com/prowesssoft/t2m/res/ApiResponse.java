package com.prowesssoft.t2m.res;

import java.io.Serializable;

public class ApiResponse implements Serializable {
	private String message;
	private Boolean success;
	private Object data;
	
	public ApiResponse(Boolean success, Object data, String message) {
		super();
		this.success = success;
		this.data = data;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ApiResponse [message=" + message + ", success=" + success + ", data=" + data + "]";
	}
	
	
	

}
