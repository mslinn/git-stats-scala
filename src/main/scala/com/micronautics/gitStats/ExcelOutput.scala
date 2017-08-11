package com.micronautics.gitStats

import org.apache.poi.xssf.usermodel.{XSSFCellStyle, XSSFFont, XSSFRow, XSSFSheet, XSSFWorkbook}
import org.apache.poi.ss.usermodel.{HorizontalAlignment, PrintSetup}
import org.apache.poi.ss.util.CellRangeAddress

// See http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/ss/examples/LoanCalculator.java
/** Only processes summary table */
class ExcelOutput(val fileName: String) {
  val workbook = new XSSFWorkbook

  protected val titleFont: XSSFFont = workbook.createFont
  titleFont.setFontHeightInPoints(14.toShort)
  titleFont.setBold(true)
  titleFont.setFontHeight(14)

  protected val titleStyle: XSSFCellStyle = workbook.createCellStyle
  titleStyle.setFont(titleFont)

  protected val commaFormat: Short = workbook.createDataFormat.getFormat("#,##0")

  protected val numberStyle: XSSFCellStyle = workbook.createCellStyle()
  numberStyle.setAlignment(HorizontalAlignment.RIGHT)
  numberStyle.setDataFormat(commaFormat)

  protected val stringStyle: XSSFCellStyle = workbook.createCellStyle()
  stringStyle.setAlignment(HorizontalAlignment.LEFT)

  protected val totalFont: XSSFFont = workbook.createFont
  totalFont.setBold(true)

  protected val totalStyle: XSSFCellStyle = workbook.createCellStyle()
  totalStyle.setAlignment(HorizontalAlignment.RIGHT)
  totalStyle.setDataFormat(commaFormat)
  totalStyle.setFont(totalFont)

  protected val totalLabelStyle: XSSFCellStyle = workbook.createCellStyle()
  totalLabelStyle.setAlignment(HorizontalAlignment.LEFT)
  totalLabelStyle.setFont(totalFont)

  /** @return newly created [[XSSFSheet]] instance */
  def addSheet(title: String, contents: List[Any]*): XSSFSheet = {
    val sheet = newSheet(title)

    contents.zipWithIndex.foreach { case (row, i) =>
      val ssRow = sheet.createRow(i + 2)
      row.zipWithIndex.foreach { case (value, j) =>
        val cell = ssRow.createCell(j)
        value match {
          case v: Int =>
            cell.setCellValue(v.toDouble)
            cell.setCellStyle(numberStyle)

          case v: Long =>
            cell.setCellValue(v.toDouble)
            cell.setCellStyle(numberStyle)

          case v: String =>
            cell.setCellValue(v)
            cell.setCellStyle(stringStyle)
        }
      }
    }

    val ssRow = sheet.createRow(contents.size + 2)

    val cell = ssRow.createCell(0)
    cell.setCellValue("Total")
    cell.setCellStyle(stringStyle)
    cell.setCellStyle(totalLabelStyle)

    (1 to 3).foreach { i =>
      val cell = ssRow.createCell(i)
      val col: String = ('A'.toInt + i).toChar.toString
      cell.setCellFormula(s"SUM(${col}3:${col}10)")
      cell.setCellStyle(totalStyle)
    }
    sheet
  }

  /** @return newly created [[XSSFSheet]] instance */
  // todo figure out why Commit.defaultCommitOrdering does not cause the commits to be ordered
  @inline def addSheetOfCommits(title: String, commits: List[Commit]*): XSSFSheet = {
    val commitsPerSheet: List[List[Any]] = commits.map(_.map(_.asExcelRow())).head
    addSheet(title=title, contents=commitsPerSheet:_*)
  }

  /** Common setup for every new sheet.
    * @return newly created [[XSSFSheet]] instance */
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
