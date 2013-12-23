import AssemblyKeys._ // put this at the top of the file

libraryDependencies ++= Seq( 
	"com.rabbitmq" % "amqp-client" % "3.2.1",
	"org.apache.commons" % "commons-lang3" % "3.1",
	"org.specs2" %% "specs2" % "2.3.6" % "test",
	"org.scalatest" %% "scalatest" % "2.0" % "test" 
)

resolvers += Resolver.sonatypeRepo("snapshots")

assemblySettings
