package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for REQUEST_EVENT table
 * Retention period: 15 days
 */
@Entity
@Table(name = "REQUEST_EVENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, length = 100)
    private String requestId;

    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
