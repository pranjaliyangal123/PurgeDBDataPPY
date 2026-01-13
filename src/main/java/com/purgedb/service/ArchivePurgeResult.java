package com.purgedb.service;

/**
 * Result class for a single table's archive and purge operation.
 */
public class ArchivePurgeResult {

    private final String tableName;
    private final int archivedCount;
    private final int purgedCount;
    private final boolean success;
    private final String errorMessage;

    public ArchivePurgeResult(String tableName, int archivedCount, int purgedCount) {
        this.tableName = tableName;
        this.archivedCount = archivedCount;
        this.purgedCount = purgedCount;
        this.success = true;
        this.errorMessage = null;
    }

    public ArchivePurgeResult(String tableName, String errorMessage) {
        this.tableName = tableName;
        this.archivedCount = 0;
        this.purgedCount = 0;
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public String getTableName() {
        return tableName;
    }

    public int getArchivedCount() {
        return archivedCount;
    }

    public int getPurgedCount() {
        return purgedCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("Table: %s, Archived: %d, Purged: %d, Status: SUCCESS",
                    tableName, archivedCount, purgedCount);
        } else {
            return String.format("Table: %s, Status: FAILED, Error: %s",
                    tableName, errorMessage);
        }
    }
}
