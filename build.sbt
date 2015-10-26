name := "flow"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-Xlog-implicits",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps"
)

val scalazVersion = "7.1.4"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
  "com.chuusai" %% "shapeless" % "2.2.5",
  "org.apache.commons" % "commons-pool2" % "2.4.2",
  "org.scala-lang" % "scala-reflect" % "2.11.7",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  "org.typelevel" %% "scalaz-scalatest" % "0.3.0" % "test"
)
    