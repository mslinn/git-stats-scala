import com.github.nscala_time.time.Imports._
import com.micronautics.gitStats._
import org.joda.time.DateTime
import scala.language.postfixOps
import scala.sys.process._

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) => doIt(config)

    case None =>
      // arguments are bad, error message will have been displayed
  }

  def doIt(config: ConfigGitStats): Unit = {
    // git log --author="Mike Slinn" --pretty=tformat: --numstat
    val gitData = run("git", config.yyyy_mm, config.author) !

    val reportDate: DateTime = ConfigGitStats.fmt.parseDateTime(config.yyyy_mm)
    val reportDateStr: String = ConfigGitStats.fmt2.print(reportDate)
    ()
  }
}
