package com.prowesssoft.t2m.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prowesssoft.t2m.entity.Project;

public interface ProjectsRepository extends JpaRepository<Project, Long> {
	
	Project findByRequestId(String requestId);
	List<Project> findAllByOrderByProjectIdDesc();
	
    @Modifying
    @Transactional
    @Query("Update Project t SET t.currentStatus=:currentStatus WHERE t.requestId=:requestId")
    public void updateProjectStatus(@Param("requestId") String requestId,@Param("currentStatus") String currentStatus);
    
    @Modifying
    @Transactional
    @Query("SELECT p FROM Project p WHERE p.uploadedDt < :thresholdDate")
    List<Project> findByUploadedDt(@Param("thresholdDate") Date thresholdDate);
    
    Page<Project> findByExpired(int i, Pageable pageable);
    List<Project> findByExpiredOrderByProjectIdDesc(int expired);
    Page<Project> findByExpiredOrderByProjectId(int expired, Pageable pageable);
    Page<Project> findByExpiredOrderByProjectIdDesc(int expired, Pageable pageable);
    
 
}
