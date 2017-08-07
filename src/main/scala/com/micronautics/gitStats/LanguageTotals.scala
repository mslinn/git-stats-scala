package com.micronautics.gitStats

import scala.collection.mutable

protected class LanguageTotals(
  val map: mutable.Map[String, Commit] = mutable.Map.empty.withDefaultValue(Commit.zero)
) {
  def combine(commit: Commit): Unit = {
    val value = map(commit.language)
    val updated = Commit(
      added = value.added + commit.added,
      deleted = value.deleted + commit.deleted,
      language = commit.language
    )
    map.put(commit.language, updated)
    ()
  }
}
