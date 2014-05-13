name := "MDServer"

organization := "com.quantweb"

version := "0.1-SNAPSHOT"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
    "com.h2database" % "h2" % "1.3.175" withSources() withJavadoc(),
    "com.rabbitmq" % "amqp-client" % "3.3.1" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-actor" % "2.3.2" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-testkit" % "2.3.2" % "test" withSources() withJavadoc(),
    "com.typesafe.slick" %% "slick" % "2.0.0" withSources() withJavadoc(),
    "org.apache.commons" % "commons-lang3" % "3.1" withSources() withJavadoc(),
    "org.apache.logging.log4j" % "log4j-api" % "2.0-beta9" withSources() withJavadoc(),
    "org.apache.logging.log4j" % "log4j-core" % "2.0-beta9" withSources() withJavadoc(),
    "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
    "org.scalatest" %% "scalatest" % "2.0" % "test" withSources() withJavadoc(),
    "org.scalacheck" %% "scalacheck" % "1.11.1" % "test" withSources() withJavadoc(),
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.specs2" %% "specs2" % "2.3.6" % "test" withSources() withJavadoc()
)

