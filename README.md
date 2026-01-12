# PurgeDBDataPPY

Database Purge and Archive Tool for optimal storage management, performance, and data retention compliance.

## Overview

The database purge and archive process ensures optimal storage usage, consistent performance, and compliance with data retention policies. Active tables store recent operational data for short durations (7–15 days) and are optimized for frequent access. Once data exceeds the active retention period, it is archived into corresponding archive tables and then deleted from the active tables to free up space.

## Features

- **Multi-Database Support**: Works with PostgreSQL, MySQL, and MS SQL Server
- **Configurable Retention Policies**: Define retention periods per table
- **Automatic Archive Table Creation**: Creates archive tables matching source structure
- **Batch Processing**: Efficiently handles large datasets in configurable batches
- **Dry-Run Mode**: Preview changes without making actual deletions
- **Comprehensive Logging**: Detailed logs for auditing and troubleshooting
- **Error Handling**: Robust error handling with transaction rollback
- **Summary Reports**: Clear summary of archived and purged records

## Installation

1. Clone the repository:
```bash
git clone https://github.com/pranjaliyangal123/PurgeDBDataPPY.git
cd PurgeDBDataPPY
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

## Configuration

Edit `config.yaml` to configure your database connection and retention policies:

```yaml
database:
  host: "localhost"
  port: 5432
  name: "production_db"
  user: "db_user"
  password: "db_password"
  type: "postgresql"  # postgresql, mysql, or mssql

retention_policies:
  - table_name: "transactions"
    active_retention_days: 7
    archive_table: "transactions_archive"
    date_column: "created_at"
  
  - table_name: "logs"
    active_retention_days: 15
    archive_table: "logs_archive"
    date_column: "log_timestamp"
```

### Configuration Options

- **database**: Database connection settings
  - `host`: Database server hostname
  - `port`: Database server port
  - `name`: Database name
  - `user`: Database username
  - `password`: Database password
  - `type`: Database type (postgresql, mysql, mssql)

- **retention_policies**: List of tables and their retention settings
  - `table_name`: Name of the active table
  - `active_retention_days`: Number of days to keep data in active table
  - `archive_table`: Name of the archive table
  - `date_column`: Column containing the timestamp for retention calculation

- **archive**: Archive settings
  - `create_archive_tables`: Auto-create archive tables (default: true)
  - `batch_size`: Number of records per batch (default: 1000)

- **purge**: Purge settings
  - `enabled`: Enable/disable purging (default: true)
  - `dry_run`: Preview mode without actual deletion (default: false)

- **logging**: Logging configuration
  - `level`: Log level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
  - `log_file`: Log file path
  - `log_to_console`: Enable console output (default: true)

## Usage

### Basic Usage

Run with default configuration:
```bash
python purge_archive.py
```

### Custom Configuration

Use a custom configuration file:
```bash
python purge_archive.py --config my_config.yaml
```

### Dry-Run Mode

Preview what would be archived and purged without making changes:
```bash
python purge_archive.py --dry-run
```

### Combined Options

```bash
python purge_archive.py --config production_config.yaml --dry-run
```

## How It Works

1. **Connection**: Establishes connection to the configured database
2. **Archive Table Creation**: Creates archive tables if they don't exist (matching source structure)
3. **Data Archival**: Copies data older than retention period to archive tables in batches
4. **Data Purging**: Deletes archived data from active tables to free up space
5. **Reporting**: Generates summary of operations performed

### Process Flow

```
Active Table (7-15 days) → Archive Table (long-term storage) → Delete from Active
```

## Example Output

```
================================================================================
PURGE AND ARCHIVE SUMMARY
================================================================================

Table: transactions
  Status: SUCCESS
  Archived: 15243 records
  Purged: 15243 records

Table: logs
  Status: SUCCESS
  Archived: 8921 records
  Purged: 8921 records

Table: user_activity
  Status: SUCCESS
  Archived: 3456 records
  Purged: 3456 records

--------------------------------------------------------------------------------
Total Tables Processed: 3
  Successful: 3
  Failed: 0
Total Records Archived: 27620
Total Records Purged: 27620
================================================================================
```

## Scheduling

For automated execution, use cron (Linux/Unix) or Task Scheduler (Windows):

### Cron Example (Daily at 2 AM)
```bash
0 2 * * * cd /path/to/PurgeDBDataPPY && /usr/bin/python3 purge_archive.py >> /var/log/purge_archive_cron.log 2>&1
```

### Windows Task Scheduler
Create a scheduled task that runs:
```
python C:\path\to\PurgeDBDataPPY\purge_archive.py
```

## Security Considerations

- **Store credentials securely**: Use environment variables or secure vaults instead of hardcoding passwords
- **Limit database permissions**: Use a dedicated database user with only necessary permissions
- **Review logs regularly**: Monitor for errors or unusual activity
- **Test in non-production first**: Always validate in a test environment before production
- **Backup before first run**: Ensure you have backups before running in production

## Troubleshooting

### Connection Issues
- Verify database credentials in `config.yaml`
- Check network connectivity to database server
- Ensure database user has necessary permissions

### No Data Archived/Purged
- Verify date column exists and has correct values
- Check retention period configuration
- Review logs for specific errors

### Performance Issues
- Adjust `batch_size` in configuration
- Run during off-peak hours
- Consider adding indexes on date columns

## Module Structure

- `purge_archive.py`: Main script and entry point
- `purge_archive_engine.py`: Core logic for archival and purging
- `db_connection.py`: Database connection handler
- `config.yaml`: Configuration file
- `requirements.txt`: Python dependencies

## Requirements

- Python 3.7+
- Database drivers (installed via requirements.txt):
  - PostgreSQL: psycopg2-binary
  - MySQL: pymysql
  - MS SQL Server: pyodbc

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## License

This project is provided as-is for database management purposes.

## Support

For issues or questions, please open an issue on GitHub.