name := "alpaca-scala"

version := "2.0.0"

// POM settings for Sonatype
organization := "com.cynance"
homepage := Some(url("https://github.com/cynance/alpaca-scala"))
scmInfo := Some(ScmInfo(url("https://github.com/cynance/alpaca-scala"),"git@github.com:cynance/alpaca-scala.git"))
developers := List(Developer("OUEasley",
  "OUEasley",
  "oueasley@gmail.com",
  url("https://github.com/OUEasley")))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

enablePlugins(MicrositesPlugin)

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
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.3" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.3",
  "com.softwaremill.macwire" %% "proxy" % "2.3.3",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "com.beachape" %% "enumeratum-circe" % "1.5.21",
  "org.mockito" %% "mockito-scala-scalatest" % "1.5.12"
)


libraryDependencies += {
  val version = scalaBinaryVersion.value match {
    case "2.10" => "1.0.3"
    case _ ⇒ "1.6.2"
  }
  "com.lihaoyi" % "ammonite" % version % "test" cross CrossVersion.full
}

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue

coverageExcludedPackages := ".*ConfigService.*;.*Config.*;alpaca\\.client\\..*"

coverageEnabled := true


//Microsite details
micrositeName := "Alpaca Scala"
micrositeDescription := "A Scala library for alpaca.markets"
micrositeAuthor := "OUEasley"
micrositeBaseUrl := "/alpaca-scala"
micrositeDocumentationUrl := "/alpaca-scala/docs"

micrositePalette := Map(
  "brand-primary"     -> "#000",
  "brand-secondary"   -> "#000",
  "brand-tertiary"    -> "#fcd600",
  "gray-dark"         -> "#453E46",
  "gray"              -> "#837F84",
  "gray-light"        -> "#E3E2E3",
  "gray-lighter"      -> "#F4F3F4",
  "white-color"       -> "#FFFFFF")

micrositePushSiteWith := GitHub4s

micrositeGithubToken := sys.env.get("GITHUB_TOKEN")

micrositeGithubOwner := "cynance"
micrositeGithubRepo := "alpaca-scala"