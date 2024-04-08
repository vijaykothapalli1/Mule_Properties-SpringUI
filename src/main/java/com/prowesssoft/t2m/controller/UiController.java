package com.prowesssoft.t2m.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prowesssoft.t2m.entity.Project;
import com.prowesssoft.t2m.exception.StorageFileNotFoundException;
import com.prowesssoft.t2m.res.ProjectStatusResponse;
import com.prowesssoft.t2m.service.ProjectsService;

@Controller
public class UiController {

	@Value("${app.basepath}")
	String basepath;
	
	@Value("${app.pagination.default-size}")
	private int defaultSize;
	
	@Autowired
	ProjectsService projectsService;
	
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public String index(Principal principal,Model model,@RequestParam(defaultValue = "0") int page) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		Page<ProjectStatusResponse> activeProjects = projectsService.getActiveProjects(PageRequest.of(page, defaultSize));
		 model.addAttribute("projects", activeProjects);
		 model.addAttribute("currentPage", page);
		 model.addAttribute("totalPages", activeProjects.getTotalPages());
		 model.addAttribute("pageSize", defaultSize);
		return "my-projects.html";
	}
	
	@RequestMapping(value = { "/tibco2mule" }, method = RequestMethod.GET)
	public String uploadProject(Principal principal,Model model) {
		model.addAttribute("BASE_URL", basepath);
		return "tibco2mule.html";
	}
	
	@RequestMapping(value = { "/generate-ramls" }, method = RequestMethod.GET)
	public String generateRamls(Principal principal,Model model) {
		model.addAttribute("BASE_URL", basepath);
		return "generate-ramls.html";
	}
	
	@RequestMapping(value = { "/generate-transformations" }, method = RequestMethod.GET)
	public String generatTransformations(Principal principal,Model model) {
		model.addAttribute("BASE_URL", basepath);
		return "generate-transformations.html";
	}
	
	@RequestMapping(value = { "/analyse-tibco-domain" }, method = RequestMethod.GET)
	public String analyseTibcoDomain(Principal principal,Model model) {
		model.addAttribute("BASE_URL", basepath);
		return "analyse-tibco-domain.html";
	}
	
	@RequestMapping(value = { "/convert-global-vars" }, method = RequestMethod.GET)
	public String convertGlobalVars(Principal principal,Model model) {
		model.addAttribute("BASE_URL", basepath);
		return "convert-global-vars.html";
	}
	
	@RequestMapping(value = { "/my-projects" }, method = RequestMethod.GET)
	public String myProject(Principal principal,Model model,@RequestParam(defaultValue = "0") int page) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		Page<ProjectStatusResponse> activeProjects = projectsService.getActiveProjects(PageRequest.of(page, defaultSize));
		 model.addAttribute("projects", activeProjects);
		 model.addAttribute("currentPage", page);
		 model.addAttribute("totalPages", activeProjects.getTotalPages());
		 model.addAttribute("pageSize", defaultSize);
		return "my-projects.html";
	}
	
	@RequestMapping(value = { "/projects/{id}" }, method = RequestMethod.GET)
	public String projectDetails(Principal principal,Model model,@PathVariable String id) {
		model.addAttribute("projectInfo", projectsService.getProjectStatus(id));
		return "project-details";
	}
	
	@RequestMapping(value = { "/projects/{id}/domain" }, method = RequestMethod.GET)
	public String projectDomainDetails(Principal principal,Model model,@PathVariable String id) {
		Map conns = projectsService.getDomainDetails(id);
//		if(conns.size() < 0) {
//			 return "redirect:/";
//		}
		model.addAttribute("projectInfo", projectsService.getProjectStatus(id));
		model.addAttribute("domainInfo", conns );
		return "domain-details";
	}
	
	
	@RequestMapping(value = { "/expired-projects" }, method = RequestMethod.GET)
	public String getExpiredProjects(Principal principal, Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
	    Page<Project> expiredProjects = projectsService.getExpiredProjects(PageRequest.of(page, defaultSize));
	    model.addAttribute("expiredProjects", expiredProjects);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("pageSize", defaultSize);
	    return "expired-projects.html";
	}
	
	@RequestMapping(value = { "/blank" }, method = RequestMethod.GET)
	public String blank(Principal principal,Model model) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		return "blank";
	}
	
	@RequestMapping(value = { "/error" }, method = RequestMethod.GET)
	public String error(Principal principal,Model model) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		return "error/500";
	}
	
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String startSession(HttpServletRequest httpRequest,Model model) {
		//model.addAttribute("BASE_URL", host+":"+port+context);
		return "sign-in.html";
	}
	
	@RequestMapping(value = { "/help" }, method = RequestMethod.GET)
	public String help(Principal principal,Model model) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		return "help";
	}
	
	@RequestMapping(value = { "/test" }, method = RequestMethod.GET)
	public String test(Principal principal,Model model) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		return "test";
	}
	
	@RequestMapping(value = { "/about" }, method = RequestMethod.GET)
	public String about(Principal principal,Model model) {
		//model.addAttribute("userInfo", userService.getUserInfo(principal));
		return "about";
	}

	@GetMapping("/download/project/{id}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String id) {
		Resource file = projectsService.getProjectFile(id);
		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@GetMapping("/download/report/{id}")
	@ResponseBody
	public ResponseEntity<Resource> serveReportFile(@PathVariable String id) {
		Resource file = projectsService.getReportFile(id);
		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@GetMapping("/download/sample-config")
	@ResponseBody
	public ResponseEntity<Resource> downloadSample() {
		Path file;
		Resource resource;
		try {
			file = new File("src/main/resources/static/res/config-sample.yaml").toPath();
			if (file == null)
				return ResponseEntity.notFound().build();
			resource = new UrlResource(file.toUri());
			
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file", e);
		}
		

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
}
