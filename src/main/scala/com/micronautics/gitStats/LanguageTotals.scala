package com.micronautics.gitStats

import scala.collection.mutable

protected object LanguageTotals {
  def apply(commits: Commits): LanguageTotals = {
    val total = new LanguageTotals
    commits.value.foreach(total.combine)
    total
  }
}

class LanguageTotals(
  val ltValue: mutable.Map[String, Commit] = mutable.Map.empty.withDefaultValue(Commit.zero)
) {
  def asCommits: Commits = Commits(ltValue.values.toList.sorted)

  def combine(commit: Commit): Unit = {
    val value = ltValue(commit.language)
    val updated = Commit(
      added = value.added + commit.added,
      deleted = value.deleted + commit.deleted,
      language = commit.language
    )
    ltValue.put(commit.language, updated)
    ()
  }

  def total: Commit = Commits(ltValue.values.toList).total
}
