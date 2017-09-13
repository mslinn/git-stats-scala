package com.micronautics.gitStats

import java.io.File

import com.github.nscala_time.time.Imports.DateTime
import com.micronautics.gitStats.render.ExcelRenderer
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * @author mslin_000 */
object ConfigGitStats {
  val fmt_yyyyMMdd: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val fmt_yyyyMM: DateTimeFormatter   = DateTimeFormat.forPattern("yyyy-MM")
  val fmt_MMMMyyyy: DateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy")

  /** This only works if the current directory is the root of a git directory tree */
  @inline def gitUserName(cwd: File)(implicit config: ConfigGitStats): String = {
    val userName = getOutputFrom(cwd, gitProgram, "config", "user.name")
    if (isWindows) "\"" + userName + "\"" else userName.replace(" ", "\\ ")
  }

  lazy val zero: DateTime      = new DateTime(0)
  lazy val today: DateTime     = DateTime.now.withTimeAtStartOfDay
  lazy val lastYear: DateTime  = today.minusDays(365 - 1) // git log dates are inclusive

  lazy val last90days: DateTime = today.minusDays(90 - 1) // git log dates are inclusive
  lazy val last30days: DateTime = today.minusDays(30 - 1) // git log dates are inclusive
  lazy val lastMonth: DateTime = today.minusMonths(1)

  lazy val zeroFormatted: String      = ConfigGitStats.fmt_yyyyMMdd.print(zero)
  lazy val todayFormatted: String     = ConfigGitStats.fmt_yyyyMMdd.print(today)
  lazy val last30Formatted: String    = ConfigGitStats.fmt_yyyyMMdd.print(last30days)
  lazy val last90Formatted: String    = ConfigGitStats.fmt_yyyyMMdd.print(last90days)
  lazy val lastMonthFormatted: String = ConfigGitStats.fmt_yyyyMMdd.print(lastMonth)
  lazy val lastYearFormatted: String  = ConfigGitStats.fmt_yyyyMMdd.print(lastYear)

  lazy val defaultValue: ConfigGitStats = ConfigGitStats()
}

/** Configuration value object for this app.
  * @param dateFrom If specified, earliest date to process commits, otherwise there is no lower limit
  * @param dateTo If specified, latest date to process commits, otherwise there is no upper limit
  * @param directoryName Top of git directory tree
  * @param excelFileName output to an Excel file with the given name instead of an UTF-8 table
  * @param output Show output of OS commands
  * @param subtotals Set to see per-repo statistics as well as the grand totals
  * @param ignoredFileTypes List of file types to ignore when processing the git commit log
  * @param ignoredSubDirectories List of subdirectories to ignore when processing the git commit log
  * @param onlyKnown If a file type is not hard-coded in the filetype match statement, do not process it. */
case class ConfigGitStats(
  dateFrom: Option[DateTime] = None,
  dateTo: Option[DateTime] = None,
  directoryName: String = sys.props("user.dir"),
  excelFileName: Option[String] = None,
  verbose: Boolean = false,
  ignoredFileTypes: List[String] = List("exe", "gif", "gz", "jpg", "log", "png", "pdf", "tar", "zip").sorted,
  ignoredSubDirectories: List[String] = List("node_modules").sorted,
  onlyKnown: Boolean = false,
  output: Boolean = false,
  subtotals: Boolean = false,
  remote: Boolean = false
) {
  import com.micronautics.gitStats.ConfigGitStats._

  lazy val directory = new java.io.File(directoryName)

  lazy val fromFormatted: Option[String] = dateFrom.map(fmt_yyyyMMdd.print)

  lazy val toFormatted: Option[String]   = dateTo.map(fmt_yyyyMMdd.print)

  lazy val excelWorkbook: Option[ExcelRenderer] = excelFileName.map(new ExcelRenderer(_))

  /** This only works if the current directory is the root of a git directory tree */
  lazy val gitRepoName: String = {
    val i: Int = directoryName.lastIndexOf(java.io.File.separator)
    if (i<0) directoryName else directoryName.substring(i).replace(java.io.File.separator, "")
  }
}
