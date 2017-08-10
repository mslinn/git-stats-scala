package com.micronautics

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern
import org.slf4j.Logger
import scala.sys.process._

package object gitStats {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger("gitStats")

  @inline def getOutputFrom(cwd: File, cmd: String*): String =
    try {
      run(cwd, cmd:_*).!!.trim
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
  /*TODO We don't need a default value here. Default dir for the tool is provided at the arg parsing level.
  * Default value means additional execution path and additional chance of a mistake.*/
  def gitProjectsUnder(file: File = new File(sys.props("user.dir"))): List[File] = {
    val childFiles = file.childFiles
    lazy val childDirs = file.childDirs
    if (childFiles.exists(_.isDotIgnore)) Nil else
      if (childDirs.exists(_.isDotGit)) {
        val files: List[File] = List(file.getCanonicalFile)
        print(".")
        files
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

  @inline protected def whichOrThrow(program: String): Path =
    which(program) match {
      case None => throw new Exception(program + " not found on path")
      case Some(programPath) => programPath
    }

  @inline def run(cwd: File, cmd: String*): ProcessBuilder = {
    val command: List[String] = whichOrThrow(cmd(0)).toString :: cmd.tail.toList
    println(s"[${ cwd.getAbsolutePath }] " + command.mkString(" "))
    logger.debug(command.mkString(" "))
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


    //TODO Used only in unused method
    //TODO Looks like bug: works only for "." directory
    @inline def shouldBeIgnored: Boolean = childFiles.contains(RichFile.dotIgnore)

    //TODO Unused
    @inline def gitSubdirectories: Seq[File] =
      if (shouldBeIgnored) Nil else
        for {
          childDir <- childDirs
          if childDir.childDirs.exists(_.getName == ".git")
        } yield childDir

    @inline def isDotIgnore: Boolean = file.getName == dotIgnore.getName
    @inline def isDotGit: Boolean    = file.getName == dotGit.getName

    //TODO Looks like bug: why set user _home_?
    //TODO There is no need in this method as we specify work dir every time we invoke git.
    @inline def setCwd(): String = System.setProperty("user.home", file.getAbsolutePath)
  }
}
