package com.micronautics.gitStats.svn

import java.io.File

import com.micronautics.gitStats.{AggCommit, ConfigGitStats}

class SvnWorkDir(val dir: File)
                (implicit config: ConfigGitStats)  {
//TODO from and to

  lazy val svnCommits: Set[SvnCommit] = ???

  lazy val aggCommits: List[AggCommit] = svnCommits.toList.flatMap(_.aggCommits)
}
