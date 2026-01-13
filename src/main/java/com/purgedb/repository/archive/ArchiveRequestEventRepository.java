package com.purgedb.repository.archive;

import com.purgedb.entity.ArchiveRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ARCHIVE_REQUEST_EVENT table (Archive Database)
 */
@Repository
public interface ArchiveRequestEventRepository extends JpaRepository<ArchiveRequestEvent, Long> {
}
