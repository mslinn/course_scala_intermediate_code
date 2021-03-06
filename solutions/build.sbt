organization := "com.micronautics"

name := "intermediate-scala-course-solutions"

description := "Intermediate Scala Course Solutions to Exercises"

//scalaVersion := "2.12.13"
scalaVersion := "2.13.5"
version := scalaVersion.value

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  bd: File => Seq[String](
    "-deprecation",
    "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/solutionsâ‚¬{FILE_PATH}.scala",
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
  "org.xerial"        %  "sqlite-jdbc"   % "3.34.0" withSources(),
  //
  "org.scalatest"     %% "scalatest"     % "3.2.7"  % Test withSources()
)

initialCommands := """
                     |""".stripMargin

logLevel := Level.Error

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
//Compile / logLevel := Level.Warn

ThisBuild / turbo := true
