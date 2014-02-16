import AssemblyKeys._ // put this at the top of the file

scalaVersion := "2.10.3"

lazy val mdserver = project in( file("mdserver") )

lazy val gui = project in( file("gui") ) dependsOn( mdserver )

lazy val play = project in( file("play") ) dependsOn( mdserver )

assemblySettings

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
