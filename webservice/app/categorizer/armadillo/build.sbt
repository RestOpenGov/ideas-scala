name := "Armadillo"

version := "0.1"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
 
libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.3"

libraryDependencies += "com.typesafe" %% "play-mini" % "2.0"

mainClass in (Compile, run) := Some("play.core.server.NettyServer")