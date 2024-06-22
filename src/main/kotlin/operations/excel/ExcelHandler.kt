package operations.excel


import extensions.*
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

    private val TELH_KYKLOFORIAS_LABEL = "Β.ΤΚ"
    private var TELH_KYKLOFORIAS_COLUMN_INDEX = -1

    private var VEHICLE_LICENSE_PLATE_COLUMN_INDEX = -1
    private var VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX = -1

    private var activeSheet: Sheet? = null

    init {
        activeSheet = workbook.getSheetAt(0)
        activeSheet?.let { activesheet ->
            findTelhKykloforiasCell(activesheet)
            findVehiclePlateColumnIndex(activesheet)
        }
    }

    private fun findTelhKykloforiasCell(sheet: Sheet) {
        val firstRow = sheet.getRow(0) ?: return
        for (cell in firstRow) {
            cell?.let { currentCell ->
                if (currentCell.cellType == CellType.STRING && currentCell.stringCellValue == TELH_KYKLOFORIAS_LABEL) {
                    TELH_KYKLOFORIAS_COLUMN_INDEX = currentCell.columnIndex
                }
            }
        }
    }

    private fun findVehiclePlateColumnIndex(sheet: Sheet) {
        for (row in sheet) {
            for (cell in row) {
                cell?.let { currentCell ->
                    if (currentCell.cellType == CellType.STRING && currentCell.stringCellValue.isVehiclePlateLetters()) {
                        VEHICLE_LICENSE_PLATE_COLUMN_INDEX = currentCell.columnIndex
                    }
                    if (currentCell.cellType == CellType.NUMERIC && currentCell.numericCellValue.isVehiclePlateNumbers()) {
                        VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX = currentCell.columnIndex
                    }
                    if (VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX != -1 && VEHICLE_LICENSE_PLATE_COLUMN_INDEX != -1) {
                        return
                    }
                }
            }
        }
    }

    fun writeTelhKykloforiasToExcel(vehicleId: String): Boolean {
        val vehicleIdLettersGR = vehicleId.substring(0,3)
        val vehicleIdNumbersGR = vehicleId.substring(3,7)

        val vehicleIdLettersRow = findVehicleIdLettersCell(vehicleIdLettersGR)?.row?.rowNum ?: return false
        val vehicleIdNumbersRow = findVehicleIdNumbersCell(vehicleIdNumbersGR)?.row?.rowNum ?: return false


        return if (vehicleIdLettersRow != vehicleIdNumbersRow) {
            val finalFoundRow = findRowContainBothValues(vehicleIdLettersRow, vehicleIdNumbersRow, vehicleIdLettersGR, vehicleIdNumbersGR)
            writeInExcel(finalFoundRow, TELH_KYKLOFORIAS_COLUMN_INDEX)
        } else {
            writeInExcel(vehicleIdLettersRow, TELH_KYKLOFORIAS_COLUMN_INDEX)
        }

    }

    private fun writeInExcel(row: Int, column: Int): Boolean {
        return try {
            activeSheet?.let { activeSheet ->
                val currRow = activeSheet.getRow(row) ?: return false
                val cell = currRow.getCell(column) ?: return false
                cell.setCellValue("OK!")

                saveAndCloseExcelFile()

                true
            } ?: false
        } catch (t: Throwable) {
            false
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

            val lettersRow_lettersValueFound = lettersExcelRow?.find { it.hasStringValue(lettersValue) }
            val lettersRow_numbersFoundValue = lettersExcelRow?.find { it.hasNumericValue(numbersValue) }

            val numbersRow_lettersValueFound = numbersExcelRow?.find { it.hasStringValue(lettersValue) }
            val numbersRow_numbersFoundValue = numbersExcelRow?.find { it.hasNumericValue(numbersValue) }

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

    private fun findVehicleIdLettersCell(vehicleId: String): Cell? {
        if (VEHICLE_LICENSE_PLATE_COLUMN_INDEX == -1) {
            return null
        }

        return activeSheet?.findCell(VEHICLE_LICENSE_PLATE_COLUMN_INDEX, vehicleId)
    }

    private fun findVehicleIdNumbersCell(vehicleIdNumbers: String): Cell? {
        if (VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX == -1) {
            return null
        }

        return activeSheet?.findCell(VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX, vehicleIdNumbers)
    }


}