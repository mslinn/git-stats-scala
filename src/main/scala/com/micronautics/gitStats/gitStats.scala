package com.micronautics

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern
import org.slf4j.Logger
import scala.sys.process._

package object gitStats {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger("gitStats")

  def getOutputFrom(cmd: String*): String =
    try {
      run(cmd:_*).!!.trim
    } catch {
      case e: Exception =>
        Console.err.println(e.getMessage)
        Console.err.println(e.getStackTrace.mkString("\n"))
        sys.exit(-1)
    }

  lazy val gitProgram: String = if (isWindows) "git.exe" else "git"

  /** Handles special case where file points to a git directory, as well os a directory of git directories
    * @return List[File] where each item is the root of a git repo's directory tree */
  def gitProjectsUnder(file: File = new File(sys.props("user.dir"))): List[File] = {
    val childDirs = file.childDirs
    if (childDirs.exists(_.isDotIgnore)) Nil else
      if (childDirs.exists(_.isDotGit)) {
        val x = List(file.getCanonicalFile)
        print(".")
        x
      } else
        childDirs.flatMap(gitProjectsUnder)
  }

  lazy val isWindows: Boolean = sys.props("os.name").toLowerCase.indexOf("win") >= 0

  protected lazy val os: String = sys.props("os.name").toLowerCase

  protected def which(program: String): Option[Path] = {
    val path = if (isWindows) sys.env("Path") else sys.env("PATH")
    path
      .split(Pattern.quote(File.pathSeparator))
      .map(Paths.get(_))
      .find(path => Files.exists(path.resolve(program)))
      .map(_.resolve(program))
  }

  protected def whichOrThrow(program: String): Path =
    which(program) match {
      case None => throw new Exception(program + " not found on path")
      case Some(programPath) => programPath
    }

  def run(cmd: String*): ProcessBuilder = {
    val command: List[String] = whichOrThrow(cmd(0)).toString :: cmd.tail.toList
    logger.debug(command.mkString(" "))
    Process(command)
  }

  object RichFile {
    def currentDirectory: File = new File(".").getCanonicalFile

    val dotIgnore: File = new File(".", ".ignore")
    val dotGit: File    = new File(".", ".git")

    def parentDirectory: File = currentDirectory.getParentFile
  }

  implicit class RichFile(val file: File) extends AnyVal {
    import RichFile._

    def childFiles: List[File] = file.listFiles.toList

    def childDirs: List[File] = childFiles.filter(_.isDirectory)

    def shouldBeIgnored: Boolean = childFiles.contains(RichFile.dotIgnore)

    def gitSubdirectories: Seq[File] =
      if (shouldBeIgnored) Nil else
        for {
          childDir <- childDirs
          if childDir.childDirs.exists(_.getName == ".git")
        } yield childDir

    def isDotIgnore: Boolean = file.getName == dotIgnore.getName
    def isDotGit: Boolean    = file.getName == dotGit.getName

    def setCwd(): String = System.setProperty("user.home", file.getAbsolutePath)
  }
}
