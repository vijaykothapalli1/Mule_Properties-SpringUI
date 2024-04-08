package com.prowesssoft.t2m.config;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.prowesssoft.t2m.entity.Project;
import com.prowesssoft.t2m.repository.ProjectsRepository;

import java.util.logging.*;

@Component
public class FileCleanupScheduler {
	private static final Logger logger = Logger.getLogger(FileCleanupScheduler.class.getName());

	@Value("${app.scheduler.active}")
	private boolean isSchedulerActive;
	
	@Value("${app.scheduler.oldRecordsInHours}")
	private int oldRecordsInHours;

	@Autowired
	private ProjectsRepository projectsRepository;

	@Scheduled(fixedRateString = "${app.scheduler.fixedRateInMilliseconds}")
	@Transactional
	public void deleteAndUpdate() {
		if (isSchedulerActive) {
			Calendar thresholdTime = Calendar.getInstance();
			thresholdTime.add(Calendar.HOUR, -oldRecordsInHours);
			Date thresholdDate = thresholdTime.getTime();
			logger.info("threshold time" + thresholdDate);

			List<Project> projectsToDelete = projectsRepository.findByUploadedDt(thresholdDate);
			logger.info("response" + projectsToDelete);

			for (Project project : projectsToDelete) {
				if ("completed".equals(project.getCurrentStatus()) && project.getExpired() == 0) {
					logger.info("Project path:" + project.getProjectPath());
					logger.info("Source path:" + project.getSourcePath());
					deleteFile(project.getProjectPath());
					deleteSourceFile(project.getSourcePath());
					project.setExpired(1);
				}
			}
			projectsRepository.saveAll(projectsToDelete);
		}
	}

	private void deleteFile(String projectPath) {
		if (projectPath != null && !projectPath.isEmpty()) {
			File file = new File(projectPath);
			if (file.exists()) {
				boolean deleted = file.delete();
				if (deleted) {
					System.out.println("File deleted successfully: " + projectPath);
				} else {
					System.err.println("Failed to delete file: " + projectPath);
				}
			} else {
				System.err.println("File not found at path: " + projectPath);
			}
		} else {
			System.err.println("Invalid or empty file path provided.");
		}
	}

	private void deleteSourceFile(String sourcePath) {
		if (sourcePath != null && !sourcePath.isEmpty()) {
			File file = new File(sourcePath);
			if (file.exists()) {
				boolean deleted = file.delete();
				if (deleted) {
					System.out.println("File deleted successfully: " + sourcePath);
				} else {
					System.err.println("Failed to delete file: " + sourcePath);
				}
			} else {
				System.err.println("File not found at path: " + sourcePath);
			}
		} else {
			System.err.println("Invalid or empty file path provided.");
		}
	}
}
