package com.micronautics.gitStats.svn

/**
  * One file commit to Subversion.
  * In practice, one Subversion commit may contain modifications to many files.
  * This class treats such commit as a set of commits for each file individually.
  *
  * @param userName User name, cannot be null or empty string.
  * @param fileModifs File modifications in this commit, cannot be null or empty.
  */
case class SvnCommit(userName: String, fileModifs: Set[FileModif]) {
  require(userName != null, "User name must not be null")
  require(!userName.isEmpty, "User name must not be empty string")
  require(fileModifs != null, "File modifications cannot be null")
  require(!fileModifs.isEmpty, "File modifications cannot be empty string")
}

/**
  * One file modification within a commit to Subversion.
  *
  * @param fileName File name, cannot be null or empty string.
  * @param linesAdded Number of added lines. Negative number means more lines were deleted rather than added.
  */
case class FileModif(fileName: String, linesAdded: Int) {
  require(fileName != null, "File name must not be null")
  require(!fileName.isEmpty, "File name must not be empty string")
}

object SvnCommit {

  def parse(svnLogOutputLines: Iterator[String]): Set[SvnCommit] = {
    ???
  }
}

import scala.util.parsing.combinator._

case class WordFreq(word: String, count: Int) {
  override def toString = "Word <" + word + "> " +
    "occurs with frequency " + count
}

class SimpleParser extends RegexParsers {
  def word: Parser[String]   = """[a-z]+""".r       ^^ { _.toString }
  def number: Parser[Int]    = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def freq: Parser[WordFreq] = word ~ number        ^^ { case wd ~ fr => WordFreq(wd,fr) }
}

object TestSimpleParser extends SimpleParser {
  def main(args: Array[String]) = {
    parse(freq, "johnny 121") match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println("FAILURE: " + msg)
      case Error(msg,_) => println("ERROR: " + msg)
    }
  }
}