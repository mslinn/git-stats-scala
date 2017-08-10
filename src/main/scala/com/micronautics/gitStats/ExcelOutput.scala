package com.micronautics.gitStats

import org.apache.poi.xssf.usermodel.{XSSFCellStyle, XSSFFont, XSSFRow, XSSFSheet, XSSFWorkbook}
import org.apache.poi.ss.usermodel.{HorizontalAlignment, PrintSetup}
import org.apache.poi.ss.util.CellRangeAddress

// See http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/ss/examples/LoanCalculator.java
class ExcelOutput(val fileName: String) {
  val workbook = new XSSFWorkbook

  protected val titleFont: XSSFFont = workbook.createFont
  titleFont.setFontHeightInPoints(14.toShort)
  titleFont.setBold(true)
  titleFont.setFontHeight(14)

  protected val titleStyle: XSSFCellStyle = workbook.createCellStyle
  titleStyle.setFont(titleFont)

  protected val numberStyle: XSSFCellStyle = workbook.createCellStyle()
  numberStyle.setAlignment(HorizontalAlignment.RIGHT)

  protected val totalFont: XSSFFont = workbook.createFont
  totalFont.setBold(true)
  protected val totalStyle: XSSFCellStyle = workbook.createCellStyle()
  totalStyle.setAlignment(HorizontalAlignment.RIGHT)
  totalStyle.setFont(totalFont)

  def addSheet(title: String, total: List[String], contents: List[String]*): XSSFWorkbook = {
    val sheet = newSheet(title)

    contents.zipWithIndex.foreach { case (row, i) =>
      val ssRow = sheet.createRow(i + 2)
      row.zipWithIndex.foreach { case (value, j) =>
        val cell = ssRow.createCell(j)
        cell.setCellValue(value)
        cell.setCellStyle(numberStyle)
      }
    }

    total.zipWithIndex.foreach { case (value, i) =>
      val ssRow = sheet.createRow(contents.size + i + 2)
      val cell = ssRow.createCell(i)
      cell.setCellValue(value)
      cell.setCellStyle(totalStyle)
    }
    workbook
  }

  def addSheet(title: String, total: Commit, contents: List[Commit]*): XSSFWorkbook = {
    val x = contents.map(_.sorted.map(_.asRow())).head
    addSheet(title=title, total=total.asRow(), contents=x:_*)
  }

  protected def newSheet(title: String): XSSFSheet = {
    val sheet: XSSFSheet = workbook.createSheet(title)

    val printSetup: PrintSetup = sheet.getPrintSetup
    printSetup.setLandscape(true)
    sheet.setFitToPage(true)
    sheet.setHorizontallyCenter(true)

    val titleRow: XSSFRow = sheet.createRow(0)
    val titleCell = titleRow.createCell(0)
    titleCell.setCellValue(title)
    titleCell.setCellStyle(titleStyle)
    sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$D$1"))

    sheet
  }

  def save(): Unit = {
    import java.io.FileOutputStream
    val fullFileName: String = if (fileName.toLowerCase.endsWith(".xlsx")) fileName else s"$fileName.xlsx"
    val out = new FileOutputStream(fullFileName)
    workbook.write(out)
  }
}
