name := "flow"

version := "1.0"

scalaVersion := "2.11.7"

val scalazVersion = "7.1.4"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  "org.typelevel" %% "scalaz-scalatest" % "0.3.0" % "test"
//  "org.scalaz" %% "scalaz-effect" % scalazVersion,
//  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
//  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test"
)
    