package com.purgedb.repository.archive;

import com.purgedb.entity.ArchiveEventLogTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ARCHIVE_EVENT_LOG_TABLE (Archive Database)
 */
@Repository
public interface ArchiveEventLogTableRepository extends JpaRepository<ArchiveEventLogTable, Long> {
}
