package com.micronautics.gitStats

import java.nio.file.Path

import com.micronautics.gitStats.Language._
import com.micronautics.gitStats.ProjectDir.RichPath

/**
  * One file modification within a commit to Subversion.
  *
  * @param file       File.
  * @param linesAdded Number of added lines. Negative number means more lines were deleted rather than added.
  * @param config Configuration object.
  */
case class FileModification(file: Path, linesAdded: Int)(implicit config: ConfigGitStats) {
  require(file != null, "File name must not be null")
  require(file.toString.nonEmpty, "File path must not be empty string")

  val isIgnoredFileType: Boolean = file.fileSuffix.exists(config.ignoredFileTypes.contains)

  val isIgnoredPath: Boolean = config.ignoredSubDirectories.exists(file.toString.contains)

  lazy val language: String = fileLanguage(file)

  lazy val isUnrecognizedLanguage: Boolean =
    language == unknownLanguage || language == miscellaneousLanguage
}
