name := "hotel-project"

version := "0.1"

scalaVersion := "2.13.3"

resolvers += "Mulerepo" at "https://repository.mulesoft.org/nexus/content/repositories/public"

libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.9.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.5.0"
libraryDependencies += "org.elasticsearch" % "elasticsearch" % "7.7.0"
libraryDependencies += "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.7.0"

libraryDependencies += "com.github.awslabs" % "aws-request-signing-apache-interceptor" % "b3772780da"

libraryDependencies += "io.circe" %% "circe-parser" % "0.13.0"
libraryDependencies += "io.circe" %% "circe-core" % "0.13.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.13.0"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.13.3"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.13.3"





