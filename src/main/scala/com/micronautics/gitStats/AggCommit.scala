package com.micronautics.gitStats

import scala.collection._

case class AggCommit(language: String, linesAdded: Int) {
  require(language != null, "Language must not be null")
  require(!language.isEmpty, "Language must not be empty string")
}

object AggCommit {

  /**
    * Aggregates commits by language.
    *
    * @param commits Collection of commits.
    * @return Collection of commits where line counts are aggregations for each language.
    * @throws IllegalArgumentException commits is null.
    */
  def aggregateByLanguage(commits: GenIterable[AggCommit]): GenIterable[AggCommit] = {
    require(commits != null, "Commits must not be null")

    val aggMap: Map[String, Int] = commits.aggregate(mutable.Map[String, Int]().withDefaultValue(0))(
      (agg, commit) => agg += ((commit.language, agg(commit.language) + commit.linesAdded)),
      (agg1, agg2) => agg1 ++= agg2
    )
    aggMap.map { case (language, linesAdded) => AggCommit(language, linesAdded) }
  }
}
