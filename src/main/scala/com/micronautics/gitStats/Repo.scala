package com.micronautics.gitStats

import java.io.File

/** Process repo at directory `dir` */
@deprecated("TODO Use git.GitRepoDir instead", "0.2.1")
class Repo(val dir: File)
          (implicit config: ConfigGitStats) {
  val fromOption: String = config.fromFormatted.map(from => s"--since={$from}").mkString
  val toOption: String   = config.toFormatted  .map(from => s"--until={$from}").mkString

  val author: String = ConfigGitStats.gitUserName(dir.getAbsoluteFile)
  lazy val authorFullName: String = author.replace("\\", "")

  //TODO Instead of println here, format the debug output where the command is invoked - Cmd.run()
  if (config.verbose) println()

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
        .filterNot(_.ignoredFiletype)
        .filterNot(_.ignoredPath)
    )

  //TODO Instead of println here, format the debug output where the command is invoked - Cmd.run()
  if (config.verbose) println("")

  def commitsByLanguage: Commits = commits.byLanguage
}
