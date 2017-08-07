package com.micronautics.gitStats

import java.io.File

/** Process repo at directory `dir` */
class Repo(config: ConfigGitStats, dir: File) {
  val fromOption: String = config.fromFormatted.map(from => s"--since={$from}").mkString
  val toOption: String   = config.toFormatted  .map(from => s"--until={$from}").mkString

  dir.setCwd()
  println()

  // git log --author="Mike Slinn" --pretty=tformat: --numstat
  // git log --author="Mike Slinn" --pretty=tformat: --numstat --since={2016-09-01} --until={2017-08-30}
  val gitCmd: Seq[String] = List(gitProgram, "log", s"--author=${ config.author }", s"--pretty=tformat:", "--numstat") :::
    (if (fromOption.isEmpty) Nil else List(fromOption)) :::
    (if (toOption.isEmpty) Nil else List(toOption))

  val gitResponse: List[String] =
    getOutputFrom(dir, gitCmd:_*)
      .split("\n")
      .filter(_.nonEmpty)
      .toList

  logger.debug(gitResponse.mkString("\n"))

  val commits: List[Commit] =
    gitResponse
      .map(Commit.apply)
      .filterNot(commit => config.ignoredFileTypes.contains(commit.fileType))
      .filterNot(commit => config.ignoredSubDirectories.contains(commit.lastFilePath))

  val languageTotals = new LanguageTotals

  val grandTotalCommit: Commit = commits.fold(Commit.zero) {
    case (acc, elem) =>
      languageTotals.combine(elem)
      val newTotal = Commit(acc.added+elem.added, acc.deleted+elem.deleted, language = elem.language)
      newTotal
  }

  val detailCommits: List[Commit] = languageTotals
                                      .map
                                      .values
                                      .toList
                                      .sortBy(x => -x.delta)

  def process: Commit = {
    if (config.verbose) {
      detailCommits.foreach { v => println(v.summarize(config.authorFullName, config.gitRepoName)) }
      println()  // separate repos with a blank line
    }
    println(grandTotalCommit.summarize(config.authorFullName, config.gitRepoName, displayLanguageInfo=false))
    grandTotalCommit
  }
}