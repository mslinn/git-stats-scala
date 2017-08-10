package com.micronautics.gitStats

import java.io.File

/** Process repo at directory `dir` */
class Repo(config: ConfigGitStats, val dir: File) {
  val fromOption: String = config.fromFormatted.map(from => s"--since={$from}").mkString
  val toOption: String   = config.toFormatted  .map(from => s"--until={$from}").mkString

  val author: String = ConfigGitStats.gitUserName(dir.getAbsoluteFile)
  lazy val authorFullName: String = author.replace("\\", "")

  //TODO This setCwd() is unnecessary and suspicious - see comments for the method.
//  dir.setCwd()
  println()

  // git log --author="Mike Slinn" --pretty=tformat: --numstat
  // git log --author="Mike Slinn" --pretty=tformat: --numstat --since={2016-09-01} --until={2017-08-30}
  val gitCmd: Seq[String] = List(gitProgram, "log", s"--author=$author", s"--pretty=tformat:", "--numstat") :::
    (if (fromOption.isEmpty) Nil else List(fromOption)) :::
    (if (toOption.isEmpty) Nil else List(toOption))

  val gitResponse: List[String] =
    getOutputFrom(dir, gitCmd:_*)
      .split("\n")
      .filter(_.nonEmpty)
      .toList

  logger.debug(gitResponse.mkString("\n"))

  val commits: Commits =
    Commits(
      gitResponse
        .map(Commit.apply)
        .filterNot(commit => commit.hasUnknownLanguage && config.onlyKnown)
        .filterNot(commit => config.ignoredFileTypes.contains(commit.fileType))
        .filterNot(commit => config.ignoredSubDirectories.exists(subdir => commit.fileName.contains(s"$subdir/")))
    )

  val grandTotal: Commit = commits.total
  val grandTotals = Commits(List(grandTotal))
  val languageTotals: LanguageTotals = commits.languageTotals
  println("")

  def commitsByLanguage: Commits = languageTotals.asCommits
}
