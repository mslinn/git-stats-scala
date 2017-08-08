package com.micronautics.gitStats

object Output {
  def formatCommits(
    userName: String,
    title: String,
    commits: List[Commit] = Nil,
    grandTotals: Commit = Commit.zero
  ): String = {
    val subtotals: List[List[String]] =
      commits.map {
        commit =>
          commit.format(grandTotals)
      }
    s"\n$title\n" +
      AsciiWidgets.asciiTable(grandTotals.format(), subtotals: _*)
  }
}
