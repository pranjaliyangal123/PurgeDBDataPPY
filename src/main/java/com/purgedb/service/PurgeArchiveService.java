package com.purgedb.service;

import com.purgedb.entity.*;
import com.purgedb.repository.archive.*;
import com.purgedb.repository.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for database purge and archive operations.
 * 
 * This service handles the archival and purging of data from active tables
 * based on configured retention policies. Data older than the retention period
 * is first copied to archive tables, then deleted from active tables.
 */
@Service
public class PurgeArchiveService {

    private static final Logger logger = LoggerFactory.getLogger(PurgeArchiveService.class);

    // Main repositories
    private final FslRequestEventRepository fslRequestEventRepository;
    private final EventLogTableRepository eventLogTableRepository;
    private final RequestEventRepository requestEventRepository;
    private final ReplayRepository replayRepository;

    // Archive repositories
    private final ArchiveFslRequestEventRepository archiveFslRequestEventRepository;
    private final ArchiveEventLogTableRepository archiveEventLogTableRepository;
    private final ArchiveRequestEventRepository archiveRequestEventRepository;
    private final ArchiveReplayRepository archiveReplayRepository;

    // Retention policies (in days)
    @Value("${retention.policy.fsl-request-event:7}")
    private int fslRequestEventRetentionDays;

    @Value("${retention.policy.event-log-table:7}")
    private int eventLogTableRetentionDays;

    @Value("${retention.policy.request-event:15}")
    private int requestEventRetentionDays;

    @Value("${retention.policy.replay:15}")
    private int replayRetentionDays;

    @Value("${batch.chunk.size:1000}")
    private int batchSize;

    public PurgeArchiveService(
            FslRequestEventRepository fslRequestEventRepository,
            EventLogTableRepository eventLogTableRepository,
            RequestEventRepository requestEventRepository,
            ReplayRepository replayRepository,
            ArchiveFslRequestEventRepository archiveFslRequestEventRepository,
            ArchiveEventLogTableRepository archiveEventLogTableRepository,
            ArchiveRequestEventRepository archiveRequestEventRepository,
            ArchiveReplayRepository archiveReplayRepository) {
        this.fslRequestEventRepository = fslRequestEventRepository;
        this.eventLogTableRepository = eventLogTableRepository;
        this.requestEventRepository = requestEventRepository;
        this.replayRepository = replayRepository;
        this.archiveFslRequestEventRepository = archiveFslRequestEventRepository;
        this.archiveEventLogTableRepository = archiveEventLogTableRepository;
        this.archiveRequestEventRepository = archiveRequestEventRepository;
        this.archiveReplayRepository = archiveReplayRepository;
    }

    /**
     * Archive and purge FSL_REQUEST_EVENT records older than retention period.
     * 
     * @return ArchivePurgeResult containing counts of archived and purged records
     */
    @Transactional
    public ArchivePurgeResult archiveAndPurgeFslRequestEvents() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(fslRequestEventRetentionDays);
        logger.info("Starting archive and purge for FSL_REQUEST_EVENT with cutoff date: {}", cutoffDate);

        long countBefore = fslRequestEventRepository.countOldRecords(cutoffDate);
        if (countBefore == 0) {
            logger.info("No FSL_REQUEST_EVENT records to archive");
            return new ArchivePurgeResult("FSL_REQUEST_EVENT", 0, 0);
        }

        // Archive records
        List<FslRequestEvent> recordsToArchive = fslRequestEventRepository.findOldRecords(cutoffDate);
        int archivedCount = 0;
        for (FslRequestEvent record : recordsToArchive) {
            ArchiveFslRequestEvent archiveRecord = ArchiveFslRequestEvent.fromSource(record);
            archiveFslRequestEventRepository.save(archiveRecord);
            archivedCount++;
        }
        logger.info("Archived {} FSL_REQUEST_EVENT records", archivedCount);

        // Purge records from active table
        fslRequestEventRepository.deleteByCreatedDateBefore(cutoffDate);
        logger.info("Purged {} FSL_REQUEST_EVENT records from active table", archivedCount);

        return new ArchivePurgeResult("FSL_REQUEST_EVENT", archivedCount, archivedCount);
    }

    /**
     * Archive and purge EVENT_LOG_TABLE records older than retention period.
     * 
     * @return ArchivePurgeResult containing counts of archived and purged records
     */
    @Transactional
    public ArchivePurgeResult archiveAndPurgeEventLogTable() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(eventLogTableRetentionDays);
        logger.info("Starting archive and purge for EVENT_LOG_TABLE with cutoff date: {}", cutoffDate);

        long countBefore = eventLogTableRepository.countOldRecords(cutoffDate);
        if (countBefore == 0) {
            logger.info("No EVENT_LOG_TABLE records to archive");
            return new ArchivePurgeResult("EVENT_LOG_TABLE", 0, 0);
        }

        // Archive records
        List<EventLogTable> recordsToArchive = eventLogTableRepository.findOldRecords(cutoffDate);
        int archivedCount = 0;
        for (EventLogTable record : recordsToArchive) {
            ArchiveEventLogTable archiveRecord = ArchiveEventLogTable.fromSource(record);
            archiveEventLogTableRepository.save(archiveRecord);
            archivedCount++;
        }
        logger.info("Archived {} EVENT_LOG_TABLE records", archivedCount);

        // Purge records from active table
        eventLogTableRepository.deleteByLogTimestampBefore(cutoffDate);
        logger.info("Purged {} EVENT_LOG_TABLE records from active table", archivedCount);

        return new ArchivePurgeResult("EVENT_LOG_TABLE", archivedCount, archivedCount);
    }

    /**
     * Archive and purge REQUEST_EVENT records older than retention period.
     * 
     * @return ArchivePurgeResult containing counts of archived and purged records
     */
    @Transactional
    public ArchivePurgeResult archiveAndPurgeRequestEvents() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(requestEventRetentionDays);
        logger.info("Starting archive and purge for REQUEST_EVENT with cutoff date: {}", cutoffDate);

        long countBefore = requestEventRepository.countOldRecords(cutoffDate);
        if (countBefore == 0) {
            logger.info("No REQUEST_EVENT records to archive");
            return new ArchivePurgeResult("REQUEST_EVENT", 0, 0);
        }

        // Archive records
        List<RequestEvent> recordsToArchive = requestEventRepository.findOldRecords(cutoffDate);
        int archivedCount = 0;
        for (RequestEvent record : recordsToArchive) {
            ArchiveRequestEvent archiveRecord = ArchiveRequestEvent.fromSource(record);
            archiveRequestEventRepository.save(archiveRecord);
            archivedCount++;
        }
        logger.info("Archived {} REQUEST_EVENT records", archivedCount);

        // Purge records from active table
        requestEventRepository.deleteByCreatedDateBefore(cutoffDate);
        logger.info("Purged {} REQUEST_EVENT records from active table", archivedCount);

        return new ArchivePurgeResult("REQUEST_EVENT", archivedCount, archivedCount);
    }

    /**
     * Archive and purge REPLAY records older than retention period.
     * 
     * @return ArchivePurgeResult containing counts of archived and purged records
     */
    @Transactional
    public ArchivePurgeResult archiveAndPurgeReplay() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(replayRetentionDays);
        logger.info("Starting archive and purge for REPLAY with cutoff date: {}", cutoffDate);

        long countBefore = replayRepository.countOldRecords(cutoffDate);
        if (countBefore == 0) {
            logger.info("No REPLAY records to archive");
            return new ArchivePurgeResult("REPLAY", 0, 0);
        }

        // Archive records
        List<Replay> recordsToArchive = replayRepository.findOldRecords(cutoffDate);
        int archivedCount = 0;
        for (Replay record : recordsToArchive) {
            ArchiveReplay archiveRecord = ArchiveReplay.fromSource(record);
            archiveReplayRepository.save(archiveRecord);
            archivedCount++;
        }
        logger.info("Archived {} REPLAY records", archivedCount);

        // Purge records from active table
        replayRepository.deleteByCreatedDateBefore(cutoffDate);
        logger.info("Purged {} REPLAY records from active table", archivedCount);

        return new ArchivePurgeResult("REPLAY", archivedCount, archivedCount);
    }

    /**
     * Execute archive and purge for all tables.
     * 
     * @return PurgeArchiveSummary containing results for all tables
     */
    public PurgeArchiveSummary archiveAndPurgeAll() {
        logger.info("Starting archive and purge process for all tables");
        
        PurgeArchiveSummary summary = new PurgeArchiveSummary();
        
        try {
            summary.addResult(archiveAndPurgeFslRequestEvents());
        } catch (Exception e) {
            logger.error("Error processing FSL_REQUEST_EVENT: {}", e.getMessage());
            summary.addError("FSL_REQUEST_EVENT", e.getMessage());
        }
        
        try {
            summary.addResult(archiveAndPurgeEventLogTable());
        } catch (Exception e) {
            logger.error("Error processing EVENT_LOG_TABLE: {}", e.getMessage());
            summary.addError("EVENT_LOG_TABLE", e.getMessage());
        }
        
        try {
            summary.addResult(archiveAndPurgeRequestEvents());
        } catch (Exception e) {
            logger.error("Error processing REQUEST_EVENT: {}", e.getMessage());
            summary.addError("REQUEST_EVENT", e.getMessage());
        }
        
        try {
            summary.addResult(archiveAndPurgeReplay());
        } catch (Exception e) {
            logger.error("Error processing REPLAY: {}", e.getMessage());
            summary.addError("REPLAY", e.getMessage());
        }
        
        logger.info("Archive and purge process completed. Summary: {}", summary);
        return summary;
    }
}
