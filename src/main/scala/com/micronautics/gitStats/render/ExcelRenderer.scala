package com.micronautics.gitStats.render

import com.micronautics.gitStats.AggCommit.AggCommits
import com.micronautics.gitStats.{AggCommit, ExcelOutput}
import org.apache.poi.xssf.usermodel.XSSFSheet

class ExcelRenderer(override val fileName: String) extends ExcelOutput(fileName) {


  /** @return newly created [[XSSFSheet]] instance */
  // todo figure out why Commit.defaultCommitOrdering does not cause the commits to be ordered
  @inline
  def addSheetOfCommits(title: String, commits: AggCommits): XSSFSheet = {
    val commitsPerSheet: List[List[Any]] = commits
      .toList
      .sorted
      .map(asRow(_))
    addSheet(title, commitsPerSheet: _*)
  }

  @inline
  def asRow(commit: AggCommit, showLanguage: Boolean = true): List[Any] =
    (if (showLanguage) List(language) else Nil) :::
      List(commit.linesAdded, -commit.linesDeleted, commit.netChange)
}
