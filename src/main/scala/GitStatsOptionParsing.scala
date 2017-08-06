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

  val lastMonth: String = ConfigGitStats.fmt_yyyyMM.print(DateTime.now.minusMonths(1))
}

case class ConfigGitStats(
  author: String = ConfigGitStats.gitUserName(new File(".").getAbsoluteFile),
  yyyy_mm: String = ConfigGitStats.fmt_yyyyMM.print(DateTime.now.minusMonths(1)),
  directoryName: String = sys.props("user.dir"),
  verbose: Boolean = false,
  ignoredFileTypes: List[String] = List("exe", "gif", "gz", "jpg", "log", "png", "pdf", "tar", "zip"),
  ignoredSubDirectories: List[String] = List("node_modules")
) {
  lazy val authorFullName: String = author.replace("\\", "")

  lazy val directory = new java.io.File(directoryName)

  lazy val from: String = if (yyyy_mm.contains("-")) s"$yyyy_mm-01" else yyyy_mm

  lazy val to: String = if (yyyy_mm.contains("-")) {
    val lastDay = new DateTime(s"$yyyy_mm-01").dayOfMonth.withMaximumValue
    ConfigGitStats.fmt_yyyyMMdd.print(lastDay)
  } else yyyy_mm + "-12-31"

  /** This only works if the current directory is the root of a git directory tree */
  lazy val gitRepoName: String = {
    val i: Int = directoryName.lastIndexOf(java.io.File.separator)
    if (i<0) directoryName else directoryName.substring(i).replace(java.io.File.separator, "")
  }

  lazy val reportDate: DateTime = ConfigGitStats.fmt_yyyyMM.parseDateTime(yyyy_mm)
  lazy val reportDateStr: String = ConfigGitStats.fmt_MMMMyyyy.print(reportDate)
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
    }.text("author to attribute")

    opt[String]('d', "dir").action { (x, c) =>
      c.copy(directoryName = x)
    }.text("directory to scan (defaults to current directory)")

    opt[String]('i', "ignore").action { (x, c) =>
      c.copy(ignoredFileTypes = x :: c.ignoredFileTypes)
    }.text("additional filetype to ignore, without the leading dot (can be specified multiple times)")

    opt[String]('I', "Ignore").action { (x, c) =>
      c.copy(ignoredSubDirectories = x :: c.ignoredSubDirectories)
    }.text("additional subdirectories to ignore, without slashes (can be specified multiple times)")

    opt[Unit]('v', "verbose").action { (_, c) =>
      c.copy(verbose = true)
    }.text("show per-repo subtotals)")

    arg[String]("<yyyy-mm>").optional().action( (x, c) =>
      c.copy(yyyy_mm = x)
    ).text(s"year or month to search (defaults to the date for the previous month, for example $lastMonth)")

    help("help").text("prints this usage text")
  }
}
