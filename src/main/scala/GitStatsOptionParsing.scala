import com.github.nscala_time.time.Imports._
import com.micronautics.gitStats.ConfigGitStats

trait GitStatsOptionParsing {
  import com.micronautics.gitStats.ConfigGitStats._

  val parser = new scopt.OptionParser[ConfigGitStats]("GitStats") {
    head("GitStats", "0.1.0")

    val ignoredDirectories: String = defaultValue.ignoredSubDirectories.mkString(",")
    val ignoredFileTypes: String = defaultValue.ignoredFileTypes.mkString(",")

    note(s"""For Linux and Mac, an executable program called git must be on the PATH;
            |for Windows, and executable called git.exe must be on the Path.
            |
            |Ignores files committed with these filetypes: $ignoredFileTypes.
            |Ignores directories committed called $ignoredDirectories.
            |
            |Tries to continue processing remaining git repos if an exception is encountered.
            |""".stripMargin)

    opt[Unit]('3', "previous30ays").action { (_, c) =>
      c.copy(dateFrom = Some(last30days), dateTo = Some(today))
    }.text(s"Same as specifying --from=$last30Formatted --to=$todayFormatted")

    opt[Unit]('9', "previous90ays").action { (_, c) =>
      c.copy(dateFrom = Some(last90days), dateTo = Some(today))
    }.text(s"Same as specifying --from=$last90Formatted --to=$todayFormatted")

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
    }.text(s"Same as specifying --from=$lastMonthFormatted --to=$todayFormatted")

    opt[String]('t', "to").action { (x, c) =>
      c.copy(dateTo = Some(new DateTime(x).withTimeAtStartOfDay))
    }.text("Last date to process, in yyyy-MM-dd format; default is no limit")

    opt[Unit]('v', "verbose").action { (_, c) =>
      c.copy(verbose = true)
    }.text("Show per-repo subtotals")

    opt[Unit]('y', "previous365days").action { (_, c) =>
      c.copy(dateFrom = Some(lastYear), dateTo = Some(today))
    }.text(s"Same as specifying --from=$lastYearFormatted --to=$todayFormatted")

    help("help").text("Print this usage text")
  }
}