package com.jobportal.jobportal.repository;

import com.jobportal.jobportal.entity.JobPostActivity;
import com.jobportal.jobportal.entity.JobSeekerApply;
import com.jobportal.jobportal.entity.JobSeekerProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

    @Repository
    public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

        List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

        List<JobSeekerApply> findByJob(JobPostActivity job);

        @Modifying
        @Transactional
        @Query("DELETE FROM JobSeekerApply j WHERE j.job.id = :jobId")
        void deleteByJobId(@Param("jobId") Long jobId);
    }

