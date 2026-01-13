package com.purgedb.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ArchivePurgeResult class.
 */
class ArchivePurgeResultTest {

    @Test
    void testSuccessfulResult() {
        ArchivePurgeResult result = new ArchivePurgeResult("TEST_TABLE", 100, 100);
        
        assertEquals("TEST_TABLE", result.getTableName());
        assertEquals(100, result.getArchivedCount());
        assertEquals(100, result.getPurgedCount());
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testFailedResult() {
        ArchivePurgeResult result = new ArchivePurgeResult("TEST_TABLE", "Connection failed");
        
        assertEquals("TEST_TABLE", result.getTableName());
        assertEquals(0, result.getArchivedCount());
        assertEquals(0, result.getPurgedCount());
        assertFalse(result.isSuccess());
        assertEquals("Connection failed", result.getErrorMessage());
    }

    @Test
    void testToStringSuccess() {
        ArchivePurgeResult result = new ArchivePurgeResult("TEST_TABLE", 50, 50);
        
        String output = result.toString();
        assertTrue(output.contains("TEST_TABLE"));
        assertTrue(output.contains("SUCCESS"));
        assertTrue(output.contains("50"));
    }

    @Test
    void testToStringFailure() {
        ArchivePurgeResult result = new ArchivePurgeResult("TEST_TABLE", "Error message");
        
        String output = result.toString();
        assertTrue(output.contains("TEST_TABLE"));
        assertTrue(output.contains("FAILED"));
        assertTrue(output.contains("Error message"));
    }
}
