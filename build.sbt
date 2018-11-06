// in the name of ALLAH

organization := "com.bisphone"

name := "cassandra"

version := "1.6.1"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.7")

fork := true

scalacOptions ++= Seq(
   "-feature",
   "-deprecation",
   "-language:postfixOps",
   "-language:implicitConversions"
)

libraryDependencies ++= Seq(
   "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.2",
   "com.bisphone" %% "std" % "0.13.1",
   "org.slf4j" % "slf4j-api" % "1.7.25",
   "com.typesafe" % "config" % "1.3.3",
   "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
