organization := "com.micronautics"

name := "Intermediate Scala Course Assignment"

description := "Intermediate Scala Assignment"

version := "0.1.0"

//scalaVersion := "2.12.11"
scalaVersion := "2.13.2"

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  (bd: File) => Seq[String](
     "-deprecation",
	 "-encoding", "UTF-8",
	 "-unchecked",
     "-feature",
	 "-target:jvm-1.7",
     "-sourcepath", bd.getAbsolutePath,
	 "-Ywarn-adapted-args",
     "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/assignment€{FILE_PATH}.scala"
  )
}.value

libraryDependencies ++= Seq(
  "com.typesafe" %  "config" % "1.3.4"
)

logLevel := Level.Error

initialCommands := """
"""

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn
