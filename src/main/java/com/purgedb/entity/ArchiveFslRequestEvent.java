package com.purgedb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Archive entity class for ARCHIVE_FSL_REQUEST_EVENT table
 */
@Entity
@Table(name = "ARCHIVE_FSL_REQUEST_EVENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveFslRequestEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, length = 100)
    private String requestId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

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
    public static ArchiveFslRequestEvent fromSource(FslRequestEvent source) {
        ArchiveFslRequestEvent archive = new ArchiveFslRequestEvent();
        archive.setId(source.getId());
        archive.setRequestId(source.getRequestId());
        archive.setEventType(source.getEventType());
        archive.setEventData(source.getEventData());
        archive.setStatus(source.getStatus());
        archive.setCreatedDate(source.getCreatedDate());
        archive.setModifiedDate(source.getModifiedDate());
        archive.setArchivedDate(LocalDateTime.now());
        return archive;
    }
}
