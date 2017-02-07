name := "cassandra-table-migration-tool"
version := "1.0"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.2"
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.8" % "test"

scalacOptions in Test ++= Seq("-Yrangepos")