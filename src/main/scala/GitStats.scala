import java.io.File
import com.micronautics.gitStats._
import scala.collection.mutable

object Commit {
  lazy val unknown = "Unknown"

  lazy val zero = Commit(0, 0)

  @inline def apply(args: String): Commit = {
    args.split("\t| ") match {
      case Array(linesAdded, linesDeleted, fileName) =>
        Commit(linesAdded.toInt, linesDeleted.toInt, language=language(fileName))

      case Array(linesAdded, linesDeleted, oldFileName, arrow, newFileName) =>
        Commit(linesAdded.toInt, linesDeleted.toInt, language=language(newFileName.replace("}", "")))

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
    case f if f.endsWith(".scala") || f.endsWith(".sc") => "Scala"
    case f if f.endsWith(".java") => "Java"
    case f if f.endsWith(".md") => "Markdown"
    case f if f.endsWith(".html") => "HTML"
    case f if contents(f).startsWith("#!/bin/bash") => "Bash shell"
    case _ => unknown
  }
}

case class Commit(added: Int, deleted: Int, fileName: String="", language: String=Commit.unknown) {
  /** Number of net lines `(added - deleted)` */
  lazy val delta: Int = added - deleted

  def summarize(userName: String, repoName: String, suppressLanguageDisplay: Boolean = false): String = {
    val forLang = if (suppressLanguageDisplay) "" else s"for language '$language' "
    s"$userName added $added lines, deleted $deleted lines, net $delta lines ${forLang}in $repoName"
  }

  override def toString: String = s"Commit: added $added lines and deleted $deleted lines, net $delta lines"
}

class LanguageTotals(val map: mutable.Map[String, Commit] = mutable.Map.empty.withDefaultValue(Commit.zero)) {
  def combine(commit: Commit): Unit = {
    val value = map(commit.language)
    val updated = Commit(
      added = value.added + commit.added,
      deleted = value.deleted + commit.deleted,
      language = commit.language
    )
    map.put(commit.language, updated)
    ()
  }
}

object GitStats extends App with GitStatsOptionParsing {
  parser.parse(args, ConfigGitStats()) match {
    case Some(config) => doIt(config)

    case None =>
      // arguments are bad, error message will have been displayed
  }

  def doIt(config: ConfigGitStats): Unit = {
    // git log --author="Mike Slinn" --pretty=tformat: --numstat
    // todo provide date range support
    val gitResponse: List[String] =
      getOutputFrom("git", "log", s"--author=${ config.author }", s"--pretty=tformat:", "--numstat")
        .split("\n")
        .toList
    logger.debug(gitResponse.mkString("\n"))

    val commits: List[Commit] = gitResponse.map(Commit.apply)
    val languageTotals = new LanguageTotals
    val grandTotal: Commit = commits.fold(Commit.zero) {
      case (acc, elem) =>
        languageTotals.combine(elem)
        val newTotal = Commit(acc.added+elem.added, acc.deleted+elem.deleted, language = elem.language)
        newTotal
    }
    languageTotals.map.values.foreach { v => println(v.summarize(config.authorFullName, config.repoName)) }
    println(grandTotal.summarize(config.authorFullName, config.repoName, suppressLanguageDisplay=true))
  }
}
