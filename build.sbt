name := "alpaca-scala"

version := "0.1"

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
  "org.scalamock" %% "scalamock" % "4.1.0" % Test
)
