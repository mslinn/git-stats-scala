import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object ConfigGitStats {
  val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM")
  val fmt2: DateTimeFormatter = DateTimeFormat.forPattern("MMMM yyyy")

  val lastMonth: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1))
}

case class ConfigGitStats(
  author: String = sys.env("user.name"),
  yyyy_mm: String = ConfigGitStats.fmt.print(DateTime.now.minusMonths(1))
)

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
