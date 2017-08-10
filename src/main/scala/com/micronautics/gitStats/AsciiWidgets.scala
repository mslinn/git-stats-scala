package com.micronautics.gitStats

import de.vandermeer.asciitable._

object AsciiWidgets {
  def asciiTable(title: String, total: List[String], contents: List[String]*): String = {
    assert(contents.nonEmpty)
    val table = new AsciiTable
    val rows = contents.length
    table.addRule()
    if (contents.head.length==4)
      table.addRow("Language", "Lines added", "Lines deleted", "Net change")
    else
      table.addRow("Lines added", "Lines deleted", "Net change")
    table.addRule()
    0 until rows foreach { i =>
      val rowContents: List[String] = contents(i)
      table.addRow(rowContents:_*)
    }
    table.addRule()
    if (total.nonEmpty) {
      table.addRow(total:_*)
      table.addRule()
    }
    s"\n$title\n" + table.render
  }
}
