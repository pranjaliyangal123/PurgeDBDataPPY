package com.purgedb.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Job execution listener for purge and archive batch jobs.
 * 
 * Provides logging before and after job execution for monitoring and auditing.
 */
@Component
public class PurgeArchiveJobListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(PurgeArchiveJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("================================================================================");
        logger.info("Starting Job: {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job ID: {}", jobExecution.getJobId());
        logger.info("Start Time: {}", LocalDateTime.now());
        logger.info("Job Parameters: {}", jobExecution.getJobParameters());
        logger.info("================================================================================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchStatus status = jobExecution.getStatus();
        
        logger.info("================================================================================");
        logger.info("Job Completed: {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job ID: {}", jobExecution.getJobId());
        logger.info("Status: {}", status);
        logger.info("End Time: {}", LocalDateTime.now());
        
        if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
            Duration duration = Duration.between(
                    jobExecution.getStartTime(), 
                    jobExecution.getEndTime()
            );
            logger.info("Duration: {} seconds", duration.getSeconds());
        }

        if (status == BatchStatus.COMPLETED) {
            logger.info("Job executed successfully");
        } else if (status == BatchStatus.FAILED) {
            logger.error("Job failed with exit status: {}", jobExecution.getExitStatus());
            jobExecution.getAllFailureExceptions().forEach(exception -> 
                logger.error("Exception: {}", exception.getMessage(), exception)
            );
        }
        
        logger.info("================================================================================");
    }
}
