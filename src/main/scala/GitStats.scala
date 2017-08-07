import com.micronautics.gitStats._

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) => processAllRepos(config)

    case None => // arguments are bad, error message will have been displayed
  }

  private def processAllRepos(config: ConfigGitStats) = {
    val commits: List[Commit] = for {
      file <- gitProjectsUnder(config.directory)
    } yield try {
      new Repo(config, file).process
    } catch {
      case e: Throwable =>
        Console.err.println("Error: " + e.getMessage + " git repo ignored")
        Commit.zero
    }

    val total: Commit = commits.fold(Commit.zero) {
      case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
    }
    println()
    val summary = total.summarize(config.authorFullName, config.gitRepoName, finalTotal=true)
    println(summary)
    total
  }
}
