package com.micronautics.gitStats.svn

import java.io.File

import com.micronautics.gitStats.{Cmd, ConfigGitStats, Version}
import SvnCmd._

class SvnCmd(svnUsers: SvnUsers)(implicit config: ConfigGitStats) {

  /**
    * Command line option to specify date range.
    * -r {2017-08-01}:{2017-09-01}
    * If `from` date is not set, then set it to 1970-01-01.
    * If `to` date is not set, then set it to today.
    */
  lazy val dateRangeOption: String = {
    val from = config.fromFormatted.getOrElse(ConfigGitStats.zeroFormatted)
    val to = config.toFormatted.getOrElse(ConfigGitStats.todayFormatted)
    s"-r {$from}:{$to}"
  }

  lazy val svnLogCmd: List[String] = List(svnProgram, "log", "--diff") ++
    svnUsers.userNames.map(userName => s"--search $userName") ++
    List(dateRangeOption)
}

object SvnCmd {

  //TODO Non-interactive or let user enter his credentials if needed?
  lazy val svnProgram: String = List(
    if (Cmd.isWindows) "svn.exe" else "svn",
    "--non-interactive")
    .mkString(" ")
//  lazy val svnProgram: String = if (Cmd.isWindows) "svn.exe" else "svn"

  /*
  * --search is available from 1.8 onward.
  * */
  val svnMinimalSupportedVersion = Version.parse("1.8")

  lazy val svnVersion: Option[Version] = detectSvnVersion

  protected def detectSvnVersion: Option[Version] = {
    val svnVersionOutput = Cmd.getOutputFrom(new File(sys.props("user.dir")), svnProgram, "--version")
    parseSvnVersion(svnVersionOutput).map(Version.parse)
  }

  private val versionPattern = """\s+version\s+(\S+)""".r

  def parseSvnVersion(svnVersionOutput: String): Option[String] = {
    versionPattern.findFirstMatchIn(svnVersionOutput).map(_.group(1))
  }
}
