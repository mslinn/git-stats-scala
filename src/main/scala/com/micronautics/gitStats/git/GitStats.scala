package com.micronautics.gitStats.git

import java.nio.file.Path

import com.micronautics.gitStats.{AggCommit, ConfigGitStats}

import scala.util.Try

object GitStats {

  def commits(scmProjectDirs: Iterable[Path])(implicit config: ConfigGitStats): Iterable[(Path, Try[AggCommit])] = {
    ???
  }
}
