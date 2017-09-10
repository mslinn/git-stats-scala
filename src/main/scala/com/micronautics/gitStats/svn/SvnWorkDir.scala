package com.micronautics.gitStats.svn

import java.nio.file.{Files, Path, Paths}

import com.micronautics.gitStats.Cmd._
import com.micronautics.gitStats.svn.SvnCommit._
import com.micronautics.gitStats.{AggCommit, ConfigGitStats, ProjectDir}

class SvnWorkDir(val dir: Path, svnLogCmd: List[String])(implicit config: ConfigGitStats) extends ProjectDir {
  require(dir != null, "Directory must not be null")
  require(svnLogCmd != null, "Svn cmd must not be null")

  //TODO Filter-out commits belonging to wrong users.
  lazy val svnCommits: Iterable[SvnCommit] = {
    val processBuilder = run(dir.toFile, svnLogCmd: _*)
    val commitEntries = commitEntriesIterator(processBuilder.lineStream.iterator)
    commitEntries.map(parseSvnCommit(_, dir)).flatMap(_.iterator).toIterable
  }

  lazy val aggCommits: Iterable[AggCommit] = svnCommits.flatMap(_.aggCommits)
}

object SvnWorkDir {

  lazy val dotSvn: Path = Paths.get(".svn")

  /**
    * Checks if this path points to a Subversion working directory.
    * Criteria are:
    * <ol>
    * <li> it is a directory
    * <li> it has a subdirectory .svn
    * </ol>
    *
    * @param path Path to check
    * @return true if this path points to a Subversion working directory.
    * @throws IllegalArgumentException Path is null
    */
  def isSvnWorkDir(path: Path): Boolean = {
    require(path != null, "Path must not be null")

    Files.isDirectory(path) &&
      Files.list(path)
        .filter(child => Files.isDirectory(child) && child.endsWith(dotSvn))
        .findAny()
        .isPresent
  }
}