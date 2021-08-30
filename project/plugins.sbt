resolvers += Classpaths.typesafeReleases

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.1")

addSbtPlugin("com.orrsella" % "sbt-sublime" % "1.1.2")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")

//addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.2")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

//addSbtPlugin("org.xerial.sbt" %% "sbt-sonatype" % "3.9.2")

addSbtPlugin("com.gilcloud" % "sbt-gitlab" % "0.0.6")
