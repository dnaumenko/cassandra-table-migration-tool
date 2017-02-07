
object MigrateTable {
  val optionParser = new scopt.OptionParser[Config]("cassandra-table-migration-tool") {
    head("Migrate Table Tool for simple table migration in Cassandra")

    opt[String]('s', "source").action( (source, config) =>
      config.copy(source = source)
    ).text("a table name or folder where this tool looks for data to export/import").required()

    opt[String]('d', "dest").action( (destination, config) =>
      config.copy(destination = destination)
    ).text("a target (table name or folder) to store results of export/import").required()

    note("\nFollowing commands supported:\n")

    cmd("export").action( (_, c) =>
      c.copy(command = "export")
    ).text("export will dump data from the given source table name to a specified folder")

    cmd("import").action( (_, c) =>
      c.copy(command = "import")
    ).text("import will load data from the given folder to a specified table")

    checkConfig( c =>
      if (c.command.isEmpty) failure("Command can't be empty") else success
    )
  }

  def main(args: Array[String]): Unit = {
    optionParser.parse(args, Config()) foreach {
      config =>
         println(s"Going to ${config.command} from ${config.source} to ${config.destination}")
         config.command match {
           case "export" => runExport(config)
           case "import" => runImport(config)
         }
     }
  }

  def runExport(config: Config) = {

  }

  def runImport(config: Config) = {

  }
}
