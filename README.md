# cassandra-table-migration-tool
Migrate Table Tool for simple table migration in Cassandra


## Build
In a project directory, run `sbt assembly` to produce an uber-jar.

## Run
java -jar cassandra-table-migration-tool-assembly-1.0.jar

## Show usage output: 

    Migrate Table Tool for simple table migration in Cassandra
    Usage: cassandra-table-migration-tool [export|import] [options]
    
      -h, --hostname <value>   Cassandra DB's hostname
      -p, --port <value>       Cassandra DB's port (9042 by default)
      -k, --keyspace <value>   Cassandra DB's keyspace (empty by default)
      -s, --source <value>     table name or file in witch this tool looks for data to export/import
      -d, --dest <value>       target (table name or file) to store results of export/import
      -v, --verbose <value>    enable debug mode to show intermediate results, disabled by default
      -t, --debugThreshold <value>
                               how often print debug messages. By default, after 5000 entries
    
    Following commands supported:
    
    Command: export [options]
    export will dump data from the given source table name to a specified folder
      -l, --limit <value>      how much lines to import/export (0 by default, which means there is no limit)
    Command: import
    import will load data from the given folder to a specified table
