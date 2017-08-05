import com.micronautics.gitStats._
import com.github.nscala_time.time.Imports._
//import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object ConfigGitStats {
  val fmt: DateTimeFormatter  = DateTimeFormat.forPattern("yyyy-MM")
  val fmt2: DateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy")

  /** This only works if the current directory is the root of a git directory tree */
  lazy val gitRepoName: String = {
    val dir = sys.props("user.dir")
    val i: Int = dir.lastIndexOf(java.io.File.separator)
    if (i<0) dir else dir.substring(i).replace(java.io.File.separator, "")
  }

  /** This only works if the current directory is the root of a git directory tree */
  lazy val gitUserName: String =
    getOutputFrom("git", "config", "user.name")
      .replace(" ", "\\ ")

  val lastMonth: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1))
}

case class ConfigGitStats(
  author: String = ConfigGitStats.gitUserName,
  yyyy_mm: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1)),
  repoName: String = ConfigGitStats.gitRepoName
) {
  lazy val authorFullName: String = author.replace("\\", "")
  lazy val reportDate: DateTime = ConfigGitStats.fmt.parseDateTime(yyyy_mm)
  lazy val reportDateStr: String = ConfigGitStats.fmt2.print(reportDate)
}

trait GitStatsOptionParsing {
  import ConfigGitStats._

  val parser = new scopt.OptionParser[ConfigGitStats]("GitStats") {
    head("GitStats", "0.1.0")

    opt[String]('a', "author").action{ (x, c) =>
      c.copy(author = x)
    }.text("author to attribute")

    arg[String]("<yyyy-mm>").optional().action( (x, c) =>
      c.copy(yyyy_mm = x)
    ).text(s"yyyy_mm to search (defaults to the date for the previous month, $lastMonth)")
  }
}
