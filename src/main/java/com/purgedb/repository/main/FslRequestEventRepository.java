package com.purgedb.repository.main;

import com.purgedb.entity.FslRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for FSL_REQUEST_EVENT table (Main Database)
 */
@Repository
public interface FslRequestEventRepository extends JpaRepository<FslRequestEvent, Long> {

    /**
     * Find records older than the specified date
     * @param cutoffDate Records created before this date
     * @return List of old records
     */
    @Query("SELECT f FROM FslRequestEvent f WHERE f.createdDate < :cutoffDate")
    List<FslRequestEvent> findOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count records older than the specified date
     * @param cutoffDate Records created before this date
     * @return Count of old records
     */
    @Query("SELECT COUNT(f) FROM FslRequestEvent f WHERE f.createdDate < :cutoffDate")
    long countOldRecords(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete records older than the specified date
     * @param cutoffDate Records created before this date
     */
    void deleteByCreatedDateBefore(LocalDateTime cutoffDate);
}
