logLevel := Level.Warn
libraryDependencies += "org.scalariform" %% "scalariform" % "0.1.7"
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.4")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")
addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver" % "0.5.0")