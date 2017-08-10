package com.micronautics.gitStats

import org.apache.poi.xssf.usermodel.XSSFWorkbook

case class Commits(value: List[Commit])
                  (implicit config: ConfigGitStats){
  def asAsciiTable(title: String): String = {
    val subTotals: List[List[String]] =
      value.filter(c => c.added!=0 || c.deleted!=0).map { commit =>
        commit.asRow()
      }

    AsciiWidgets.asciiTable(title, total.asRow(), subTotals: _*)
  }

  def asExcelSheet(excelOutput: ExcelOutput, title: String): XSSFWorkbook = {
    val subTotals: List[List[String]] =
      value.filter(c => c.added!=0 || c.deleted!=0).map { commit =>
        commit.asRow()
      }

    excelOutput.addSheet(title, total.asRow(), subTotals: _*)
  }

  def byLanguage: Commits =
    Commits(
      value
        .groupBy(_.language)
        .map { case (key, values) =>
          Commit(
            language = key,
            added    = values.map(_.added).sum,
            deleted  = values.map(_.deleted).sum
          )
        }
        .toList
        .sorted
    )

  def combine(others: List[Commits]): Commits =
    Commits(this.value ::: others.flatMap(_.value))
      .byLanguage

  def languageTotals: LanguageTotals = LanguageTotals(this)

  def total: Commit = value.fold(Commit.zero) {
    case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
  }.copy(language = Commit.languageTotal)
}
