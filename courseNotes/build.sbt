organization := "com.micronautics"
name := "intermediate-scala-course"
description := "Core Scala - Intermediate Scala Course Notes"
version := "2.12.2.1"

scalaVersion := "2.12.2"
autoCompilerPlugins := true
scalacOptions in (Compile, doc) ++= baseDirectory.map {
  (bd: File) => Seq[String](
    "-deprecation",
    "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/coursenotes€{FILE_PATH}.scala",
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

libraryDependencies ++= {
  val akkaV = "2.5.2"
  Seq(
  "com.typesafe.akka"            %% "akka-actor"       % akkaV   withSources(),
  "com.beachape.filemanagement"  %% "schwatcher"       % "0.3.2" withSources(),
  "com.github.pureconfig"        %% "pureconfig"       % "0.7.2" withSources(),
  "com.google.guava"             %  "guava"            % "18.0"  withSources(),
  "com.typesafe"                 %  "config"           % "1.3.0" withSources(),
  "org.scalactic"                %% "scalactic"        % "3.0.0" withSources(),
  "org.scala-lang"               %  "scala-reflect"    % scalaVersion.value,
  //
  "com.typesafe.akka"            %% "akka-testkit"     % akkaV    % "test" withSources(),
  "org.specs2"                   %% "specs2-core"      % "3.8.6"  % "test" withSources(),
  "org.specs2"                   %% "specs2-junit"     % "3.8.6"  % "test" withSources(),
  "org.scalatest"                %% "scalatest"        % "3.0.1"  % "test" withSources(),
  "junit"                        %  "junit"            % "4.12"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
  )
}
updateOptions := updateOptions.value.withCachedResolution(true)

// set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
initialCommands in console := """import akka.actor._
                                |import akka.pattern.ask
                                |import akka.util.Timeout
                                |import scala.language.postfixOps
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

