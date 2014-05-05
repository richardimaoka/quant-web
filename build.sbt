scalaVersion := "2.10.3"

lazy val core = project in( file("core") )

lazy val webapp = project in( file("webapp") ) dependsOn( core )

scalacOptions ++= Seq("-feature", "-unchecked" ,"-deprecation")