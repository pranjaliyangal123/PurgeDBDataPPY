#!/usr/bin/env python3
"""
Database Purge and Archive Main Script

This script manages the database purge and archive process to ensure optimal 
storage usage, consistent performance, and compliance with data retention policies.

Active tables store recent operational data for short durations (7-15 days) and 
are optimized for frequent access. Once data exceeds the active retention period, 
it is archived into corresponding archive tables and then deleted from the active 
tables to free up space.

Usage:
    python purge_archive.py [--config CONFIG_FILE] [--dry-run]
"""

import argparse
import logging
import sys
import yaml
from datetime import datetime
from typing import Dict, Any
from purge_archive_engine import PurgeArchiveEngine


def setup_logging(config: Dict[str, Any]) -> None:
    """
    Configure logging based on configuration.
    
    Args:
        config: Logging configuration dictionary
    """
    log_level = config.get('level', 'INFO')
    log_file = config.get('log_file', 'purge_archive.log')
    log_to_console = config.get('log_to_console', True)
    
    # Configure logging format
    log_format = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    
    handlers = []
    
    # File handler
    file_handler = logging.FileHandler(log_file)
    file_handler.setFormatter(logging.Formatter(log_format))
    handlers.append(file_handler)
    
    # Console handler
    if log_to_console:
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setFormatter(logging.Formatter(log_format))
        handlers.append(console_handler)
    
    # Configure root logger
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format=log_format,
        handlers=handlers
    )


def load_config(config_file: str) -> Dict[str, Any]:
    """
    Load configuration from YAML file.
    
    Args:
        config_file: Path to configuration file
        
    Returns:
        Configuration dictionary
    """
    try:
        with open(config_file, 'r') as f:
            config = yaml.safe_load(f)
        return config
    except Exception as e:
        print(f"Error loading configuration file: {str(e)}")
        sys.exit(1)


def print_summary(results: list) -> None:
    """
    Print summary of purge and archive operations.
    
    Args:
        results: List of operation results
    """
    print("\n" + "=" * 80)
    print("PURGE AND ARCHIVE SUMMARY")
    print("=" * 80)
    
    total_archived = 0
    total_purged = 0
    successful_tables = 0
    failed_tables = 0
    
    for result in results:
        print(f"\nTable: {result['table']}")
        print(f"  Status: {'SUCCESS' if result['success'] else 'FAILED'}")
        print(f"  Archived: {result['archived']} records")
        print(f"  Purged: {result['purged']} records")
        
        if not result['success']:
            print(f"  Error: {result['error']}")
            failed_tables += 1
        else:
            successful_tables += 1
        
        total_archived += result['archived']
        total_purged += result['purged']
    
    print("\n" + "-" * 80)
    print(f"Total Tables Processed: {len(results)}")
    print(f"  Successful: {successful_tables}")
    print(f"  Failed: {failed_tables}")
    print(f"Total Records Archived: {total_archived}")
    print(f"Total Records Purged: {total_purged}")
    print("=" * 80 + "\n")


def main():
    """Main entry point for the purge and archive script."""
    # Parse command line arguments
    parser = argparse.ArgumentParser(
        description='Database Purge and Archive Tool',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Run with default configuration
  python purge_archive.py
  
  # Run with custom configuration file
  python purge_archive.py --config my_config.yaml
  
  # Run in dry-run mode (preview only)
  python purge_archive.py --dry-run
  
  # Run with custom config in dry-run mode
  python purge_archive.py --config my_config.yaml --dry-run
        """
    )
    
    parser.add_argument(
        '--config',
        default='config.yaml',
        help='Path to configuration file (default: config.yaml)'
    )
    
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Run in dry-run mode (preview only, no actual deletion)'
    )
    
    args = parser.parse_args()
    
    # Load configuration
    config = load_config(args.config)
    
    # Override dry-run setting if specified on command line
    if args.dry_run:
        config['purge']['dry_run'] = True
    
    # Setup logging
    setup_logging(config.get('logging', {}))
    logger = logging.getLogger(__name__)
    
    # Log start of process
    logger.info("=" * 80)
    logger.info("Starting Database Purge and Archive Process")
    logger.info(f"Timestamp: {datetime.now()}")
    logger.info(f"Configuration: {args.config}")
    logger.info(f"Dry Run Mode: {config['purge'].get('dry_run', False)}")
    logger.info("=" * 80)
    
    try:
        # Initialize engine
        engine = PurgeArchiveEngine(
            db_config=config['database'],
            archive_config=config['archive'],
            purge_config=config['purge']
        )
        
        # Run purge and archive process
        retention_policies = config['retention_policies']
        logger.info(f"Processing {len(retention_policies)} tables")
        
        results = engine.run(retention_policies)
        
        # Print summary
        print_summary(results)
        
        # Log completion
        logger.info("=" * 80)
        logger.info("Database Purge and Archive Process Completed")
        logger.info(f"Timestamp: {datetime.now()}")
        logger.info("=" * 80)
        
        # Exit with appropriate code
        failed_count = sum(1 for r in results if not r['success'])
        sys.exit(1 if failed_count > 0 else 0)
        
    except Exception as e:
        logger.error(f"Fatal error during purge and archive process: {str(e)}")
        sys.exit(1)


if __name__ == "__main__":
    main()
