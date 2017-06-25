// in the name of ALLAH

organization := "com.bisphone"

name := "cassandra"

version := "1.3.3"

scalaVersion := "2.11.8"

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
   "com.bisphone" %% "std" % "0.10.0"
)
