resolvers += "Sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases/"

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")

// See https://github.com/orrsella/sbt-sublime
addSbtPlugin("com.orrsella" % "sbt-sublime" % "1.1.2")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

