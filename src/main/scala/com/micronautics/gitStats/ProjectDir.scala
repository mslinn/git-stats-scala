package com.micronautics.gitStats

import java.io.File
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

  import java.util.{stream => jus}
  import scala.collection.JavaConverters._

  def findProjectDirs3(rootDir: File)(implicit config: ConfigGitStats): Iterable[File] = {
    if (rootDir.isDirectory) {
      val childs = Option(rootDir.listFiles).toList.flatMap(_.toList)
      if (childs.exists(child => child.isFile && child.getAbsolutePath.endsWith(ignoreMarker.toString)))
        Iterable.empty
      else if (childs.exists(child => child.isDirectory && child.getAbsolutePath.endsWith(".svn"))) {
        if (config.verbose) print(".")
        Iterable(rootDir.getAbsoluteFile)
      } else
        childs.flatMap(findProjectDirs3)
    } else
      Iterable.empty
  }

  def findProjectDirs2(rootDir: Path)(implicit config: ConfigGitStats): Iterable[Path] = {
    if (Files.isDirectory(rootDir)) {
      val childs = Files.list(rootDir).collect(jus.Collectors.toList[Path]).asScala
      if (childs.exists(child => Files.isRegularFile(child) && child.endsWith(ignoreMarker)))
        Iterable.empty
      else if (childs.exists(child => Files.isDirectory(child) && child.endsWith(".svn"))) {
        if (config.verbose) print(".")
        Iterable(rootDir.toAbsolutePath)
      } else
        childs.flatMap(findProjectDirs2)
    } else
      Iterable.empty
  }

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
    println(s"-------- $rootDir")

    import java.util.{stream => jus}

    def findProjectDirs0(path: Path): jus.Stream[Path] = {
      if (!Files.isDirectory(path))
        jus.Stream.empty()
      else {
        println(s"+++ directory: $path")
        Files.list(path)
          .filter(isProjectDir(_))
          .filter(isNotIgnoredDir)
          .flatMap(findProjectDirs0(_))
      }
    }

    def findProjectDirs1(path: Path): jus.Stream[Path] = {
      if (!Files.isDirectory(path))
        jus.Stream.empty()
      else if (isIgnoredDir(path))
        jus.Stream.empty()
      else if (isProjectDir(path))
        jus.Stream.of[Path](path)
      else
        Files.list(path).flatMap(findProjectDirs1)
    }

    import scala.collection.JavaConverters._
    val res0 = findProjectDirs1(rootDir).collect(jus.Collectors.toList[Path])
    println(s"-------- $res0")
    val res = res0
      .asScala
    println(s"-------- $res")
    res
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
