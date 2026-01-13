package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for EVENT_LOG_TABLE
 * Retention period: 7 days
 */
@Entity
@Table(name = "EVENT_LOG_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLogTable {

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

    @PrePersist
    protected void onCreate() {
        if (logTimestamp == null) {
            logTimestamp = LocalDateTime.now();
        }
    }
}
