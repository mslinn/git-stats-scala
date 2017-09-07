package com.micronautics.gitStats.svn

import org.scalatest.FunSuite
import com.micronautics.gitStats.AggCommit
import com.micronautics.gitStats.AggCommit._

class AggCommitTest extends FunSuite {

  test("AggCommit - file type is null") {
    intercept[IllegalArgumentException] {
      AggCommit(null, 5)
    }
  }

  test("AggCommit - file type is empty string") {
    intercept[IllegalArgumentException] {
      AggCommit("", 5)
    }
  }



  test("aggregateByFileType - null commits") {
    intercept[IllegalArgumentException] {
      aggregateByFileType(null)
    }
  }

  test("aggregateByFileType - empty commits") {
    val res = aggregateByFileType(Iterable.empty)
    assert(res.isEmpty, "Result is empty")
  }

  test("aggregateByFileType - one commit") {
    val res = aggregateByFileType(List(AggCommit("Scala", 1)))
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Scala", 1))
  }

  test("aggregateByFileType - many commits, same file type") {
    val sample = List(
      AggCommit("Scala", 1),
      AggCommit("Scala", -10),
      AggCommit("Scala", 3)
    )
    val res = aggregateByFileType(sample)
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Scala", -6), "Number of lines added")
  }

  test("aggregateByFileType - many commits, different file types") {
    val sample = List(
      AggCommit("Scala", 1),
      AggCommit("Groovy", -10),
      AggCommit("Scala", 3),
      AggCommit("Java", 5),
      AggCommit("Java", -1),
      AggCommit("Groovy", 15),
    )
    val res = aggregateByFileType(sample)
    assert(res.size === 3, "Number of commits")
    res.foreach { commit =>
      commit.fileType match {
        case "Scala" => assert(commit.linesAdded === 4, "Number of lines added")
        case "Groovy" => assert(commit.linesAdded === 5, "Number of lines added")
        case "Java" => assert(commit.linesAdded === 4, "Number of lines added")
        case _ @ fileType => fail(s"Unexpected file type: $fileType")
      }
    }
  }
}
