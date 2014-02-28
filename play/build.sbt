name := "playweb"

version := "1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scalatest" %% "scalatest" % "2.0" % "test" withSources() withJavadoc()
)     

play.Project.playScalaSettings

