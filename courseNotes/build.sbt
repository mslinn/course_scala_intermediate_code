organization := "com.micronautics"
name := "IntermediateScalaCourse"
description := "Core Scala - Intermediate Scala Course Notes"
version := "2.11.5"

scalaVersion := "2.11.5"
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

libraryDependencies ++= {
  val akkaV = "2.3.9"
  Seq(
  "com.typesafe.akka"            %% "akka-actor"   % akkaV   withSources(),
  "com.beachape.filemanagement"  %% "schwatcher"   % "0.1.5",
  "com.typesafe"                 %  "config"       % "1.2.1" withSources(),
  "org.scalautils"               %% "scalautils"   % "2.1.7" withSources(),
  //
  "com.typesafe.akka"            %% "akka-testkit" % akkaV    % "test" withSources(),
  "org.specs2"                   %% "specs2"       % "2.3.12" % "test" withSources(), // do not update, sensitive!
  "org.scalatest"                %% "scalatest"    %  "2.2.3" % "test" withSources(),
  "junit"                        %  "junit"        % "4.12"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
  )
}
updateOptions := updateOptions.value.withCachedResolution(true)

// set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
initialCommands in console := """import akka.actor._
                                |import akka.pattern.ask
                                |import akka.util.Timeout
                                |import java.io.File
                                |import java.net.URL
                                |import concurrent._
                                |import concurrent.duration._
                                |import concurrent.ExecutionContext.Implicits.global
                                |import scala.sys.process._
                                |import scala.util.control.NoStackTrace
                                |import scala.util.{Try,Success,Failure}
                                |import multi._
                                |import multi.futures._
                                |import multi.futures.FutureArtifacts._
                                |""".stripMargin

logLevel := Level.Info
logLevel in test := Level.Info // Level.Info is needed to see detailed output when running tests
logLevel in compile := Level.Info
