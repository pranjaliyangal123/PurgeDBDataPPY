"""
Database connection handler for various database types.
Supports PostgreSQL, MySQL, and MS SQL Server.
"""

import logging
from typing import Optional, List, Dict, Any
import psycopg2
import pymysql
import pyodbc


class DatabaseConnection:
    """Handles database connections for different database types."""
    
    def __init__(self, config: Dict[str, Any]):
        """
        Initialize database connection.
        
        Args:
            config: Database configuration dictionary
        """
        self.config = config
        self.connection = None
        self.db_type = config.get('type', 'postgresql').lower()
        self.logger = logging.getLogger(__name__)
        
    def connect(self) -> None:
        """Establish database connection based on database type."""
        try:
            if self.db_type == 'postgresql':
                self.connection = psycopg2.connect(
                    host=self.config['host'],
                    port=self.config['port'],
                    database=self.config['name'],
                    user=self.config['user'],
                    password=self.config['password']
                )
            elif self.db_type == 'mysql':
                self.connection = pymysql.connect(
                    host=self.config['host'],
                    port=self.config['port'],
                    database=self.config['name'],
                    user=self.config['user'],
                    password=self.config['password']
                )
            elif self.db_type == 'mssql':
                conn_str = (
                    f"DRIVER={{ODBC Driver 17 for SQL Server}};"
                    f"SERVER={self.config['host']},{self.config['port']};"
                    f"DATABASE={self.config['name']};"
                    f"UID={self.config['user']};"
                    f"PWD={self.config['password']}"
                )
                self.connection = pyodbc.connect(conn_str)
            else:
                raise ValueError(f"Unsupported database type: {self.db_type}")
            
            self.logger.info(f"Connected to {self.db_type} database: {self.config['name']}")
            
        except Exception as e:
            self.logger.error(f"Failed to connect to database: {str(e)}")
            raise
    
    def disconnect(self) -> None:
        """Close database connection."""
        if self.connection:
            self.connection.close()
            self.logger.info("Database connection closed")
    
    def execute_query(self, query: str, params: Optional[tuple] = None) -> None:
        """
        Execute a database query without returning results.
        
        Args:
            query: SQL query to execute
            params: Query parameters
        """
        cursor = self.connection.cursor()
        try:
            cursor.execute(query, params or ())
            self.connection.commit()
        except Exception as e:
            self.connection.rollback()
            self.logger.error(f"Query execution failed: {str(e)}")
            raise
        finally:
            cursor.close()
    
    def fetch_query(self, query: str, params: Optional[tuple] = None) -> List[tuple]:
        """
        Execute a query and return results.
        
        Args:
            query: SQL query to execute
            params: Query parameters
            
        Returns:
            List of result rows
        """
        cursor = self.connection.cursor()
        try:
            cursor.execute(query, params or ())
            results = cursor.fetchall()
            return results
        except Exception as e:
            self.logger.error(f"Query fetch failed: {str(e)}")
            raise
        finally:
            cursor.close()
    
    def table_exists(self, table_name: str) -> bool:
        """
        Check if a table exists in the database.
        
        Args:
            table_name: Name of the table to check
            
        Returns:
            True if table exists, False otherwise
        """
        try:
            if self.db_type == 'postgresql':
                query = """
                    SELECT EXISTS (
                        SELECT FROM information_schema.tables 
                        WHERE table_name = %s
                    )
                """
            elif self.db_type == 'mysql':
                query = """
                    SELECT COUNT(*) 
                    FROM information_schema.tables 
                    WHERE table_schema = %s AND table_name = %s
                """
                result = self.fetch_query(query, (self.config['name'], table_name))
                return result[0][0] > 0
            elif self.db_type == 'mssql':
                query = """
                    SELECT COUNT(*) 
                    FROM INFORMATION_SCHEMA.TABLES 
                    WHERE TABLE_NAME = ?
                """
                result = self.fetch_query(query, (table_name,))
                return result[0][0] > 0
            
            result = self.fetch_query(query, (table_name,))
            return result[0][0] if self.db_type == 'postgresql' else result[0][0] > 0
            
        except Exception as e:
            self.logger.error(f"Error checking table existence: {str(e)}")
            return False
    
    def get_table_columns(self, table_name: str) -> List[str]:
        """
        Get list of columns for a table.
        
        Args:
            table_name: Name of the table
            
        Returns:
            List of column names
        """
        try:
            if self.db_type in ['postgresql', 'mysql']:
                query = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = %s
                    ORDER BY ordinal_position
                """
            else:  # mssql
                query = """
                    SELECT column_name 
                    FROM INFORMATION_SCHEMA.COLUMNS 
                    WHERE TABLE_NAME = ?
                    ORDER BY ORDINAL_POSITION
                """
            
            param = (table_name,)
            results = self.fetch_query(query, param)
            return [row[0] for row in results]
            
        except Exception as e:
            self.logger.error(f"Error getting table columns: {str(e)}")
            raise
    
    def __enter__(self):
        """Context manager entry."""
        self.connect()
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit."""
        self.disconnect()
