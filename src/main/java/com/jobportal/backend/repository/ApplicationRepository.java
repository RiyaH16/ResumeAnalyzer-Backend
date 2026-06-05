package com.jobportal.backend.repository;
import java.util.List;
import com.jobportal.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository
        extends JpaRepository<Application, Long> {
	
	List<Application> findByJobId(Long jobId);
	List<Application> findByUserEmail(String email);

}