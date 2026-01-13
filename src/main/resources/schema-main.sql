-- Main Database Schema (Production Tables)

-- FSL_REQUEST_EVENT table (7 days retention)
CREATE TABLE IF NOT EXISTS FSL_REQUEST_EVENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data TEXT,
    status VARCHAR(20),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP,
    INDEX idx_created_date (created_date),
    INDEX idx_request_id (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- EVENT_LOG_TABLE (7 days retention)
CREATE TABLE IF NOT EXISTS EVENT_LOG_TABLE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_level VARCHAR(20) NOT NULL,
    log_message TEXT NOT NULL,
    log_source VARCHAR(100),
    user_id VARCHAR(50),
    session_id VARCHAR(100),
    log_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_log_timestamp (log_timestamp),
    INDEX idx_log_level (log_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- REQUEST_EVENT table (15 days retention)
CREATE TABLE IF NOT EXISTS REQUEST_EVENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(100) NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    request_payload TEXT,
    response_payload TEXT,
    status_code INT,
    processing_time_ms BIGINT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_date (created_date),
    INDEX idx_request_id (request_id),
    INDEX idx_request_type (request_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- REPLAY table (15 days retention)
CREATE TABLE IF NOT EXISTS REPLAY (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL,
    original_request TEXT,
    replay_request TEXT,
    replay_status VARCHAR(20),
    replay_count INT DEFAULT 0,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    replayed_date TIMESTAMP,
    INDEX idx_created_date (created_date),
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
