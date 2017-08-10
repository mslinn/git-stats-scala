import com.github.nscala_time.time.Imports._
import com.micronautics.gitStats.ConfigGitStats

trait GitStatsOptionParsing {
  import com.micronautics.gitStats.ConfigGitStats._

  val parser = new scopt.OptionParser[ConfigGitStats]("GitStats") {
    head("GitStats", "0.1.0")

    val ignoredDirectories: String = defaultValue.ignoredSubDirectories.sorted.mkString(", ")
    val ignoredFileTypes: String = defaultValue.ignoredFileTypes.sorted.mkString(", ")

    note(s"""For Linux and Mac, an executable program called git must be on the PATH;
            |for Windows, and executable called git.exe must be on the Path.
            |
            |Ignores files committed with these filetypes: $ignoredFileTypes.
            |Ignores directories committed called $ignoredDirectories.
            |
            |Tries to continue processing remaining git repos if an exception is encountered.
            |""".stripMargin)

    opt[Unit]('3', "prev-30").action { (_, c) =>
      c.copy(dateFrom = Some(last30days), dateTo = Some(today))
    }.text(s"Process the most recent 30 days; same as specifying --from=$last30Formatted --to=$todayFormatted")

    opt[Unit]('9', "prev-90").action { (_, c) =>
      c.copy(dateFrom = Some(last90days), dateTo = Some(today))
    }.text(s"Process the most recent 90 days; same as specifying --from=$last90Formatted --to=$todayFormatted")

    opt[String]('d', "dir").action { (x, c) =>
      c.copy(directoryName = x)
    }.text("Directory to scan (defaults to current directory)")

    opt[String]('f', "from").action { (x, c) =>
      c.copy(dateFrom = Some(new DateTime(x).withTimeAtStartOfDay))
    }.text("First date to process, in yyyy-MM-dd format; default is no limit")

    opt[String]('i', "ignore").action { (x, c) =>
      val c2 = c.copy(ignoredFileTypes = (x.split(",").toList ::: c.ignoredFileTypes).distinct.sorted)
      c2
    }.text("Comma-separated additional filetypes to ignore, without the leading dot")

    opt[String]('I', "Ignore").action { (x, c) =>
      c.copy(ignoredSubDirectories = (x.split(",").toList ::: c.ignoredSubDirectories).distinct.sorted)
    }.text("Comma-separated additional subdirectories to ignore, without slashes")

    opt[Unit]('m', "prev-month").action { (_, c) =>
      c.copy(dateFrom = Some(lastMonth), dateTo = Some(today))
    }.text(s"Process the most recent complete month's data; same as specifying --from=$lastMonthFormatted --to=$todayFormatted")

    opt[Unit]('o', "only-known").action { (_, c) =>
      c.copy(onlyKnown = true)
    }.text(s"If a filetype is not recognized, ignore it's data when summarizing commits; filters out Unknown and Miscellaneous filetypes")

    opt[Unit]('s', "subtotals").action { (_, c) =>
      c.copy(subtotals = true)
    }.text("Show per-repo subtotals")

    opt[String]('t', "to").action { (x, c) =>
      c.copy(dateTo = Some(new DateTime(x).withTimeAtStartOfDay))
    }.text("Last date to process, in yyyy-MM-dd format; default is no limit")

    opt[Unit]('v', "verbose").action { (_, c) =>
      c.copy(verbose = true)
    }.text("Show OS commands executed and dots indicating progress")

    opt[Unit]('y', "prev-365").action { (_, c) =>
      c.copy(dateFrom = Some(lastYear), dateTo = Some(today))
    }.text(s"Same as specifying --from=$lastYearFormatted --to=$todayFormatted")

    help("help").text("Print this usage text")
  }
}
