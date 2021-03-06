import com.github.migrate.{AddColumnConfig, Config, MigrateTable}
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

    "save no more lines than in a specified limit" in {
      val config = Config(limit = 1, source = "vault", destination = "dump2.json", hostname = "localhost", keyspace = "vault")
      MigrateTable.runExport(config)

      val lines = Source.fromFile("dump2.json").getLines()
      lines must haveSize(1)
    }
  }

  "The 'runImport' method" should {
    "load data from file in json format to table" in {
      val config = Config(source = "dump.json", destination = "vault2", hostname = "localhost", keyspace = "vault")
      MigrateTable.runImport(config)

      success
    }
  }

  "The 'runTransform' method" should {
    "support adding new columns" in {
      val config = Config(source = "dump.json", destination = "dump_add.json", subCommand = "add-column",
        addColumnConfig = AddColumnConfig(name = "newColumn", value = "someValue"))
      MigrateTable.runTransform(config)

      val lines = Source.fromFile("dump_add.json").getLines()
      lines must not be empty
    }
  }
}
