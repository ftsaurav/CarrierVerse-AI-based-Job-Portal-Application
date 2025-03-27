package com.jobportal.jobportal.services;

import com.jobportal.jobportal.entity.*;
import com.jobportal.jobportal.repository.JobPostActivityRepository;
import com.jobportal.jobportal.repository.JobSeekerApplyRepository;
import com.jobportal.jobportal.repository.JobSeekerSaveRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository, JobSeekerApplyRepository jobSeekerApplyRepository, JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity)
    {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter)
    {
        List<IRecruiterJobs> recruiterJobsDtos= jobPostActivityRepository.getRecruiterJobs(recruiter);
        List<RecruiterJobsDto> recruiterJobsDtoList= new ArrayList<>();
        for(IRecruiterJobs rec: recruiterJobsDtos)
        {
            JobLocation loc= new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp= new JobCompany(rec.getCompanyId(), rec.getName(), "");
            recruiterJobsDtoList.add(new RecruiterJobsDto(rec.getTotalCandidates(),
                    rec.getJob_post_id(), rec.getJob_title(), loc, comp));
        }
        return recruiterJobsDtoList;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(()->new RuntimeException("Job not found"));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        return Objects.isNull(searchDate) ? jobPostActivityRepository.searchWithoutDate(job, location, remote,type) :
                jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }

    @Transactional // Ensures database integrity
    public void deleteJobById(Long jobId) {
        // First, delete related applications in job_seeker_apply
        jobSeekerApplyRepository.deleteByJobId(jobId);

        jobSeekerSaveRepository.deleteByJobId(jobId);

        jobPostActivityRepository.deleteById(Math.toIntExact(jobId));
    }
}
