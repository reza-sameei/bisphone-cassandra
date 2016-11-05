// in the name of ALLAH

val globe = Seq(
   organization := "com.bisphone",
   name := "cassandra",
   version := "1.0",
   scalaVersion := "2.11.8",
   fork := true,
   scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-language:postfixOps",
      "-language:implicitConversions",
      s"-target:jvm-1.8"
   ),
   javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

lazy val root = (project in file("."))
   .settings(globe: _*)
   .settings(
      name := "cassandra",
      libraryDependencies ++= Seq(
         "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.2"
      )
   )
