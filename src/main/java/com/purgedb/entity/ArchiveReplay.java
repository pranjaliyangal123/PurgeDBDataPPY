package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Archive entity class for ARCHIVE_REPLAY table
 */
@Entity
@Table(name = "ARCHIVE_REPLAY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveReplay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    @Column(name = "original_request", columnDefinition = "TEXT")
    private String originalRequest;

    @Column(name = "replay_request", columnDefinition = "TEXT")
    private String replayRequest;

    @Column(name = "replay_status", length = 20)
    private String replayStatus;

    @Column(name = "replay_count")
    private Integer replayCount;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "replayed_date")
    private LocalDateTime replayedDate;

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
    public static ArchiveReplay fromSource(Replay source) {
        ArchiveReplay archive = new ArchiveReplay();
        archive.setId(source.getId());
        archive.setTransactionId(source.getTransactionId());
        archive.setOriginalRequest(source.getOriginalRequest());
        archive.setReplayRequest(source.getReplayRequest());
        archive.setReplayStatus(source.getReplayStatus());
        archive.setReplayCount(source.getReplayCount());
        archive.setCreatedDate(source.getCreatedDate());
        archive.setReplayedDate(source.getReplayedDate());
        archive.setArchivedDate(LocalDateTime.now());
        return archive;
    }
}
