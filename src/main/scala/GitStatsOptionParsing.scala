import java.io.File
import com.micronautics.gitStats._
import com.github.nscala_time.time.Imports._
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

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
  import ConfigGitStats._

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

trait GitStatsOptionParsing {
  import ConfigGitStats._

  val parser = new scopt.OptionParser[ConfigGitStats]("GitStats") {
    head("GitStats", "0.1.0")

    note("""For Linux and Mac, an executable program called git must be on the PATH;
         |for Windows, and executable called git.exe must be on the Path.
         |
         |Ignores files committed with these filetypes: exe, gif, gz, jpg, log, png, pdf, tar, zip.
         |Ignores directories committed called node_modules.
         |
         |Tries to continue processing remaining git repos if an exception is encountered.
         |""".stripMargin)

    opt[String]('a', "author").action { (x, c) =>
      c.copy(author = x)
    }.text("Author to attribute")

    opt[String]('d', "dir").action { (x, c) =>
      c.copy(directoryName = x)
    }.text("Directory to scan (defaults to current directory)")

    opt[String]('f', "from").action { (x, c) =>
      c.copy(dateFrom = Some(new DateTime(x).withTimeAtStartOfDay))
    }.text("First date to process, in yyyy-MM-dd format; default is no limit")

    opt[String]('i', "ignore").action { (x, c) =>
      val c2 = c.copy(ignoredFileTypes = x :: c.ignoredFileTypes)
      c2
    }.text("Additional filetype to ignore, without the leading dot (can be specified multiple times)")

    opt[String]('I', "Ignore").action { (x, c) =>
      c.copy(ignoredSubDirectories = x :: c.ignoredSubDirectories)
    }.text("Additional subdirectories to ignore, without slashes (can be specified multiple times)")

    opt[Unit]('m', "previousMonth").action { (_, c) =>
      c.copy(dateFrom = Some(lastMonth), dateTo = Some(today))
    }.text(s"Same as specifying --from={$lastMonthFormatted} and --to={$todayFormatted}")

    opt[String]('t', "to").action { (x, c) =>
      c.copy(dateTo = Some(new DateTime(x).withTimeAtStartOfDay))
    }.text("Last date to process, in yyyy-MM-dd format; default is no limit")

    opt[Unit]('v', "verbose").action { (_, c) =>
      c.copy(verbose = true)
    }.text("Show per-repo subtotals)")

    opt[Unit]('y', "previous365days").action { (_, c) =>
      c.copy(dateFrom = Some(lastYear), dateTo = Some(today))
    }.text(s"Same as specifying --from={$lastYearFormatted} and --to={$todayFormatted}")

    help("help").text("Print this usage text")
  }
}
