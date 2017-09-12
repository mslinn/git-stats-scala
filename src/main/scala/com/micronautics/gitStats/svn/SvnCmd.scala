package com.micronautics.gitStats.svn

import java.nio.file.Paths

import com.micronautics.gitStats.{Cmd, ConfigGitStats, Version}

object SvnCmd {

  //TODO Non-interactive or let user enter his credentials if needed?
  lazy val svnProgram: List[String] = List(
    if (Cmd.isWindows) "svn.exe" else "svn",
    "--non-interactive"
  )
//  lazy val svnProgram: List[String] = List(if (Cmd.isWindows) "svn.exe" else "svn")

  /*
  * --search is available from version 1.8 onward.
  * */
  val svnMinimalSupportedVersion: Version = Version.parse("1.8")

  def detectSvnVersion(implicit config: ConfigGitStats): Option[Version] = {
    val svnVersionCmd = svnProgram :+ "--version"
    val svnVersionOutput = Cmd.getOutputFrom(Paths.get(sys.props("user.dir")), svnVersionCmd: _*)
    parseSvnVersion(svnVersionOutput).map(Version.parse)
  }

  private val versionPattern = """\s+version\s+(\S+)""".r

  def parseSvnVersion(svnVersionOutput: String): Option[String] = {
    versionPattern.findFirstMatchIn(svnVersionOutput).map(_.group(1))
  }

  def generateSvnLogCmd(userNames: Set[String])(implicit config: ConfigGitStats): List[String] = {
    val dateRangeOption = generateDateRangeOption
    svnProgram ++
      List("log", "--diff") ++
      userNames.flatMap(userName => List("--search", userName)) ++
      dateRangeOption
  }

  /**
    * Generates command line option to specify date range.
    * -r {2017-08-01}:{2017-09-01}
    * If `from` date is not set, then set it to 1970-01-01.
    * If `to` date is not set, then set it to today.
    */
  def generateDateRangeOption(implicit config: ConfigGitStats): List[String] = {
    val from = config.fromFormatted.getOrElse(ConfigGitStats.zeroFormatted)
    val to = config.toFormatted.getOrElse(ConfigGitStats.todayFormatted)
    List("-r", s"{$from}:{$to}")
  }
}
