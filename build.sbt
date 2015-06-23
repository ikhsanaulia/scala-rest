organization in ThisBuild := "com.ikhsan.scala.rest"

name := "scala-rest"

version := "1.0"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "spray repo" at "http://repo.spray.io"

val sprayVersion = "1.3.2"
val akkaVersion = "2.3.6"

libraryDependencies ++= Seq(
    "com.typesafe.akka" 	%% "akka-actor" 		% akkaVersion,
    "io.spray" 				%% "spray-can" 			% sprayVersion,
    "io.spray" 				%% "spray-routing" 		% sprayVersion,
    "org.json4s" 			%% "json4s-native" 		% "3.2.4",
	"com.typesafe.slick" 	%% "slick" 				% "3.0.0",
	"org.slf4j" 			% "slf4j-nop" 			% "1.6.4",
	"postgresql" 			% "postgresql" 			% "9.1-901.jdbc4",
  	"com.zaxxer" 			% "HikariCP"			% "2.2.5")

libraryDependencies ++= Seq(
  "com.typesafe.akka" 	%% "akka-testkit" % akkaVersion % "test",
  "io.spray" 			%% "spray-testkit" % sprayVersion % "test",
  "org.scalatest" 		%% "scalatest" % "2.1.4" % "test",
  "com.typesafe.play" 	%% "play-json" % "2.4.0-M1",
  "org.infinispan" 		%% "infinispan-cachestore-mongodb" % "5.3.0.Final")

fork in run := true