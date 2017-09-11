package com.micronautics.gitStats.svn

import java.nio.file.Path

import com.micronautics.gitStats.AggCommit.AggCommits
import com.micronautics.gitStats.ConfigGitStats
import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.svn.SvnCmd._
import com.micronautics.gitStats.svn.SvnUsers._

import scala.util.{Failure, Success, Try}

object SvnStats {

  def commits(scmProjectDirs: Iterable[Path])(implicit config: ConfigGitStats): Iterable[(Path, Try[AggCommits])] = {
    val svnUsers: Set[String] = autoDetectUserNames
    if (config.verbose)
      println(s"Subversion user names: $svnUsers")
    if (svnUsers.isEmpty) {
      throw new RuntimeException("Failed to detect Subversion users")
    }

    val svnVersionOpt = detectSvnVersion
    if (svnVersionOpt.isEmpty) {
      throw new RuntimeException("Failed to determine Subversion command version")
    }
    val svnVersion = svnVersionOpt.get
    if (config.verbose)
      println(s"Subversion command version: $svnVersion")
    if (svnVersion < svnMinimalSupportedVersion) {
      throw new RuntimeException(s"Unsupported Subversion version: $svnVersion; $svnMinimalSupportedVersion or higher is required")
    }

    val svnLogCmd = generateSvnLogCmd(svnUsers)

    val svnProjectDirs = scmProjectDirs.filter(_.isSvnWorkDir)
    if (config.verbose)
      println(svnProjectDirs.mkString("Detected Subversion working directories:\n", "\n", "\n"))

    val svnWorkDirs = svnProjectDirs.map(new SvnWorkDir(_, svnLogCmd))
    svnWorkDirs.map { workDir =>
      try {
        workDir.dir -> Success(workDir.aggCommits)
      } catch {
        case e: Throwable => workDir.dir -> Failure(e)
      }
    }
  }
}
