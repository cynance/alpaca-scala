name := "alpaca-scala"

version := "0.1"

// POM settings for Sonatype
organization := "com.github.oueasley"
homepage := Some(url("https://github.com/OUEasley/alpaca-scala"))
scmInfo := Some(ScmInfo(url("https://github.com/OUEasley/alpaca-scala"),"git@github.com:OUEasley/alpaca-scala.git"))
developers := List(Developer("OUEasley",
  "OUEasley",
  "oueasley@gmail.com",
  url("https://github.com/OUEasley")))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

scalaVersion := "2.12.8"

val circeVersion = "0.10.0"
val hammockVersion = "0.8.6"

libraryDependencies ++= Seq(
  "com.pepegar" %% "hammock-core" % hammockVersion,
  "com.pepegar" %% "hammock-circe" % hammockVersion,
  "com.github.pureconfig" %% "pureconfig" % "0.10.1",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "com.typesafe.akka" %% "akka-http"   % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19", // or whatever the latest version is,
  "io.nats" % "jnats" % "2.2.0",
  "org.typelevel" %% "cats-core" % "1.5.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)
