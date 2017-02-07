package com.github.migrate

import java.io.{File, PrintWriter}

import ch.qos.logback.classic.Level
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{Cluster, ConsistencyLevel, Session}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.io.Source

object MigrateTable {
  val logger: Logger = LoggerFactory.getLogger(getClass.getName)

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

    opt[Boolean]('v', "verbose").action( (debugMode, config) =>
      config.copy(debugMode = debugMode)
    ).text("enable debug mode to show intermediate results, disabled by default")

    opt[Int]('t', "debugThreshold").action( (debugThreshold, config) =>
      config.copy(debugThreshold = debugThreshold)
    ).text("how often print debug messages. By default, after 5000 entries")

    note("\nFollowing commands supported:\n")

    cmd("export").action( (_, c) => c.copy(command = "export"))
      .text("export will dump data from the given source table name to a specified folder")
      .children({
        opt[Int]('l', "limit").action( (limit, config) =>
          config.copy(limit = limit)
        ).text("how much lines to import/export (0 by default, which means there is no limit)")
      })

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
        if (config.debugMode) {
          val loggerContext = LoggerFactory.getILoggerFactory
          val logger = loggerContext.getLogger("com.github.migrate").asInstanceOf[ch.qos.logback.classic.Logger]
          logger.setLevel(Level.DEBUG)
        }

        logger.info(s"Going to ${config.command} from ${config.source} to ${config.destination}")
         config.command match {
           case "export" => runExport(config)
           case "import" => runImport(config)
         }
    }
    System.exit(0)
  }

  def runExport(config: Config): Unit = {
    logger.info("Export start")
    withSession(config.hostname, config.port, config.keyspace) { session =>
      val query = QueryBuilder.select().json().from(config.source)
      if (config.limit > 0) {
        query.limit(config.limit)
      }
      query.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
      val result = session.execute(query)
      logger.info(s"Got ${result.getAvailableWithoutFetching} rows to export.")

      var count = 0
      withPrintWriter(new File(config.destination)) { writer =>
        result.all().iterator().map(r => r.getString(0)).foreach { string =>
          writer.write(string)
          writer.write("\n")
          count = count + 1
          if (count % config.debugThreshold == 0) {
            logger.debug(s"Exported $count rows")
          }
        }
      }
      logger.info(s"Export end. Exported $count rows")
    }
  }

  def runImport(config: Config): Unit = {
    logger.info("Import start")
    withSession(config.hostname, config.port, config.keyspace) { session =>
      val lines = Source.fromFile(config.source).getLines().filter(!_.isEmpty)

      var count = 0
      lines.foreach( line => {
        session.execute(QueryBuilder.insertInto(config.destination).json(line).setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM))
        count = count + 1
        if (count % config.debugThreshold == 0) {
          logger.debug(s"Imported $count rows")
        }
      })

      logger.info(s"Import end. Imported $count rows")
    }
  }

  private def withSession(hostname: String, port: Int, keyspace: String)(op: Session => Unit) = {
    val cluster = Cluster.builder()
          .addContactPoint(hostname).withPort(port).build()

    val metadata = cluster.getMetadata
    logger.info(s"Using cluster: ${metadata.getClusterName}")

    val session = cluster.newSession()
    if (!keyspace.isEmpty) {
      session.execute(s"USE $keyspace")
    }

    try {
      op(session)
    } finally {
      session.close()
      cluster.close()
    }
  }

  private def withPrintWriter(file: File)(op: PrintWriter => Unit) = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }
}
