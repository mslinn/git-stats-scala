package com.micronautics.gitStats.svn

import java.io.File

import com.micronautics.gitStats.svn.SvnCmd._
import com.micronautics.gitStats.{Cmd, ConfigGitStats, Version}

class SvnCmd(implicit config: ConfigGitStats) {

  lazy val svnVersion: Option[Version] = detectSvnVersion

  protected def detectSvnVersion: Option[Version] = {
    val svnVersionOutput = Cmd.getOutputFrom(new File(sys.props("user.dir")), svnProgram, "--version")
    parseSvnVersion(svnVersionOutput).map(Version.parse)
  }
}

object SvnCmd {

  //TODO Non-interactive or let user enter his credentials if needed?
//  lazy val svnProgram: String = List(if (Cmd.isWindows) "svn.exe" else "svn", "--non-interactive").mkString(" ")
  lazy val svnProgram: String = if (Cmd.isWindows) "svn.exe" else "svn"

  private val versionPattern = "\\s+version\\s+(\\S+)".r

  def parseSvnVersion(svnVersionOutput: String): Option[String] = {
    versionPattern.findFirstMatchIn(svnVersionOutput).map(_.group(1))
  }
}