name := "VelibProducer"

version := "0.1"

scalaVersion := "2.12.18"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.apache.kafka" % "kafka-clients" % "3.5.1"
)
