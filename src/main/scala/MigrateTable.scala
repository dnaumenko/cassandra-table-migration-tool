import java.io.{File, PrintWriter}

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.querybuilder.QueryBuilder

import scala.collection.JavaConversions._
import scala.io.Source

object MigrateTable {
  val optionParser = new scopt.OptionParser[Config]("cassandra-table-migration-tool") {
    head("Migrate Table Tool for simple table migration in Cassandra")

    opt[String]('h', "hostname").action( (hostname, config) =>
      config.copy(hostname = hostname)
    ).text("Cassandra DB's hostname").required()

    opt[Int]('p', "port").action( (port, config) =>
      config.copy(port = port)
    ).text("Cassandra DB's port (9042 by default)")

    opt[String]('k', "keyspace").action( (keyspace, config) =>
      config.copy(keyspace = keyspace)
    ).text("Cassandra DB's keyspace (empty by default)")

    opt[String]('s', "source").action( (source, config) =>
      config.copy(source = source)
    ).text("table name or file in witch this tool looks for data to export/import").required()

    opt[String]('d', "dest").action( (destination, config) =>
      config.copy(destination = destination)
    ).text("target (table name or file) to store results of export/import").required()

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
        log(s"Going to ${config.command} from ${config.source} to ${config.destination}")
         config.command match {
           case "export" => runExport(config)
           case "import" => runImport(config)
         }
    }
    System.exit(0)
  }

  def runExport(config: Config): Unit = {
    log("Export start")
    val session = getSession(config.hostname, config.port, config.keyspace)

    val result = session.execute(QueryBuilder.select().json().from(config.source))

    withPrintWriter(new File(config.destination)) { writer =>
      result.all().iterator().map(r => r.getString(0)).foreach { string =>
        writer.write(string)
        writer.write("\n")
      }
    }
    log("Export end")
  }

  def runImport(config: Config): Unit = {
    log("Import start")
    val session = getSession(config.hostname, config.port, config.keyspace)

    Source.fromFile(config.source).getLines().foreach( line =>
      session.execute(QueryBuilder.insertInto(config.destination).json(line))
    )
    log("Import end")
  }

  private def getSession(hostname: String, port: Int, keyspace: String) = {
    val cluster = Cluster.builder()
          .addContactPoint(hostname).withPort(port).build()

    val metadata = cluster.getMetadata
    log(s"Using cluster: ${metadata.getClusterName}")

    val session = cluster.newSession()
    if (!keyspace.isEmpty) {
      session.execute(s"USE $keyspace")
    }

    session
  }

  private def withPrintWriter(file: File)(op: PrintWriter => Unit) = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

  private def log(string: String): Unit = {
    println("[info] " + string)
  }
}
