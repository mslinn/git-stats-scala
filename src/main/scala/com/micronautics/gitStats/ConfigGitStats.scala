package com.micronautics.gitStats

import java.io.File
import com.github.nscala_time.time.Imports.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * @author mslin_000 */
object ConfigGitStats {
  val fmt_yyyyMMdd: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val fmt_yyyyMM: DateTimeFormatter   = DateTimeFormat.forPattern("yyyy-MM")
  val fmt_MMMMyyyy: DateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy")

  /** This only works if the current directory is the root of a git directory tree */
  @inline def gitUserName(cwd: File): String = {
    val userName = getOutputFrom(cwd, gitProgram, "config", "user.name")
    if (isWindows) "\"" + userName + "\"" else userName.replace(" ", "\\ ")
  }

  lazy val today: DateTime     = DateTime.now.withTimeAtStartOfDay
  lazy val lastYear: DateTime  = today.minusDays(365)
  lazy val lastMonth: DateTime = today.minusDays(30)  // more accurately, the last 30 days

  lazy val todayFormatted: String     = ConfigGitStats.fmt_yyyyMMdd.print(today)
  lazy val lastMonthFormatted: String = ConfigGitStats.fmt_yyyyMMdd.print(lastMonth)
  lazy val lastYearFormatted: String  = ConfigGitStats.fmt_yyyyMMdd.print(lastYear)

  lazy val defaultValue: ConfigGitStats = ConfigGitStats()
}

case class ConfigGitStats(
  author: String = ConfigGitStats.gitUserName(new File(".").getAbsoluteFile),
  dateFrom: Option[DateTime] = None,
  dateTo: Option[DateTime] = None,
  directoryName: String = sys.props("user.dir"),
  verbose: Boolean = false,
  ignoredFileTypes: List[String] = List("exe", "gif", "gz", "jpg", "log", "png", "pdf", "tar", "zip"),
  ignoredSubDirectories: List[String] = List("node_modules")
) {
  import com.micronautics.gitStats.ConfigGitStats._

  lazy val authorFullName: String = author.replace("\\", "")

  lazy val directory = new java.io.File(directoryName)

  lazy val fromFormatted: String = dateFrom.map(fmt_yyyyMMdd.print).mkString

  lazy val toFormatted: String   = dateTo.map(fmt_yyyyMMdd.print).mkString

  /** This only works if the current directory is the root of a git directory tree */
  lazy val gitRepoName: String = {
    val i: Int = directoryName.lastIndexOf(java.io.File.separator)
    if (i<0) directoryName else directoryName.substring(i).replace(java.io.File.separator, "")
  }
}
