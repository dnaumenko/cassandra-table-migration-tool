package com.github.migrate

case class Config(command: String = "", source: String = "", destination: String = "",
                  hostname: String = "", port: Int = 9042, keyspace: String = "",
                  limit: Int = 0,
                  debugMode: Boolean = false, debugThreshold: Int = 5000)

