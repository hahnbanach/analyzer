name := "analyzer"

organization := "com.hahnbanach"
maintainer := "angelo.leto@hahnbanach.com"


lazy val scala2 = "2.13.16"
lazy val scala3 = "3.8.1"


ThisBuild / scalaVersion := scala3
ThisBuild / versionScheme := Some("semver-spec")

crossScalaVersions := Seq(scala3, scala2)

resolvers ++= Seq("Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  Resolver.bintrayRepo("hseeberger", "maven"))

libraryDependencies ++= {
  val breezeVersion	= "2.1.0"
  val catsVersion = "2.13.0"
  val roundeightsHasherVersion	= "1.2.3"
  //val scalaUuidVersion = "0.3.1"
  val scalatestVersion	= "3.2.19"
  val scoptVersion	= "4.1.0"
  val sprayJsonVersion = "1.3.6"

  Seq(
    "com.github.scopt" %% "scopt" % scoptVersion,
    "com.outr" %% "hasher" % roundeightsHasherVersion,
    //"io.jvm.uuid" %% "scala-uuid" % scalaUuidVersion,
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

homepage := Some(url("http://www.hahnbanach.com"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/hahnbanach/analyzer"),
    "scm:git@github.com:hahnbanach/analyzer.git"
  )
)

developers := List(
  Developer(
    id    = "angleto",
    name  = "Angelo Leto",
    email = "angelo.leto@hahnbanach.com",
    url   = url("http://www.hahnbanach.com")
  )
)

// Publish to GitHub Packages (Maven) under hahnbanach/analyzer.
// Auth from env: GH_PACKAGES_TOKEN (write:packages) / GH_PACKAGES_USER, falling back to the
// GitHub Actions built-ins GITHUB_TOKEN / GITHUB_ACTOR.
val ghPackagesUser = sys.env.getOrElse("GH_PACKAGES_USER", sys.env.getOrElse("GITHUB_ACTOR", "unknown"))
val ghPackagesToken = sys.env.getOrElse("GH_PACKAGES_TOKEN", sys.env.getOrElse("GITHUB_TOKEN", "unknown"))
credentials += Credentials("GitHub Package Registry", "maven.pkg.github.com", ghPackagesUser, ghPackagesToken)
publishTo := Some("GitHub Packages" at "https://maven.pkg.github.com/hahnbanach/analyzer")

licenses := Seq(("GPLv2", url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.md")))

