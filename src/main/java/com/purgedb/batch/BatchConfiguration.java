package com.purgedb.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.purgedb.service.PurgeArchiveService;

/**
 * Spring Batch configuration for database purge and archive operations.
 * 
 * This configuration defines batch jobs that:
 * 1. Archive data from active tables to archive tables based on retention policies
 * 2. Purge archived data from active tables to free up space
 */
@Configuration
public class BatchConfiguration {

    @Autowired
    private PurgeArchiveService purgeArchiveService;

    @Autowired
    private PurgeArchiveJobListener jobListener;

    /**
     * Main purge and archive job that processes all tables.
     */
    @Bean
    public Job purgeArchiveJob(JobRepository jobRepository, Step purgeArchiveAllStep) {
        return new JobBuilder("purgeArchiveJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(purgeArchiveAllStep)
                .build();
    }

    /**
     * Step to archive and purge all tables.
     */
    @Bean
    public Step purgeArchiveAllStep(JobRepository jobRepository, 
                                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("purgeArchiveAllStep", jobRepository)
                .tasklet(purgeArchiveAllTasklet(), transactionManager)
                .build();
    }

    /**
     * Tasklet that executes the archive and purge for all tables.
     */
    @Bean
    public Tasklet purgeArchiveAllTasklet() {
        return (contribution, chunkContext) -> {
            purgeArchiveService.archiveAndPurgeAll();
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * Job for archiving and purging FSL_REQUEST_EVENT table only.
     */
    @Bean
    public Job fslRequestEventJob(JobRepository jobRepository, Step fslRequestEventStep) {
        return new JobBuilder("fslRequestEventJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fslRequestEventStep)
                .build();
    }

    @Bean
    public Step fslRequestEventStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("fslRequestEventStep", jobRepository)
                .tasklet(fslRequestEventTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet fslRequestEventTasklet() {
        return (contribution, chunkContext) -> {
            purgeArchiveService.archiveAndPurgeFslRequestEvents();
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * Job for archiving and purging EVENT_LOG_TABLE only.
     */
    @Bean
    public Job eventLogTableJob(JobRepository jobRepository, Step eventLogTableStep) {
        return new JobBuilder("eventLogTableJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(eventLogTableStep)
                .build();
    }

    @Bean
    public Step eventLogTableStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) {
        return new StepBuilder("eventLogTableStep", jobRepository)
                .tasklet(eventLogTableTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet eventLogTableTasklet() {
        return (contribution, chunkContext) -> {
            purgeArchiveService.archiveAndPurgeEventLogTable();
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * Job for archiving and purging REQUEST_EVENT table only.
     */
    @Bean
    public Job requestEventJob(JobRepository jobRepository, Step requestEventStep) {
        return new JobBuilder("requestEventJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(requestEventStep)
                .build();
    }

    @Bean
    public Step requestEventStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager) {
        return new StepBuilder("requestEventStep", jobRepository)
                .tasklet(requestEventTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet requestEventTasklet() {
        return (contribution, chunkContext) -> {
            purgeArchiveService.archiveAndPurgeRequestEvents();
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * Job for archiving and purging REPLAY table only.
     */
    @Bean
    public Job replayJob(JobRepository jobRepository, Step replayStep) {
        return new JobBuilder("replayJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(replayStep)
                .build();
    }

    @Bean
    public Step replayStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("replayStep", jobRepository)
                .tasklet(replayTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet replayTasklet() {
        return (contribution, chunkContext) -> {
            purgeArchiveService.archiveAndPurgeReplay();
            return RepeatStatus.FINISHED;
        };
    }
}
