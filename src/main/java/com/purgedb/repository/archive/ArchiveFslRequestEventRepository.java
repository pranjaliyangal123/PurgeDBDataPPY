package com.purgedb.repository.archive;

import com.purgedb.entity.ArchiveFslRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ARCHIVE_FSL_REQUEST_EVENT table (Archive Database)
 */
@Repository
public interface ArchiveFslRequestEventRepository extends JpaRepository<ArchiveFslRequestEvent, Long> {
}
