package com.prowesssoft.t2m.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prowesssoft.t2m.entity.ProjectConfig;

public interface ProjectConfigRepository extends JpaRepository<ProjectConfig, Long> {
	
	public List<ProjectConfig> findByProjectId(Long id);
    
 
}
