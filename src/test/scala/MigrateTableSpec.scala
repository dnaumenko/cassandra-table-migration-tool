import org.specs2.mutable.Specification

import scala.io.Source

class MigrateTableSpec extends Specification {
  args(skipAll = true)

  "The 'runExport' method" should {
    "save data from table to file in json format" in {
      val config = Config(source = "vault", destination = "dump.json", hostname = "localhost", keyspace = "vault")
      MigrateTable.runExport(config)

      val lines = Source.fromFile("dump.json").getLines()
      lines must not be empty

    }
  }

  "The 'runImport' method" should {
    "load data from file in json format to table" in {
      val config = Config(source = "dump.json", destination = "vault2", hostname = "localhost", keyspace = "vault")
      MigrateTable.runImport(config)

      success
    }
  }
}
