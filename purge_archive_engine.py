"""
Database Purge and Archive Engine
Handles archiving old data and purging it from active tables.
"""

import logging
from typing import Dict, Any, List
from datetime import datetime, timedelta
from db_connection import DatabaseConnection


class PurgeArchiveEngine:
    """Main engine for database purge and archive operations."""
    
    def __init__(self, db_config: Dict[str, Any], archive_config: Dict[str, Any], 
                 purge_config: Dict[str, Any]):
        """
        Initialize the purge and archive engine.
        
        Args:
            db_config: Database configuration
            archive_config: Archive settings
            purge_config: Purge settings
        """
        self.db_config = db_config
        self.archive_config = archive_config
        self.purge_config = purge_config
        self.logger = logging.getLogger(__name__)
        self.db_connection = DatabaseConnection(db_config)
        
    def create_archive_table(self, source_table: str, archive_table: str) -> None:
        """
        Create archive table with the same structure as source table.
        
        Args:
            source_table: Name of the source table
            archive_table: Name of the archive table to create
        """
        try:
            # Check if archive table already exists
            if self.db_connection.table_exists(archive_table):
                self.logger.info(f"Archive table {archive_table} already exists")
                return
            
            # Create archive table as a copy of source table structure
            db_type = self.db_config.get('type', 'postgresql').lower()
            
            if db_type == 'postgresql':
                query = f"CREATE TABLE {archive_table} (LIKE {source_table} INCLUDING ALL)"
            elif db_type == 'mysql':
                query = f"CREATE TABLE {archive_table} LIKE {source_table}"
            elif db_type == 'mssql':
                query = f"SELECT * INTO {archive_table} FROM {source_table} WHERE 1=0"
            
            self.db_connection.execute_query(query)
            self.logger.info(f"Created archive table: {archive_table}")
            
        except Exception as e:
            self.logger.error(f"Failed to create archive table {archive_table}: {str(e)}")
            raise
    
    def archive_old_data(self, table_name: str, archive_table: str, 
                        date_column: str, retention_days: int) -> int:
        """
        Archive old data from active table to archive table.
        
        Args:
            table_name: Source table name
            archive_table: Archive table name
            date_column: Column name containing the date
            retention_days: Number of days to retain in active table
            
        Returns:
            Number of records archived
        """
        try:
            cutoff_date = datetime.now() - timedelta(days=retention_days)
            batch_size = self.archive_config.get('batch_size', 1000)
            total_archived = 0
            
            self.logger.info(
                f"Archiving data from {table_name} older than {cutoff_date.date()} "
                f"to {archive_table}"
            )
            
            # Get count of records to archive
            db_type = self.db_config.get('type', 'postgresql').lower()
            
            if db_type in ['postgresql', 'mysql']:
                count_query = f"""
                    SELECT COUNT(*) FROM {table_name} 
                    WHERE {date_column} < %s
                """
                param = (cutoff_date,)
            else:  # mssql
                count_query = f"""
                    SELECT COUNT(*) FROM {table_name} 
                    WHERE {date_column} < ?
                """
                param = (cutoff_date,)
            
            result = self.db_connection.fetch_query(count_query, param)
            total_to_archive = result[0][0]
            
            if total_to_archive == 0:
                self.logger.info(f"No data to archive from {table_name}")
                return 0
            
            self.logger.info(f"Found {total_to_archive} records to archive")
            
            # Archive data in batches
            if db_type == 'postgresql':
                archive_query = f"""
                    INSERT INTO {archive_table}
                    SELECT * FROM {table_name}
                    WHERE {date_column} < %s
                    LIMIT %s
                """
            elif db_type == 'mysql':
                archive_query = f"""
                    INSERT INTO {archive_table}
                    SELECT * FROM {table_name}
                    WHERE {date_column} < %s
                    LIMIT %s
                """
            else:  # mssql
                archive_query = f"""
                    INSERT INTO {archive_table}
                    SELECT TOP (?) * FROM {table_name}
                    WHERE {date_column} < ?
                """
            
            while total_archived < total_to_archive:
                try:
                    if db_type == 'mssql':
                        self.db_connection.execute_query(
                            archive_query, (batch_size, cutoff_date)
                        )
                    else:
                        self.db_connection.execute_query(
                            archive_query, (cutoff_date, batch_size)
                        )
                    
                    # Check how many were actually inserted
                    check_query = f"SELECT COUNT(*) FROM {archive_table}"
                    result = self.db_connection.fetch_query(check_query)
                    current_count = result[0][0]
                    
                    archived_in_batch = min(batch_size, total_to_archive - total_archived)
                    total_archived += archived_in_batch
                    
                    self.logger.info(
                        f"Archived {total_archived}/{total_to_archive} records"
                    )
                    
                    # If we archived less than batch_size, we're done
                    if archived_in_batch < batch_size:
                        break
                        
                except Exception as e:
                    self.logger.error(f"Error in batch archival: {str(e)}")
                    break
            
            self.logger.info(
                f"Successfully archived {total_archived} records from {table_name}"
            )
            return total_archived
            
        except Exception as e:
            self.logger.error(f"Failed to archive data from {table_name}: {str(e)}")
            raise
    
    def purge_old_data(self, table_name: str, date_column: str, 
                      retention_days: int, dry_run: bool = False) -> int:
        """
        Purge old data from active table.
        
        Args:
            table_name: Table name
            date_column: Column name containing the date
            retention_days: Number of days to retain
            dry_run: If True, only count records without deleting
            
        Returns:
            Number of records purged (or would be purged in dry_run mode)
        """
        try:
            cutoff_date = datetime.now() - timedelta(days=retention_days)
            
            # Count records to purge
            db_type = self.db_config.get('type', 'postgresql').lower()
            
            if db_type in ['postgresql', 'mysql']:
                count_query = f"""
                    SELECT COUNT(*) FROM {table_name} 
                    WHERE {date_column} < %s
                """
                param = (cutoff_date,)
            else:  # mssql
                count_query = f"""
                    SELECT COUNT(*) FROM {table_name} 
                    WHERE {date_column} < ?
                """
                param = (cutoff_date,)
            
            result = self.db_connection.fetch_query(count_query, param)
            records_to_purge = result[0][0]
            
            if records_to_purge == 0:
                self.logger.info(f"No data to purge from {table_name}")
                return 0
            
            if dry_run:
                self.logger.info(
                    f"DRY RUN: Would purge {records_to_purge} records from {table_name} "
                    f"older than {cutoff_date.date()}"
                )
                return records_to_purge
            
            # Perform actual deletion
            if db_type in ['postgresql', 'mysql']:
                delete_query = f"""
                    DELETE FROM {table_name} 
                    WHERE {date_column} < %s
                """
            else:  # mssql
                delete_query = f"""
                    DELETE FROM {table_name} 
                    WHERE {date_column} < ?
                """
            
            self.db_connection.execute_query(delete_query, param)
            
            self.logger.info(
                f"Successfully purged {records_to_purge} records from {table_name}"
            )
            return records_to_purge
            
        except Exception as e:
            self.logger.error(f"Failed to purge data from {table_name}: {str(e)}")
            raise
    
    def process_table(self, policy: Dict[str, Any]) -> Dict[str, int]:
        """
        Process a single table: archive and purge old data.
        
        Args:
            policy: Retention policy configuration for the table
            
        Returns:
            Dictionary with archived and purged counts
        """
        table_name = policy['table_name']
        archive_table = policy['archive_table']
        date_column = policy['date_column']
        retention_days = policy['active_retention_days']
        
        self.logger.info(f"Processing table: {table_name}")
        
        result = {
            'table': table_name,
            'archived': 0,
            'purged': 0,
            'success': True,
            'error': None
        }
        
        try:
            # Create archive table if needed
            if self.archive_config.get('create_archive_tables', True):
                self.create_archive_table(table_name, archive_table)
            
            # Archive old data
            archived_count = self.archive_old_data(
                table_name, archive_table, date_column, retention_days
            )
            result['archived'] = archived_count
            
            # Purge old data if enabled
            if self.purge_config.get('enabled', True):
                dry_run = self.purge_config.get('dry_run', False)
                purged_count = self.purge_old_data(
                    table_name, date_column, retention_days, dry_run
                )
                result['purged'] = purged_count
            
        except Exception as e:
            result['success'] = False
            result['error'] = str(e)
            self.logger.error(f"Failed to process table {table_name}: {str(e)}")
        
        return result
    
    def run(self, retention_policies: List[Dict[str, Any]]) -> List[Dict[str, int]]:
        """
        Run the purge and archive process for all configured tables.
        
        Args:
            retention_policies: List of retention policy configurations
            
        Returns:
            List of results for each table
        """
        results = []
        
        try:
            self.db_connection.connect()
            
            for policy in retention_policies:
                result = self.process_table(policy)
                results.append(result)
            
        except Exception as e:
            self.logger.error(f"Error during purge and archive process: {str(e)}")
            raise
        finally:
            self.db_connection.disconnect()
        
        return results
