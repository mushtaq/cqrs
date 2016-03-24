name := "minimal-scala"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.4.2",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.2",

  "com.typesafe.akka" %% "akka-persistence" % "2.4.2",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.11",

  "org.scala-lang.modules" %% "scala-async" % "0.9.5",
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.2",

  "com.github.alexarchambault" %% "case-app" % "0.3.0",

  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)
