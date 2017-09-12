package com.micronautics.gitStats.git

import java.nio.file.Path

import com.micronautics.gitStats.git.GitCmd._
import com.micronautics.gitStats.git.GitCommit._
import com.micronautics.gitStats.{AggCommit, Cmd, ConfigGitStats, ProjectDir}

class GitRepoDir(val dir: Path)(implicit config: ConfigGitStats) extends ProjectDir {
  val fromOption: String = config.fromFormatted.map(from => s"--since={$from}").mkString
  val toOption: String   = config.toFormatted  .map(from => s"--until={$from}").mkString

  val author: String = gitUserName(dir)

  val gitLogCmd: List[String] =
    gitProgram ++
      List("log", s"--author=$author", s"--pretty=tformat:", "--numstat") ++
      (if (fromOption.isEmpty) Nil else List(fromOption)) ++
      (if (toOption.isEmpty) Nil else List(toOption))

  val gitResponse: List[String] =
    Cmd.getOutputFrom(dir, gitLogCmd: _*)
      .split("\n")
      .filter(_.nonEmpty)
      .toList

  val commits: Iterable[GitCommit] =
    gitResponse.flatMap(parseGitCommit(_, dir).iterator)

  lazy val aggCommits: Iterable[AggCommit] =
    commits.map(_.fileModif)
      .filterNot(_.isIgnoredFileType)
      .filterNot(_.isIgnoredPath)
      .filterNot(config.onlyKnown && _.isUnrecognizedLanguage)
      .map(fileModif => AggCommit(fileModif.language, fileModif.linesAdded, fileModif.linesDeleted))
}
