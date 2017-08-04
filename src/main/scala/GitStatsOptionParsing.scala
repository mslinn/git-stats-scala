import com.micronautics.gitStats.run
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object ConfigGitStats {
  val fmt: DateTimeFormatter  = DateTimeFormat.forPattern("yyyy-MM")
  val fmt2: DateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy")

  /** This only works if the current directory is the root of a git directory tree */
  def gitUserName: String =
    run("git", "config", "user.name")
      .!!
      .trim
      .replace(" ", "\\ ")

  val lastMonth: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1))
}

case class ConfigGitStats(
  author: String = ConfigGitStats.gitUserName,
  yyyy_mm: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1))
) {
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
