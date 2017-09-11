import com.micronautics.gitStats.AggCommit.aggregateByLanguage
import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.svn.SvnStats
import com.micronautics.gitStats.{ConfigGitStats, git}

import scala.util.{Failure, Success}

object ProgStats extends App with GitStatsOptionParsing {

  parser.parse(args,
    ConfigGitStats()
    //TODO Don't forget remove this hardcode (added for tests)
//    ConfigGitStats(verbose = true, directoryName = "/work/workspace", dateFrom = Some(ConfigGitStats.last30days))
  ) match {
    case Some(config) => process(config)
    case None => // arguments are bad, error message will have been displayed
  }

  protected def process(implicit config: ConfigGitStats): Unit = {
    val scmProjectDirs = findScmProjectDirs(config.directory.toPath)
    if (config.verbose) {
      val dirsReport = scmProjectDirs.mkString("Found SCM project directories:\n", "\n", "\n")
      println(dirsReport)
    }

    val gitCommits = git.GitStats.commits(scmProjectDirs)
    //TODO Run Subversion stats only when user asked for it. It is much slower than Git stats.
    val svnCommits = SvnStats.commits(scmProjectDirs)
    val (perProjectCommits, perProjectFailures) = (gitCommits ++ svnCommits).partition { case (_, t) => t.isSuccess }
    val allProjectCommits = perProjectCommits.flatMap {
      case (_, Success(projectCommits)) => projectCommits
      case _ => Iterator.empty
    }
    val allByLanguage = aggregateByLanguage(allProjectCommits).toList.sortBy(-_.netChange)
    println(allByLanguage.mkString("=== All commits grouped by language ===\n", "\n", "\n========================"))

    //TODO Pretty failure report
    perProjectFailures
      .collect { case (workDir, Failure(e)) => (workDir, e) }
      .foreach { case (workDir, e) =>
        println(s"Directory ${workDir}: ${e.getMessage}")
      }
  }
}
