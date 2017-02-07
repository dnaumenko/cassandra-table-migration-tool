# cassandra-table-migration-tool
Migrate Table Tool for simple table migration in Cassandra


## Build
In a project directory, run `sbt assembly` that will produce a uber-jar.

## Run
java -jar cassandra-table-migration-tool-assembly-1.0.jar 

## Command line options: 

    cassandra-table-migration-tool [export|import] [options]
    
      -s, --source <value>  a table name or folder where this tool looks for data to export/import
      -d, --dest <value>    a target (table name or folder) to store results of export/import
    
    Following commands supported:
    
    Command: export
    export will dump data from the given source table name to a specified folder
    Command: import
    import will load data from the given folder to a specified table
