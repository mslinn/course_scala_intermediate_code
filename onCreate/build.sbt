// If you have JDK 6 and not JDK 7 then replace all three instances of the number 7 to the number 6

organization := "com.micronautics"

name := "onCreate"

scalaVersion := "2.12.11"
//scalaVersion := "2.13.2"
version := scalaVersion.value

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-target:jvm-1.8",
  "-unchecked",
  "-Ywarn-adapted-args",
  "-Ywarn-value-discard",
  "-Xlint"
)

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  bd: File => Seq[String](
     "-sourcepath", bd.getAbsolutePath,
     "-doc-source-url", "https://github.com/mslinn/onCreate/tree/master€{FILE_PATH}.scala"
  )
}.value

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

resolvers ++= Seq(
  "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++= Seq(
  "com.beachape.filemanagement" %% "schwatcher"   % "0.3.5",
  "com.typesafe"                %  "config"       % "1.3.4" withSources()
)

logLevel := Level.Warn

// define the statements initially evaluated when entering 'console', 'console-quick', or 'console-project'
initialCommands := """
                     |""".stripMargin

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn

cancelable := true

sublimeTransitive := true

ThisBuild / turbo := true
