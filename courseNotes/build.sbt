organization := "com.micronautics"
name := "intermediate-scala-course"
description := "Core Scala - Intermediate Scala Course Notes"

//scalaVersion := "2.12.13"
scalaVersion := "2.13.5"

version := scalaVersion.value

autoCompilerPlugins := true
scalacOptions in (Compile, doc) ++= baseDirectory.map {
  _: File => Seq[String](
    "-doc-source-url", "https://bitbucket.org/mslinn/course_scala_intermediate_code/src/master/coursenotesâ‚¬{FILE_PATH}.scala",
    "-encoding", "UTF-8",
    "-feature",
    "-target:jvm-1.8",
    "-unchecked",
    "-Ywarn-adapted-args",
    "-Ywarn-numeric-widen",
    "-Xlint"
  )
}.value
scalacOptions in Test ++= Seq("-Yrangepos")
scalacOptions += "-deprecation"

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

libraryDependencies ++= {
  val akkaV = "2.6.14"
  Seq(
  "com.typesafe.akka"      %% "akka-actor"                 % akkaV          withSources(),
  "com.github.pureconfig"  %% "pureconfig"                 % "0.15.0"       withSources(),
  "com.google.guava"       %  "guava"                      % "30.1.1-jre"   withSources(),
  //"com.micronautics"       %% "scalacourses-utils"         % "0.3.2"        withSources(),
  "com.typesafe"           %  "config"                     % "1.4.1"        withSources(),
  "org.scalactic"          %% "scalactic"                  % "3.2.8"        withSources(),
  "org.scala-lang"         %  "scala-reflect"              % scalaVersion.value,
  //
  "com.typesafe.akka"      %% "akka-testkit"               % akkaV          % Test withSources(),
  "org.specs2"             %% "specs2-core"                % "4.11.0"        % Test withSources(),
  "org.specs2"             %% "specs2-junit"               % "4.11.0"        % Test withSources(),
  "org.scalatest"          %% "scalatest"                  % "3.2.8"        % Test withSources(),
  "junit"                  %  "junit"                      % "4.12"         % Test // Scala IDE requires this; IntelliJ IDEA does not
  )
}
libraryDependencies ++=
  scalaVersion {
    case sv if sv.startsWith("2.13") => List(
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.2" withSources()
    )

    case _ => Nil
  }.value
updateOptions := updateOptions.value.withCachedResolution(true)

resolvers += "micronautics/scala on bintray" at "https://dl.bintray.com/micronautics/scala"

ThisBuild / turbo := true

// set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
initialCommands in console := """import akka.actor._
                                |import akka.pattern.ask
                                |import akka.util.Timeout
                                |import scala.language.postfixOps
                                |import java.io.File
                                |import java.net.URL
                                |import scala.concurrent._
                                |import scala.concurrent.duration._
                                |import scala.concurrent.ExecutionContext.Implicits.global
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

version := "2.13_3"
