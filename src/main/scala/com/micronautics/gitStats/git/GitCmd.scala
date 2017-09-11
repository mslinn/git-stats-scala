package com.micronautics.gitStats.git

import java.nio.file.Path

import com.micronautics.gitStats.{Cmd, ConfigGitStats}

object GitCmd {

  lazy val gitProgram: List[String] = List(if (Cmd.isWindows) "git.exe" else "git")

  /** This only works if the current directory is the root of a git directory tree */
  @inline
  def gitUserName(cwd: Path)(implicit config: ConfigGitStats): String = {
    val gitConfigCmd = gitProgram ++ List("config", "user.name")
    val userName = Cmd.getOutputFrom(cwd.toFile, gitConfigCmd: _*)
    if (Cmd.isWindows)
      "\"" + userName + "\""
    else
      userName.replace(" ", "\\ ")
  }
}
