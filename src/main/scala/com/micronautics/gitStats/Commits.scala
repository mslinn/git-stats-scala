package com.micronautics.gitStats

import com.github.tototoshi.csv.CSVWriter
import scala.collection.mutable

case class Commits(value: List[Commit])
                  (implicit config: ConfigGitStats){
  def asAsciiTable(title: String): String = {
    val subTotals: List[List[String]] =
      value.filter(c => c.added!=0 | c.deleted!=0).map { commit =>
        commit.asAsciiTable()
      }

    if (config.csvOutput) {
      List(List(title)) ::: subTotals ::: total.asCsv()

      val stringWriter = new java.io.StringWriter
      val csvWriter = new CSVWriter(stringWriter)
      Console.out.println(title)
      csvWriter.writeAll(subTotals)
      csvWriter.writeAll(total.asCsv())

      ""
    } else
      AsciiWidgets.asciiTable(title, total.asAsciiTable(), subTotals: _*)
  }

  def asCommitsGroupedByLanguage: Map[String, Commit] = {
    val map = mutable.Map.empty[String, Commit]
    value.foreach { commit =>
      val updated: Commit = map.get(commit.language).map { acc =>
        Commit(
          added = acc.added + commit.added,
          deleted = acc.deleted + commit.deleted,
          language = commit.language
        )
      }.getOrElse(commit)
      map.put(commit.language, updated)
    }
    map.toMap
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

  def combine(other: Commits): Commits =
    Commits(this.value ::: other.value)
      .byLanguage

  def combine(others: List[Commits]): Commits =
    Commits(this.value ::: others.flatMap(_.value))
      .byLanguage

  def languageTotals: LanguageTotals = LanguageTotals(this)

  def total: Commit = value.fold(Commit.zero) {
    case (acc, elem) => Commit(acc.added + elem.added, acc.deleted + elem.deleted)
  }.copy(language = Commit.languageTotal)
}
