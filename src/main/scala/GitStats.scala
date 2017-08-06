import com.micronautics.gitStats._
import scala.collection.mutable

class LanguageTotals(val map: mutable.Map[String, Commit] = mutable.Map.empty.withDefaultValue(Commit.zero)) {
  def combine(commit: Commit): Unit = {
    val value = map(commit.language)
    val updated = Commit(
      added = value.added + commit.added,
      deleted = value.deleted + commit.deleted,
      language = commit.language
    )
    map.put(commit.language, updated)
    ()
  }
}

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) => processAllRepos(config)

    case None => // arguments are bad, error message will have been displayed
  }

  private def processAllRepos(config: ConfigGitStats) = {
    val commits: List[Commit] = for {
      file <- gitProjectsUnder(config.directory)
    } yield {
      println()
      file.setCwd()
      processOneRepo(config)
    }
    val total: Commit = commits.fold(Commit.zero) {
      case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
    }
    println()
    //      val summary = total.summarize(config.authorFullName, config.gitRepoName, finalTotal=true)
    //      println(summary)
    total
  }

  /** Process repo at current directory */
  def processOneRepo(config: ConfigGitStats): Commit = {
    // git log --author="Mike Slinn" --pretty=tformat: --numstat
    val gitResponse: List[String] =
      getOutputFrom("git", "log", s"--author=${ config.author }", s"--pretty=tformat:", "--numstat")
        .split("\n")
        .filter(_.nonEmpty)
        .toList

    logger.debug(gitResponse.mkString("\n"))

    val commits: List[Commit] =
      gitResponse
        .map(Commit.apply)
        .filterNot(_.fileName.endsWith(".log"))

    val languageTotals = new LanguageTotals

    val grandTotalCommit: Commit = commits.fold(Commit.zero) {
      case (acc, elem) =>
        languageTotals.combine(elem)
        val newTotal = Commit(acc.added+elem.added, acc.deleted+elem.deleted, language = elem.language)
        newTotal
    }

    val detailCommits: List[Commit] = languageTotals
                                        .map
                                        .values
                                        .toList
                                        .sortBy(x => -x.delta)
    if (config.verbose) {
      detailCommits.foreach { v => println(v.summarize(config.authorFullName, config.gitRepoName)) }
      println()  // separate repos with a blank line
    }
    println(grandTotalCommit.summarize(config.authorFullName, config.gitRepoName, displayLanguageInfo=false))
    grandTotalCommit
  }
}
