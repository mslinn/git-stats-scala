package com.micronautics.gitStats

import com.micronautics.gitStats.AggCommit._
import org.scalatest.FunSuite

class AggCommitTest extends FunSuite {

  test("AggCommit - language is null") {
    intercept[IllegalArgumentException] {
      AggCommit(null, 5, 7)
    }
  }

  test("AggCommit - language is empty string") {
    intercept[IllegalArgumentException] {
      AggCommit("", 5, 7)
    }
  }



  test("aggregateByLanguage - null commits") {
    intercept[IllegalArgumentException] {
      aggregateByLanguage(null)
    }
  }

  test("aggregateByLanguage - empty commits") {
    val res = aggregateByLanguage(Iterable.empty)
    assert(res.isEmpty, "Result is empty")
  }

  test("aggregateByLanguage - one commit") {
    val res = aggregateByLanguage(List(AggCommit("Scala", 1, 5)))
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Scala", 1, 5))
    assert(res.head.netChange === -4, "Net cnahge")
  }

  test("aggregateByLanguage - many commits, same language") {
    val sample = List(
      AggCommit("Scala", 1, 5),
      AggCommit("Scala", 6, 1),
      AggCommit("Scala", 3, 3)
    )
    val res = aggregateByLanguage(sample)
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Scala", 10, 9), "Number of lines added")
    assert(res.head.netChange === 1, "Net change")
  }

  test("aggregateByLanguage - many commits, different languages") {
    val sample = List(
      AggCommit("Scala", 1, 5),
      AggCommit("Groovy", 10, 8),
      AggCommit("Scala", 3, 0),
      AggCommit("Java", 5, 6),
      AggCommit("Java", 4, 2),
      AggCommit("Groovy", 15, 10),
    )
    val res = aggregateByLanguage(sample)
    assert(res.size === 3, "Number of commits")
    res.foreach { commit =>
      commit.language match {
        case "Scala" =>
          assert(commit.linesAdded === 4, "Number of lines added")
          assert(commit.linesDeleted === 5, "Number of lines deleted")
          assert(commit.netChange === -1, "Net change")
        case "Groovy" =>
          assert(commit.linesAdded === 25, "Number of lines added")
          assert(commit.linesDeleted === 18, "Number of lines deleted")
          assert(commit.netChange === 7, "Net change")
        case "Java" =>
          assert(commit.linesAdded === 9, "Number of lines added")
          assert(commit.linesDeleted === 8, "Number of lines deleted")
          assert(commit.netChange === 1, "Net change")
        case _ @ language => fail(s"Unexpected language: $language")
      }
    }
  }
}
