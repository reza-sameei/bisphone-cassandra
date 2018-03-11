// in the name of ALLAH

organization := "com.bisphone"

name := "cassandra"

version := "1.5.0"

scalaVersion := "2.11.11"

fork := true

scalacOptions ++= Seq(
   "-feature",
   "-deprecation",
   "-language:postfixOps",
   "-language:implicitConversions",
   s"-target:jvm-1.8"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
   "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.2",
   "com.bisphone" %% "std" % "0.11.0",
   "org.slf4j" % "slf4j-api" % "1.7.25",
   "com.typesafe" % "config" % "1.3.2",
   "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
