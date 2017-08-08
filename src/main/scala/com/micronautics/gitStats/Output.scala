package com.micronautics.gitStats

import com.micronautics.gitStats.Commit.intFormat

object Output {
  def formatCommits(userName: String, title: String, grandTotal: Boolean = false, commits: List[Commit]): String = {
    val subtotals: List[List[String]] =
      commits.map {
        commit =>
          (if (grandTotal) Nil else List(commit.language)) :::
            List(intFormat(commit.added), intFormat(-commit.deleted), intFormat(commit.delta))
      }
    s"\n$title\n" +
    AsciiWidgets.asciiTable(subtotals:_*)
  }
}
