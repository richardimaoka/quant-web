import sbt._
import sbt.Keys._

object MdserverBuild extends Build {

  lazy val mdserver = Project(
    id = "mdserver",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "MDServer",
      organization := "com.paulsnomura",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.3",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"
    )
  )
}
