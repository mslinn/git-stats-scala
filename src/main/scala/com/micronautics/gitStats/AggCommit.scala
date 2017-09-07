package com.micronautics.gitStats

import scala.collection._

case class AggCommit(fileType: String, linesAdded: Int) {
  require(fileType != null, "File type must not be null")
  require(!fileType.isEmpty, "File type must not be empty string")
}

object AggCommit {

  /**
    * Aggregates commits by file type.
    *
    * @param commits Collection of commits.
    * @return Collection of commits where line counts are aggregations for each file type.
    * @throws IllegalArgumentException commits is null.
    */
  def aggregateByFileType(commits: GenIterable[AggCommit]): GenIterable[AggCommit] = {
    require(commits != null, "Commits must not be null")

    val aggMap: Map[String, Int] = commits.aggregate(mutable.Map[String, Int]().withDefaultValue(0))(
      (agg, commit) => agg += ((commit.fileType, agg(commit.fileType) + commit.linesAdded)),
      (agg1, agg2) => agg1 ++= agg2
    )
    aggMap.map { case (fileType, linesAdded) => AggCommit(fileType, linesAdded) }
  }
}
