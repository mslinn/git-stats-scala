import com.micronautics.gitStats.ProjectDir._
import com.micronautics.gitStats.svn.SvnStats
import com.micronautics.gitStats.{ConfigGitStats, git}

object ProgStats extends App with GitStatsOptionParsing {

  //TODO Don't forget remove this hardcode (added for tests)
  parser.parse(args, ConfigGitStats(verbose = true, directoryName = "/work/workspace", dateFrom = Some(ConfigGitStats.last30days))) match {
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
    val svnCommits = SvnStats.commits(scmProjectDirs)
    val allCommits = gitCommits ++ svnCommits
  }
}
