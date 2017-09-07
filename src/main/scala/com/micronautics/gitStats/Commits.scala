package com.micronautics.gitStats

import org.apache.poi.xssf.usermodel.XSSFSheet

@deprecated("TODO Use AvgCommit instead - it does not depend neither on Git nor on presentation format")
case class Commits(value: List[Commit])
                  (implicit config: ConfigGitStats){
  @inline def asAsciiTable(title: String): String = {
    val subTotals: List[List[String]] =
      value.filter(c => c.added!=0 || c.deleted!=0).map { commit =>
        commit.asRow()
      }

    AsciiWidgets.asciiTable(title, total.asRow(), subTotals: _*)
  }

  @inline def asExcelSheet(excelOutput: ExcelOutput, title: String): XSSFSheet = {
    val subTotals: List[List[String]] =
      value.filter(c => c.added!=0 || c.deleted!=0).map { commit =>
        commit.asRow()
      }

    excelOutput.addSheet(title, subTotals: _*)
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

  @inline def combine(others: List[Commits]): Commits =
    Commits(this.value ::: others.flatMap(_.value))
      .byLanguage

  @inline def total: Commit = value.fold(Commit.zero) {
    case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
  }.copy(language = Commit.languageTotal)
}
