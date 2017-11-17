organization := "gaelrenoux"
name := "darwin-core"
version := "1.0-SNAPSHOT"
maintainer := "Gaël Renoux"

scalaVersion := "2.11.11"
scalacOptions ++= Seq(
  "-feature", "-deprecation",
  "-language:postfixOps", "-language:reflectiveCalls", "-language:implicitConversions",
  "-Ywarn-dead-code", "-Ywarn-value-discard", "-Ywarn-unused"
)

/* Suppress problems with Scaladoc links */
scalacOptions in(Compile, doc) ++= Seq("-no-link-warnings")

val V = new {
  val typesafeConfig = "1.3.1"
  val scalaLogging = "3.7.2"

  val scalatest = "3.0.1"
  val logback = "1.0.13"
}

libraryDependencies ++= Seq(

  "com.typesafe" % "config" % V.typesafeConfig,
  "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,

  "org.scalatest" %% "scalatest" % V.scalatest % "test",
  "ch.qos.logback" % "logback-classic" % V.logback % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "https://repo.typesafe.com/typesafe/maven-releases/",
  "Snowplow Repo" at "http://maven.snplow.com/releases/",
  /* Bintray is necessary : SBT itself needs a version of Scalaz missing from the standard public repositories. */
  "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
)
