package com.micronautics.gitStats

import java.text.NumberFormat

object Commit {
  lazy val unknown = "Unknown"
  lazy val miscellaneous = "Miscellaneous"

  lazy val zero = Commit(0, 0)

  val intFormatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  @inline def sign(x: Int): String = x match {
    case _ if x>0 => "+"
    case _ => ""
  }

  @inline def intFormat(int: Int): String = sign(int) + intFormatter.format(int.toLong)

  @inline def toInt(string: String): Int = if (string=="-") 0 else string.toInt

  @inline def apply(args: String): Commit = {
    args.split("\t| ") match {
      case Array(linesAdded, linesDeleted, fileName) =>
        Commit(toInt(linesAdded), toInt(linesDeleted), language=language(fileName.trim), fileName=fileName)

      case Array(linesAdded, linesDeleted, oldFileName@_, arrow@_, newFileName) => // a file was renamed
        val language = if (newFileName.contains(".")) Commit.unknown else "Bash"
        Commit(toInt(linesAdded), toInt(linesDeleted), language=language, fileName=newFileName)

      case _ =>
        Commit(0, 0, language=unknown)
    }
  }

  @inline def contents(fileName: String): String = try {
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
    case f if f.startsWith(".") => miscellaneous
    case f if contents(f).startsWith("#!/bin/bash") => "Bash shell"
    case _ => unknown
  }
}

case class Commit(added: Int, deleted: Int, fileName: String="", language: String=Commit.unknown) {
  import com.micronautics.gitStats.Commit._

  /** Number of net lines `(added - deleted)` */
  lazy val delta: Int = added - deleted

  /** Filetype of fileName, not including the dot */
  lazy val fileType: String = {
    val i = fileName.lastIndexOf(".")
    if (i<0) fileName else fileName.substring(i+1)
  }

  lazy val hasUnknownLanguage: Boolean = language==unknown || language==miscellaneous

  lazy val lastFilePath: String = {
    val array = fileName.split(java.io.File.separator)
    if (array.size<2) fileName else array.takeRight(2).head
  }

  @inline def format(grandTotals: Commit=Commit.zero): List[String] =
    (if (grandTotals==Commit.zero) Nil else List(language)) :::
      List(intFormat(added), intFormat(-deleted), intFormat(delta))

  @inline def summarize(userName: String, repoName: String, finalTotal: Boolean = false, displayLanguageInfo: Boolean = true): String = {
    val forLang = if (!displayLanguageInfo || finalTotal) "" else s" for '$language'"
    val forRepo = if (finalTotal) " in all git repositories" else s" in $repoName"
    s"$userName added ${ intFormat(added) } lines and deleted ${ intFormat(deleted) } lines, net ${ intFormat(delta) } lines$forLang$forRepo"
  }

  override def toString: String =
    s"added ${ intFormat(added) } lines and deleted ${ intFormat(deleted) } lines, net ${ intFormat(delta) } lines"
}
