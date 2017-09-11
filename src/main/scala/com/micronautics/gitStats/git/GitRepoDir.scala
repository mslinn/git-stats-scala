package com.micronautics.gitStats.git

import java.nio.file.Path

import com.micronautics.gitStats.{AggCommit, ConfigGitStats, ProjectDir, Repo}

class GitRepoDir(val dir: Path)(implicit config: ConfigGitStats) extends ProjectDir {

  val repo: Repo = new Repo(dir.toFile)

  lazy val aggCommits: Iterable[AggCommit] = repo.commits.value
    .map(commit => AggCommit(commit.language, commit.added, commit.deleted))
}
