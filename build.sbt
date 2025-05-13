libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.4.1"

name := "velib-streaming"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "3.4.0",
  "com.softwaremill.sttp.client3" %% "core" % "3.8.3"
)

ThisBuild / scalaVersion := "2.13.12"

val sparkVersion = "3.4.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
)

