import AssemblyKeys._ // put this at the top of the file

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq( 
	"com.rabbitmq" % "amqp-client" % "3.2.1" withSources() withJavadoc(),
	"org.apache.commons" % "commons-lang3" % "3.1" withSources() withJavadoc(),
	"org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
	"org.specs2" %% "specs2" % "2.3.6" % "test" withSources() withJavadoc(),
	"org.scalatest" %% "scalatest" % "2.0" % "test" withSources() withJavadoc(),
        "org.scalacheck" %% "scalacheck" % "1.11.1" % "test" withSources() withJavadoc(),
	"com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test" withSources() withJavadoc()
)

resolvers += Resolver.sonatypeRepo("snapshots")

assemblySettings

EclipseKeys.withSource := true