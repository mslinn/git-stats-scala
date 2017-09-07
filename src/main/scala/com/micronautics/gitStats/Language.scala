package com.micronautics.gitStats

import scala.io.Source

object Language {

  /**
    * Determines programming language for the given source file.
    *
    * @param fileName Source file name.
    * @return Programming language.
    * @throws IllegalArgumentException File name is null or empty string.
    */
  def fileLanguage(fileName: String): String = {
    require(fileName != null, "File name must not be null")
    require(fileName.nonEmpty, "File name must not be empty string")

    fileSuffix(fileName)
      .flatMap(suffixToLanguage.get)
      .orElse(nameToLanguage(fileName))
      .orElse(contentToLanguage(fileName))
      .getOrElse(unknownLanguage)
  }

  def fileSuffix(fileName: String): Option[String] = {
    require(fileName != null, "File name must not be null")

    val idx = fileName.lastIndexOf(".")
    if (idx < 0) None
    else Some(fileName.substring(idx + 1))
  }

  def nameToLanguage(fileName: String): Option[String] = {
    require(fileName != null, "File name must not be null")

    fileName.toLowerCase match {
      case name if name.startsWith("dockerfile") => Some("Dockerfile")
      case name if name.startsWith(".") => Some(miscellaneousLanguage)
      case _ => None
    }
  }

  def contentToLanguage(fileName: String): Option[String] = {
    val fileContent = try {
      /* Read only the first 100 chars - just enough to decide about the language.
       * The performance improvement can be visible, as we check _all_ files with unrecognized suffixes.*/
      Source.fromFile(fileName).take(100).mkString
    } catch {
      case _: Exception => ""
    }
    fileContent match {
      case content if content.startsWith("#!/bin/bash") || content.startsWith("#!/bin/sh") => Some("Shell")
      case _ => None
    }
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
