package com.micronautics.gitStats

import java.nio.file.Path

import com.micronautics.gitStats.Language._
import com.micronautics.gitStats.ProjectDir.RichPath

/**
  * One file modification within a commit to Subversion.
  *
  * @param file       File.
  * @param linesAdded Number of added lines.
  * @param linesDeleted Number of deleted lines.
  * @param config Configuration object.
  */
case class FileModification(file: Path, linesAdded: Int, linesDeleted: Int)(implicit config: ConfigGitStats) {
  require(file != null, "File name must not be null")
  require(file.toString.nonEmpty, "File path must not be empty string")
  //TODO Require non-negative lines added/deleted

  val isIgnoredFileType: Boolean = file.fileSuffix.exists(config.ignoredFileTypes.contains)

  val isIgnoredPath: Boolean = config.ignoredSubDirectories.exists(file.toString.contains)

  lazy val language: String = fileLanguage(file)

  lazy val isUnrecognizedLanguage: Boolean =
    language == unknownLanguage || language == miscellaneousLanguage
}

object FileModification {

  implicit class RichIntTuple2(val self: (Int, Int)) extends AnyVal {

    def +(that: (Int, Int)): (Int, Int) = (self._1 + that._1, self._2 + that._2)
  }
}
