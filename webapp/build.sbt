name := "webapp"

version := "1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.scalatest" %% "scalatest" % "2.0" % "test" withSources() withJavadoc()
)     

play.Project.playScalaSettings
