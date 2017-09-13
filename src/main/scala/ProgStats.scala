import java.nio.file.Path

import com.micronautics.gitStats.AggCommit.{AggCommits, _}
import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.render.AsciiRenderer
import com.micronautics.gitStats.svn.SvnStats
import com.micronautics.gitStats.{ConfigGitStats, git}

import scala.util.{Failure, Success}

object ProgStats extends App with GitStatsOptionParsing {

  parser.parse(args,
    ConfigGitStats()
  ) match {
    case Some(config) => process(config)
    case None => // arguments are bad, error message will have been displayed
  }

  protected def process(implicit config: ConfigGitStats): Unit = {
    val scmProjectDirs = findScmProjectDirs(config.directory.toPath)
    if (config.verbose) {
      val dirsReport = scmProjectDirs.mkString(s"Found SCM project directories (${scmProjectDirs.size}):\n", "\n", "\n")
      println(dirsReport)
    }

    //TODO commits() return ProjectDir -> Iterable instead of Path -> Iterable?
    val gitCommits = git.GitStats.commits(scmProjectDirs)
    val svnCommits = if (config.remote) SvnStats.commits(scmProjectDirs) else Nil
    val (successes, failures) = (gitCommits ++ svnCommits).partition { case (_, t) => t.isSuccess }

    val perProjectCommits: Iterable[(Path, AggCommits)] = successes.flatMap {
      case (workDir, Success(projectCommits)) => Iterable((workDir, projectCommits))
      case _ => Iterable.empty
    }
    reportCommits(perProjectCommits)
    config.excelWorkbook.foreach(_.save())

    val perProjectFailures = failures.collect { case (workDir, Failure(e)) => (workDir, e) }
    reportFailures(perProjectFailures)
  }

  protected def reportCommits(perProjectCommits: Iterable[(Path, AggCommits)])(implicit config: ConfigGitStats): Unit = {
    val asciiRenderer = new AsciiRenderer()
    val projectNumber = perProjectCommits.size
    val allCommits = perProjectCommits.flatMap(_._2)

    println(asciiRenderer.headline(perProjectCommits))

    if (config.subtotals) {
      reportProjectSubtotals(perProjectCommits, asciiRenderer)
    }

    if (projectNumber > 1 || !config.subtotals) {
      val allByLanguage = aggregateByLanguage(allCommits)
      if (allByLanguage.isEmpty) {
        println(s"No activity across $projectNumber projects.")
      } else {
        val projects = if (perProjectCommits.size > 1) s" (lines changed across $projectNumber projects)" else ""
        if (config.excelWorkbook.isDefined)
          config.excelWorkbook.foreach(_.addSheetOfCommits(s"Subtotals By Language$projects", allByLanguage))
        else
          println(asciiRenderer.table(s"Subtotals By Language$projects", allByLanguage))
      }
    }
  }

  //TODO Make renderer a member
  protected def reportProjectSubtotals(perProjectCommits: Iterable[(Path, AggCommits)], asciiRenderer: AsciiRenderer)(implicit config: ConfigGitStats): Unit = {
    val perProjectSubtotals = perProjectCommits.map { case (dir, commits) => (dir, aggregateByLanguage(commits)) }
    perProjectSubtotals.foreach {
      case (dir, commits) =>
        if (commits.nonEmpty)
          if (config.excelWorkbook.isDefined)
            config.excelWorkbook.foreach(_.addSheetOfCommits(dir.toFile.getName, commits))
          else
            println(asciiRenderer.table(dir.toAbsolutePath.toString, commits))
    }
  }

  //TODO Pretty failure report
  protected def reportFailures(perProjectFailures: Iterable[(Path, Throwable)]): Unit = {
    perProjectFailures.foreach { case (workDir, e) =>
      println(s"Directory $workDir: ${e.getMessage}")
    }
  }
}
