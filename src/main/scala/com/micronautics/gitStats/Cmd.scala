package com.micronautics.gitStats

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern

import scala.sys.process.{Process, ProcessBuilder}

/**
  * Command line utilities.
  */
//TODO Refactor to trait and derive SvnCmd and GitCmd from it?
object Cmd {

  protected lazy val os: String = sys.props("os.name").toLowerCase

  lazy val isWindows: Boolean = os.indexOf("win") >= 0

  @inline def getOutputFrom(cwd: Path, cmd: String*)
                           (implicit config: ConfigGitStats): String =
    try {
      val result = run(cwd, cmd: _*).!!.trim
      if (config.output)
        println(result)
      result
    } catch {
      case e: Exception =>
        Console.err.println(e.getMessage)
        Option(e.getCause).map(_.toString).foreach { cause =>
          if (cause.nonEmpty)
            Console.err.println(cause)
        }
        Console.err.println(e.getStackTrace.mkString("\n"))
        throw e
    }

  @inline def run(cwd: Path, cmd: String*)(implicit config: ConfigGitStats): ProcessBuilder = {
    val command: List[String] = whichOrThrow(cmd(0)).toString :: cmd.tail.toList
    if (config.verbose)
      println(s"[${cwd.toAbsolutePath}] " + command.mkString(" "))
    Process(command = command, cwd = cwd.toFile)
  }

  @inline protected def whichOrThrow(program: String): Path =
    which(program) match {
      case None => throw new Exception(program + " not found on path")
      case Some(programPath) => programPath
    }

  protected def which(program: String): Option[Path] = {
    val path = if (isWindows) sys.env("Path") else sys.env("PATH")
    path
      .split(Pattern.quote(File.pathSeparator))
      .map(Paths.get(_))
      .find(path => Files.exists(path.resolve(program)))
      .map(_.resolve(program))
  }

}
