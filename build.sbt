name := "liquibase-playground"
version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging"                   % "3.9.4",
  "ch.qos.logback"              % "logback-classic"                 % "1.2.10",
  "org.postgresql"              % "postgresql"                      % "42.3.1",
  "org.liquibase"               % "liquibase-core"                  % "4.7.1",
  "org.yaml"                    % "snakeyaml"                       % "1.30",
  "com.typesafe.slick"         %% "slick"                           % "3.3.3",
  "com.typesafe.slick"         %% "slick-hikaricp"                  % "3.3.3",
  "org.scalatest"              %% "scalatest"                       % "3.2.5"  % Test,
  "com.dimafeng"               %% "testcontainers-scala-scalatest"  % "0.40.0" % Test,
  "com.dimafeng"               %% "testcontainers-scala-postgresql" % "0.40.0" % Test
)
