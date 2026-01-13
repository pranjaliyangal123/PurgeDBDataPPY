package com.purgedb.repository.main;

import com.purgedb.entity.EventLogTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for EVENT_LOG_TABLE (Main Database)
 */
@Repository
public interface EventLogTableRepository extends JpaRepository<EventLogTable, Long> {

    /**
     * Find records older than the specified date
     * @param cutoffDate Records created before this date
     * @return List of old records
     */
    @Query("SELECT e FROM EventLogTable e WHERE e.logTimestamp < :cutoffDate")
    List<EventLogTable> findOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count records older than the specified date
     * @param cutoffDate Records created before this date
     * @return Count of old records
     */
    @Query("SELECT COUNT(e) FROM EventLogTable e WHERE e.logTimestamp < :cutoffDate")
    long countOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete records older than the specified date
     * @param cutoffDate Records created before this date
     */
    void deleteByLogTimestampBefore(LocalDateTime cutoffDate);
}
