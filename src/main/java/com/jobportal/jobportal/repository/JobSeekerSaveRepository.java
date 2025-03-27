package com.jobportal.jobportal.repository;

import com.jobportal.jobportal.entity.JobPostActivity;
import com.jobportal.jobportal.entity.JobSeekerProfile;
import com.jobportal.jobportal.entity.JobSeekerSave;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);

    boolean existsByUserIdAndJob(JobSeekerProfile user, JobPostActivity job);

    @Modifying
    @Transactional
    @Query("DELETE FROM JobSeekerSave j WHERE j.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);
}