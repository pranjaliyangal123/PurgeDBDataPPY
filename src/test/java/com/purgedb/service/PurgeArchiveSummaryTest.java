package com.purgedb.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PurgeArchiveSummary class.
 */
class PurgeArchiveSummaryTest {

    @Test
    void testEmptySummary() {
        PurgeArchiveSummary summary = new PurgeArchiveSummary();
        
        assertNotNull(summary.getStartTime());
        assertNull(summary.getEndTime());
        assertTrue(summary.getResults().isEmpty());
        assertEquals(0, summary.getTotalArchived());
        assertEquals(0, summary.getTotalPurged());
        assertEquals(0, summary.getSuccessCount());
        assertEquals(0, summary.getFailureCount());
        assertTrue(summary.isAllSuccessful());
    }

    @Test
    void testAddSuccessfulResult() {
        PurgeArchiveSummary summary = new PurgeArchiveSummary();
        
        summary.addResult(new ArchivePurgeResult("TABLE_1", 100, 100));
        summary.addResult(new ArchivePurgeResult("TABLE_2", 50, 50));
        
        assertEquals(2, summary.getResults().size());
        assertEquals(150, summary.getTotalArchived());
        assertEquals(150, summary.getTotalPurged());
        assertEquals(2, summary.getSuccessCount());
        assertEquals(0, summary.getFailureCount());
        assertTrue(summary.isAllSuccessful());
    }

    @Test
    void testAddFailedResult() {
        PurgeArchiveSummary summary = new PurgeArchiveSummary();
        
        summary.addResult(new ArchivePurgeResult("TABLE_1", 100, 100));
        summary.addError("TABLE_2", "Connection failed");
        
        assertEquals(2, summary.getResults().size());
        assertEquals(100, summary.getTotalArchived());
        assertEquals(100, summary.getTotalPurged());
        assertEquals(1, summary.getSuccessCount());
        assertEquals(1, summary.getFailureCount());
        assertFalse(summary.isAllSuccessful());
    }

    @Test
    void testComplete() {
        PurgeArchiveSummary summary = new PurgeArchiveSummary();
        assertNull(summary.getEndTime());
        
        summary.complete();
        assertNotNull(summary.getEndTime());
    }

    @Test
    void testToString() {
        PurgeArchiveSummary summary = new PurgeArchiveSummary();
        summary.addResult(new ArchivePurgeResult("TABLE_1", 100, 100));
        
        String output = summary.toString();
        assertTrue(output.contains("PURGE AND ARCHIVE SUMMARY"));
        assertTrue(output.contains("TABLE_1"));
        assertTrue(output.contains("Total Records Archived: 100"));
    }
}
