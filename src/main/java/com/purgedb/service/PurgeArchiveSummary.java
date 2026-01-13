package com.purgedb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary class for the entire purge and archive operation.
 */
public class PurgeArchiveSummary {

    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private final List<ArchivePurgeResult> results;
    private int totalArchived;
    private int totalPurged;
    private int successCount;
    private int failureCount;

    public PurgeArchiveSummary() {
        this.startTime = LocalDateTime.now();
        this.results = new ArrayList<>();
        this.totalArchived = 0;
        this.totalPurged = 0;
        this.successCount = 0;
        this.failureCount = 0;
    }

    public void addResult(ArchivePurgeResult result) {
        results.add(result);
        if (result.isSuccess()) {
            totalArchived += result.getArchivedCount();
            totalPurged += result.getPurgedCount();
            successCount++;
        } else {
            failureCount++;
        }
    }

    public void addError(String tableName, String errorMessage) {
        results.add(new ArchivePurgeResult(tableName, errorMessage));
        failureCount++;
    }

    public void complete() {
        this.endTime = LocalDateTime.now();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<ArchivePurgeResult> getResults() {
        return results;
    }

    public int getTotalArchived() {
        return totalArchived;
    }

    public int getTotalPurged() {
        return totalPurged;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public boolean isAllSuccessful() {
        return failureCount == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================================================================\n");
        sb.append("PURGE AND ARCHIVE SUMMARY\n");
        sb.append("================================================================================\n\n");
        
        for (ArchivePurgeResult result : results) {
            sb.append(result.toString()).append("\n");
        }
        
        sb.append("\n--------------------------------------------------------------------------------\n");
        sb.append(String.format("Total Tables Processed: %d%n", results.size()));
        sb.append(String.format("  Successful: %d%n", successCount));
        sb.append(String.format("  Failed: %d%n", failureCount));
        sb.append(String.format("Total Records Archived: %d%n", totalArchived));
        sb.append(String.format("Total Records Purged: %d%n", totalPurged));
        sb.append("================================================================================\n");
        
        return sb.toString();
    }
}
