package com.micronautics

import java.io.File
import org.slf4j.Logger
import scala.sys.process._

package object gitStats {
  val logger: Logger = org.slf4j.LoggerFactory.getLogger("gitStats")

  def getOutputFrom(cmd: String*): String = run(cmd:_*).!!.trim

  /** Handles special case where file points to a git directory, as well os a directory of git directories
    * @return List[File] where each item is the root of a git repo's directory tree */
  def gitProjectsUnder(file: File = new File(sys.env("WORK"))): List[File] = {
    val childDirs = file.childDirs
    if (childDirs.exists(_.isDotIgnore)) Nil else
      if (childDirs.exists(_.isDotGit)) List(file.getCanonicalFile) else
        childDirs.flatMap(gitProjectsUnder)
  }

  protected lazy val os: String = sys.props("os.name").toLowerCase

  protected def panderToWindows(command: Seq[String]): List[String] = os match {
    case x if x contains "windows" => List("cmd", "/C") ++ command.toList
    case _ => command.toList
  }

  def run(cmd: String*): ProcessBuilder = {
    val command: List[String] = panderToWindows(cmd)
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
