organization := "gaelrenoux"
name := "darwin"
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

val scalatestVersion = "3.0.1"
val typesafeConfigVersion = "1.3.1"
val scalaLoggingVersion = "3.7.2"

/*
val playVersion = "2.5.12"
val playSlickVersion = "2.1.0"*/

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",

  "com.typesafe" % "config" % typesafeConfigVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
/*
  "com.typesafe.play" %% "play" % playVersion,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion*/
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe" at "https://repo.typesafe.com/typesafe/maven-releases/",
  "Snowplow Repo" at "http://maven.snplow.com/releases/",
  /* Bintray is necessary : SBT itself needs a version of Scalaz missing from the standard public repositories. */
  "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
)
