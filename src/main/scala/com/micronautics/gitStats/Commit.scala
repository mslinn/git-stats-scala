package com.micronautics.gitStats

import java.text.NumberFormat

object Commit {
  implicit val defaultCommitOrdering: Ordering[Commit] = Ordering.by { commit: Commit =>
    (-commit.delta, -commit.added, -commit.deleted)
  }

  lazy val unknownLanguage = "Unknown"
  lazy val miscellaneousLanguage = "Miscellaneous"
  lazy val languageTotal = "Total"

  lazy val zero: Commit = Commit(0, 0)(ConfigGitStats.defaultValue)

  val intFormatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  @inline def sign(x: Int): String = x match {
    case _ if x>0 => "+"
    case _ => ""
  }

  @inline def intFormat(int: Int): String = sign(int) + intFormatter.format(int.toLong)

  @inline def toInt(string: String): Int = if (string=="-") 0 else string.toInt

  @inline def apply(args: String)
                   (implicit config: ConfigGitStats): Commit = {
    //TODO \s+ or \h+
    args.split("\t| ") match {
      case Array(linesAdded, linesDeleted, fileName) =>
        Commit(toInt(linesAdded), toInt(linesDeleted), language=language(fileName.trim), fileName=fileName)

      case Array(linesAdded, linesDeleted, oldFileName@_, arrow@_, newFileName) => // a file was renamed
        // Assume that filenames without a filetype are shell scripts
        val language = if (newFileName.contains(".")) Commit.unknownLanguage else "Shell"
        Commit(toInt(linesAdded), toInt(linesDeleted), language=language, fileName=newFileName)

      case _ =>
        Commit(0, 0, language=unknownLanguage)
    }
  }

  /* Read only the first 100 chars - just enough to decide about the language.
  * The performance improvement can be visible, as we check _all_ files with unrecognized suffixes.*/
  @inline def contents(fileName: String): String = try {
    scala.io.Source.fromFile(fileName).take(100).mkString
  } catch {
    case _: Exception => ""
  }

  val suffixToLanguage: Map[String, String] = Map(
    "asp"        -> "ASP",
    "bat"        -> "MS-DOS batch",
    "cmd"        -> "Windows script",
    "c"          -> "C",
    "C"          -> "C++",
    "cc"         -> "C++",
    "CPP"        -> "C++",
    "cxx"        -> "C++",
    "CXX"        -> "C++",
    "c++"        -> "C++",
    "dart"       -> "Dart",
    "dtd"        -> "Document type definition",
    "dfm"        -> "Delphi",
    "dpk"        -> "Delphi",
    "dpr"        -> "Delphi",
    "fs"         -> "F#",
    "fsi"        -> "F#",
    "fsx"        -> "F#",
    "fsscript"   -> "F#",
    "go"         -> "Go",
    "groovy"     -> "Groovy",
    "h"          -> "C/C++",
    "H"          -> "C/C++",
    "hs"         -> "Haskell",
    "htm"        -> "HTML",
    "html"       -> "HTML",
    "java"       -> "Java",
    "js"         -> "JavaScript",
    "jsp"        -> "Java Server Pages",
    "lhs"        -> "Haskell",
    "m"          -> "Objective-C",
    "md"         -> "Markdown",
    "ml"         -> "F#",
    "mli"        -> "F#",
    "pl"         -> "Perl",
    "php"        -> "PHP",
    "py"         -> "Python",
    "pyw"        -> "Python",
    "properties" -> "Properties",
    "r"          -> "R",
    "rake"       -> "Ruby",
    "rb"         -> "Ruby",
    "repl"       -> "Scala",
    "sbt"        -> "SBT",
    "sc"         -> "Scala",
    "scala"      -> "Scala",
    "sh"         -> "Shell scripts",
    "sql"        -> "SQL",
    "swift"      -> "Swift",
    "vb"         -> "Visual Basic",
    "yaml"       -> "Yaml",
    "yml"        -> "Yaml",
    "xml"        -> "XML"
  )

  @inline def suffixedIsDefined(fileName: String): Boolean = {
    val i = fileName.lastIndexOf(".")
    if (i<0) false else {
      val suffix = fileName.substring(i+1)
      suffixToLanguage.get(suffix).isDefined
    }
  }

  @inline def suffix(fileName: String): String = {
    val i = fileName.lastIndexOf(".")
    if (i<0) unknownLanguage else {
      val suffix = fileName.substring(i+1)
      suffixToLanguage(suffix)
    }
  }

  def language(fileName: String): String = fileName.toLowerCase match {
    case f if suffixedIsDefined(f) => suffix(f)
    case f if f.startsWith(".") => miscellaneousLanguage
    case f if contents(f).startsWith("#!/bin/bash") | contents(f).startsWith("#!/bin/sh") => "Shell"
    case _ => unknownLanguage
  }
}

//TODO Calculate language from fileType. Currently the fileName is parsed twice: for language, then for fileType.
case class Commit(added: Int, deleted: Int, fileName: String="", language: String=Commit.unknownLanguage)
                 (implicit config: ConfigGitStats) {
  import com.micronautics.gitStats.Commit._

  /** Number of net lines `(added - deleted)` */
  lazy val delta: Int = added - deleted

  /** Filetype of fileName, not including the dot */
  lazy val fileType: String = {
    val i = fileName.lastIndexOf(".")
    if (i<0) fileName else fileName.substring(i+1)
  }

  lazy val hasUnknownLanguage: Boolean = language==unknownLanguage || language==miscellaneousLanguage

  lazy val ignoredFiletype: Boolean = config.ignoredFileTypes.contains(fileType)

  lazy val ignoredPath: Boolean = config.ignoredSubDirectories.exists(fileName.contains)

  @inline def asRow(showLanguage: Boolean = true): List[String] =
    (if (showLanguage) List(language) else Nil) :::
      List(intFormat(added), intFormat(-deleted), intFormat(delta))

  @inline def asExcelRow(showLanguage: Boolean = true): List[Any] =
    (if (showLanguage) List(language) else Nil) ::: List(added, -deleted, delta)

  @inline override def toString: String =
    s"$language: added ${ intFormat(added) } lines and deleted ${ intFormat(deleted) } lines, net ${ intFormat(delta) } lines"
}
