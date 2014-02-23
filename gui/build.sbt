name := "gui"

organization := "com.paulsnomura"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq( 
    "com.rabbitmq" % "amqp-client" % "3.2.1" withSources() withJavadoc(),
    "org.apache.commons" % "commons-lang3" % "3.1" withSources() withJavadoc()
)