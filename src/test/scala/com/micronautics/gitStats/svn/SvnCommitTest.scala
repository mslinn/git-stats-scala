package com.micronautics.gitStats.svn

import java.nio.charset.CodingErrorAction

import com.micronautics.gitStats.svn.SvnCommit._
import org.scalatest.FunSuite

import scala.io.{Codec, Source}

class SvnCommitTest extends FunSuite {

  test("SvnCommit - null user name") {
    intercept[IllegalArgumentException] {
      SvnCommit(null, Set(FileModif("a", 1)))
    }
  }

  test("SvnCommit - empty user name") {
    intercept[IllegalArgumentException] {
      SvnCommit("", Set(FileModif("a", 1)))
    }
  }

  test("SvnCommit - null file modifications") {
    intercept[IllegalArgumentException] {
      SvnCommit("user", null)
    }
  }

  test("SvnCommit - empty file modifications") {
    intercept[IllegalArgumentException] {
      SvnCommit("user", Set())
    }
  }



  test("FileModif - null file name") {
    intercept[IllegalArgumentException] {
      FileModif(null, 4)
    }
  }

  test("FileModif - empty file name") {
    intercept[IllegalArgumentException] {
      FileModif("", 4)
    }
  }



  test("isCommitDelimiter - match") {
    val res = isCommitDelimiter("------------------------------------------------------------------------")
    assert(res, "is commit delimiter")
  }

  test("isCommitDelimiter - no match") {
    val res = isCommitDelimiter("--- branches/1.9.x/STATUS	(revision 1803636)")
    assert(!res, "is not commit delimiter")
  }



  test("isCommitHeadline - match") {
    val res = isCommitHeadline("r1803639 | kotkov | 2017-08-01 15:18:23 +0300 (Tue, 01 Aug 2017) | 45 lines")
    assert(res, "is commit headline")
  }

  test("isCommitHeadline - no match") {
    val res = isCommitHeadline("* STATUS: Vote +1 on the 1.9.x-r1802316 branch.")
    assert(!res, "is not commit headline")
  }



  test("isFileIndex - match") {
    val res = isFileIndex("Index: branches/1.9.x/STATUS")
    assert(res, "is file index")
  }

  test("isFileIndex - no match") {
    val res = isFileIndex("--- trunk/subversion/tests/cmdline/svntest/main.py	(revision 1803638)")
    assert(!res, "is not file index")
  }



  test("isLineCounts - match") {
    val res = isLineCounts("@@ -1043,14 +1043,14 @@")
    assert(res, "is line counts")
  }

  test("isLineCounts - no match") {
    val res = isLineCounts("-               options.fsfs_compression_level is not None:")
    assert(!res, "is not line counts")
  }



  test("commitEntriesIterator - null svn log output") {
    intercept[IllegalArgumentException] {
      commitEntriesIterator(null)
    }
  }

  test("commitEntriesIterator - empty svn log output") {
    val res = commitEntriesIterator(Iterator.empty)
    assert(res.isEmpty, "Commit entries iterator")
  }

  test("commitEntriesIterator - svn log output from a real sample") {
    val codec: Codec = Codec.UTF8.onMalformedInput(CodingErrorAction.IGNORE)
    val input = Source.fromInputStream(getClass.getResourceAsStream("svn-log-kotkov-danielsh.log"))(codec)
    val res = commitEntriesIterator(input.getLines()).toList
    assert(res.size === 71, "Number of commits")
    res.foreach { commitEntry =>
      commitEntry.foreach(line => assert(isUseful(line), "Commit entry contains only lines with useful info"))
    }
  }

  //TODO commitEntriesIterator - tests for bad input



  test("parseSvnCommit - one file, one line count") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-one-file-one-line-count.log")).getLines().toList
    val res = parseSvnCommit(commitEntry).get
    assert(res.userName === "danielsh", "User name")
    assert(res.fileModifs.size === 1, "Number of files")
    assert(res.fileModifs.head === FileModif("branches/1.9.x/STATUS", 1), "File modification")
  }

  test("parseSvnCommit - one file, many line counts") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-one-file-many-line-counts.log")).getLines().toList
    val res = parseSvnCommit(commitEntry).get
    assert(res.userName === "kotkov", "User name")
    assert(res.fileModifs.size === 1, "Number of files")
    assert(res.fileModifs.head === FileModif("trunk/subversion/libsvn_fs_fs/fs.h", -3), "File modification")
  }

  test("parseSvnCommit - many files, many line counts") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-many-files.log")).getLines().toList
    val res = parseSvnCommit(commitEntry).get
    assert(res.userName === "kotkov", "User name")
    assert(res.fileModifs.size === 6, "Number of files")
    val fileModifs = res.fileModifs.map(modif => modif.fileName -> modif.linesAdded).toMap
    val expectations = Iterable(
      "trunk/win-tests.py" -> 0,
      "trunk/build/run_tests.py" -> -1,
      "trunk/subversion/tests/cmdline/svntest/main.py" -> -1,
      "trunk/subversion/libsvn_fs_fs/fs_fs.c" -> 107,
      "trunk/subversion/libsvn_fs_fs/fs.h" -> 11,
      "trunk/subversion/libsvn_fs_fs/transaction.c" -> -3
    )
    for (expectation <- expectations) {
      val fileName = expectation._1
      assert(fileModifs(fileName) === expectation._2, s"Number of lines for file: $fileName")
    }
  }



  test("commitEntriesIterator and parseSvnCommit - svn log output from a real sample") {
    val codec: Codec = Codec.UTF8.onMalformedInput(CodingErrorAction.IGNORE)
    val input = Source.fromInputStream(getClass.getResourceAsStream("svn-log-kotkov-danielsh.log"))(codec)
    val commitEntries = commitEntriesIterator(input.getLines())
    val svnCommits = commitEntries.map(parseSvnCommit(_)).flatMap(_.iterator)
    assert(svnCommits.size === 71, "Number of commits")
    svnCommits.foreach{ commit =>
      assert(commit.fileModifs.nonEmpty, s"Number of files in commit: $commit")
    }
  }
}
