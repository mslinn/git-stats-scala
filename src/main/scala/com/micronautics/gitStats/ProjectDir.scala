package com.micronautics.gitStats

import java.nio.file.{Files, Path, Paths}

//TODO Do we really need this trait? Maybe remove.
trait ProjectDir {

  val aggCommits: Iterable[AggCommit]
}

object ProjectDir {

  /**
    * Finds project directories under the given directory.
    * A directory is considered as SCM project directory when:
    * <ol>
    * <li> it has an SCM-specific directory in it, like .git or .svn
    * <li> it does not have ignore marker file in it
    * </ol>
    *
    * @param baseDir Base directory.
    * @param config Predicate to recognize project directories.
    * @return Collection of SCM project directories.
    * @throws IllegalArgumentException Base directory is null.
    */
  def findScmProjectDirs(baseDir: Path)(implicit config: ConfigGitStats): Iterable[Path] = {
    require(baseDir != null, "Base directory must not be null")

    val children = baseDir.listChildren
    if (children.exists(_.isIgnoreMarker))
      Iterable.empty
    else if (children.exists(_.isScmDir)) {
      if (config.verbose)
        print(".")
      Iterable(baseDir)
    } else
      children.flatMap(findScmProjectDirs)
  }

  //TODO Move to the top level?
  implicit class RichPath(val path: Path) extends AnyVal {
    import RichPath._

    @inline
    def listChildren: Iterable[Path] =
      try {
        /* Using File.listFiles(),
     * because java.nio.file.Files.list(Path) may lead to "too many open files" exception
     * when scanning deep directories recursively. */
        Option(path.toFile.listFiles())
          .toIterable
          .flatMap(_.iterator)
          .map(_.toPath)
      } catch {
        case e: Exception =>
          Console.err.println(s"${e.getMessage} ${e.getCause} ${e.getStackTrace.mkString("\n")}")
          Nil
      }

    @inline
    def fileSuffix: Option[String] = {
      val idx = path.toString.lastIndexOf(".")
      if (idx < 0) None
      else Some(path.toString.substring(idx + 1))
    }

    @inline
    def isIgnoreMarker: Boolean = Files.isRegularFile(path) && path.endsWith(ignoreMarker)

    @inline
    def isScmDir: Boolean = isDotGit || isDotSvn

    @inline
    def isDotSvn: Boolean = Files.isDirectory(path) && path.endsWith(dotSvn)

    @inline
    def isDotGit: Boolean = Files.isDirectory(path) && path.endsWith(dotGit)

    @inline
    def isGitRepo: Boolean = Files.isDirectory(path) && listChildren.exists(_.isDotGit)

    @inline
    def isSvnWorkDir: Boolean = Files.isDirectory(path) && listChildren.exists(_.isDotSvn)
  }

  object RichPath {
    lazy val ignoreMarker: Path = Paths.get(".ignore.stats")
    lazy val dotSvn: Path = Paths.get(".svn")
    lazy val dotGit: Path = Paths.get(".git")
  }

}
