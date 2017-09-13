package com.micronautics.gitStats.render

import java.nio.file.Path
import java.text.NumberFormat

import com.micronautics.gitStats.AggCommit.{AggCommits, _}
import com.micronautics.gitStats.{AggCommit, AsciiWidgets, ConfigGitStats}
import org.joda.time.{DateTime, Days}

class AsciiRenderer(implicit config: ConfigGitStats) {

  def headline(perProjectCommits: Iterable[(Path, AggCommits)]): String = {
    val dateRange = s"${config.fromFormatted.map(x => s"from $x").mkString} ${config.toFormatted.map(x => s"to $x").mkString}".trim
    val between: String = (
      for {
        from <- config.dateFrom
        to = config.dateTo.getOrElse(DateTime.now.withTimeAtStartOfDay)
      } yield {
        val days = Days.daysBetween(from, to).getDays + 1 // account for inclusive dates
        s"for the $days days "
      }
      ).getOrElse("")
    val dateStr = between + (if (dateRange.nonEmpty) dateRange + ", inclusive" else "for all time")
    val projectNumber = perProjectCommits.size
    s"Commits in $projectNumber project${if (projectNumber != 1) "s" else ""} under ${config.directory.getAbsolutePath} $dateStr"
  }

  def table(title: String, commits: AggCommits): String = {
    val subTotals = commits
      .filter(c => c.linesAdded != 0 || c.linesDeleted != 0)
      .toList
      .sorted
      .map(asRow(_))
    AsciiWidgets.asciiTable(title, asRow(total(commits)), subTotals: _*)
  }

  @inline
  def asRow(commit: AggCommit, showLanguage: Boolean = true): List[String] =
    (if (showLanguage) List(commit.language) else Nil) :::
      List(intFormat(commit.linesAdded), intFormat(-commit.linesDeleted), intFormat(commit.netChange))

  private val intFormatter: NumberFormat = NumberFormat.getIntegerInstance

  @inline
  def intFormat(int: Int): String = sign(int) + intFormatter.format(int.toLong)

  @inline
  def sign(x: Int): String = if (x > 0) "+" else ""
}
