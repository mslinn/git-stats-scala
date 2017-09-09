package com.micronautics.gitStats

import java.nio.file.{Files, Path, Paths}

/*
* TODO Remove this comment
* - gitProjectsUnder in gitStats.scala is specific to Git. Need generic.
* - Hard to test in unit tests - use standard API.
* - Don't invent a wheel.
* */

trait ProjectDir {

  val aggCommits: Iterable[AggCommit]
}

object ProjectDir {

  /**
    * Finds project directories under the root directory.
    * Recursively scans the root directory to get all directories, that
    * <ol>
    * <li> meet the given predicate
    * <li> does not have ignore marker file
    * </ol>
    *
    * @param rootDir      Search in this directory.
    * @param isProjectDir Predicate to recognize project directories.
    * @return Collection of project directories.
    * @throws IllegalArgumentException Path is null
    */
  def findProjectDirs(rootDir: Path)(isProjectDir: Path => Boolean): Iterable[Path] = {

    import java.util.{stream => jus}
    def findProjectDirs0(path: Path): jus.Stream[Path] = {
      if (!Files.isDirectory(path))
        jus.Stream.empty()
      else
        Files.list(path)
          .filter(isProjectDir(_))
          .filter(isNotIgnoredDir)
          .flatMap(findProjectDirs0(_))
    }

    import scala.collection.JavaConverters._
    findProjectDirs0(rootDir)
      .collect(jus.Collectors.toList[Path])
      .asScala
  }

  lazy val ignoreMarker: Path = Paths.get(".ignore.stats")

  def isNotIgnoredDir(path: Path): Boolean = !isIgnoredDir(path)

  def isIgnoredDir(path: Path): Boolean = {
    Files.isDirectory(path) &&
      Files.list(path)
        .filter(child => Files.isRegularFile(child) && child.endsWith(ignoreMarker))
        .findAny()
        .isPresent
  }

}
