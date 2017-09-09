package com.micronautics.gitStats.svn

import java.nio.file.{Files, Path, Paths}

import com.micronautics.gitStats.{AggCommit, ProjectDir, Cmd}
import Cmd._
import SvnCommit._

class SvnWorkDir(val dir: Path, svnCmd: SvnCmd) extends ProjectDir {
  require(dir != null, "Directory must not be null")
  require(svnCmd != null, "Svn cmd must not be null")

  lazy val svnCommits: Iterable[SvnCommit] = {
    val processBuilder = run(dir.toFile, svnCmd.svnLogCmd: _*)
    val commitEntries = commitEntriesIterator(processBuilder.lineStream.iterator)
    commitEntries.map(parseSvnCommit).flatMap(_.iterator).toIterable
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