import java.text.NumberFormat

object Commit {
  lazy val unknown = "Unknown"

  lazy val zero = Commit(0, 0)

  val intFormatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  def intFormat(int: Int): String = intFormatter.format(int.toLong)

  @inline def apply(args: String): Commit = {
    args.split("\t| ") match {
      case Array(linesAdded, linesDeleted, fileName) =>
        Commit(linesAdded.toInt, linesDeleted.toInt, language=language(fileName))

      case Array(linesAdded, linesDeleted, oldFileName@_, arrow@_, newFileName) => // a file was renamed
        val language = if (newFileName.contains(".")) Commit.unknown else "Bash"
        Commit(linesAdded.toInt, linesDeleted.toInt, language=language)

      case _ =>
        throw new Exception(args)
    }
  }

  def contents(fileName: String): String = try {
    scala.io.Source.fromFile(fileName).mkString
  } catch {
    case _: Exception => ""
  }

  def language(fileName: String): String = fileName.toLowerCase match {
    case f if f.endsWith(".js") => "JavaScript"
    case f if f.endsWith(".scala") || f.endsWith(".sc") || f.endsWith(".repl") => "Scala"
    case f if f.endsWith(".sbt") => "SBT"
    case f if f.endsWith(".java") => "Java"
    case f if f.endsWith(".md") => "Markdown"
    case f if f.endsWith(".html") => "HTML"
    case f if f.endsWith(".properties") => "Properties"
    case f if f.endsWith(".xml") => "XML"
    case f if f.startsWith(".") => "Miscellaneous"
    case f if contents(f).startsWith("#!/bin/bash") => "Bash shell"
    case _ => unknown
  }
}

case class Commit(added: Int, deleted: Int, fileName: String="", language: String=Commit.unknown) {
  import Commit._

  /** Number of net lines `(added - deleted)` */
  lazy val delta: Int = added - deleted

  def summarize(userName: String, repoName: String, finalTotal: Boolean = false, displayLanguageInfo: Boolean = true): String = {
    val forLang = if (!displayLanguageInfo || finalTotal) "" else s" for language '$language'"
    val forRepo = if (finalTotal) " in all git repositories" else s" in $repoName"
    s"$userName $this"
  }

  override def toString: String = {
    val forLang = s" for language '$language'"
    val forRepo = " in this git repository"
    s"added ${ intFormat(added) } lines and deleted ${ intFormat(deleted) } lines, net ${ intFormat(delta) } lines$forLang$forRepo"
  }
}
