name := "analyzer"

organization := "com.hahnbanach"
maintainer := "angelo.leto@hahnbanach.com"


lazy val scala213 = "2.13.10"
lazy val scala3 = "3.2.2"

ThisBuild / scalaVersion := scala213
ThisBuild / versionScheme := Some("semver-spec")

crossScalaVersions := Seq(scala213)

resolvers ++= Seq("Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  Resolver.bintrayRepo("hseeberger", "maven"))

libraryDependencies ++= {
  val breezeVersion	= "2.1.0"
  val catsVersion = "2.9.0"
  val scalatestVersion	= "3.2.16"
  val scoptVersion	= "4.1.0"
  val sprayJsonVersion = "1.3.6"
  Seq(
    "com.github.scopt" %% "scopt" % scoptVersion,
    "io.spray" %%  "spray-json" % sprayJsonVersion,
    "org.scalanlp" %% "breeze" % breezeVersion,
    "org.scalanlp" %% "breeze-natives" % breezeVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.typelevel" %% "cats-kernel" % catsVersion
  )
}

scalacOptions += "-deprecation"
scalacOptions += "-feature"
//scalacOptions += "-Ylog-classpath"
Test / testOptions += Tests.Argument("-oF")

enablePlugins(GitVersioning)
enablePlugins(GitBranchPrompt)
enablePlugins(UniversalPlugin)

git.useGitDescribe := true

Test / fork := true

// do not buffer test output
Test / logBuffered := false

releaseCrossBuild := true

publishMavenStyle := true

Test / publishArtifact := false

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

val projectId = sys.env.getOrElse("CI_PROJECT_ID", "1")
val token = sys.env.getOrElse("CI_JOB_TOKEN", "unknown")
credentials += Credentials("GitLab Packages Registry", s"gitlab.com", "gitlab-ci-token", s"$token")
publishTo := Some("GitLab Packages Registry" at s"https://gitlab.com/api/v4/projects/$projectId/packages/maven")

licenses := Seq(("GPLv2", url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.md")))

