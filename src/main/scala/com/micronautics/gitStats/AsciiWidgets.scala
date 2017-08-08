package com.micronautics.gitStats

import de.vandermeer.asciitable._

object AsciiWidgets {
  protected lazy val defaultAsciiTableData: List[List[String]] =
    List(
      List("row 1 col 1", "row 1 col 2"),
      List("row 2 col 1", "row 2 col 2")
    )

  def asciiTable(contents: List[String]*): String = {
    val table = new AsciiTable
    val rows = contents.head.length
    0 until rows foreach { i =>
      table.addRule()
      table.addRow(contents(i):_*)
    }
    table.addRule()
    table.render
  }

  def demo(): Unit = println(asciiTable(defaultAsciiTableData:_*))
}
