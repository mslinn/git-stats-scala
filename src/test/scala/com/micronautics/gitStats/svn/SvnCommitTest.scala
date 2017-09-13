package com.micronautics.gitStats.svn

import java.nio.charset.CodingErrorAction
import java.nio.file.Paths

import com.micronautics.gitStats.svn.SvnCommit._
import com.micronautics.gitStats.{AggCommit, ConfigGitStats, FileModification}
import org.scalatest.FunSuite

import scala.io.{Codec, Source}

class SvnCommitTest extends FunSuite {

  test("SvnCommit - null user name") {
    intercept[IllegalArgumentException] {
      SvnCommit(null, Set(FileModification(Paths.get("a"), 1, 2)))
    }
  }

  test("SvnCommit - empty user name") {
    intercept[IllegalArgumentException] {
      SvnCommit("", Set(FileModification(Paths.get("a"), 1, 2)))
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
  //TODO parseSvnCommit - tests for bad input, for ignored file types, for ignored directories, for only known languages


  implicit val config: ConfigGitStats = ConfigGitStats()

  test("parseSvnCommit - one file, one line count") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-one-file-one-line-count.log")).getLines().toList
    val res = parseSvnCommit(commitEntry, Paths.get("/workdir")).get
    assert(res.userName === "danielsh", "User name")
    assert(res.fileModifs.size === 1, "Number of files")
    assert(res.fileModifs.head === FileModification(Paths.get("/workdir/branches/1.9.x/STATUS"), 8, 7), "File modification")
  }

  test("parseSvnCommit - one file, many line counts") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-one-file-many-line-counts.log")).getLines().toList
    val res = parseSvnCommit(commitEntry, Paths.get("/workdir")).get
    assert(res.userName === "kotkov", "User name")
    assert(res.fileModifs.size === 1, "Number of files")
    assert(res.fileModifs.head === FileModification(Paths.get("/workdir/trunk/subversion/libsvn_fs_fs/fs.h"), 12, 15), "File modification")
  }

  test("parseSvnCommit - many files, many line counts") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-many-files.log")).getLines().toList
    val res = parseSvnCommit(commitEntry, Paths.get("/workdir")).get
    assert(res.userName === "kotkov", "User name")
    assert(res.fileModifs.size === 6, "Number of files")
    val fileModifs = res.fileModifs.map(modif => modif.file -> ((modif.linesAdded, modif.linesDeleted))).toMap
    val expectations = Iterable(
      Paths.get("/workdir/trunk/win-tests.py") -> ((28, 28)),
      Paths.get("/workdir/trunk/build/run_tests.py") -> ((23, 24)),
      Paths.get("/workdir/trunk/subversion/tests/cmdline/svntest/main.py") -> ((39, 40)),
      Paths.get("/workdir/trunk/subversion/libsvn_fs_fs/fs_fs.c") -> ((171, 64)),
      Paths.get("/workdir/trunk/subversion/libsvn_fs_fs/fs.h") -> ((30, 19)),
      Paths.get("/workdir/trunk/subversion/libsvn_fs_fs/transaction.c") -> ((24, 27))
    )
    for (expectation <- expectations) {
      val file = expectation._1
      assert(fileModifs(file) === expectation._2, s"Number of lines for file: $file")
    }
  }

  test("parseSvnCommit - all files are filtered out") {
    val commitEntry = Source.fromInputStream(getClass.getResourceAsStream("commit-many-files.log")).getLines().toList
    val res = parseSvnCommit(commitEntry, Paths.get("/workdir"))(config.copy(ignoredFileTypes = List("py", "c", "h")))
    assert(res === None, "None commits")
  }



  test("commitEntriesIterator and parseSvnCommit - svn log output from a real sample") {
    val codec: Codec = Codec.UTF8.onMalformedInput(CodingErrorAction.IGNORE)
    val input = Source.fromInputStream(getClass.getResourceAsStream("svn-log-kotkov-danielsh.log"))(codec)
    val commitEntries = commitEntriesIterator(input.getLines())
    val svnCommits = commitEntries.map(parseSvnCommit(_, Paths.get("/workdir"))).flatMap(_.iterator)
    assert(svnCommits.size === 71, "Number of commits")
    svnCommits.foreach{ commit =>
      assert(commit.fileModifs.nonEmpty, s"Number of files in commit: $commit")
    }
  }



  test("aggCommits - one file modification") {
    val svnCommit = SvnCommit("moses", Set(FileModification(Paths.get("commandments.txt"), 10, 0)))
    val res = svnCommit.aggCommits
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Unknown", 10, 0), "Commit")
  }

  test("aggCommits - many file modifications, different file types") {
    val svnCommit = SvnCommit("linus.torvalds",
      Set(
        FileModification(Paths.get("Makefile"), 10, 5),
        FileModification(Paths.get("fs.h"), 20, 15),
        FileModification(Paths.get("README"), 30, 0)
      )
    )
    val res = svnCommit.aggCommits
    assert(res.size === 3, "Number of commits")
    assert(res.asInstanceOf[List[_]].contains(AggCommit("Makefile", 10, 5)), "Commit for Makefile")
    assert(res.asInstanceOf[List[_]].contains(AggCommit("C/C++", 20, 15)), "Commit for C/C++")
    assert(res.asInstanceOf[List[_]].contains(AggCommit("Unknown", 30, 0)), "Commit for Unknown")
  }

  test("aggCommits - many file modifications, same file type") {
    val svnCommit = SvnCommit("mark.twain",
      Set(
        FileModification(Paths.get("tom_sawyer.txt"), 500, 100),
        FileModification(Paths.get("connecticut_yankee.txt"), 500, 100),
        FileModification(Paths.get("mysterious_stranger.txt"), 0, 200)
      )
    )
    val res = svnCommit.aggCommits
    assert(res.size === 3, "Number of commits")
    assert(res.count(_ == AggCommit("Unknown", 500, 100)) === 2, "Commits with 500 lines added and 100 lines deleted")
    assert(res.asInstanceOf[List[_]].contains(AggCommit("Unknown", 0, 200)), "Commit with -0 lines added and 200 lines deleted")
  }
}
