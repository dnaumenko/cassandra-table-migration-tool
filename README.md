# cassandra-table-migration-tool
Migrate Table Tool for simple table migration in Cassandra


## Build
In a project directory, run `sbt assembly` to produce an uber-jar.

## Run
java -jar cassandra-table-migration-tool-assembly-1.0.jar 

## Show usage output: 

    Migrate Table Tool for simple table migration in Cassandra
    Usage: cassandra-table-migration-tool [export|import] [options]
    
      -h, --hostname <value>  Cassandra DB's hostname
      -p, --port <value>      Cassandra DB's port (9042 by default)
      -k, --keyspace <value>  Cassandra DB's keyspace (empty by default)
      -s, --source <value>    table name or file in witch this tool looks for data to export/import
      -d, --dest <value>      target (table name or file) to store results of export/import
    
    Following commands supported:
    
    Command: export
    export will dump data from the given source table name to a specified folder
    Command: import
    import will load data from the given folder to a specified table
