package com.micronautics.gitStats.git

import java.nio.file.Path

import com.micronautics.gitStats.AggCommit.AggCommits
import com.micronautics.gitStats.ConfigGitStats
import com.micronautics.gitStats.ProjectDir._

import scala.util.{Failure, Success, Try}

object GitStats {

  def commits(scmProjectDirs: Iterable[Path])(implicit config: ConfigGitStats): Iterable[(Path, Try[AggCommits])] = {
    val gitProjectDirs = scmProjectDirs.filter(_.isGitRepo)
    if (config.verbose)
      println(gitProjectDirs.mkString("Detected Git repository directories:\n", "\n", "\n"))

    val gitRepoDirs = gitProjectDirs.map(new GitRepoDir(_))
    gitRepoDirs.map { repoDir =>
      try {
        repoDir.dir -> Success(repoDir.aggCommits)
      } catch {
        case e: Throwable => repoDir.dir -> Failure(e)
      }
    }
  }
}
