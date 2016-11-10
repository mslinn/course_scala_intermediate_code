organization := "com.micronautics"

name := "Intermediate Scala Course Solutions"

description := "Intermediate Scala Course Solutions to Exercises"

version := "2.12.0"

scalaVersion := "2.12.0"

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  (bd: File) => Seq[String](
    "-deprecation",
    "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/solutions€{FILE_PATH}.scala",
    "-encoding", "UTF-8", 
    "-feature", 
    "-target:jvm-1.8", 
    "-unchecked",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-Xlint"
  )
}.value

scalacOptions in Test ++= Seq("-Yrangepos")

javacOptions ++= Seq(
  "-Xlint:deprecation", 
  "-Xlint:unchecked", 
  "-source", "1.8", 
  "-target", "1.8", 
  "-g:vars"
)

libraryDependencies ++= Seq(
  "org.xerial"        %  "sqlite-jdbc"   % "3.14.2.1" withSources(),
  //
  "org.scalatest"     %% "scalatest"     % "3.0.0"  % "test" withSources(),
  "junit"             %  "junit"         % "4.12"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
)

// Optional settings from https://github.com/harrah/xsbt/wiki/Quick-Configuration-Examples follow
initialCommands := """
                     |""".stripMargin

logLevel := Level.Error

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
//logLevel in compile := Level.Warn

