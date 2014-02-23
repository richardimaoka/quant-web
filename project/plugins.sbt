//sbt-eclipse to crate an eclipse project file
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")