organization in ThisBuild := "cqrs"

scalaVersion in ThisBuild := "2.11.8"

version in ThisBuild := "1.0"

val akkaVersion = "2.4.4"

lazy val libs = Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,

  "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.14",

  "org.scala-lang.modules" %% "scala-async" % "0.9.5",
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.2",

  "com.github.alexarchambault" %% "case-app" % "0.3.0",

  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)


lazy val commonMessages = project
lazy val gatewayMessages = project.dependsOn(commonMessages)
lazy val customerMessages = project.dependsOn(commonMessages)
lazy val creditHistoryMessages = project.dependsOn(commonMessages)
lazy val accountMessages = project.dependsOn(commonMessages)

lazy val commonService = project.settings(libraryDependencies ++= libs).dependsOn(commonMessages)
lazy val gatewayService = project.dependsOn(gatewayMessages, customerMessages, accountMessages, commonService)
lazy val customerService = project.dependsOn(customerMessages, creditHistoryMessages, commonService)
lazy val creditHistoryService = project.dependsOn(creditHistoryMessages, commonService)
lazy val accountService = project.dependsOn(accountMessages, commonService)

lazy val reportService = project.dependsOn(commonService, customerMessages)

lazy val client = project.dependsOn(gatewayMessages, customerMessages, accountMessages, commonService)
