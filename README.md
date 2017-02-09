# cassandra-table-migration-tool
Migrate Table Tool for simple table migration in Cassandra


## Build
In a project directory, run `sbt assembly` to produce an uber-jar or download [binary jar](https://github.com/dnaumenko/cassandra-table-migration-tool/releases/download/1.1/cassandra-migrate.jar)

## Run
For export, run `java -jar cassandra-migrate.jar export -h <hostname> -k <keyspace> -s <table> -d <filename>`

For import, run `java -jar cassandra-migrate.jar import -h <hostname> -k <keyspace> -s <filename> -d <table>`

In addition, there are also simple transformation commands:

* To add new column, use `run transform add-column -s <source_file> -d <destination_file> --name=<column_name> --value=<column_value` 

## Show usage output: 

    Migrate Table Tool for simple table migration in Cassandra
    Usage: cassandra-table-migration-tool [export|import|transform] [options]
    
      -h, --hostname <value>   Cassandra DB's hostname
      -p, --port <value>       Cassandra DB's port (9042 by default)
      -k, --keyspace <value>   Cassandra DB's keyspace (empty by default)
      -s, --source <value>     table name or file in witch this tool looks for data to export/import
      -d, --dest <value>       target (table name or file) to store results of export/import
      --verbose <value>        enable debug mode to show intermediate results, disabled by default
      -t, --debugThreshold <value>
                               how often print debug messages. By default, after 5000 entries
    
    Following commands supported:
    
    Command: export [options]
    export will dump data from the given source table name to a specified folder
      -l, --limit <value>      how much lines to import/export (0 by default, which means there is no limit)
    Command: import
    import will load data from the given folder to a specified table
    Command: transform [add-column]
    transform will update the given dump file and stores a result in destination file
    Command: transform add-column [options]
    adds new column in json file
      -n, --name <value>       column name
      -v, --value <value>      column value
