package com.prowesssoft.t2m.req;

import java.io.Serializable;
import java.util.List;

import com.prowesssoft.prowex.core.component.list.Database;
import com.prowesssoft.prowex.core.component.list.FileSystem;
import com.prowesssoft.prowex.core.component.list.HttpServer;

public class DomainDetails implements Serializable {

	private List<Database> databases;
	private List<FileSystem> filesystems;
	private List<HttpServer> httpservers;
	public List<Database> getDatabases() {
		return databases;
	}
	public void setDatabases(List<Database> databases) {
		this.databases = databases;
	}
	public List<FileSystem> getFilesystems() {
		return filesystems;
	}
	public void setFilesystems(List<FileSystem> filesystems) {
		this.filesystems = filesystems;
	}
	public List<HttpServer> getHttpservers() {
		return httpservers;
	}
	public void setHttpservers(List<HttpServer> httpservers) {
		this.httpservers = httpservers;
	}
	
	
	
	
	
}
