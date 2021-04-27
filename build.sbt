//import com.lightbend.cinnamon.sbt._

name := "akka-stream"

version := "0.1"

scalaVersion := "2.13.3"


def akkaVersion = "2.6.8"
lazy val akkaHttpVersion = "10.1.11"

lazy val helloAkka = project in file(".") enablePlugins (CinnamonAgentOnly)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  Cinnamon.library.cinnamonAkkaStream,
  Cinnamon.library.cinnamonPrometheus,
  Cinnamon.library.cinnamonPrometheusHttpServer,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)

scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")

//lazy val exercises = project
//  .enablePlugins(CinnamonAgentOnly)