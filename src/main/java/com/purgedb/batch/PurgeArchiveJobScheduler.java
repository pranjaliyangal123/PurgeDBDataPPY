package com.purgedb.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled job launcher for database purge and archive operations.
 * 
 * This component schedules and launches batch jobs based on configured cron expressions.
 * The default schedule runs daily at 2 AM.
 */
@Component
public class PurgeArchiveJobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PurgeArchiveJobScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("purgeArchiveJob")
    private Job purgeArchiveJob;

    @Value("${purge.archive.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    /**
     * Scheduled task to run the purge and archive job.
     * Default schedule: Daily at 2 AM
     * 
     * This can be customized via the property: purge.archive.scheduler.cron
     */
    @Scheduled(cron = "${purge.archive.scheduler.cron:0 0 2 * * ?}")
    public void runScheduledPurgeArchiveJob() {
        if (!schedulerEnabled) {
            logger.debug("Purge archive scheduler is disabled");
            return;
        }

        logger.info("Starting scheduled purge and archive job at {}", LocalDateTime.now());
        
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("timestamp", LocalDateTime.now().toString())
                    .addString("triggeredBy", "scheduler")
                    .toJobParameters();

            jobLauncher.run(purgeArchiveJob, jobParameters);
            logger.info("Scheduled purge and archive job completed successfully");
        } catch (Exception e) {
            logger.error("Error running scheduled purge and archive job: {}", e.getMessage(), e);
        }
    }

    /**
     * Manually trigger the purge and archive job.
     * Can be called via REST endpoint or programmatically.
     */
    public void triggerPurgeArchiveJob() {
        logger.info("Manually triggering purge and archive job at {}", LocalDateTime.now());
        
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("timestamp", LocalDateTime.now().toString())
                    .addString("triggeredBy", "manual")
                    .toJobParameters();

            jobLauncher.run(purgeArchiveJob, jobParameters);
            logger.info("Manual purge and archive job completed successfully");
        } catch (Exception e) {
            logger.error("Error running manual purge and archive job: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to run purge and archive job", e);
        }
    }
}
