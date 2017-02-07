name := "cassandra-table-migration-tool"
version := "1.0"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.21"
libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.21"

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.8" % "test"

scalacOptions in Test ++= Seq("-Yrangepos")

assemblyJarName in assembly := "cassandra-migrate.jar"

assemblyMergeStrategy in assembly := {
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}