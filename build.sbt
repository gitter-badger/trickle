import scoverage.ScoverageKeys
import ReleaseTransformations._

enablePlugins(GitVersioning, GitBranchPrompt)

git.baseVersion := "0.1.0"
lazy val buildSettings = Seq(
  organization := "com.github.benoitlouy",
  scalaVersion := "2.11.7"
)

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageMinimum := 90,
  ScoverageKeys.coverageFailOnMinimum := false,
  ScoverageKeys.coverageHighlighting := scalaBinaryVersion.value != "2.10"
)

val scalazVersion = "7.1.4"

lazy val trickle = project.in(file("."))
  .settings(noPublishSettings)
  .aggregate(core)
  .aggregate(examples)

lazy val core = project.in(file("core"))
  .settings(moduleName := "trickle-core")
  .settings(trickleSettings)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
    "com.chuusai" %% "shapeless" % "2.2.5",
    "org.apache.commons" % "commons-pool2" % "2.4.2",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
    "org.typelevel" %% "scalaz-scalatest" % "0.3.0" % "test"
  ))

lazy val examples = project.in(file("examples"))
  .settings(moduleName := "trickle-examples")
  .settings(trickleSettings)
  .settings(noPublishSettings)
  .dependsOn(core)


lazy val trickleSettings = buildSettings ++ commonSettings ++ scoverageSettings

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
  homepage := Some(url("https://github.com/benoitlouy/trickle")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/benoitlouy/trickle"), "scm:git:git@github.com:benoitlouy/trickle.git")),
  autoAPIMappings := true,
  pomExtra := (
    <developers>
      <developer>
        <id>benoitlouy</id>
        <name>Benoit Louy</name>
        <url>http://github.com/benoitlouy/</url>
      </developer>
    </developers>
    )
) ++ credentialSettings ++ sharedPublishSettings ++ sharedReleaseProcess

lazy val sharedPublishSettings = Seq(
  publishMavenStyle := true,
  releaseCrossBuild := true
)

lazy val sharedReleaseProcess = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
//    publishArtifacts,
    ReleaseStep(action = Command.process("publishSigned", _)),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
  )
)

lazy val credentialSettings = Seq(
  // For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

addCommandAlias("build", ";core/scalastyle;coverage;core/test;examples/compile")
addCommandAlias("gitSnapshots", ";set version in ThisBuild := git.gitDescribedVersion.value.get + \"-SNAPSHOT\"")