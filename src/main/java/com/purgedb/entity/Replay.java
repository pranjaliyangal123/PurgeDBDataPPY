package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for REPLAY table
 * Retention period: 15 days
 */
@Entity
@Table(name = "REPLAY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Replay {

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
    private Integer replayCount = 0;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "replayed_date")
    private LocalDateTime replayedDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
