import com.micronautics.gitStats._

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) => new AllRepos(config).process

    case None => // arguments are bad, error message will have been displayed
  }
}

class AllRepos(config: ConfigGitStats) {
  def process: Commit = {
    val repos: List[Repo] =
      for {
        file <- gitProjectsUnder(config.directory)
      } yield new Repo(config, file)

    val repoSubtotals: List[Commit] = repos.map { repo =>
      try {
        repo.process
      } catch {
        case e: Throwable =>
          Console.err.println("Error: " + e.getMessage + " git repo ignored")
          Commit.zero
      }
    }

    val total: Commit = repoSubtotals.fold(Commit.zero) {
      case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
    }
    println()

    val summary: String = total.summarize(config.authorFullName, config.gitRepoName, finalTotal=true)
    println(summary)

    total
  }
}
