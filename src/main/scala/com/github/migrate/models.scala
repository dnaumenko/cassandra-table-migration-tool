package com.github.migrate

case class AddColumnConfig(name: String = "", value: String = "")

case class Config(command: String = "", subCommand: String = "", source: String = "", destination: String = "",
                  hostname: String = "", port: Int = 9042, keyspace: String = "",
                  addColumnConfig: AddColumnConfig = AddColumnConfig(),
                  limit: Int = 0, debugMode: Boolean = false, debugThreshold: Int = 5000)

