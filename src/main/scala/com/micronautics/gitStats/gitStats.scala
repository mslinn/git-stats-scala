package com.micronautics

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern
import org.slf4j.Logger
import scala.sys.process._

package object gitStats {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger("gitStats")

  @deprecated("TODO Use Cmd object instead")
  @inline def getOutputFrom(cwd: File, cmd: String*)
                           (implicit config: ConfigGitStats): String =
    try {
      val result = run(cwd, cmd:_*).!!.trim
      if (config.output) println(result)
      result
    } catch {
      case e: Exception =>
        Console.err.println(e.getMessage)
        if (e.getCause.toString.nonEmpty) Console.err.println(e.getCause)
        Console.err.println(e.getStackTrace.mkString("\n"))
        sys.exit(-1)
    }

  lazy val gitProgram: String = if (isWindows) "git.exe" else "git"

  /** Handles special case where file points to a git directory, as well os a directory of git directories
    * @return List[File] where each item is the root of a git repo's directory tree */
  def gitProjectsUnder(file: File)
                      (implicit config: ConfigGitStats): List[File] = {
    val childFiles = file.childFiles
    lazy val childDirs = file.childDirs
    if (childFiles.exists(_.isDotIgnore)) Nil else
      if (childDirs.exists(_.isDotGit)) {
        val files: List[File] = List(file.getCanonicalFile)
        if (config.verbose) print(".")
        files
      } else
        childDirs.flatMap(gitProjectsUnder)
  }

  @deprecated("TODO Use Cmd object instead")
  protected lazy val os: String = sys.props("os.name").toLowerCase

  @deprecated("TODO Use Cmd object instead")
  lazy val isWindows: Boolean = os.indexOf("win") >= 0

  @deprecated("TODO Use Cmd object instead")
  protected def which(program: String): Option[Path] = {
    val path = if (isWindows) sys.env("Path") else sys.env("PATH")
    path
      .split(Pattern.quote(File.pathSeparator))
      .map(Paths.get(_))
      .find(path => Files.exists(path.resolve(program)))
      .map(_.resolve(program))
  }

  @deprecated("TODO Use Cmd object instead")
  @inline protected def whichOrThrow(program: String): Path =
    which(program) match {
      case None => throw new Exception(program + " not found on path")
      case Some(programPath) => programPath
    }

  @deprecated("TODO Use Cmd object instead")
  @inline def run(cwd: File, cmd: String*)(implicit config: ConfigGitStats): ProcessBuilder = {
    val command: List[String] = whichOrThrow(cmd(0)).toString :: cmd.tail.toList
    if (config.verbose) println(s"[${ cwd.getAbsolutePath }] " + command.mkString(" "))
    Process(command=command, cwd=cwd)
  }

  object RichFile {
    @inline def currentDirectory: File = new File(".").getCanonicalFile

    val dotIgnore: File = new File(".", ".ignore.stats")
    val dotGit: File    = new File(".", ".git")

    def parentDirectory: File = currentDirectory.getParentFile
  }

  implicit class RichFile(val file: File) extends AnyVal {
    import RichFile._

    @inline def childFiles: List[File] = {
      val files: List[File] = try { // File.listFiles can return null, so deal with it:
        Option(file.listFiles).toList.flatMap(_.toList)
      } catch {
        case e: Exception =>
          logger.error(e.getMessage)
          Console.err.println(s"${ e.getMessage } ${ e.getCause } ${ e.getStackTrace.mkString("\n") }")
          Nil
      }
      files
    }

    @inline def childDirs: List[File] = childFiles.filter(_.isDirectory)

    @inline def isDotIgnore: Boolean = file.getName == dotIgnore.getName
    @inline def isDotGit: Boolean    = file.getName == dotGit.getName
  }
}
