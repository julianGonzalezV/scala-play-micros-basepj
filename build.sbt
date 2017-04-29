name := """scala-play-micros-basepj"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
  // Sivas a trabajar con Slick, importar jdbc te presenta problema en la compilacion porque confunde al compilador
  //jdbc,
  evolutions,
  filters,
  cache,
  ws,
  "com.typesafe.play"       %%  "play-slick"              % "2.0.0",
  "com.typesafe.play"       %%  "play-slick-evolutions"   % "2.0.0",
  "com.typesafe.slick"      %%  "slick-extensions"        % "3.1.0",
  "com.typesafe.slick"      %%  "slick-codegen"           % "3.1.0",
  "com.h2database"          %   "h2"                      % "1.4.193",
  "oracle"                  %  "ojdbc"                    % "7",
  "org.typelevel"           %%  "cats"                    % "0.7.2" withSources(),
  "org.scalatestplus.play"  %%  "scalatestplus-play"      % "1.5.1" % Test
)

resolvers ++= Seq(
  "Typesafe Releases"               at "http://repo.typesafe.com/typesafe/maven-releases/",
  "scalaz-bintray"                  at "http://dl.bintray.com/scalaz/releases",
  "SpinGo OSS"                      at "http://spingo-oss.s3.amazonaws.com/repositories/releases"
)

