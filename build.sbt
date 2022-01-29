name := "liquibase-playground"
version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.4",
  "ch.qos.logback"              % "logback-classic" % "1.2.10",
  "org.postgresql"              % "postgresql"      % "42.3.1",
  "org.liquibase"               % "liquibase-core"  % "4.7.1",
  "org.yaml"                    % "snakeyaml"       % "1.30",
  "org.scalatest"              %% "scalatest"       % "3.2.5" % Test
)
