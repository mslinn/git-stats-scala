package com.micronautics.gitStats

import java.nio.file.Path

import com.micronautics.gitStats.ProjectDir.RichPath

import scala.io.Source
import scala.util.matching.Regex

object Language {

  /**
    * Determines programming language for the given source file.
    *
    * @param file Path to the source file.
    * @return Programming language.
    * @throws IllegalArgumentException File name is null or empty string.
    */
  //TODO Test with file samples
  def fileLanguage(file: Path): String = {
    require(file != null, "File must not be null")
    require(file.toString.nonEmpty, "File path must not be empty string")

    //TODO If verbose, print files with unknown language
    file.fileSuffix
      .flatMap(suffixToLanguage.get)
      .orElse(nameToLanguage(file))
      .orElse(contentToLanguage(file))
      .getOrElse(unknownLanguage)
  }

  def nameToLanguage(file: Path): Option[String] = {
    require(file != null, "File name must not be null")

    file.toFile.getName.toLowerCase match {
      case name if name.startsWith("dockerfile") => Some("Dockerfile")
      case name if name.startsWith("makefile") => Some("Makefile")
      case name if name.startsWith(".") => Some(miscellaneousLanguage)
      case _ => None
    }
  }

  private lazy val shellContentRegexes: Set[Regex] = Set(
    """#!/bin/bash""".r,
    """#!/bin/sh""".r,
    """#!/usr/bin/env\s+bash""".r,
    """#!/usr/bin/env\s+sh""".r
  )

  private lazy val groovyContentRegex: Regex = """#!/usr/bin/env\s+groovy""".r

  def contentToLanguage(file: Path): Option[String] = {
    //TODO Skip binary files
    val fileContent = try {
      /* Read only the first 100 chars - just enough to decide about the language.
       * The performance improvement can be visible, as we check _all_ files with unrecognized suffixes.*/
      Source.fromFile(file.toFile).take(100).mkString
    } catch {
      case _: Exception => ""
    }
    if (shellContentRegexes.exists(_.findFirstMatchIn(fileContent).isDefined))
      Some("Shell")
    else if (groovyContentRegex.findFirstMatchIn(fileContent).isDefined)
      Some("Groovy")
    else
      None
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
    "gradle"     -> "Gradle",
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
    "sh"         -> "Shell",
    "sql"        -> "SQL",
    "swift"      -> "Swift",
    "vb"         -> "Visual Basic",
    "yaml"       -> "Yaml",
    "yml"        -> "Yaml",
    "xml"        -> "XML"
  )

  lazy val unknownLanguage = "Unknown"
  lazy val miscellaneousLanguage = "Miscellaneous"
}
