organization := "com.micronautics"

name := "Intermediate Scala Course Solutions"

description := "Intermediate Scala Course Solutions to Exercises"

version := "2.11.1"

scalaVersion := "2.11.1"

scalacOptions in (Compile, doc) <++= baseDirectory.map {
  (bd: File) => Seq[String](
     "-deprecation",
	   "-encoding", "UTF-8",
	   "-unchecked",
     "-feature",
	   "-target:jvm-1.6",
     "-sourcepath", bd.getAbsolutePath,
	   "-Ywarn-adapted-args",
     "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/solutions€{FILE_PATH}.scala"
  )
}

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "org.xerial"        %  "sqlite-jdbc"   % "3.7.2" withSources(),
  "com.typesafe.akka" %% "akka-actor"    % "2.3.3" withSources(),
  "com.typesafe"      %  "config"        % "1.2.1" withSources(),
  "org.scalautils"    %% "scalautils"    % "2.1.7" withSources(),
  //
  "com.typesafe.akka" %% "akka-testkit"  % "2.3.3"  % "test" withSources(),
  "org.specs2"        %% "specs2"        % "2.3.12" % "test" withSources(),
  "org.scalatest"     %% "scalatest"     % "2.2.0"  % "test" withSources(),
  "junit"             %  "junit"         % "4.11"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
)

resolvers ++= Seq(
  "releases" at "http://oss.sonatype.org/content/repositories/releases"
)

// Optional settings from https://github.com/harrah/xsbt/wiki/Quick-Configuration-Examples follow
initialCommands := """
                     |""".stripMargin

logLevel := Level.Error

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
//logLevel in compile := Level.Warn
