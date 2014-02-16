import AssemblyKeys._ // put this at the top of the file

scalaVersion := "2.10.3"

name := "MDServer"

organization := "com.paulsnomura"

version := "0.1-SNAPSHOT"

scalacOptions ++= Seq("-feature", "-deprecation")

libraryDependencies ++= Seq( 
    "com.rabbitmq" % "amqp-client" % "3.2.1" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-actor" % "2.2.3" withSources() withJavadoc(),
	"com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test" withSources() withJavadoc(),
	"org.apache.commons" % "commons-lang3" % "3.1" withSources() withJavadoc(),
	"org.apache.logging.log4j" % "log4j-api" % "2.0-beta9" withSources() withJavadoc(),
	"org.apache.logging.log4j" % "log4j-core" % "2.0-beta9" withSources() withJavadoc(),
	"org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
    "org.scalatest" %% "scalatest" % "2.0" % "test" withSources() withJavadoc(),
    "org.scalacheck" %% "scalacheck" % "1.11.1" % "test" withSources() withJavadoc(),    
	"org.specs2" %% "specs2" % "2.3.6" % "test" withSources() withJavadoc()
)

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

assemblySettings

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
