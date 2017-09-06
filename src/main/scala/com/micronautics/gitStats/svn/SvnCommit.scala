package com.micronautics.gitStats.svn

import scala.collection.mutable

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
  require(fileModifs.nonEmpty, "File modifications cannot be empty string")
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

  //TODO Maybe Iterator[String] is enough instead of List[String]
  //TODO Introduce type alias CommitEntry
  def commitEntriesIterator(svnLogOutputLines: Iterator[String]): Iterator[List[String]] = {
    require(svnLogOutputLines != null, "svn log output must not be null")

    def readFirstCommitEntry: List[String] = {
      svnLogOutputLines
        .takeWhile(!isCommitDelimiter(_))
        .filter(isUseful)
        .toList
    }

    /* svn command output starts with a commit delimiter line; skip this first commit delimiter. */
    if (svnLogOutputLines.hasNext)
      svnLogOutputLines.next()

    new Iterator[List[String]] {
      override def hasNext: Boolean = svnLogOutputLines.hasNext
      override def next(): List[String] = readFirstCommitEntry
    }
  }

  private val commitDelimiterPattern = """^-{5,}$""".r

  def isCommitDelimiter(line: String): Boolean =
    commitDelimiterPattern.pattern.matcher(line).matches()

  private val commitHeadlinePattern = """^r\d+\s+\|\s+(\S+)\s+\|.+?\|.+$""".r
  private val fileIndexPattern = """^Index:\s+(\S+)$""".r
  private val lineCountsPattern = """^@@\s+\-\d+,(\d+)\s+\+\d+,(\d+)\s+@@$""".r

  def isUseful(line: String): Boolean =
    isCommitHeadline(line) || isFileIndex(line) || isLineCounts(line)

  def isCommitHeadline(line: String): Boolean =
    commitHeadlinePattern.pattern.matcher(line).matches()

  def isFileIndex(line: String): Boolean =
    fileIndexPattern.pattern.matcher(line).matches()

  def isLineCounts(line: String): Boolean =
    lineCountsPattern.pattern.matcher(line).matches()

  def parseSvnCommit(commitEntry: List[String]): Option[SvnCommit] = {
    var userNameOpt: Option[String] = None
    var fileNameOpt: Option[String] = None
    val fileModifEntries: mutable.Map[String, Int] = mutable.Map()
    for (line <- commitEntry) {
      line match {
        case commitHeadlinePattern(userName) =>
          userNameOpt = Some(userName)
        case fileIndexPattern(fileName) =>
          fileNameOpt = Some(fileName)
          fileModifEntries += (fileName -> 0)
        case lineCountsPattern(oldCount, newCount) =>
          fileNameOpt.foreach { fileName =>
            fileModifEntries(fileName) = fileModifEntries(fileName) - oldCount.toInt + newCount.toInt
          }
        case _=>
          println(s"WARNING: Unexpected line: $line")
      }
    }
    userNameOpt.map { userName =>
      SvnCommit(userName,
        fileModifEntries.map { case (fileName, linesAdded) => FileModif(fileName, linesAdded) }.toSet)
    }.filter(_.fileModifs.nonEmpty)
  }
}
