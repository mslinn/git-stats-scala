package com.micronautics.gitStats.git

import java.nio.file.{Path, Paths}

import com.micronautics.gitStats.AggCommit.AggCommits
import com.micronautics.gitStats.{AggCommit, ConfigGitStats, FileModification}

case class GitCommit(fileModif: FileModification) {

  lazy val aggCommits: AggCommits =
    Iterable(AggCommit(fileModif.language, fileModif.linesAdded, fileModif.linesDeleted))
}

object GitCommit {

  def parseGitCommit(args: String, workDir: Path)(implicit config: ConfigGitStats): Option[GitCommit] = {
    args.split("""\s+""") match {
      case Array(linesAdded, linesDeleted, fileName) =>
        /* Do not resolve file path here:
          * - the file may not exist already
          * - resolve takes time, but we do not need file content at this time*/
        Some(GitCommit(FileModification(Paths.get(workDir.toString, fileName),
          safeToInt(linesAdded),
          safeToInt(linesDeleted))))
      case Array(linesAdded, linesDeleted, oldFileName@_, arrow@_, newFileName) =>
        /* A file was renamed */
        Some(GitCommit(FileModification(Paths.get(workDir.toString, newFileName),
          safeToInt(linesAdded),
          safeToInt(linesDeleted))))
      case _ =>
        None
    }
  }

  @inline
  private def safeToInt(string: String): Int =
    if (string=="-") 0 else string.toInt
}
