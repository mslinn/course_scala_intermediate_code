organization := "com.micronautics"

name := "IntermediateScalaCourse"

description := "Core Scala - Intermediate Scala Course Notes"

version := "2.11.4"

scalaVersion := "2.11.4"

autoCompilerPlugins := true

scalacOptions in (Compile, doc) <++= baseDirectory.map {
  (bd: File) => Seq[String](
     "-deprecation",
	   "-encoding", "UTF-8",
	   "-unchecked",
     "-feature",
	   "-target:jvm-1.7",
     "-sourcepath", bd.getAbsolutePath,
	   "-Ywarn-adapted-args",
     "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/coursenotes€{FILE_PATH}.scala"
  )
}

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "com.typesafe.akka"           %% "akka-actor"                      % "2.3.8"  withSources(),
  "com.beachape.filemanagement" %% "schwatcher"                      % "0.1.5",
  "com.typesafe"                %  "config"                          % "1.2.1"  withSources(),
  "org.scalautils"              %% "scalautils"                      % "2.1.7"  withSources(),
  //
  "com.typesafe.akka"           %% "akka-testkit"                    % "2.3.8"  % "test" withSources(),
  "org.specs2"                  %% "specs2"                          % "2.3.12" % "test" withSources(), // do not update, sensitive!
  "org.scalatest"               %% "scalatest"                       % "2.2.3"  % "test" withSources(),
  "junit"                       %  "junit"                           % "4.12"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
)

resolvers ++= Seq(
  "releases" at "http://oss.sonatype.org/content/repositories/releases"
)

logLevel := Level.Error

// Optional settings from https://github.com/harrah/xsbt/wiki/Quick-Configuration-Examples follow
initialCommands := """
                   |import java.io.File
                   |import java.net.URL
                   |import scala.sys.process._
                   |import concurrent._
                   |import concurrent.ExecutionContext.Implicits.global
                   |import scala.util.control.NoStackTrace
                   |import scala.util.{Try,Success,Failure}
                   |import concurrent.{Await, Future}
                   |import concurrent.duration._
                   |import multi._
                   |import multi.futures._
                   |import multi.futures.FutureArtifacts._
                   |""".stripMargin

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn
