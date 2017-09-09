package com.micronautics.gitStats.svn

import java.nio.file.Path

import com.micronautics.gitStats.{AggCommit, ConfigGitStats}
import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.svn.SvnCmd._
import com.micronautics.gitStats.svn.SvnWorkDir._
import AggCommit._

import scala.util.{Failure, Success, Try}

//TODO Embed into the main app, remove main method from here
object SvnStats extends App {

  implicit val config: ConfigGitStats = ConfigGitStats(directoryName = "/work/workspace")
  if (svnVersion.isEmpty) {
    Console.err.println("Failed to determine Subversion command version; exiting")
  }
  Console.err.println(s"Subversion command version: $svnVersion")

  if (svnVersion.get < svnMinimalSupportedVersion) {
    Console.err.println(s"Unsupported Subversion version: $svnVersion; $svnMinimalSupportedVersion or higher is required")
    sys.exit(-1)
  }

  val svnUsers: SvnUsers = new SvnUsers()
  println(s"Subversion user names: ${svnUsers.userNames}")
  if (svnUsers.userNames.isEmpty) {
    Console.err.println("Failed to detect Subversion users; exiting")
    sys.exit(-1)
  }

  val svnCmd = new SvnCmd(svnUsers)

  val projectDirs: Iterable[Path] = findProjectDirs(config.directory.toPath)(isSvnWorkDir)
  val svnWorkDirs: Iterable[SvnWorkDir] = projectDirs.map(new SvnWorkDir(_, svnCmd))
  val perDirCommits: Iterable[(SvnWorkDir, Try[Iterable[AggCommit]])] =
    svnWorkDirs.map { workDir =>
      try {
        workDir -> Success(workDir.aggCommits)
      } catch {
        case e: Throwable => workDir -> Failure(e)
      }
    }
  val (successes, failures) = perDirCommits.partition { case (_, commits) => commits.isSuccess }

  val allCommits = successes
    .flatMap { case (workDir, Success(commits)) => commits }
  val allByLanguage = aggregateByLanguage(allCommits)
  println(allByLanguage.mkString("\n"))

  failures
    .collect { case (workDir, Failure(e)) => (workDir, e) }
    .foreach { case (workDir, e) =>
      println(s"Directory ${workDir.dir}: ${e.getMessage}")
    }
}
