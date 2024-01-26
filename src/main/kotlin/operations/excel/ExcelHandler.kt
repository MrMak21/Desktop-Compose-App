package operations.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ExcelHandler(private val workbook: Workbook, private val excelFile: File) {

    companion object {
        fun initExcelHandler(excelFile: File): ExcelHandler? {
//            val workbook = WorkbookFactory.create(excelFile) ?: return null
            val workbook = XSSFWorkbook(FileInputStream(excelFile)) ?: return null
            return ExcelHandler(workbook, excelFile)
        }
    }

    val ID_LETTERS = 1
    val ID_NUMBERS = 2
    val TELH_KYKLOFORIAS = -1

    private val TELH_KYKLOFORIAS_LABEL = "Β.ΤΚ"
    private var TELH_KYKLOFORIAS_COLUMN_INDEX = -1

    private var activeSheet: Sheet? = null

    init {
        activeSheet = workbook.getSheetAt(0)
        activeSheet?.let { activesheet ->
            findTelhKykloforiasCell(activesheet)
        }
    }

    private fun findTelhKykloforiasCell(sheet: Sheet) {
        val firstRow = sheet.getRow(0) ?: return
        for (cell in firstRow) {
            cell?.let { cell ->
                if (cell.cellType == CellType.STRING && cell.stringCellValue == TELH_KYKLOFORIAS_LABEL) {
                    TELH_KYKLOFORIAS_COLUMN_INDEX = cell.columnIndex
                }
            }
        }
    }

    fun writeTelhKykloforiasToExcel(vehicleId: String) {
        val vehicleIdLetters = vehicleId.substring(0,3)
        val vehicleIdNumbers = vehicleId.substring(3,7)

        val foundVehicleIdLetters = findStringCellInExcel(vehicleIdLetters) ?: return
        val foundVehicleIdNumbers = findNumberCellInExcel(vehicleIdNumbers) ?: return

        val foundRowLetters = foundVehicleIdLetters.row?.rowNum ?: return
        val foundRowNumbers = foundVehicleIdNumbers.row?.rowNum ?: return

        if (foundRowLetters != foundRowNumbers) {
            val finalFoundRow = findRowContainBothValues(foundRowLetters, foundRowNumbers, vehicleIdLetters, vehicleIdNumbers)
            writeInExcel(finalFoundRow, TELH_KYKLOFORIAS_COLUMN_INDEX)
        } else {
            writeInExcel(foundRowLetters, TELH_KYKLOFORIAS_COLUMN_INDEX)
        }

    }

    private fun writeInExcel(row: Int, column: Int) {
        activeSheet?.let { activeSheet ->
            val row = activeSheet.getRow(row) ?: return
            val cell = row.getCell(column) ?: return
            cell.setCellValue("OK!")

            saveAndCloseExcelFile()
        }
    }

    private fun saveAndCloseExcelFile() {
        FileOutputStream(excelFile).use { outputStream ->
            workbook.write(outputStream)
            outputStream.close()
        }
    }

    private fun findRowContainBothValues(lettersRow: Int, numbersRow: Int, lettersValue: String, numbersValue: String): Int {
        activeSheet?.let { activeSheet ->
            val lettersExcelRow = activeSheet.getRow(lettersRow)
            val numbersExcelRow = activeSheet.getRow(numbersRow)

            val lettersRow_lettersValueFound = lettersExcelRow?.find { it.cellType == CellType.STRING && it.stringCellValue == lettersValue }
            val lettersRow_numbersFoundValue = lettersExcelRow?.find { it.cellType == CellType.NUMERIC && it.numericCellValue == numbersValue.toDouble() }

            val numbersRow_lettersValueFound = numbersExcelRow?.find { it.cellType == CellType.STRING && it.stringCellValue == lettersValue }
            val numbersRow_numbersFoundValue = numbersExcelRow?.find { it.cellType == CellType.NUMERIC && it.numericCellValue == numbersValue.toDouble() }

            if (lettersRow_lettersValueFound != null && lettersRow_numbersFoundValue != null) {
                lettersRow_lettersValueFound.row?.let {
                    return it.rowNum
                }
            }

            if (numbersRow_lettersValueFound != null && numbersRow_numbersFoundValue != null) {
                numbersRow_lettersValueFound.row?.let {
                    return it.rowNum
                }
            }

            return -1

        }
        return -1
    }

    private fun findStringCellInExcel(value: String): Cell? {
        activeSheet?.let { activeSheet ->
            for (row in activeSheet) {
                row?.let { row ->
                    for (cell in row) {
                        cell?.let { cell ->
                            checkStringCell(cell, value) ?.let { foundCell ->
                                return foundCell
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun findNumberCellInExcel(value: String): Cell? {
        activeSheet?.let { activeSheet ->
            for (row in activeSheet) {
                row?.let { row ->
                    for (cell in row) {
                        cell?.let { cell ->
                            checkNumberCell(cell, value) ?.let { foundCell ->
                                return foundCell
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun checkNumberCell(cell: Cell, value: String): Cell? {
        if (cell.cellType == CellType.NUMERIC && cell.numericCellValue == value.toDouble()) {
            return cell
        }
        return null
    }

    private fun checkStringCell(cell: Cell, value: String): Cell? {
        if (cell.cellType == CellType.STRING && cell.stringCellValue == value) {
            return cell
        }
        return null
    }


}