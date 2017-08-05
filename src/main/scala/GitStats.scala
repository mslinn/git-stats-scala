import com.micronautics.gitStats.RichFile.currentDirectory
import com.micronautics.gitStats._

object Commit {
  @inline def apply(args: String): Commit = {
    val stringArray = args.split("\t| ")
    val Array(linesAdded, linesDeleted, _ @ _*) = stringArray
    Commit(linesAdded.toInt, linesDeleted.toInt)
  }

  lazy val zero = Commit(0, 0)
}

case class Commit(added: Int, deleted: Int, directory: String="") {
  /** Number of net lines `(added - deleted)` */
  lazy val delta: Int = added - deleted

  def summarize(userName: String, repoName: String): String =
    s"$userName added $added lines, deleted $deleted lines, net $delta lines for $repoName"

  override def toString: String = s"Commit: added $added lines and deleted $deleted lines, net $delta lines"
}

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) =>
      val commits: List[Commit] = for {
        _ <- gitProjectsUnder(currentDirectory)
      } yield doIt(config)
      val total: Commit = commits.fold(Commit.zero) {
        case (acc, elem) => Commit(acc.added+elem.added, acc.deleted+elem.deleted)
      }
      val summary = total.summarize(config.authorFullName, config.repoName)
      logger.info(summary)
      total

    case None =>
      // arguments are bad, error message will have been displayed
  }

  def doIt(config: ConfigGitStats): Commit = {
    // git log --author="Mike Slinn" --pretty=tformat: --numstat
    // todo provide date range support
    val gitResponse: List[String] =
      getOutputFrom("git", "log", s"--author=${ config.author }", s"--pretty=tformat:", "--numstat")
        .split("\n")
        .toList
    logger.debug(gitResponse.mkString("\n"))

    val commits: List[Commit] = gitResponse.map(Commit.apply)
    val total: Commit = commits.fold(Commit.zero) {
      case (acc, elem) => Commit(acc.added+elem.added, acc.deleted+elem.deleted)
    }
    val summary = total.summarize(config.authorFullName, config.repoName)
    logger.info(summary)
    total
  }
}
