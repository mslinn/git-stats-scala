package com.micronautics.gitStats.svn

import org.scalatest.FunSuite
import com.micronautics.gitStats.AggCommit
import com.micronautics.gitStats.AggCommit._

class AggCommitTest extends FunSuite {

  test("AggCommit - language is null") {
    intercept[IllegalArgumentException] {
      AggCommit(null, 5)
    }
  }

  test("AggCommit - language is empty string") {
    intercept[IllegalArgumentException] {
      AggCommit("", 5)
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
    val res = aggregateByLanguage(List(AggCommit("Scala", 1)))
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Scala", 1))
  }

  test("aggregateByLanguage - many commits, same language") {
    val sample = List(
      AggCommit("Scala", 1),
      AggCommit("Scala", -10),
      AggCommit("Scala", 3)
    )
    val res = aggregateByLanguage(sample)
    assert(res.size === 1, "Number of commits")
    assert(res.head === AggCommit("Scala", -6), "Number of lines added")
  }

  test("aggregateByLanguage - many commits, different languages") {
    val sample = List(
      AggCommit("Scala", 1),
      AggCommit("Groovy", -10),
      AggCommit("Scala", 3),
      AggCommit("Java", 5),
      AggCommit("Java", -1),
      AggCommit("Groovy", 15),
    )
    val res = aggregateByLanguage(sample)
    assert(res.size === 3, "Number of commits")
    res.foreach { commit =>
      commit.language match {
        case "Scala" => assert(commit.linesAdded === 4, "Number of lines added")
        case "Groovy" => assert(commit.linesAdded === 5, "Number of lines added")
        case "Java" => assert(commit.linesAdded === 4, "Number of lines added")
        case _ @ language => fail(s"Unexpected language: $language")
      }
    }
  }
}
