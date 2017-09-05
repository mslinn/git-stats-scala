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

  def commitEntriesIterator(svnLogOutputLines: Iterator[String]): Iterator[List[String]] = {
    require(svnLogOutputLines != null, "svn log output must not be null")

    def readFirstCommitEntry: List[String] = {
      svnLogOutputLines
        .takeWhile(line => !isCommitDelimiter(line))
        .filter(line => isUseful(line))
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

  private val commitHeadlinePattern = """^r\d+\s+\|\s+(\S+)\s+\|.+?\|.+$""".r("userName")
  private val fileIndexPattern = """^Index:\s+(\S+)$""".r("fileName")
  private val lineCountsPattern = """^@@\s+\-\d+,(\d+)\s+\+\d+,(\d+)\s+@@$""".r("oldCount", "newCount")

  def isUseful(line: String): Boolean =
    isCommitHeadline(line) || isFileIndex(line) || isLineCounts(line)

  def isCommitHeadline(line: String): Boolean =
    commitHeadlinePattern.pattern.matcher(line).matches()

  def isFileIndex(line: String): Boolean =
    fileIndexPattern.pattern.matcher(line).matches()

  def isLineCounts(line: String): Boolean =
    lineCountsPattern.pattern.matcher(line).matches()

  def parseUserName(line: String): Option[String] =
    fileIndexPattern.findFirstMatchIn(line).map(_.group("userName"))
}
