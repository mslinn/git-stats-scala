import com.micronautics.gitStats._
import com.micronautics.gitStats.Output.formatCommits

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) => new AllRepos(config).process()

    case None => // arguments are bad, error message will have been displayed
  }
}

/** Walk through all repos and process them */
class AllRepos(config: ConfigGitStats) {

  /** Generates text output on stdout */
  def process(): Unit = {
    lazy val repos: List[Repo] =
      for {
        file <- gitProjectsUnder(config.directory)
      } yield new Repo(config, file)

    /** Each [[Commit]] returned is actually a summary of related `Commit`s */
    lazy val repoSubtotals: List[Commit] = repos.map { repo =>
      try {
        repo.process()
      } catch {
        case e: Throwable =>
          Console.err.println(s"${ e.getClass.getSimpleName }, ignoring git repo at ${ repo.dir }")
          Commit.zero
      }
    }

    lazy val languageTotals: List[Commit] =
      repoSubtotals
        .groupBy(_.language)
        .map { case (_, values) =>
          val commit0: Commit = values.head
          Commit(added=values.map(_.added).sum, deleted=values.map(_.deleted).sum, fileName=commit0.fileName, language=commit0.language)
        }
        .toList
        .sortBy(x => (-x.added, -x.deleted))

    lazy val grandTotal: Commit = total(languageTotals)

    println(formatCommits(
      userName = config.authorFullName,
      title = "Language Subtotals (lines changed across all projects)",
      commits = languageTotals,
      grandTotals = grandTotal
    ))

    def total(commits: List[Commit]): Commit = commits.fold(Commit.zero) {
      case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
    }

    ()
  }
}
