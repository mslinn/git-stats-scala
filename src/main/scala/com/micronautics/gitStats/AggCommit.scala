package com.micronautics.gitStats

import com.micronautics.gitStats.FileModification.RichIntTuple2

import scala.collection._

case class AggCommit(language: String, linesAdded: Int, linesDeleted: Int) {
  require(language != null, "Language must not be null")
  require(language.nonEmpty, "Language must not be empty string")
  //TODO Require non-negative lines added/deleted

  val netChange: Int = linesAdded - linesDeleted
}

object AggCommit {

  type AggCommits = Iterable[AggCommit]

  implicit val defaultOrdering: Ordering[AggCommit] =
    Ordering.by(c => (-c.netChange, -c.linesAdded, -c.linesDeleted))

  /**
    * Aggregates commits by language.
    *
    * @param commits Collection of commits.
    * @return Collection of commits where line counts are aggregations for each language.
    * @throws IllegalArgumentException commits is null.
    */
  def aggregateByLanguage(commits: AggCommits): AggCommits = {
    require(commits != null, "Commits must not be null")

    val aggMap: Map[String, (Int, Int)] = commits.aggregate(mutable.Map[String, (Int, Int)]().withDefaultValue((0, 0)))(
      (agg, commit) => agg += {
        val currentVal: (Int, Int) = agg(commit.language)
        val newVal = currentVal + ((commit.linesAdded, commit.linesDeleted))
        commit.language -> newVal
      },
      (agg1, agg2) => agg1 ++= agg2
    )
    aggMap.map { case (language, (linesAdded, linesDeleted)) => AggCommit(language, linesAdded, linesDeleted) }
  }

  lazy val languageTotal = "Total"

  /**
    * Calculates total amounts for added and deleted lines.
    *
    * @param commits Colllection of commits.
    * @return Commit with total numbers.
    * @throws IllegalArgumentException commits is null.
    */
  def total(commits: AggCommits): AggCommit = {
    require(commits != null, "Commits must not be null")

    val (totalAdded, totalDeleted) = commits.aggregate((0, 0))(
      (agg, c) => agg + ((c.linesAdded, c.linesDeleted)),
      _ + _
    )
    AggCommit(languageTotal, totalAdded, totalDeleted)
  }
}
