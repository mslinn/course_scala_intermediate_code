organization := "com.micronautics"

name := "Intermediate Scala Course Solutions"

description := "Intermediate Scala Course Solutions to Exercises"

version := "2.11.6"

scalaVersion := "2.11.6"

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
  //
  "org.scalatest"     %% "scalatest"     % "2.2.3"  % "test" withSources(),
  "junit"             %  "junit"         % "4.12"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
)

// Optional settings from https://github.com/harrah/xsbt/wiki/Quick-Configuration-Examples follow
initialCommands := """
                     |""".stripMargin

logLevel := Level.Error

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
//logLevel in compile := Level.Warn
