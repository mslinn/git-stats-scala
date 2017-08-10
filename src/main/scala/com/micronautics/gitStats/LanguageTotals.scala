package com.micronautics.gitStats

import scala.collection.mutable

/* TODO LanguageTotals seems duplicating Commits.byLanguage. Can be removed? */
protected object LanguageTotals {
  def apply(commits: Commits)
           (implicit config: ConfigGitStats): LanguageTotals = {
    val total = new LanguageTotals
    commits.value.foreach(total.combine)
    total
  }
}

class LanguageTotals(
  val ltValue: mutable.Map[String, Commit] = mutable.Map.empty.withDefaultValue(Commit.zero)
)(implicit
  config: ConfigGitStats
) {
  def asCommits: Commits = Commits(ltValue.values.toList.sorted)

  def combine(commit: Commit)
             (implicit config: ConfigGitStats): Unit = {
    val value = ltValue(commit.language)
    val updated = Commit(
      added = value.added + commit.added,
      deleted = value.deleted + commit.deleted,
      language = commit.language
    )
    ltValue.put(commit.language, updated)
    ()
  }

  //TODO Unused
  def total: Commit = Commits(ltValue.values.toList).total
}
