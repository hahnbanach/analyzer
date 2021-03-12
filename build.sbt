name := "analyzer"

organization := "io.elegans"
maintainer := "angelo.leto@elegans.io"

lazy val scala212 = "2.12.12"
lazy val scala213 = "2.13.5"

ThisBuild / scalaVersion := scala212

crossScalaVersions := Seq(scala212, scala213)

resolvers ++= Seq("Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  Resolver.bintrayRepo("hseeberger", "maven"))

libraryDependencies ++= {
  val BreezeVersion	= "1.1"
  val ScalatestVersion	= "3.2.0"
  val ScalazVersion	= "7.2.30"
  val ScoptVersion	= "3.7.1"
  val SprayJsonVersion = "1.3.5"
  Seq(
    "com.github.scopt" %% "scopt" % ScoptVersion,
    "org.scalanlp" %% "breeze" % BreezeVersion,
    "org.scalanlp" %% "breeze-natives" % BreezeVersion,
    "org.scalatest" %% "scalatest" % ScalatestVersion % Test,
    "org.scalaz" %% "scalaz-core" % ScalazVersion,
    "io.spray" %%  "spray-json" % SprayJsonVersion
  )
}

scalacOptions += "-deprecation"
scalacOptions += "-feature"
//scalacOptions += "-Ylog-classpath"
testOptions in Test += Tests.Argument("-oF")

enablePlugins(GitVersioning)
enablePlugins(GitBranchPrompt)
enablePlugins(UniversalPlugin)

git.useGitDescribe := true

fork in Test := true

// do not buffer test output
logBuffered in Test := false

releaseCrossBuild := true

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

homepage := Some(url("http://www.elegans.io"))

scmInfo := Some(
  ScmInfo(
    url("https://gitlab.com/hahnbanach/analyzer"),
    "scm:git@gitlab.com:hahnbanach/analyzer.git"
  )
)

developers := List(
  Developer(
    id    = "angleto",
    name  = "Angelo Leto",
    email = "angelo.leto@elegans.io",
    url   = url("http://www.elegans.io")
  )
)

licenses := Seq(("GPLv2", url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.md")))

