package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Archive entity class for ARCHIVE_REQUEST_EVENT table
 */
@Entity
@Table(name = "ARCHIVE_REQUEST_EVENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveRequestEvent {

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
    public static ArchiveRequestEvent fromSource(RequestEvent source) {
        ArchiveRequestEvent archive = new ArchiveRequestEvent();
        archive.setId(source.getId());
        archive.setRequestId(source.getRequestId());
        archive.setRequestType(source.getRequestType());
        archive.setRequestPayload(source.getRequestPayload());
        archive.setResponsePayload(source.getResponsePayload());
        archive.setStatusCode(source.getStatusCode());
        archive.setProcessingTimeMs(source.getProcessingTimeMs());
        archive.setCreatedDate(source.getCreatedDate());
        archive.setArchivedDate(LocalDateTime.now());
        return archive;
    }
}
