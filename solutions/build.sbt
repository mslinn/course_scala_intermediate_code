organization := "com.micronautics"

name := "Intermediate Scala Course Solutions"

description := "Intermediate Scala Course Solutions to Exercises"

version := "0.1.1"

scalaVersion := "2.10.3"

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
  "com.typesafe.akka" %% "akka-dataflow"  % "2.2.1" withSources,
  "com.typesafe.akka" %% "akka-actor"     % "2.2.1" withSources,
  "com.typesafe"      %  "config"         % "1.0.2" withSources,
  "org.xerial"        %  "sqlite-jdbc"    % "3.7.2" withSources,
  //
  "com.typesafe.akka" %% "akka-testkit"   % "2.2.1"  % "test" withSources,
  "org.specs2"        %% "specs2"         % "2.1.1"  % "test" withSources,
  "org.scalatest"     %  "scalatest_2.10" % "2.0.M7" % "test" withSources,
  "junit"             %  "junit"          % "4.8.1"  % "test" // Scala IDE requires this; IntelliJ IDEA does not
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