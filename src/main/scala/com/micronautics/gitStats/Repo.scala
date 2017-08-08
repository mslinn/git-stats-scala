package com.micronautics.gitStats

import java.io.File
import Output._

/** Process repo at directory `dir` */
class Repo(config: ConfigGitStats, val dir: File) {
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
      .filterNot(commit => commit.hasUnknownLanguage && config.onlyKnown)
      .filterNot(commit => config.ignoredFileTypes.contains(commit.fileType))
      .filterNot(commit => config.ignoredSubDirectories.exists(subdir => commit.fileName.contains(s"$subdir/")))

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
      println(formatCommits(userName=config.authorFullName, title=dir.getAbsolutePath, grandTotal=true, commits=detailCommits))
    }
    println(formatCommits(userName=config.authorFullName, title=dir.getAbsolutePath, grandTotal=true, commits=List(grandTotalCommit)))
    grandTotalCommit
  }
}
