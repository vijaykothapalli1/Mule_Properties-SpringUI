package com.prowesssoft.t2m.service;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.prowesssoft.prowex.core.component.list.Database;
import com.prowesssoft.prowex.core.component.list.Domain;
import com.prowesssoft.prowex.core.component.list.FTPServer;
import com.prowesssoft.prowex.core.component.list.FileSystem;
import com.prowesssoft.prowex.core.component.list.HttpServer;
import com.prowesssoft.prowex.core.component.list.JMSServer;
import com.prowesssoft.prowex.core.component.list.MailServer;
import com.prowesssoft.t2m.config.Constants;
import com.prowesssoft.t2m.entity.Project;
import com.prowesssoft.t2m.entity.ProjectConfig;
import com.prowesssoft.t2m.entity.ProjectStatus;
import com.prowesssoft.t2m.exception.StorageFileNotFoundException;
import com.prowesssoft.t2m.repository.ProjectConfigRepository;
import com.prowesssoft.t2m.repository.ProjectStatusRepository;
import com.prowesssoft.t2m.repository.ProjectsRepository;
import com.prowesssoft.t2m.req.BuildArtifactRequest;
import com.prowesssoft.t2m.req.ProjectStatusRequest;
import com.prowesssoft.t2m.res.ApiResponse;
import com.prowesssoft.t2m.res.BackendApiCallResponse;
import com.prowesssoft.t2m.res.ProjectStatusResponse;

@Service
public class ProjectsService {
	
	@Value("${app.basepath}")
	private String basepath;
	
	@Value("${app.pythonapi.url}")
	private String pythonApiUrl;
	
	@Value("${app.prowerse.output-path}")
	private String prowesrOutputPath;

	@Autowired
	private ProjectsRepository projectRepository;

	@Autowired
	private ProjectConfigRepository projectConfigRepository;

	@Autowired
	private ProjectStatusRepository projectStatusRepository;

	@Autowired
	private StorageService storageService;

	private final RestTemplate restTemplate;

	public ProjectsService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(10))
				.setReadTimeout(Duration.ofSeconds(10)).build();
	}

	public ApiResponse processE2EProject(String inputType, MultipartFile inputFile, MultipartFile configFile) {
		String requestId = null;
		Long projectId = null;
		try {
			requestId = Constants.ProjectType.E2EAUTO+""+Instant.now().toEpochMilli();
			Project project = new Project();
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_START"));
			project.setRequestId(requestId + "");
			project.setUploadedDt(new Date());
			project.setUserId(1L);
			project.setProjectType(Constants.ProjectType.E2EAUTO);
			project = projectRepository.save(project);
			projectId = project.getProjectId();

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_START"));
			List<ProjectConfig> config = new ArrayList<>();
			
			String inputFilePath = storageService.store(inputFile, requestId, "project-source.zip");
			String configFilePath = storageService.store(configFile, requestId, "config.yaml");
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_COMPLETE"));
			project.setSourcePath(inputFilePath);
			
			config.add(new ProjectConfig(projectId, Constants.ProjectConfigs.CONFIG_FILE_PATH, configFilePath, new Date()));
			config.add(new ProjectConfig(projectId, Constants.ProjectConfigs.INPUT_TYPE, inputType, new Date()));
			projectConfigRepository.saveAll(config);
			projectRepository.save(project);

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_COMPLETE"));
			HttpHeaders reqHeaders = new HttpHeaders();
			reqHeaders.setContentType(MediaType.APPLICATION_JSON);
			reqHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Map<String, String> reqBody = new HashMap<String, String>();
			reqBody.put("request_id", requestId);
			reqBody.put("source", inputFilePath);
			reqBody.put("config", configFilePath);
			reqBody.put("input_type", inputType);
			reqBody.put("location", "on-premise");
			System.out.println("Invoking python API"+pythonApiUrl+" with data : "+reqBody);

			try {
				HttpEntity<Map> entity = new HttpEntity<>(reqBody, reqHeaders);
				ResponseEntity<BackendApiCallResponse> response = this.restTemplate.exchange(
						pythonApiUrl+"t2m-trigger", HttpMethod.POST, entity, BackendApiCallResponse.class);
				System.out.println("Python API response : "+response);
				if (response.getBody().getStatus()) {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_START"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_START");
				} else {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				}
			} catch (Exception e) {
				e.printStackTrace();
				projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
				projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				return new ApiResponse(false, requestId, "Project conversion failed");
			}

		} catch (Exception e) {
			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_FAILED"));
			projectRepository.updateProjectStatus(requestId, "UPLOAD_FAILED");
			// e.printStackTrace();
			return new ApiResponse(true, requestId, "Project upload failed");
		}

		return new ApiResponse(true, requestId, "Project uploaded Successfully");
	}
	
	public ApiResponse processXSD2RAMLProject(String projectName,Boolean preserveCase, MultipartFile inputFile, MultipartFile configFile) {
		String requestId = null;
		Long projectId = null;
		try {
			requestId = Constants.ProjectType.XSD2RAML+""+Instant.now().toEpochMilli();
			Project project = new Project();
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_START"));
			project.setRequestId(requestId + "");
			project.setUploadedDt(new Date());
			project.setUserId(1L);
			project.setProjectType(Constants.ProjectType.XSD2RAML);
			project = projectRepository.save(project);
			projectId = project.getProjectId();

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_START"));
			List<ProjectConfig> config = new ArrayList<>();
			
			String inputFilePath = storageService.store(inputFile, requestId, "project-source.zip");
			String configFilePath = storageService.store(configFile, requestId, "config.yaml");
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_COMPLETE"));
			project.setSourcePath(inputFilePath);
			
			config.add(new ProjectConfig(projectId, Constants.ProjectConfigs.CONFIG_FILE_PATH, configFilePath, new Date()));
			config.add(new ProjectConfig(projectId, Constants.ProjectConfigs.PRESERVE_CASE, preserveCase.toString(), new Date()));
			config.add(new ProjectConfig(projectId, Constants.ProjectConfigs.RAML_NAME, projectName, new Date()));
			
			projectConfigRepository.saveAll(config);
			projectRepository.save(project);

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_COMPLETE"));
			HttpHeaders reqHeaders = new HttpHeaders();
			reqHeaders.setContentType(MediaType.APPLICATION_JSON);
			reqHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Map<String, String> reqBody = new HashMap<String, String>();
			reqBody.put("request_id", requestId);
			reqBody.put("source_path", inputFilePath);
			reqBody.put("config", configFilePath);
			reqBody.put("preserve_case", preserveCase.toString());
			reqBody.put("project_name", projectName);
			System.out.println("Invoking python API "+pythonApiUrl+" with data : "+reqBody);

			try {
				HttpEntity<Map> entity = new HttpEntity<>(reqBody, reqHeaders);
				ResponseEntity<BackendApiCallResponse> response = this.restTemplate.exchange(
						pythonApiUrl+"xsd-raml", HttpMethod.POST, entity, BackendApiCallResponse.class);
				if (response.getBody().getStatus()) {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_START"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_START");
				} else {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				}
			} catch (Exception e) {
				e.printStackTrace();
				projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
				projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				return new ApiResponse(false,requestId , "Project conversion failed");
			}

		} catch (Exception e) {
			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_FAILED"));
			projectRepository.updateProjectStatus(requestId, "UPLOAD_FAILED");
			// e.printStackTrace();
			return new ApiResponse(true,requestId , "Project upload failed");
		}

		return new ApiResponse(true, requestId, "Project uploaded Successfully");
	}
//	
	public ApiResponse processXSLT2DWLProject(MultipartFile inputFile) {
		String requestId = null;
		Long projectId = null;
		try {
			requestId = Constants.ProjectType.XSLT2DWL+""+Instant.now().toEpochMilli();
			Project project = new Project();
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_START"));
			project.setRequestId(requestId + "");
			project.setUploadedDt(new Date());
			project.setUserId(1L);
			project.setProjectType(Constants.ProjectType.XSLT2DWL);
			project = projectRepository.save(project);
			projectId = project.getProjectId();

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_START"));
			
			String inputFilePath = storageService.store(inputFile, requestId, "project-source.zip");
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_COMPLETE"));
			project.setSourcePath(inputFilePath);

			projectRepository.save(project);

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_COMPLETE"));
			HttpHeaders reqHeaders = new HttpHeaders();
			reqHeaders.setContentType(MediaType.APPLICATION_JSON);
			reqHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Map<String, String> reqBody = new HashMap<String, String>();
			reqBody.put("request_id", requestId);
			reqBody.put("source_path", inputFilePath);
			System.out.println("Invoking python API "+pythonApiUrl+" with data : "+reqBody);

			try {
				HttpEntity<Map> entity = new HttpEntity<>(reqBody, reqHeaders);
				ResponseEntity<BackendApiCallResponse> response = this.restTemplate.exchange(
						pythonApiUrl+"xslt-dwl", HttpMethod.POST, entity, BackendApiCallResponse.class);
				if (response.getBody().getStatus()) {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_START"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_START");
				} else {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				}
			} catch (Exception e) {
				e.printStackTrace();
				projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
				projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				return new ApiResponse(false, requestId, "Project conversion failed");
			}

		} catch (Exception e) {
			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_FAILED"));
			projectRepository.updateProjectStatus(requestId, "UPLOAD_FAILED");
			// e.printStackTrace();
			return new ApiResponse(true, requestId, "Project upload failed");
		}

		return new ApiResponse(true, requestId, "Project uploaded Successfully");
	}
	
	public ApiResponse processGV2YAMLProject(MultipartFile inputFile) {
		String requestId = null;
		Long projectId = null;
		try {
			requestId = Constants.ProjectType.GV2YAML+""+Instant.now().toEpochMilli();
			Project project = new Project();
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_START"));
			project.setRequestId(requestId + "");
			project.setUploadedDt(new Date());
			project.setUserId(1L);
			project.setProjectType(Constants.ProjectType.GV2YAML);
			project = projectRepository.save(project);
			projectId = project.getProjectId();

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_START"));
			
			String inputFilePath = storageService.store(inputFile, requestId, "project-source.zip");
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_COMPLETE"));
			project.setSourcePath(inputFilePath);

			projectRepository.save(project);

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_COMPLETE"));
			HttpHeaders reqHeaders = new HttpHeaders();
			reqHeaders.setContentType(MediaType.APPLICATION_JSON);
			reqHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Map<String, String> reqBody = new HashMap<String, String>();
			reqBody.put("request_id", requestId);
			reqBody.put("source_path", inputFilePath);
			System.out.println("Invoking python API "+pythonApiUrl+" with data : "+reqBody);

			try {
				HttpEntity<Map> entity = new HttpEntity<>(reqBody, reqHeaders);
				ResponseEntity<BackendApiCallResponse> response = this.restTemplate.exchange(
						pythonApiUrl+"global-config", HttpMethod.POST, entity, BackendApiCallResponse.class);
				if (response.getBody().getStatus()) {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_START"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_START");
				} else {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				}
			} catch (Exception e) {
				e.printStackTrace();
				projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
				projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				return new ApiResponse(false, requestId, "Project conversion failed");
			}

		} catch (Exception e) {
			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_FAILED"));
			projectRepository.updateProjectStatus(requestId, "UPLOAD_FAILED");
			// e.printStackTrace();
			return new ApiResponse(true, requestId, "Project upload failed");
		}

		return new ApiResponse(true, requestId, "Project uploaded Successfully");
	}
	
	public ApiResponse processDomainAnalysisProject(MultipartFile inputFile) {
		String requestId = null;
		Long projectId = null;
		try {
			requestId = Constants.ProjectType.DMNANLS+""+Instant.now().toEpochMilli();
			Project project = new Project();
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_START"));
			project.setRequestId(requestId + "");
			project.setUploadedDt(new Date());
			project.setUserId(1L);
			project.setProjectType(Constants.ProjectType.DMNANLS);
			project = projectRepository.save(project);
			projectId = project.getProjectId();

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_START"));
			
			String inputFilePath = storageService.store(inputFile, requestId, "project-source.zip");
			project.setCurrentStatus(Constants.projectStatus.get("UPLOAD_COMPLETE"));
			project.setSourcePath(inputFilePath);

			projectRepository.save(project);

			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_COMPLETE"));
			HttpHeaders reqHeaders = new HttpHeaders();
			reqHeaders.setContentType(MediaType.APPLICATION_JSON);
			reqHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Map<String, String> reqBody = new HashMap<String, String>();
			reqBody.put("request_id", requestId);
			reqBody.put("source_path", inputFilePath);
			System.out.println("Invoking python API "+pythonApiUrl+" with data : "+reqBody);

			try {
				HttpEntity<Map> entity = new HttpEntity<>(reqBody, reqHeaders);
				ResponseEntity<BackendApiCallResponse> response = this.restTemplate.exchange(
						pythonApiUrl+"analyze-domain", HttpMethod.POST, entity, BackendApiCallResponse.class);
				if (response.getBody().getStatus()) {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_START"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_START");
				} else {
					projectStatusRepository
							.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
					projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				}
			} catch (Exception e) {
				e.printStackTrace();
				projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "CONVERSION_FAILED"));
				projectRepository.updateProjectStatus(requestId, "CONVERSION_FAILED");
				return new ApiResponse(false, requestId, "Project conversion failed");
			}

		} catch (Exception e) {
			projectStatusRepository.save(new ProjectStatus(projectId, requestId, new Date(), "UPLOAD_FAILED"));
			projectRepository.updateProjectStatus(requestId, "UPLOAD_FAILED");
			// e.printStackTrace();
			return new ApiResponse(true,requestId , "Project upload failed");
		}

		return new ApiResponse(true, requestId, "Project uploaded Successfully");
	}
	
	

	public ApiResponse updateStatus(ProjectStatusRequest req) {
		Project project = projectRepository.findByRequestId(req.getRequestId());

		if (project != null) {
			project.setCurrentStatus(req.getStatus());
			project.setPercentCompleted(req.getOverallPercentCompleted());
			projectStatusRepository.save(
					new ProjectStatus(project.getProjectId(), project.getRequestId(), new Date(), req.getStatus()));
			projectRepository.updateProjectStatus(req.getRequestId(), req.getStatus());
			return new ApiResponse(true, null, "Project Status Updated Successfully");
		}

		return new ApiResponse(false, null, "Status update failed");
	}

	public ApiResponse getProjectStatus(String reqId) {
		Project project = projectRepository.findByRequestId(reqId);
		if (project != null) {
			List<ProjectStatus> list = projectStatusRepository.findByRequestIdOrderByProjectStatusIdDesc(reqId);
			List<ProjectConfig> configs = projectConfigRepository.findByProjectId(project.getProjectId());
			ProjectStatusResponse res = new ProjectStatusResponse();
			res.setProjectId(project.getProjectId());
			res.setCurrentStatus(Constants.projectStatus.getOrDefault(project.getCurrentStatus(),"N/A"));
			res.setRequestId(reqId);
			res.setUserId(project.getUserId());
			res.setUploadedDt(project.getUploadedDt());
			res.setConfigs(configs);
			if("READY_TO_DOWNLOAD".equals(project.getCurrentStatus())) {
				res.setIsConversionCompleted(true);
				res.setIndicatorStatus("completed");
			}else if("CONVERSION_FAILED".equals(project.getCurrentStatus())) {
				res.setIsConversionCompleted(false);
				res.setIndicatorStatus("error");
			}else {
				res.setIsConversionCompleted(false);
				res.setIndicatorStatus("progress");
			}
			res.setProjectPath(project.getProjectPath());
			res.setReportPath(project.getReportPath());
			res.setSourcePath(project.getSourcePath());
			res.setProjectType(Constants.getProjectType(project.getProjectType()));
			Map<Date, String> map = new LinkedHashMap<>();
			for (ProjectStatus s : list) {
				map.put(s.getDatetime(), Constants.projectStatus.getOrDefault(s.getStatus(),"N/A"));
			}
//		List<String> statuses = list.stream()
//                .map(s -> new String(s.getDatetime() + " | "+s.getStatus()))
//                .collect(Collectors.toList());
			res.setOldStatusUpdates(map);
			return new ApiResponse(true, res, "");
		} else {
			return new ApiResponse(false, reqId, "Project not found with requested ID");
		}

	}

	public ApiResponse getProjectByReqId(String requestId) {
		Project project = projectRepository.findByRequestId(requestId);
		if (project != null) {
		String dlPath = basepath + "download/" + project.getProjectPath();
		project.setProjectPath(dlPath);
		return new ApiResponse(true, project, "");
		}else {
			return new ApiResponse(false, null, "Project not found with requested ID");
		}
	}

	public ApiResponse updateProjectArtifact(BuildArtifactRequest req) {
		Project project = projectRepository.findByRequestId(req.getRequestId());

		if (project != null) {
			project.setProjectPath(req.getProjectPath());
			project.setReportPath(req.getReportPath());
			//project.setDcProjects(req.getDcProjects().stream().map(Object::toString).collect(Collectors.joining(",")));
			projectStatusRepository.save(
					new ProjectStatus(project.getProjectId(), project.getRequestId(), new Date(), "READY_TO_DOWNLOAD"));
			project.setCurrentStatus("READY_TO_DOWNLOAD");
			project.setPercentCompleted("100");
			projectRepository.save(project);
			return new ApiResponse(true, null, "Project Artifacts Updated Successfully");
		}
		return new ApiResponse(false, null, "Status update failed");
	}

	// below APIs are for testing/debugging

	public ApiResponse getAllProjectStatus() {
		List<ProjectStatus> project = projectStatusRepository.findAllByOrderByProjectStatusIdDesc();
		return new ApiResponse(true, project, "");
	}

	public ApiResponse getAllProjects() {
		List<Project> projects = new ArrayList<>();
		for (Project proj :  projectRepository.findAllByOrderByProjectIdDesc()) {
			proj.setCurrentStatus(Constants.projectStatus.getOrDefault(proj.getCurrentStatus(),"N/A"));
			proj.setProjectType(Constants.getProjectType(proj.getProjectType()));
			projects.add(proj);
		}
		return new ApiResponse(true, projects, "");
	}

	public ApiResponse resetData() {
		projectRepository.deleteAll();
		projectStatusRepository.deleteAll();
		return new ApiResponse(true, null, "Data cleared");
	}
	
	public Resource getProjectFile(String id) {
		Project project = projectRepository.findByRequestId(id);
		if (project != null) {
			try {
				Path file = new File(project.getProjectPath()).toPath();
				Resource resource = new UrlResource(file.toUri());
				if (resource.exists() || resource.isReadable()) {
					return resource;
				} else {
					throw new StorageFileNotFoundException("Could not read file for id: " + id);

				}
			} catch (MalformedURLException e) {
				throw new StorageFileNotFoundException("Could not read file for id: " + id, e);
			}
		}
		return null;
	}
	
	public Resource getReportFile(String id) {
		Project project = projectRepository.findByRequestId(id);
		if (project != null) {
			try {
				Path file = new File(project.getReportPath()).toPath();
				Resource resource = new UrlResource(file.toUri());
				if (resource.exists() || resource.isReadable()) {
					return resource;
				} else {
					throw new StorageFileNotFoundException("Could not read file for id: " + id);

				}
			} catch (MalformedURLException e) {
				throw new StorageFileNotFoundException("Could not read file for id: " + id, e);
			}
		}
		return null;
	}
	
	public Map getDomainDetails(String reqId) {
		Map<String,List> connections = new HashMap<String,List>();
		
		String filePath = prowesrOutputPath.replace("%rid%", reqId);
		DocumentBuilder builder;
		
		try {
			String s = Files.readString(Paths.get(filePath), Charset.defaultCharset());
			
			JAXBContext context = JAXBContext.newInstance(Domain.class); 
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			Domain domain = (Domain) unmarshaller.unmarshal(new StringReader(s));
			ArrayList<Database> databases = domain.getDatabaseList().getDatabases();
			ArrayList<FileSystem> filesystems = domain.getFileSystemList().getFileSystems();
			ArrayList<FTPServer> ftpservers = domain.getFTPServerList().getFTPServers();
			ArrayList<HttpServer> httpservers = domain.getHttpServerList().getHttpServers();
			ArrayList<JMSServer> jmsservers = domain.getJmslist().getJMSServers();
			ArrayList<MailServer> mailservers = domain.getMailServerList().getMailServers();
			
			
			connections.put("Databases",databases);
			connections.put("FileSystems",filesystems);
			connections.put("FTP Servers",ftpservers);
			connections.put("HTTP Servers",httpservers);
			connections.put("JMS Servers",jmsservers);
			connections.put("Mail Servers",mailservers);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return connections;
	}
	
	 public Page<Project> getExpiredProjects(Pageable pageable) {
		    return projectRepository.findByExpired(1, pageable);
		}
	 
//	 public Page<Project>getActiveProjects(Pageable pageable){
//		return projectRepository.findByExpiredOrderByProjectIdDesc(0,pageable);
	 
	 public Page<ProjectStatusResponse> getActiveProjects(Pageable pageable) {
		    Page<Project> activeProjectsPage = projectRepository.findByExpiredOrderByProjectIdDesc(0, pageable);
		    List<ProjectStatusResponse> projectStatusResponses = new ArrayList<>();
		    for (Project project : activeProjectsPage.getContent()) {
		        ProjectStatusResponse response = mapToProjectStatusResponse(project);
		        projectStatusResponses.add(response);
		    }
		    return new PageImpl<>(projectStatusResponses, pageable, activeProjectsPage.getTotalElements());
		}

		private ProjectStatusResponse mapToProjectStatusResponse(Project project) {
		    ProjectStatusResponse response = new ProjectStatusResponse();
		    response.setRequestId(project.getRequestId());
		    response.setProjectType(Constants.getProjectType(project.getProjectType()));		    
		    response.setUploadedDt(project.getUploadedDt());
		    response.setCurrentStatus(Constants.projectStatus.getOrDefault(project.getCurrentStatus(), "N/A"));
		    return response;
		}
	 
	 
		 
	 }


