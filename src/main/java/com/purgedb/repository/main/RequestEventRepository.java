package com.purgedb.repository.main;

import com.purgedb.entity.RequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for REQUEST_EVENT table (Main Database)
 */
@Repository
public interface RequestEventRepository extends JpaRepository<RequestEvent, Long> {

    /**
     * Find records older than the specified date
     * @param cutoffDate Records created before this date
     * @return List of old records
     */
    @Query("SELECT r FROM RequestEvent r WHERE r.createdDate < :cutoffDate")
    List<RequestEvent> findOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count records older than the specified date
     * @param cutoffDate Records created before this date
     * @return Count of old records
     */
    @Query("SELECT COUNT(r) FROM RequestEvent r WHERE r.createdDate < :cutoffDate")
    long countOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete records older than the specified date
     * @param cutoffDate Records created before this date
     */
    void deleteByCreatedDateBefore(LocalDateTime cutoffDate);
}
