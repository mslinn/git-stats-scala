import com.github.nscala_time.time.Imports._
import com.micronautics.gitStats._
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
    // todo provide date range support
    val gitResponse: List[String] =
      run("git", "log", s"--author=${ config.author }", s"--pretty=tformat:", "--numstat")
        .!!
        .trim
        .split("\n")
        .toList
    logger.info(gitResponse.mkString("\n"))
  }
}
