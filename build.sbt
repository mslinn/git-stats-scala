cancelable := true

developers := List(
  Developer(
    "mslinn",
    "Mike Slinn",
    "mslinn@micronauticsresearch.com",
    url("https://github.com/mslinn")
  ),
  Developer(
    "tashoyan",
    "Arseniy Tashoyan",
    "", // not sure if you want this published, please add if so
    url("https://github.com/tashoyan")
  )
)

// define the statements initially evaluated when entering 'console', 'console-quick', but not 'console-project'
initialCommands in console := """import com.micronautics.gitStats._
                                |""".stripMargin

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

libraryDependencies ++= Seq(
  "ch.qos.logback"           %  "logback-classic" % "1.2.3",
  "com.github.nscala-time"   %% "nscala-time"     % "2.16.0" withSources(),
  "com.github.scopt"         %% "scopt"           % "3.6.0"  withSources(),
  "de.vandermeer"            %  "asciitable"      % "0.3.2"  withSources(),
  "org.apache.poi"           %  "poi-ooxml"       % "3.16"   withSources(),
  //
  "org.scalatest"            %% "scalatest"       % "3.0.1" % Test withSources(),
  "junit"                    %  "junit"           % "4.12"  % Test
)

// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn

// Level.INFO is needed to see detailed output when running tests
logLevel in test := Level.Info

name := "git-stats-scala"

organization := "com.micronautics"

resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

scalaVersion := "2.12.8"

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

scmInfo := Some(
  ScmInfo(
    url(s"https://github.com/mslinn/$name"),
    s"git@github.com:mslinn/$name.git"
  )
)

sublimeTransitive := true

version := "0.2.1"
