package com.jobportal.backend.service;

import java.util.List;
import com.jobportal.backend.entity.Application;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    
  
    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository,
			JobRepository jobRepository) {
		super();
		this.applicationRepository = applicationRepository;
		this.userRepository = userRepository;
		this.jobRepository = jobRepository;
		
	}

	public Application applyJob(
            Long userId,
            Long jobId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        Application application = new Application();

        application.setUser(user);
        application.setJob(job);

        application.setStatus("APPLIED");

        application.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }
	
	public Application applyByEmail(
	        String email,
	        Long jobId
	) {

	    User user = userRepository
	            .findByEmail(email)
	            .orElseThrow();

	    Job job = jobRepository
	            .findById(jobId)
	            .orElseThrow();

	    Application application =
	            new Application();

	    application.setUser(user);

	    application.setJob(job);

	    application.setStatus("APPLIED");

	    application.setAppliedAt(
	            LocalDateTime.now()
	    );

	    return applicationRepository.save(
	            application
	    );
	}
    
    public List<Application> getApplicationsByJob(
            Long jobId
    ) {
        return applicationRepository.findByJobId(jobId);
    }
    
    public Application updateStatus(
            Long applicationId,
            String status
    ) {

        Application application =
                applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found"
                        ));

        application.setStatus(status);

        // SAVE FIRST
        Application updatedApplication =
                applicationRepository.save(application);

      

        return updatedApplication;
    }
    
    public List<Application> getApplicationsByEmail(
            String email
    ) {

        return applicationRepository
                .findByUserEmail(email);
    }
}