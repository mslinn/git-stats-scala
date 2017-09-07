package com.micronautics.gitStats

object Language {

  def fileLanguage(fileName: String): String = {
    fileSuffix(fileName)
      .map(suffixToLanguage)
      .getOrElse("")//TODO
  }

  def fileSuffix(fileName: String): Option[String] = {
    require(fileName != null, "File name must not be null")

    val idx = fileName.lastIndexOf(".")
    if (idx < 0) None
    else Some(fileName.substring(idx + 1))
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

}
