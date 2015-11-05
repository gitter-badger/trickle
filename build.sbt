import scoverage.ScoverageKeys

enablePlugins(GitVersioning, GitBranchPrompt)

git.baseVersion := "0.1.0"
lazy val buildSettings = Seq(
  organization := "com.benoitlouy",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.7")
)

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageMinimum := 60,
  ScoverageKeys.coverageFailOnMinimum := false,
  ScoverageKeys.coverageHighlighting := scalaBinaryVersion.value != "2.10"
)

val scalazVersion = "7.1.4"

lazy val root = project.in(file(".")).aggregate(core)

lazy val core = project.in(file("core"))
  .settings(moduleName := "trickle-core")
  .settings(tricleSettings)
  .settings(libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
    "com.chuusai" %% "shapeless" % "2.2.5",
    "org.apache.commons" % "commons-pool2" % "2.4.2",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
    "org.typelevel" %% "scalaz-scalatest" % "0.3.0" % "test"
  ))


lazy val tricleSettings = buildSettings ++ commonSettings ++ scoverageSettings ++ publishSettings

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
//  "-Xlint",
  "-Yinline-warnings",
//  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://my.artifact.repo.net/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }
)
