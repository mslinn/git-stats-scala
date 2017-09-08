package com.micronautics.gitStats.svn

import java.nio.file.{Files, Path, Paths}

import com.micronautics.gitStats.{AggCommit, ProjectDir}

class SvnWorkDir(dir: Path) extends ProjectDir {

  lazy val svnCommits: Set[SvnCommit] = ???

  lazy val aggCommits: List[AggCommit] = svnCommits.toList.flatMap(_.aggCommits)
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