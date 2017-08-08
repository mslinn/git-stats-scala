package com.micronautics.gitStats

import java.io.File
import com.micronautics.gitStats.Commit.intFormat

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
      println()  // separate repos with a blank line
      detailCommits.foreach { v => println(v.summarize(config.authorFullName, dir.getAbsolutePath)) }
    }
    println(grandTotalCommit.summarize(config.authorFullName, dir.getAbsolutePath, displayLanguageInfo=false))
    grandTotalCommit
  }

  // List("SBT: +141 / 0 / net 141")
  def formatCommits(userName: String, repoName: String, finalTotal: Boolean = false, commits: List[Commit]): String = {
//    val forRepo: String = if (finalTotal) " in all git repositories" else s" in $repoName"
//    s"$userName added ${ intFormat(added) } lines and deleted ${ intFormat(deleted) } lines, net ${ intFormat(delta) } lines$forLang$forRepo"

    val subtotals: List[List[String]] = List(
      commits.map {
        commit => s"${ commit.language } / +${ intFormat(commit.added) } / -${ intFormat(commit.deleted) } / net ${ intFormat(commit.delta) } lines"
      }
    )
    AsciiWidgets.asciiTable(subtotals:_*)
  }
}
