import sbtassembly.MergeStrategy

organization := "com.micronautics"

name := "git-stats-scala"

version := "0.1.0"

scalaVersion := "2.12.3"

scalacOptions ++= Seq(
  "-deprecation",
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

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

libraryDependencies ++= Seq(
  "ch.qos.logback"           %  "logback-classic" % "1.2.3",
  "com.github.nscala-time"   %% "nscala-time"     % "2.16.0" withSources(),
  "com.github.scopt"         %% "scopt"           % "3.6.0"  withSources(),
  //
  "org.scalatest"            %% "scalatest"       % "3.0.1" % Test withSources(),
  "junit"                    %  "junit"           % "4.12"  % Test
)

logLevel := Level.Warn

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn

// Level.INFO is needed to see detailed output when running tests
logLevel in test := Level.Info

// define the statements initially evaluated when entering 'console', 'console-quick', but not 'console-project'
initialCommands in console := """
                                |""".stripMargin

cancelable := true

sublimeTransitive := true

assemblyMergeStrategy in assembly := { // this is the default plus one more for mime.types
  // See https://github.com/sbt/sbt-assembly#merge-strategy
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat

  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename

  case PathList("META-INF", xs @ _*) =>
    xs map {_.toLowerCase} match {
      case "mime.types" :: _ =>
        MergeStrategy.filterDistinctLines

      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard

      case ps @ (x :: _) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard

      case "plexus" :: x =>
        MergeStrategy.discard

      case "services" :: x =>
        MergeStrategy.filterDistinctLines

      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines

      case _ => MergeStrategy.deduplicate
    }

  case _ => MergeStrategy.deduplicate
}
