package com.purgedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Database Purge and Archive Batch Application.
 * 
 * This application manages the database purge and archive process to ensure optimal 
 * storage usage, consistent performance, and compliance with data retention policies.
 * 
 * Active tables store recent operational data for short durations (7-15 days) and 
 * are optimized for frequent access. Once data exceeds the active retention period, 
 * it is archived into corresponding archive tables and then deleted from the active 
 * tables to free up space.
 */
@SpringBootApplication
@EnableScheduling
public class PurgeArchiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurgeArchiveApplication.class, args);
    }
}
