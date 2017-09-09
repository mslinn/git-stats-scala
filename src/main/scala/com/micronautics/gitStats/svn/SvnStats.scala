package com.micronautics.gitStats.svn

import java.nio.file.Path

import com.micronautics.gitStats.AggCommit._
import com.micronautics.gitStats.ConfigGitStats
import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.svn.SvnCmd._
import com.micronautics.gitStats.svn.SvnUsers._

import scala.util.{Failure, Success}

//TODO Embed into the main app, remove main method from here
object SvnStats extends App {

  implicit val config: ConfigGitStats = ConfigGitStats(
    directoryName = "/work/workspace",
    dateFrom = Some(ConfigGitStats.last30days),
    verbose = true
  )
  println(s"Configuration: $config")

  val svnUsers: Set[String] = autoDetectUserNames
  println(s"Subversion user names: $svnUsers")
  if (svnUsers.isEmpty) {
    Console.err.println("Failed to detect Subversion users; exiting")
    sys.exit(-1)
  }

  val svnVersionOpt = detectSvnVersion
  if (svnVersionOpt.isEmpty) {
    Console.err.println("Failed to determine Subversion command version; exiting")
    sys.exit(-1)
  }
  val svnVersion = svnVersionOpt.get
  println(s"Subversion command version: $svnVersion")

  if (svnVersion < svnMinimalSupportedVersion) {
    Console.err.println(s"Unsupported Subversion version: $svnVersion; $svnMinimalSupportedVersion or higher is required")
    sys.exit(-1)
  }

  val svnLogCmd = generateSvnLogCmd(svnUsers)

  val projectDirs: Iterable[Path] = findProjectDirs3(config.directory).map(_.toPath)
//  val projectDirs: Iterable[Path] = findProjectDirs2(config.directory.toPath)
//    val projectDirs: Iterable[Path] = findProjectDirs(config.directory.toPath)(isSvnWorkDir)
//  val projectDirs: Iterable[Path] = com.micronautics.gitStats.gitProjectsUnder(config.directory).map(_.toPath)
  println(s"Project dirs: ${projectDirs.mkString("\n")}")

  val svnWorkDirs: Iterable[SvnWorkDir] = projectDirs.map(new SvnWorkDir(_, svnLogCmd))
  val (perDirCommits, perDirFailures) =
    svnWorkDirs.map { workDir =>
      try {
        workDir -> Success(workDir.aggCommits)
      } catch {
        case e: Throwable => workDir -> Failure(e)
      }
    }.partition { case (_, commits) => commits.isSuccess }

  val allCommits = perDirCommits
    .flatMap {
      case (_, Success(commits)) => commits
      case _ => Iterator.empty
    }
  val allByLanguage = aggregateByLanguage(allCommits).toList.sortBy(-_.linesAdded)
  println(allByLanguage.mkString("=== All Subversion commits grouped by language ===\n", "\n", "\n========================"))

  perDirFailures
    .collect { case (workDir, Failure(e)) => (workDir, e) }
    .foreach { case (workDir, e) =>
      println(s"Directory ${workDir.dir}: ${e.getMessage}")
    }
}
