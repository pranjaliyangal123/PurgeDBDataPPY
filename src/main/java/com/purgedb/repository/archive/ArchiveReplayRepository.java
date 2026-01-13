package com.purgedb.repository.archive;

import com.purgedb.entity.ArchiveReplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ARCHIVE_REPLAY table (Archive Database)
 */
@Repository
public interface ArchiveReplayRepository extends JpaRepository<ArchiveReplay, Long> {
}
