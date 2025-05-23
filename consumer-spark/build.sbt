name := "VelibConsumer"

version := "0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.4.1",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.4.1",
  "org.scalaj" %% "scalaj-http" % "2.4.2"  // <--- AJOUTER CETTE LIGNE
)
