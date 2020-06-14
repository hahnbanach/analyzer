import NativePackagerHelper._

name := "analyzer"

organization := "com.getjenny"
maintainer := "angelo@getjenny.com"

crossScalaVersions := Seq("2.12.10")

resolvers ++= Seq("Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
  Resolver.bintrayRepo("hseeberger", "maven"))

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= {
  val BreezeVersion	= "1.0"
  val ScalatestVersion	= "3.1.1"
  val ScalazVersion	= "7.2.30"
  val ScoptVersion	= "3.7.0"
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

homepage := Some(url("http://www.getjenny.com"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/GetJenny/analyzer"),
    "scm:git@github.com:GetJenny/analyzer.git"
  )
)

developers := List(
  Developer(
    id    = "angleto",
    name  = "Angelo Leto",
    email = "angelo@getjenny.com",
    url   = url("http://www.getjenny.com")
  )
)

licenses := Seq(("GPLv2", url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.md")))

