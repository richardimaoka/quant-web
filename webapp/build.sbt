name := "webapp"

version := "1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.typesafe.akka" %% "akka-testkit" % "2.3.2" % "test" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.0" % "test" withSources() withJavadoc()
)

play.Project.playScalaSettings
