package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Archive entity class for ARCHIVE_EVENT_LOG_TABLE
 */
@Entity
@Table(name = "ARCHIVE_EVENT_LOG_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveEventLogTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_level", nullable = false, length = 20)
    private String logLevel;

    @Column(name = "log_message", nullable = false, columnDefinition = "TEXT")
    private String logMessage;

    @Column(name = "log_source", length = 100)
    private String logSource;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "log_timestamp", nullable = false)
    private LocalDateTime logTimestamp;

    @Column(name = "archived_date", nullable = false)
    private LocalDateTime archivedDate;

    @PrePersist
    protected void onCreate() {
        if (archivedDate == null) {
            archivedDate = LocalDateTime.now();
        }
    }

    /**
     * Create archive entity from source entity
     */
    public static ArchiveEventLogTable fromSource(EventLogTable source) {
        ArchiveEventLogTable archive = new ArchiveEventLogTable();
        archive.setId(source.getId());
        archive.setLogLevel(source.getLogLevel());
        archive.setLogMessage(source.getLogMessage());
        archive.setLogSource(source.getLogSource());
        archive.setUserId(source.getUserId());
        archive.setSessionId(source.getSessionId());
        archive.setLogTimestamp(source.getLogTimestamp());
        archive.setArchivedDate(LocalDateTime.now());
        return archive;
    }
}
