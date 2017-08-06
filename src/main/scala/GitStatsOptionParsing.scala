import java.io.File
import com.micronautics.gitStats._
import com.github.nscala_time.time.Imports._
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object ConfigGitStats {
  val fmt: DateTimeFormatter  = DateTimeFormat.forPattern("yyyy-MM")
  val fmt2: DateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy")

  /** This only works if the current directory is the root of a git directory tree */
  @inline def gitUserName(cwd: File): String = {
    val userName = getOutputFrom(cwd, gitProgram, "config", "user.name")
    if (isWindows) "\"" + userName + "\"" else userName.replace(" ", "\\ ")
  }

  val lastMonth: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1))
}

case class ConfigGitStats(
  author: String = ConfigGitStats.gitUserName(new File(".").getAbsoluteFile),
  yyyy_mm: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1)),
  directoryName: String = sys.props("user.dir"),
  verbose: Boolean = false,
  ignoredFileTypes: List[String] = List("exe", "gif", "gz", "jpg", "log", "png", "pdf", "tar", "zip"),
  ignoredSubDirectories: List[String] = List("node_modules")
) {
  lazy val authorFullName: String = author.replace("\\", "")
  lazy val reportDate: DateTime = ConfigGitStats.fmt.parseDateTime(yyyy_mm)
  lazy val reportDateStr: String = ConfigGitStats.fmt2.print(reportDate)

  lazy val directory = new java.io.File(directoryName)

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
    ).text(s"year or month to search (defaults to the date for the previous month, $lastMonth)")

    help("help").text("prints this usage text")
  }
}
