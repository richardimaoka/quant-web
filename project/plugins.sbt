//sbt-eclipse to crate an eclipse project file
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")

//Use sbt-assembly plugin to create jars
resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.1")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")