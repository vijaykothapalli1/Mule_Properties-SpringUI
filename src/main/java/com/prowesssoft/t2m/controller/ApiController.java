package com.prowesssoft.t2m.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prowesssoft.t2m.entity.User;
import com.prowesssoft.t2m.req.BuildArtifactRequest;
import com.prowesssoft.t2m.req.ProjectStatusRequest;
import com.prowesssoft.t2m.res.ApiResponse;
import com.prowesssoft.t2m.res.BackendApiCallResponse;
import com.prowesssoft.t2m.service.ProjectsService;
import com.prowesssoft.t2m.service.StorageService;
import com.prowesssoft.t2m.service.UsersService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Java APIs", description = "Java APIs")
@RestController
public class ApiController {

	@Autowired
	UsersService userService;

	@Autowired
	ProjectsService projectsService;

	@Autowired
	StorageService storageService;

	@Operation(summary = "Add new user", tags = { "User" })
	@PostMapping("/api/users")
	public ApiResponse saveuser(@RequestBody User user) {
		return userService.saveUser(user);
	}

	@Operation(summary = "Get Users List", tags = { "User" })
	@GetMapping("/api/users")
	public ApiResponse getUsers() {
		return userService.getUsers();
	}

	@Operation(summary = "Upload new Project", tags = { "Project" })
	@PostMapping("/api/t2m-trigger")
	public ApiResponse uploadProject(@RequestParam(name = "input_type", required = false) String inputType,
			@RequestPart("input_file") MultipartFile inputFile, @RequestPart("config_file") MultipartFile configFile) {

		return projectsService.processE2EProject(inputType, inputFile, configFile);
	}
	
	@Operation(summary = "Upload XSD to RAML zip file", tags = { "Project" })
	@PostMapping("/api/xsd-raml")
	public ApiResponse uploadRaml(@RequestParam(name = "raml_name", required = false) String ramlName,@RequestParam(name = "preserve_case", required = true) Boolean preserveCase,
			@RequestPart("input_file") MultipartFile inputFile, @RequestPart("config_file") MultipartFile configFile) {

		return projectsService.processXSD2RAMLProject(ramlName,preserveCase, inputFile, configFile);
	}

	@Operation(summary = "Upload XSLT2DWL Project", tags = { "Project" })
	@PostMapping("/api/xslt-dwl")
	public ApiResponse generateTransformations(@RequestPart("input_file") MultipartFile inputFile) {
		return projectsService.processXSLT2DWLProject(inputFile);
	}
	
	@Operation(summary = "Upload new GV Conversion Project", tags = { "Project" })
	@PostMapping("/api/global-config")
	public ApiResponse convertGlobalVars(@RequestPart("input_file") MultipartFile inputFile) {
		return projectsService.processGV2YAMLProject(inputFile);
	}
	
	@Operation(summary = "Upload new Project", tags = { "Project" })
	@PostMapping("/api/domain-analysis")
	public ApiResponse uploadDomainAnalysis(@RequestPart("input_file") MultipartFile inputFile){
		return projectsService.processDomainAnalysisProject(inputFile);
	}
	
	@Operation(summary = "Retrive list of projects", tags = { "Project" })
	@GetMapping("/api/projects")
	public ApiResponse getAllProjects() {
		return projectsService.getAllProjects();
	}

	@Operation(summary = "Retrive list of project status", tags = { "Project" })
	@GetMapping("/api/status")
	public ApiResponse getProjectStatus() {
		return projectsService.getAllProjectStatus();
	}

	@Operation(summary = "Retrive project status by request id", tags = { "Project" })
	@GetMapping("/api/status/{reqId}")
	public ApiResponse getProjectStatusByReqId(@PathVariable String reqId) {
		return projectsService.getProjectStatus(reqId);
	}

	@Operation(summary = "Update Project Status", tags = { "Project" })
	@PostMapping("/api/status")
	public ApiResponse updateProjectStatus(@RequestBody ProjectStatusRequest req) {
		return projectsService.updateStatus(req);
	}

	@Operation(summary = "Get Artifacts for a project based on request ID", tags = { "Project" })
	@GetMapping("/api/artifacts/{reqId}")
	public ApiResponse getProjectArtifacts(@PathVariable String reqId) {
		return projectsService.getProjectByReqId(reqId);
	}

	@Operation(summary = "Updates artifact for project", tags = { "Project" })
	@PostMapping("/api/artifacts")
	public ApiResponse updateProjectArtifacts(@RequestBody BuildArtifactRequest req) {
		return projectsService.updateProjectArtifact(req);
	}

//	@GetMapping("/download/{filename:.+}")
//	@ResponseBody
//	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//		Resource file = storageService.loadAsResource(filename);
//
//		if (file == null)
//			return ResponseEntity.notFound().build();
//
//		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
//	}

	@PostMapping("/api/backen-api-trigger-test")
	public BackendApiCallResponse triggerDummyApi(@RequestBody String json) throws Exception {
		Map<String, Object> jsonToMap = new ObjectMapper().readValue(json, Map.class);
		BackendApiCallResponse res = new BackendApiCallResponse();
		res.setRequestId((String) jsonToMap.get("requestId"));
		res.setStatus(true);
		Thread.sleep(3000);
		return res;
	}

	@GetMapping("/api/reset-data")
	public ApiResponse resetData(String reqId) {
		return projectsService.resetData();
	}

}
