import java.io.File
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
    } yield try {
      processOneRepo(config, file)
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

  /** Process repo at current directory */
  private def processOneRepo(config: ConfigGitStats, dir: File): Commit = {
    assert(config.dateFrom.isBefore(config.dateTo))
    val fromOption: String = s"--since={${ config.from }}"
    val toOption: String   = s"--until={${ config.to }}"

    dir.setCwd()
    println()

    // git log --author="Mike Slinn" --pretty=tformat: --numstat --previous365days
    // git log --author="Mike Slinn" --pretty=tformat: --numstat --since={2016-09-01} --until={2017-08-30}
    val gitResponse: List[String] =
      getOutputFrom(dir, gitProgram, "log", s"--author=${ config.author }", s"--pretty=tformat:", "--numstat", fromOption, toOption)
        .split("\n")
        .filter(_.nonEmpty)
        .toList

    logger.debug(gitResponse.mkString("\n"))

    val commits: List[Commit] =
      gitResponse
        .map(Commit.apply)
        .filterNot(commit => config.ignoredFileTypes.contains(commit.fileType))
        .filterNot(commit => config.ignoredSubDirectories.contains(commit.lastFilePath))

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
